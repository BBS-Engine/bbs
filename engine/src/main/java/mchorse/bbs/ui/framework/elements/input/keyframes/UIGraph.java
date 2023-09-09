package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.graphics.line.Line;
import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.Scale;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Graph view
 *
 * This GUI element is responsible for displaying and editing of
 * keyframe channel (keyframes and its bezier handles)
 */
public class UIGraph extends UIKeyframes
{
    public UISheet sheet = new UISheet("main", IKey.EMPTY, 0, null);
    
    private Scale scaleY;

    public UIGraph(Consumer<Keyframe> callback)
    {
        super(callback);

        this.scaleY = new Scale(this.area, ScrollDirection.VERTICAL);
        this.scaleY.inverse().anchor(0.5F);
    }

    public Scale getScaleY()
    {
        return this.scaleY;
    }

    public void setChannel(KeyframeChannel channel, int color)
    {
        this.sheet.channel = channel;
        this.sheet.color = color;
    }

    public void setColor(int color)
    {
        this.sheet.color = color;
    }

    /* Implementation of setters */

    @Override
    public void setTick(double tick, boolean opposite)
    {
        if (this.isMultipleSelected())
        {
            if (this.which == Selection.KEYFRAME)
            {
                tick = (long) tick;
            }

            this.sheet.setTick(tick - this.which.getX(this.getCurrent()), this.which, opposite);
        }
        else
        {
            this.which.setX(this.getCurrent(), tick, opposite);
        }

        this.sliding = true;
    }

    @Override
    public void setValue(double value, boolean opposite)
    {
        if (this.isMultipleSelected())
        {
            this.sheet.setValue(value - this.which.getY(this.getCurrent()), this.which, opposite);
        }
        else
        {
            this.which.setY(this.getCurrent(), value, opposite);
        }
    }

    @Override
    public void setInterpolation(KeyframeInterpolation interp)
    {
        this.sheet.setInterpolation(interp);
    }

    @Override
    public void setEasing(KeyframeEasing easing)
    {
        this.sheet.setEasing(easing);
    }

    /* Graphing code */

    public int toGraphY(double value)
    {
        return (int) this.scaleY.to(value);
    }

    public double fromGraphY(int mouseY)
    {
        return this.scaleY.from(mouseY);
    }

    @Override
    public void resetView()
    {
        this.scaleX.set(0, 2);
        this.scaleY.set(0, 2);

        KeyframeChannel channel = this.sheet.channel;
        int c = channel.getKeyframes().size();

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        if (c > 1)
        {
            for (Keyframe frame : channel.getKeyframes())
            {
                minX = Math.min(minX, frame.getTick());
                minY = Math.min(minY, frame.getValue());
                maxX = Math.max(maxX, frame.getTick());
                maxY = Math.max(maxY, frame.getValue());
            }
        }
        else
        {
            minX = 0;
            maxX = this.duration;
            minY = -10;
            maxY = 10;

            if (c == 1)
            {
                Keyframe first = channel.get(0);

                minX = Math.min(0, first.getTick());
                maxX = Math.max(this.duration, first.getTick());
                minY = maxY = first.getValue();
            }
        }

        if (Math.abs(maxY - minY) < 0.01F)
        {
            /* Centerize */
            this.scaleY.setShift(minY);
        }
        else
        {
            /* Spread apart vertically */
            this.scaleY.viewOffset(minY, maxY, this.area.h, 20);
        }

        /* Spread apart horizontally */
        this.scaleX.viewOffset(minX, maxX, this.area.w, 20);
    }

    @Override
    public Keyframe getCurrent()
    {
        return this.sheet.getKeyframe();
    }

    @Override
    public List<UISheet> getSheets()
    {
        return Arrays.asList(this.sheet);
    }

    @Override
    public UISheet getSheet(int mouseY)
    {
        return this.sheet;
    }

    @Override
    public void selectAll()
    {
        this.sheet.selectAll();
        this.which = Selection.KEYFRAME;
        this.setKeyframe(this.getCurrent());
    }

    @Override
    public int getSelectedCount()
    {
        return this.sheet.getSelectedCount();
    }

    @Override
    public void clearSelection()
    {
        this.which = Selection.NOT_SELECTED;
        this.sheet.clearSelection();
    }

    @Override
    public void addCurrent(int mouseX, int mouseY)
    {
        long tick = Math.round(this.fromGraphX(mouseX));
        double value = this.fromGraphY(mouseY);

        KeyframeEasing easing = KeyframeEasing.IN;
        KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;
        Keyframe frame = this.getCurrent();
        long oldTick = tick;

        if (frame != null)
        {
            easing = frame.getEasing();
            interp = frame.getInterpolation();
            oldTick = frame.getTick();
        }

        this.sheet.selected.clear();
        this.sheet.selected.add(this.sheet.channel.insert(tick, value));

        if (oldTick != tick)
        {
            frame = this.getCurrent();
            frame.setEasing(easing);
            frame.setInterpolation(interp);
        }
    }

    @Override
    public void removeCurrent()
    {
        Keyframe frame = this.getCurrent();

        if (frame == null)
        {
            return;
        }

        this.sheet.channel.remove(this.sheet.selected.get(0));
        this.sheet.clearSelection();

        this.which = Selection.NOT_SELECTED;
    }

    @Override
    public void removeSelectedKeyframes()
    {
        this.sheet.removeSelectedKeyframes();
        this.setKeyframe(null);
        this.which = Selection.NOT_SELECTED;
    }

    /**
     * Make current keyframe by given duration
     */
    public void selectByDuration(long duration)
    {
        if (this.sheet.channel == null)
        {
            return;
        }

        int i = 0;
        this.sheet.selected.clear();

        for (Keyframe frame : this.sheet.channel.getKeyframes())
        {
            if (frame.getTick() >= duration)
            {
                this.sheet.selected.add(i);

                break;
            }

            i++;
        }

        this.setKeyframe(this.getCurrent());
    }

    /* Mouse input handling */

    @Override
    protected void duplicateKeyframe(UIContext context, int mouseX, int mouseY)
    {
        this.sheet.duplicate((long) this.fromGraphX(mouseX));
        this.setKeyframe(this.getCurrent());
    }

    @Override
    protected boolean pickKeyframe(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        int index = 0;
        int count = this.sheet.channel.getKeyframes().size();
        Keyframe prev = null;

        for (Keyframe frame : this.sheet.channel.getKeyframes())
        {
            boolean left = prev != null && prev.getInterpolation() == KeyframeInterpolation.BEZIER && this.isInside(frame.getTick() - frame.getLx(), frame.getValue() + frame.getLy(), mouseX, mouseY);
            boolean right = frame.getInterpolation() == KeyframeInterpolation.BEZIER && this.isInside(frame.getTick() + frame.getRx(), frame.getValue() + frame.getRy(), mouseX, mouseY) && index != count - 1;
            boolean point = this.isInside(frame.getTick(), frame.getValue(), mouseX, mouseY);

            if (left || right || point)
            {
                int key = this.sheet.selected.indexOf(index);

                if (!shift && key == -1)
                {
                    this.clearSelection();
                }

                Selection which = left ? Selection.LEFT_HANDLE : (right ? Selection.RIGHT_HANDLE : Selection.KEYFRAME);

                if (!shift || which == this.which)
                {
                    this.which = which;

                    if (shift && this.isMultipleSelected() && key != -1)
                    {
                        this.sheet.selected.remove(key);
                        frame = this.getCurrent();
                    }
                    else if (key == -1)
                    {
                        this.sheet.selected.add(index);
                        frame = this.isMultipleSelected() ? this.getCurrent() : frame;
                    }
                    else
                    {
                        frame = this.getCurrent();
                    }

                    this.setKeyframe(frame);
                }

                if (frame != null)
                {
                    this.lastT = left ? frame.getTick() - frame.getLx() : (right ? frame.getTick() + frame.getRx() : frame.getTick());
                    this.lastV = left ? frame.getValue() + frame.getLy() : (right ? frame.getValue() + frame.getRy() : frame.getValue());
                }

                return true;
            }

            prev = frame;
            index++;
        }

        return false;
    }

    private boolean isInside(double tick, double value, int mouseX, int mouseY)
    {
        int x = this.toGraphX(tick);
        int y = this.toGraphY(value);
        double d = Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2);

        return d < 16;
    }

    @Override
    protected void setupScrolling(UIContext context, int mouseX, int mouseY)
    {
        super.setupScrolling(context, mouseX, mouseY);

        this.lastV = this.scaleY.getShift();
    }

    @Override
    protected void zoom(UIContext context, int scroll)
    {
        boolean x = Window.isShiftPressed();
        boolean y = Window.isCtrlPressed();
        boolean none = !x && !y;

        /* Scaling X */
        if (x && !y || none)
        {
            this.scaleX.zoomAnchor(Scale.getAnchorX(context, this.area), Math.copySign(this.scaleX.getZoomFactor(), scroll), MIN_ZOOM, MAX_ZOOM);
        }

        /* Scaling Y */
        if (y && !x || none)
        {
            this.scaleY.zoomAnchor(Scale.getAnchorY(context, this.area), Math.copySign(this.scaleY.getZoomFactor(), scroll), MIN_ZOOM, MAX_ZOOM);
        }
    }

    @Override
    protected void resetMouseReleased(UIContext context)
    {
        if (this.isGrabbing())
        {
            /* Multi select */
            Area area = new Area();
            KeyframeChannel channel = this.sheet.channel;

            area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY, 3);

            for (int i = 0, c = channel.getKeyframes().size(); i < c; i ++)
            {
                Keyframe keyframe = channel.get(i);

                if (area.isInside(this.toGraphX(keyframe.getTick()), this.toGraphY(keyframe.getValue())) && !this.sheet.selected.contains(i))
                {
                    this.sheet.selected.add(i);
                }
            }

            if (!this.sheet.selected.isEmpty())
            {
                this.which = Selection.KEYFRAME;
                this.setKeyframe(this.getCurrent());
            }
        }

        super.resetMouseReleased(context);
    }

    /* Rendering */

    @Override
    protected void renderGrid(UIContext context)
    {
        super.renderGrid(context);

        /* Draw vertical grid */
        int ty = (int) this.fromGraphY(this.area.ey());
        int by = (int) this.fromGraphY(this.area.y - 12);

        int min = Math.min(ty, by) - 1;
        int max = Math.max(ty, by) + 1;
        int mult = this.scaleY.getMult();

        min -= min % mult + mult;
        max -= max % mult - mult;

        for (int j = 0, c = (max - min) / mult; j < c; j++)
        {
            int y = this.toGraphY(min + j * mult);

            if (y > this.area.ey())
            {
                continue;
            }

            context.batcher.box(this.area.x, y, this.area.ex(), y + 1, Colors.setA(Colors.WHITE, 0.25F));
            context.batcher.text(String.valueOf(min + j * mult), this.area.x + 4, y + 4);
        }
    }

    /**
     * Render the graph
     */
    @Override
    protected void renderGraph(UIContext context, int mouseX, int mouseY)
    {
        if (this.sheet.channel == null || this.sheet.channel.isEmpty())
        {
            return;
        }

        KeyframeChannel channel = this.sheet.channel;
        LineBuilder lines = new LineBuilder(0.75F);
        Line main = new Line();

        /* Colorize the graph for given channel */
        COLOR.set(this.sheet.color, false);
        float r = COLOR.r;
        float g = COLOR.g;
        float b = COLOR.b;

        /* Draw the graph */
        int index = 0;
        int count = channel.getKeyframes().size();
        Keyframe prev = null;

        for (Keyframe frame : channel.getKeyframes())
        {
            if (prev != null)
            {
                int px = this.toGraphX(prev.getTick());
                int fx = this.toGraphX(frame.getTick());

                /* Main line */
                if (prev.getInterpolation() == KeyframeInterpolation.LINEAR)
                {
                    main.add(px, this.toGraphY(prev.getValue()))
                        .add(fx, this.toGraphY(frame.getValue()));
                }
                else
                {
                    float seg = 10;

                    if (prev.getInterpolation() == KeyframeInterpolation.BOUNCE || prev.getInterpolation() == KeyframeInterpolation.ELASTIC)
                    {
                        seg = 30;
                    }

                    for (int i = 0; i < seg; i++)
                    {
                        main.add(px + (fx - px) * (i / seg), this.toGraphY(prev.interpolate(frame, i / seg)))
                            .add(px + (fx - px) * ((i + 1) / seg), this.toGraphY(prev.interpolate(frame, (i + 1) / seg)));
                    }
                }

                if (prev.getInterpolation() == KeyframeInterpolation.BEZIER)
                {
                    /* Left bezier handle */
                    lines.push()
                        .add(this.toGraphX(frame.getTick() - frame.getLx()), this.toGraphY(frame.getValue() + frame.getLy()))
                        .add(this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()));
                }
            }
            else
            {
                /* Left edge line */
                main.add(0, this.toGraphY(frame.getValue()))
                    .add(this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()));
            }

            if (frame.getInterpolation() == KeyframeInterpolation.BEZIER && index != count - 1)
            {
                /* Right bezier handle */
                lines.push()
                    .add(this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()))
                    .add(this.toGraphX(frame.getTick() + frame.getRx()), this.toGraphY(frame.getValue() + frame.getRy()));
            }

            prev = frame;
            index++;
        }

        /* Right edge line */
        main.add(this.toGraphX(prev.getTick()), this.toGraphY(prev.getValue()))
            .add(this.area.ex(), this.toGraphY(prev.getValue()));

        lines.push(main).render(context.batcher, SolidColorLineRenderer.get(r, g, b, 0.65F));

        /* Draw points */
        index = 0;
        prev = null;

        for (Keyframe frame : channel.getKeyframes())
        {
            this.renderRect(context, this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()), 3, Colors.WHITE);

            if (frame.getInterpolation() == KeyframeInterpolation.BEZIER && index != count - 1)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), this.toGraphY(frame.getValue() + frame.getRy()), 3, Colors.WHITE);
            }

            if (prev != null && prev.getInterpolation() == KeyframeInterpolation.BEZIER)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() - frame.getLx()), this.toGraphY(frame.getValue() + frame.getLy()), 3, Colors.WHITE);
            }

            prev = frame;
            index++;
        }

        index = 0;
        prev = null;

        for (Keyframe frame : channel.getKeyframes())
        {
            boolean has = this.sheet.selected.contains(index);

            this.renderRect(context, this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()), 2, has && this.which == Selection.KEYFRAME ? Colors.ACTIVE : 0);

            if (frame.getInterpolation() == KeyframeInterpolation.BEZIER && index != count - 1)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), this.toGraphY(frame.getValue() + frame.getRy()), 2, has && this.which == Selection.RIGHT_HANDLE ? Colors.ACTIVE : 0);
            }

            if (prev != null && prev.getInterpolation() == KeyframeInterpolation.BEZIER)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() - frame.getLx()), this.toGraphY(frame.getValue() + frame.getLy()), 2, has && this.which == Selection.LEFT_HANDLE ? Colors.ACTIVE : 0);
            }

            prev = frame;
            index++;
        }
    }

    /* Handling dragging */

    @Override
    protected void scrolling(int mouseX, int mouseY)
    {
        super.scrolling(mouseX, mouseY);

        this.scaleY.setShift((mouseY - this.lastY) / this.scaleY.getZoom() + this.lastV);
    }

    @Override
    protected Keyframe moving(UIContext context, int mouseX, int mouseY)
    {
        Keyframe frame = this.getCurrent();
        double x = this.fromGraphX(mouseX);
        double y = this.fromGraphY(mouseY);

        if (this.which == Selection.NOT_SELECTED)
        {
            this.moveNoKeyframe(context, frame, x, y);
        }
        else
        {
            if (this.isMultipleSelected())
            {
                int dx = mouseX - this.lastX;
                int dy = mouseY - this.lastY;

                int xx = this.toGraphX(this.lastT);
                int yy = this.toGraphY(this.lastV);

                x = this.fromGraphX(xx + dx);
                y = this.fromGraphY(yy + dy);
            }

            if (Window.isShiftPressed()) x = this.lastT;
            if (Window.isCtrlPressed()) y = this.lastV;

            if (this.which == Selection.LEFT_HANDLE)
            {
                x = -(x - frame.getTick());
                y = y - frame.getValue();
            }
            else if (this.which == Selection.RIGHT_HANDLE)
            {
                x = x - frame.getTick();
                y = y - frame.getValue();
            }

            boolean altPressed = Window.isAltPressed();

            this.setTick(Math.round(x), !altPressed);
            this.setValue(y, !altPressed);
        }

        return frame;
    }
}