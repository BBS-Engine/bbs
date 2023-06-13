package mchorse.bbs.ui.particles.utils;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.ParticleCurve;
import mchorse.bbs.particles.ParticleCurveType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeSection;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class UICurve extends UIElement
{
    private UIParticleSchemeSection section;
    private UITrackpad value;

    private Area graph = new Area();
    private ParticleCurve curve;
    private int index;
    private boolean dragging;
    private boolean moving;
    private int lastX;
    private int lastY;

    /* x = min, y = max */
    private Vector2d range = new Vector2d();

    public UICurve(UIParticleSchemeSection section)
    {
        this.section = section;

        this.value = new UITrackpad((v) ->
        {
            this.curve.nodes.set(this.index, new MolangValue(null, new Constant(v)));
            this.section.dirty();
            this.updateRange();
        });
        this.value.relative(this).y(1F, -20).w(1F);

        this.add(this.value);

        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SNOWSTORM_CURVES_CONTEXT_ADD, this::addPoint);

            if (this.index >= 0)
            {
                menu.action(Icons.REMOVE, UIKeys.SNOWSTORM_CURVES_CONTEXT_REMOVE, this::removePoint);
            }
        });
    }

    private void addPoint()
    {
        int index = this.index + 1;

        if (index < this.curve.nodes.size())
        {
            this.curve.nodes.add(index, MolangParser.ZERO);
            this.setIndex(index);
        }
        else
        {
            this.curve.nodes.add(MolangParser.ZERO);
            this.setIndex(this.curve.nodes.size() - 1);
        }

        this.section.dirty();
    }

    private void removePoint()
    {
        if (this.index < 0)
        {
            return;
        }

        this.curve.nodes.remove(this.index);
        this.setIndex(this.index - 1);
        this.section.dirty();
    }

    public void fill(ParticleCurve curve)
    {
        this.curve = curve;

        this.setIndex(-1);
        this.updateRange();
    }

    private void setIndex(int i)
    {
        this.index = i;

        boolean isValid = i >= 0 && i < this.curve.nodes.size();

        this.value.setVisible(true);
        this.value.setEnabled(isValid);

        if (isValid)
        {
            this.value.setValue(this.curve.nodes.get(i).get());
        }
    }

    private Vector2d getVector(int index, double min, double max)
    {
        index = MathUtils.clamp(index, 0, this.curve.nodes.size() - 1);

        MolangExpression expression = this.curve.nodes.get(index);
        double value = expression.get();
        double factor = 1 - (value - min) / (max - min);

        int x = this.graph.x + (int) (index / (float) (this.curve.nodes.size() - 1) * this.graph.w);
        int y = this.graph.y + (int) (this.graph.h * factor);

        return new Vector2d(x, y);
    }

    private void updateRange()
    {
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < this.curve.nodes.size(); i++)
        {
            MolangExpression expression = this.curve.nodes.get(i);
            double value = expression.get();

            min = Math.min(min, value);
            max = Math.max(max, value);
        }

        this.range.set(min, max);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.graph.copy(this.area);
        this.graph.x += 10;
        this.graph.y += 10;
        this.graph.w -= 20;
        this.graph.h -= 40;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            for (int i = 0; i < this.curve.nodes.size(); i++)
            {
                Vector2d point = this.getVector(i, this.range.x, this.range.y);

                double dx = point.x - context.mouseX;
                double dy = point.y - context.mouseY;
                double d = dx * dx + dy * dy;

                if (d <= 25)
                {
                    this.setIndex(i);

                    this.dragging = true;
                    this.lastX = context.mouseX;
                    this.lastY = context.mouseY;

                    return true;
                }
            }

            this.setIndex(-1);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.moving)
        {
            this.updateRange();
        }

        this.dragging = false;
        this.moving = false;

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.draw, Colors.A50);

        if (this.curve != null)
        {
            this.handleDragging(context);

            context.draw.clip(this.area, context);
            this.drawGraph(context);
            context.draw.unclip(context);
        }

        super.render(context);
    }

    private void handleDragging(UIContext context)
    {
        if (this.dragging && !this.moving)
        {
            int dx = context.mouseX - this.lastX;
            int dy = context.mouseY - this.lastY;
            int d = dx * dx + dy * dy;

            if (d > 9)
            {
                this.moving = true;
            }
        }

        if (this.moving)
        {
            double factor = -(context.mouseY - this.graph.ey()) / (double) this.graph.h;
            double value = this.range.x + factor * (this.range.y - this.range.x);

            this.curve.nodes.set(this.index, new MolangValue(null, new Constant(value)));
            this.value.setValue(value);
            this.section.dirty();
        }
    }

    private void drawGraph(UIContext context)
    {
        int c = this.curve.nodes.size();

        Shader shader = context.render.getShaders().get(VBOAttributes.VERTEX_RGBA_2D);
        VAOBuilder builder = context.render.getVAO().setup(shader, VAO.DATA);

        builder.begin();

        /* Top and bottom */
        builder.xy(this.area.x, this.graph.y).rgba(0.5F, 0.5F, 0.5F, 0.5F);
        builder.xy(this.area.ex(), this.graph.y).rgba(0.5F, 0.5F, 0.5F, 0.5F);

        builder.xy(this.area.x, this.graph.ey()).rgba(0.5F, 0.5F, 0.5F, 0.5F);
        builder.xy(this.area.ex(), this.graph.ey()).rgba(0.5F, 0.5F, 0.5F, 0.5F);

        /* Left and right */
        builder.xy(this.graph.x, this.area.y).rgba(0.5F, 0.5F, 0.5F, 0.5F);
        builder.xy(this.graph.x, this.area.ey()).rgba(0.5F, 0.5F, 0.5F, 0.5F);

        builder.xy(this.graph.ex(), this.area.y).rgba(0.5F, 0.5F, 0.5F, 0.5F);
        builder.xy(this.graph.ex(), this.area.ey()).rgba(0.5F, 0.5F, 0.5F, 0.5F);

        if (this.curve.type == ParticleCurveType.HERMITE && c >= 4)
        {
            Vector2d first = this.getVector(1, this.range.x, this.range.y);
            Vector2d last = this.getVector(c - 2, this.range.x, this.range.y);

            /* Hermite bounds */
            builder.xy((float) first.x, this.graph.y).rgba(0.25F, 0.25F, 0.25F, 0.5F);
            builder.xy((float) first.x, this.graph.ey()).rgba(0.25F, 0.25F, 0.25F, 0.5F);

            builder.xy((float) last.x, this.graph.y).rgba(0.25F, 0.25F, 0.25F, 0.5F);
            builder.xy((float) last.x, this.graph.ey()).rgba(0.25F, 0.25F, 0.25F, 0.5F);
        }

        builder.render(GL11.GL_LINES);

        Color color = Colors.COLOR;
        LineBuilder line = new LineBuilder(0.75F);

        color.set(BBSSettings.primaryColor.get(), false);

        for (int i = 0; i < c; i++)
        {
            Vector2d v1 = this.getVector(i, this.range.x, this.range.y);
            Vector2d v2 = this.getVector(i + 1, this.range.x, this.range.y);
            boolean last = i == c - 1;

            if (this.curve.type == ParticleCurveType.LINEAR)
            {
                line.add((float) v1.x, (float) v1.y);

                if (last)
                {
                    line.add((float) v2.x, (float) v2.y);
                }
            }
            else
            {
                Vector2d v0 = this.getVector(i - 1, this.range.x, this.range.y);
                Vector2d v3 = this.getVector(i + 2, this.range.x, this.range.y);
                final double d = 5;

                for (int j = 0; j < d; j++)
                {
                    int x1 = (int) Interpolations.lerp(v1.x, v2.x, j / d);
                    int vy1 = (int) Interpolations.cubicHermite(v0.y, v1.y, v2.y, v3.y, j / d);

                    line.add(x1, vy1);

                    if (last)
                    {
                        int x2 = (int) Interpolations.lerp(v1.x, v2.x, (j + 1) / d);
                        int vy2 = (int) Interpolations.cubicHermite(v0.y, v1.y, v2.y, v3.y, (j + 1) / d);

                        line.add(x2, vy2);
                    }
                }
            }
        }

        line.render(builder, SolidColorLineRenderer.get(color.r, color.g, color.b, 1F));

        for (int i = 0; i < c; i++)
        {
            Vector2d vector = this.getVector(i, this.range.x, this.range.y);
            int x = (int) vector.x;
            int y = (int) vector.y;

            context.draw.box(x - 3, y - 3, x + 3, y + 3, this.index == i ? Colors.setA(Colors.ACTIVE, 1F) : Colors.WHITE);
            context.draw.box(x - 2, y - 2, x + 2, y + 2, Colors.A100);
        }
    }
}