package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Dope sheet editor
 *
 * This GUI element is responsible for editing mutliple keyframe channels
 * at the same time (however without editing the Y values of these individual
 * channels)
 */
public class UIDopeSheet extends UIKeyframes
{
    public static final int TOP_MARGIN = 15;

    public List<UISheet> sheets = new ArrayList<UISheet>();

    public UIDopeSheet(Consumer<Keyframe> callback)
    {
        super(callback);
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

            double dx = tick - this.which.getX(this.getCurrent());

            for (UISheet sheet : this.sheets)
            {
                sheet.setTick(dx, this.which, opposite);
            }
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
            double dy = value - this.which.getY(this.getCurrent());

            for (UISheet sheet : this.sheets)
            {
                sheet.setValue(dy, this.which, opposite);
            }
        }
        else
        {
            this.which.setY(this.getCurrent(), value, opposite);
        }
    }

    @Override
    public void setInterpolation(KeyframeInterpolation interp)
    {
        for (UISheet sheet : this.sheets)
        {
            sheet.setInterpolation(interp);
        }
    }

    @Override
    public void setEasing(KeyframeEasing easing)
    {
        for (UISheet sheet : this.sheets)
        {
            sheet.setEasing(easing);
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
        for (UISheet sheet : this.sheets)
        {
            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                min = Integer.min((int) frame.tick, min);
                max = Integer.max((int) frame.tick, max);
            }

            c = Math.max(c, sheet.channel.getKeyframes().size());
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
    public Keyframe getCurrent()
    {
        UISheet current = this.getCurrentSheet();

        return current == null ? null : current.getKeyframe();
    }

    @Override
    public List<UISheet> getSheets()
    {
        return this.sheets;
    }

    @Override
    public UISheet getSheet(int mouseY)
    {
        int sheetCount = this.sheets.size();
        int h = (this.area.h - TOP_MARGIN) / sheetCount;

        for (int i = 0; i < sheetCount; i++)
        {
            UISheet sheet = this.sheets.get(i);
            int y = this.area.y + h * i + TOP_MARGIN;

            if (mouseY >= y && mouseY < y + h)
            {
                return sheet;
            }
        }

        return null;
    }

    @Override
    public void selectAll()
    {
        for (UISheet sheet : this.sheets)
        {
            sheet.selectAll();
        }

        this.which = Selection.KEYFRAME;
        this.setKeyframe(this.getCurrent());
    }

    public UISheet getCurrentSheet()
    {
        for (UISheet sheet : this.sheets)
        {
            if (!sheet.selected.isEmpty())
            {
                return sheet;
            }
        }

        return null;
    }

    @Override
    public int getSelectedCount()
    {
        int i = 0;

        for (UISheet sheet : this.sheets)
        {
            i += sheet.getSelectedCount();
        }

        return i;
    }

    @Override
    public void clearSelection()
    {
        this.which = Selection.NOT_SELECTED;

        for (UISheet sheet : this.sheets)
        {
            sheet.clearSelection();
        }
    }

    @Override
    public void addCurrent(int mouseX, int mouseY)
    {
        int sheetCount = this.sheets.size();
        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int i = (mouseY - (this.area.ey() - h * sheetCount)) / h;

        if (i < 0 || i >= sheetCount)
        {
            return;
        }

        UISheet sheet = this.sheets.get(i);
        KeyframeEasing easing = KeyframeEasing.IN;
        KeyframeInterpolation interp = KeyframeInterpolation.LINEAR;
        Keyframe frame = this.getCurrent();
        long tick = (long) this.fromGraphX(mouseX);
        long oldTick = tick;

        if (frame != null)
        {
            easing = frame.easing;
            interp = frame.interp;
            oldTick = frame.tick;
        }

        sheet.selected.clear();
        sheet.selected.add(sheet.channel.insert(tick, sheet.channel.interpolate(tick)));
        frame = this.getCurrent();

        if (oldTick != tick)
        {
            frame.setEasing(easing);
            frame.setInterpolation(interp);
        }

        this.addedDoubleClick(frame, tick, mouseX, mouseY);
    }

    protected void addedDoubleClick(Keyframe frame, long tick, int mouseX, int mouseY)
    {}

    @Override
    public void removeCurrent()
    {
        Keyframe frame = this.getCurrent();

        if (frame == null)
        {
            return;
        }

        UISheet current = this.getCurrentSheet();

        current.channel.remove(current.selected.get(0));
        current.selected.clear();
        this.which = Selection.NOT_SELECTED;
    }

    @Override
    public void removeSelectedKeyframes()
    {
        for (UISheet sheet : this.sheets)
        {
            sheet.removeSelectedKeyframes();
        }

        this.setKeyframe(null);
        this.which = Selection.NOT_SELECTED;
    }

    /* Mouse input handling */

    @Override
    protected void duplicateKeyframe(UIContext context, int mouseX, int mouseY)
    {
        long offset = (long) this.fromGraphX(mouseX);

        for (UISheet sheet : this.sheets)
        {
            sheet.duplicate(offset);
        }

        this.setKeyframe(this.getCurrent());
    }

    @Override
    protected boolean pickKeyframe(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        int sheetCount = this.sheets.size();
        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int y = this.area.ey() - h * sheetCount;
        boolean alt = Window.isAltPressed();
        boolean finished = false;
        boolean isMultiSelect = this.isMultipleSelected();

        for (UISheet sheet : this.sheets)
        {
            int index = 0;
            int count = sheet.channel.getKeyframes().size();
            Keyframe prev = null;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                boolean left = sheet.handles && prev != null && prev.interp == KeyframeInterpolation.BEZIER && this.isInside(this.toGraphX(frame.tick - frame.lx), y + h / 2, mouseX, mouseY);
                boolean right = sheet.handles && frame.interp == KeyframeInterpolation.BEZIER && this.isInside(this.toGraphX(frame.tick + frame.rx), y + h / 2, mouseX, mouseY) && index != count - 1;
                boolean point = this.isInside(this.toGraphX(frame.tick), alt ? mouseY : y + h / 2, mouseX, mouseY);

                if (left || right || point)
                {
                    int key = sheet.selected.indexOf(index);

                    if (!shift && key == -1 && !alt)
                    {
                        this.clearSelection();
                    }

                    Selection which = left ? Selection.LEFT_HANDLE : (right ? Selection.RIGHT_HANDLE : Selection.KEYFRAME);

                    if (!shift || which == this.which)
                    {
                        this.which = which;

                        if (shift && isMultiSelect && key != -1)
                        {
                            sheet.selected.remove(key);
                            frame = this.getCurrent();
                        }
                        else if (key == -1)
                        {
                            sheet.selected.add(index);
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
                        this.lastT = left ? frame.tick - frame.lx : (right ? frame.tick + frame.rx : frame.tick);
                        this.lastV = left ? frame.value + frame.ly : (right ? frame.value + frame.ry : frame.value);
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

                prev = frame;
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
    protected void postSlideSort(UIContext context)
    {
        /* Resort after dragging the tick thing */
        for (UISheet sheet : this.sheets)
        {
            if (!sheet.selected.isEmpty())
            {
                sheet.sort();
            }
        }

        this.sliding = false;
    }

    @Override
    protected void resetMouseReleased(UIContext context)
    {
        if (this.isGrabbing())
        {
            /* Multi select */
            Area area = new Area();

            area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY, 3);

            int count = this.sheets.size();
            int h = (this.area.h - TOP_MARGIN) / count;
            int y = this.area.ey() - h * count;
            int c = 0;

            for (UISheet sheet : this.sheets)
            {
                int i = 0;

                for (Keyframe keyframe : sheet.channel.getKeyframes())
                {
                    if (area.isInside(this.toGraphX(keyframe.tick), y + h / 2) && !sheet.selected.contains(i))
                    {
                        sheet.selected.add(i);
                        c++;
                    }

                    i++;
                }

                y += h;
            }

            if (c > 0)
            {
                this.which = Selection.KEYFRAME;
                this.setKeyframe(this.getCurrent());
            }
        }

        super.resetMouseReleased(context);
    }

    /* Rendering */

    @Override
    protected void renderGraph(UIContext context, int mouseX, int mouseY)
    {
        /* Draw dope sheet */
        int sheetCount = this.sheets.size();

        if (sheetCount == 0)
        {
            return;
        }

        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int y = this.area.ey() - h * sheetCount;

        for (UISheet sheet : this.sheets)
        {
            COLOR.set(sheet.color, false);

            LineBuilder line = new LineBuilder(0.75F);

            line.add(this.area.x, y + h / 2);
            line.add(this.area.ex(), y + h / 2);
            line.render(context.batcher, SolidColorLineRenderer.get(COLOR.r, COLOR.g, COLOR.b, 0.65F));

            /* Draw points */
            int index = 0;
            int count = sheet.channel.getKeyframes().size();
            Keyframe prev = null;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                this.renderRect(context, this.toGraphX(frame.tick), y + h / 2, 3, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);

                if (frame.interp == KeyframeInterpolation.BEZIER && sheet.handles && index != count - 1)
                {
                    this.renderRect(context, this.toGraphX(frame.tick + frame.rx), y + h / 2, 2, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);
                }

                if (prev != null && prev.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.renderRect(context, this.toGraphX(frame.tick - frame.lx), y + h / 2, 2, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);
                }

                prev = frame;
                index++;
            }

            index = 0;
            prev = null;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                this.renderRect(context, this.toGraphX(frame.tick), y + h / 2, 2, this.which == Selection.KEYFRAME && sheet.hasSelected(index) ? Colors.ACTIVE : 0);

                if (frame.interp == KeyframeInterpolation.BEZIER && sheet.handles && index != count - 1)
                {
                    this.renderRect(context, this.toGraphX(frame.tick + frame.rx), y + h / 2, 1, this.which == Selection.RIGHT_HANDLE && sheet.hasSelected(index) ? Colors.ACTIVE : 0);
                }

                if (prev != null && prev.interp == KeyframeInterpolation.BEZIER && sheet.handles)
                {
                    this.renderRect(context, this.toGraphX(frame.tick - frame.lx), y + h / 2, 1, this.which == Selection.LEFT_HANDLE && sheet.hasSelected(index) ? Colors.ACTIVE : 0);
                }

                prev = frame;
                index++;
            }

            context.batcher.render();

            int lw = context.font.getWidth(sheet.title.get()) + 10;
            context.batcher.gradientHBox(this.area.ex() - lw - 10, y, this.area.ex(), y + h, sheet.color, Colors.A75 | sheet.color);
            context.batcher.textShadow(sheet.title.get(), this.area.ex() - lw + 5, y + (h - context.font.getHeight()) / 2);

            y += h;
        }
    }

    /* Handling dragging */

    @Override
    protected Keyframe moving(UIContext context, int mouseX, int mouseY)
    {
        Keyframe frame = this.getCurrent();
        double x = this.fromGraphX(mouseX);

        if (this.which == Selection.NOT_SELECTED)
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

            if (this.which == Selection.LEFT_HANDLE)
            {
                x = (int) -(x - frame.tick);
            }
            else if (this.which == Selection.RIGHT_HANDLE)
            {
                x = (int) x - frame.tick;
            }

            this.setTick(x, !Window.isAltPressed());
        }

        return frame;
    }
}