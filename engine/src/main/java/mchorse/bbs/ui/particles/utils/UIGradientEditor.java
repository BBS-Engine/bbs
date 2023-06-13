package mchorse.bbs.ui.particles.utils;

import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.components.appearance.colors.Gradient;
import mchorse.bbs.particles.components.appearance.colors.Solid;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeSection;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

public class UIGradientEditor extends UIElement
{
    private UIParticleSchemeSection section;
    private UIColor color;

    private Gradient gradient;

    private Gradient.ColorStop current;
    private int dragging = -1;
    private int lastX;

    private Area a = new Area();
    private Area b = new Area();
    private Color c = new Color();

    public UIGradientEditor(UIParticleSchemeSection section, UIColor color)
    {
        super();

        this.section = section;
        this.color = color;

        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SNOWSTORM_LIGHTING_CONTEXT_ADD_STOP, this::addColorStop);

            if (this.gradient.stops.size() > 1)
            {
                menu.action(Icons.REMOVE, UIKeys.SNOWSTORM_LIGHTING_CONTEXT_REMOVE_STOP, this::removeColorStop);
            }
        });

        this.h(20);
    }

    private Color fillColor(Solid solid)
    {
        this.c.r = (float) solid.r.get();
        this.c.g = (float) solid.g.get();
        this.c.b = (float) solid.b.get();
        this.c.a = (float) solid.a.get();

        return this.c;
    }

    private Area fillBound(Gradient.ColorStop stop)
    {
        int x = this.a.x(stop.stop / this.gradient.range);

        this.b.set(x - 3, this.a.ey() - 7, 6, 10);

        return this.b;
    }

    private void fillStop(Gradient.ColorStop stop)
    {
        this.current = stop;
        this.color.setColor(this.fillColor(stop.color).getARGBColor());
    }

    public void setColor(int color)
    {
        this.c.set(color);

        ((MolangValue) this.current.color.r).expression.set(this.c.r);
        ((MolangValue) this.current.color.g).expression.set(this.c.g);
        ((MolangValue) this.current.color.b).expression.set(this.c.b);
        ((MolangValue) this.current.color.a).expression.set(this.c.a);
    }

    public void setGradient(Gradient gradient)
    {
        this.gradient = gradient;

        if (this.gradient.stops.isEmpty())
        {
            this.gradient.stops.add(new Gradient.ColorStop(0, new Solid()));
        }

        this.fillStop(this.gradient.stops.get(0));
        this.color.setColor(this.fillColor(this.current.color).getARGBColor());
    }

    private void addColorStop()
    {
        float x = (this.getContext().mouseX - this.area.x) / (float) this.area.w * this.gradient.range;

        Solid color = new Solid();
        Gradient.ColorStop stop = new Gradient.ColorStop(x, color);

        color.r = new MolangValue(null, new Constant(1F));
        color.g = new MolangValue(null, new Constant(1F));
        color.b = new MolangValue(null, new Constant(1F));
        color.a = new MolangValue(null, new Constant(1F));

        this.gradient.stops.add(stop);
        this.gradient.sort();

        this.fillStop(stop);
    }

    private void removeColorStop()
    {
        int index = this.gradient.stops.indexOf(this.current);

        this.gradient.stops.remove(index);

        index = MathUtils.clamp(index, 0, this.gradient.stops.size() - 1);

        this.fillStop(this.gradient.stops.get(index));
    }

    @Override
    public void resize()
    {
        super.resize();

        this.a.copy(this.area);
        this.a.offset(-1);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            for (Gradient.ColorStop stop : this.gradient.stops)
            {
                Area area = this.fillBound(stop);

                if (area.isInside(context))
                {
                    this.dragging = 0;
                    this.lastX = context.mouseX;
                    this.fillStop(stop);

                    return true;
                }
            }

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.dragging != -1)
        {
            this.section.dirty();
        }

        this.dragging = -1;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.dragging == 0 && Math.abs(context.mouseX - this.lastX) > 3)
        {
            this.dragging = 1;
        }
        else if (this.dragging == 1)
        {
            float x = (context.mouseX - this.area.x) / (float) this.area.w * this.gradient.range;

            this.current.stop = MathUtils.clamp(x, 0, this.gradient.range);
            this.gradient.sort();
        }

        this.area.render(context.draw, Colors.A100);

        int size = this.gradient.stops.size();

        Icons.CHECKBOARD.renderArea(context.draw, this.a.x, this.a.y, this.a.w, this.a.h);

        Gradient.ColorStop first = this.gradient.stops.get(0);

        if (first.stop > 0)
        {
            int x1 = this.a.x(first.stop / this.gradient.range);
            int rgba1 = this.fillColor(first.color).getARGBColor();

            context.draw.box(this.a.x, this.a.y, x1, this.a.ey(), rgba1);
        }

        for (int i = 0; i < size; i++)
        {
            Gradient.ColorStop stop = this.gradient.stops.get(i);
            Gradient.ColorStop next = i + 1 < size ? this.gradient.stops.get(i + 1) : stop;

            int x1 = this.a.x(stop.stop / this.gradient.range);
            int x2 = this.a.x((next == stop ? this.gradient.range : next.stop) / this.gradient.range);

            int rgba1 = this.fillColor(stop.color).getARGBColor();
            int rgba2 = this.fillColor(next.color).getARGBColor();

            context.draw.gradientHBox(x1, this.a.y, x2, this.a.ey(), rgba1, rgba2);
        }

        for (int i = 0; i < size; i++)
        {
            Gradient.ColorStop stop = this.gradient.stops.get(i);
            Area area = this.fillBound(stop);
            int handleColor = this.fillColor(stop.color).getARGBColor();

            context.draw.box(area.x, area.y, area.ex(), area.ey(), this.current == stop ? Colors.WHITE : Colors.A100);
            context.draw.box(area.x + 1, area.y + 1, area.ex() - 1, area.ey() - 1, handleColor);
        }

        super.render(context);
    }
}
