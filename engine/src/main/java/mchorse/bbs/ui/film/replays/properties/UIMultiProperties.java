package mchorse.bbs.ui.film.replays.properties;

import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIMultiProperties extends UIProperties
{
    public static final int TOP_MARGIN = 15;

    public List<UIProperty> properties = new ArrayList<>();

    private IUIClipsDelegate delegate;

    public UIMultiProperties(IUIClipsDelegate delegate, Consumer<GenericKeyframe> callback)
    {
        super(callback);

        this.delegate = delegate;
    }

    /* Implementation of setters */

    @Override
    public void setTick(double tick)
    {
        if (this.isMultipleSelected())
        {
            tick = (long) tick;

            double dx = tick - this.getCurrent().tick;

            for (UIProperty property : this.properties)
            {
                property.setTick(dx);
            }
        }
        else
        {
            this.getCurrent().tick = (long) tick;
        }

        this.sliding = true;
    }

    @Override
    public void setValue(Object value)
    {
        GenericKeyframe current = this.getCurrent();

        if (this.isMultipleSelected())
        {
            for (UIProperty property : this.properties)
            {
                if (current.getFactory() == property.channel.getFactory())
                {
                    property.setValue(current.value);
                }
            }
        }
        else
        {
            current.value = value;
        }
    }

    @Override
    public void setInterpolation(IInterpolation interp)
    {
        for (UIProperty property : this.properties)
        {
            property.setInterpolation(interp);
        }
    }

    /* Graphing code */

    @Override
    public void resetView()
    {
        int c = 0;

        this.scaleX.set(0, 2);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        /* Find minimum and maximum */
        for (UIProperty property : this.properties)
        {
            for (Object object : property.channel.getKeyframes())
            {
                GenericKeyframe frame = (GenericKeyframe) object;

                min = Integer.min((int) frame.tick, min);
                max = Integer.max((int) frame.tick, max);
            }

            c = Math.max(c, property.channel.getKeyframes().size());
        }

        if (c <= 1)
        {
            if (c == 0)
            {
                min = 0;
            }

            max = this.duration;
        }

        if (Math.abs(max - min) > 0.01F)
        {
            this.scaleX.viewOffset(min, max, this.area.w, 20);
        }
    }

    @Override
    public GenericKeyframe getCurrent()
    {
        UIProperty current = this.getCurrentSheet();

        return current == null ? null : current.getKeyframe();
    }

    @Override
    public List<UIProperty> getProperties()
    {
        return this.properties;
    }

    @Override
    public UIProperty getSheet(int mouseY)
    {
        int propertyCount = this.properties.size();
        int h = (this.area.h - TOP_MARGIN) / propertyCount;

        for (int i = 0; i < propertyCount; i++)
        {
            UIProperty property = this.properties.get(i);
            int y = this.area.y + h * i + TOP_MARGIN;

            if (mouseY >= y && mouseY < y + h)
            {
                return property;
            }
        }

        return null;
    }

    @Override
    public void selectAll()
    {
        for (UIProperty property : this.properties)
        {
            property.selectAll();
        }

        this.selected = true;

        this.setKeyframe(this.getCurrent());
    }

    public UIProperty getCurrentSheet()
    {
        for (UIProperty property : this.properties)
        {
            if (!property.selected.isEmpty())
            {
                return property;
            }
        }

        return null;
    }

    @Override
    public int getSelectedCount()
    {
        int i = 0;

        for (UIProperty property : this.properties)
        {
            i += property.getSelectedCount();
        }

        return i;
    }

    @Override
    public void clearSelection()
    {
        this.selected = false;

        for (UIProperty property : this.properties)
        {
            property.clearSelection();
        }
    }

    @Override
    public void addCurrent(int mouseX, int mouseY)
    {
        int propertyCount = this.properties.size();
        int h = (this.area.h - TOP_MARGIN) / propertyCount;
        int i = (mouseY - (this.area.ey() - h * propertyCount)) / h;

        if (i < 0 || i >= propertyCount)
        {
            return;
        }

        UIProperty property = this.properties.get(i);
        IInterpolation interp = Interpolation.LINEAR;
        GenericKeyframe frame = this.getCurrent();
        IGenericKeyframeFactory factory = property.channel.getFactory();
        long tick = Math.round(this.fromGraphX(mouseX));
        long oldTick = tick;

        if (frame != null)
        {
            interp = frame.interp;
            oldTick = frame.tick;
        }

        Object value;
        Pair segment = property.channel.findSegment(tick);

        if (segment == null)
        {
            value = factory.copy(property.property.get());
        }
        else
        {
            GenericKeyframe a = (GenericKeyframe) segment.a;
            GenericKeyframe b = (GenericKeyframe) segment.b;

            if (a == b)
            {
                value = a.value;
            }
            else
            {
                value = factory.interpolate(a.value, b.value, a.interp, (tick - a.tick) / (float) (b.tick - a.tick));
            }

            value = factory.copy(value);
        }

        property.selected.clear();
        property.selected.add(property.channel.insert(tick, value));
        frame = this.getCurrent();

        if (oldTick != tick)
        {
            frame.setInterpolation(interp);
        }

        this.addedDoubleClick(frame, tick, mouseX, mouseY);
    }

    protected void addedDoubleClick(GenericKeyframe frame, long tick, int mouseX, int mouseY)
    {}

    @Override
    public void removeCurrent()
    {
        GenericKeyframe frame = this.getCurrent();

        if (frame == null)
        {
            return;
        }

        UIProperty current = this.getCurrentSheet();

        current.channel.remove(current.selected.get(0));
        current.selected.clear();

        this.selected = false;
    }

    @Override
    public void removeSelectedKeyframes()
    {
        for (UIProperty property : this.properties)
        {
            property.removeSelectedKeyframes();
        }

        this.setKeyframe(null);

        this.selected = false;
    }

    /* Mouse input handling */

    @Override
    protected void duplicateKeyframe(UIContext context, int mouseX, int mouseY)
    {
        long offset = (long) this.fromGraphX(mouseX);

        for (UIProperty property : this.properties)
        {
            property.duplicate(offset);
        }

        this.setKeyframe(this.getCurrent());
    }

    @Override
    protected boolean pickKeyframe(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        int propertyCount = this.properties.size();
        int h = (this.area.h - TOP_MARGIN) / propertyCount;
        int y = this.area.ey() - h * propertyCount;
        boolean alt = Window.isAltPressed();
        boolean finished = false;
        boolean isMultiSelect = this.isMultipleSelected();

        for (UIProperty property : this.properties)
        {
            int index = 0;

            for (Object object : property.channel.getKeyframes())
            {
                GenericKeyframe frame = (GenericKeyframe) object;
                boolean point = this.isInside(this.toGraphX(frame.tick), alt ? mouseY : y + h / 2, mouseX, mouseY);

                if (point)
                {
                    int key = property.selected.indexOf(index);

                    if (!shift && key == -1 && !alt)
                    {
                        this.clearSelection();
                    }

                    if (!shift)
                    {
                        this.selected = true;

                        if (key == -1)
                        {
                            property.selected.add(index);
                            frame = isMultiSelect ? this.getCurrent() : frame;
                        }
                        else
                        {
                            frame = this.getCurrent();
                        }

                        this.setKeyframe(frame);
                    }

                    if (frame != null)
                    {
                        this.lastT = frame.tick;
                    }

                    if (alt)
                    {
                        if (frame != null)
                        {
                            finished = true;
                        }
                    }
                    else
                    {
                        return true;
                    }
                }

                index++;
            }

            y += h;
        }

        return finished;
    }

    private boolean isInside(double x, double y, int mouseX, int mouseY)
    {
        double d = Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2);

        return Math.sqrt(d) < 4;
    }

    @Override
    protected void resetMouseReleased(UIContext context)
    {
        if (this.isGrabbing())
        {
            /* Multi select */
            Area area = new Area();

            area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY, 3);

            int count = this.properties.size();
            int h = (this.area.h - TOP_MARGIN) / count;
            int y = this.area.ey() - h * count;
            int c = 0;

            for (UIProperty property : this.properties)
            {
                int i = 0;

                for (Object object : property.channel.getKeyframes())
                {
                    GenericKeyframe keyframe = (GenericKeyframe) object;

                    if (area.isInside(this.toGraphX(keyframe.tick), y + h / 2) && !property.selected.contains(i))
                    {
                        property.selected.add(i);
                        c++;
                    }

                    i++;
                }

                y += h;
            }

            if (c > 0)
            {
                this.selected = true;

                this.setKeyframe(this.getCurrent());
            }
        }

        super.resetMouseReleased(context);
    }

    /* Rendering */

    @Override
    protected void renderGraph(UIContext context, int mouseX, int mouseY)
    {
        /* Draw dope property */
        int propertyCount = this.properties.size();

        if (propertyCount == 0)
        {
            return;
        }

        int h = (this.area.h - TOP_MARGIN) / propertyCount;
        int y = this.area.ey() - h * propertyCount;

        for (UIProperty property : this.properties)
        {
            COLOR.set(property.color, false);

            LineBuilder line = new LineBuilder(0.75F);

            line.add(this.area.x, y + h / 2);
            line.add(this.area.ex(), y + h / 2);
            line.render(context.batcher, SolidColorLineRenderer.get(COLOR.r, COLOR.g, COLOR.b, 0.65F));

            /* Draw points */
            int index = 0;

            for (Object object : property.channel.getKeyframes())
            {
                GenericKeyframe frame = (GenericKeyframe) object;

                this.renderRect(context, this.toGraphX(frame.tick), y + h / 2, 3, property.hasSelected(index) ? Colors.WHITE : property.color);

                index++;
            }

            index = 0;

            for (Object object : property.channel.getKeyframes())
            {
                GenericKeyframe frame = (GenericKeyframe) object;

                this.renderRect(context, this.toGraphX(frame.tick), y + h / 2, 2, property.hasSelected(index) ? Colors.ACTIVE : 0);

                index++;
            }

            int lw = context.font.getWidth(property.title.get()) + 10;
            context.batcher.gradientHBox(this.area.ex() - lw - 10, y, this.area.ex(), y + h, property.color, Colors.A75 | property.color);
            context.batcher.textShadow(property.title.get(), this.area.ex() - lw + 5, y + (h - context.font.getHeight()) / 2);

            y += h;
        }
    }

    /* Handling dragging */

    @Override
    protected GenericKeyframe moving(UIContext context, int mouseX, int mouseY)
    {
        GenericKeyframe frame = this.getCurrent();
        double x = this.fromGraphX(mouseX);

        if (!this.selected)
        {
            this.moveNoKeyframe(context, frame, x, 0);
        }
        else
        {
            if (this.isMultipleSelected())
            {
                int dx = mouseX - this.lastX;
                int xx = this.toGraphX(this.lastT);

                x = this.fromGraphX(xx + dx);
            }

            this.setTick(x);
        }

        return frame;
    }

    /* ... */

    @Override
    protected void moveNoKeyframe(UIContext context, GenericKeyframe frame, double x, double y)
    {
        if (this.delegate != null)
        {
            this.delegate.setCursor((int) x);
        }
    }

    @Override
    protected void renderCursor(UIContext context)
    {
        if (this.delegate != null)
        {
            int cx = this.toGraphX(this.delegate.getCursor());

            context.batcher.box(cx - 1, this.area.y, cx + 1, this.area.ey(), Colors.CURSOR);
        }
    }
}