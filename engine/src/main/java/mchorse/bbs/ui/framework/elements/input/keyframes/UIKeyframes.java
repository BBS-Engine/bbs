package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.graphics.line.Line;
import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.Scale;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIKeyframes extends UIBaseKeyframes<Keyframe>
{
    public static final int TOP_MARGIN = 15;

    public Selection which = Selection.NOT_SELECTED;
    public List<UISheet> sheets = new ArrayList<>();

    private Scale scaleY;

    private List<UISheet> currentSheet = new ArrayList<>();
    private UISheet current;
    private Area editArea = new Area();

    public UIKeyframes(Consumer<Keyframe> callback)
    {
        super(callback);

        this.scaleY = new Scale(this.area, ScrollDirection.VERTICAL);
        this.scaleY.inverse().anchor(0.5F);
    }

    public Scale getScaleY()
    {
        return this.scaleY;
    }

    public void editSheet(UISheet sheet)
    {
        this.clearSelection();

        this.current = sheet;

        this.currentSheet.clear();
        this.currentSheet.add(sheet);

        this.resetViewY();
    }

    public void resetViewY()
    {
        if (this.current == null)
        {
            return;
        }

        this.scaleY.set(0, 2);

        KeyframeChannel channel = this.current.channel;
        int c = channel.getKeyframes().size();

        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        if (c > 1)
        {
            for (Keyframe frame : channel.getKeyframes())
            {
                minY = Math.min(minY, frame.getValue());
                maxY = Math.max(maxY, frame.getValue());
            }
        }
        else
        {
            minY = -10;
            maxY = 10;

            if (c == 1)
            {
                Keyframe first = channel.get(0);

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
    }

    /* Implementation of setters */

    public void setTick(double tick, boolean opposite)
    {
        if (this.isMultipleSelected())
        {
            if (this.which == Selection.KEYFRAME)
            {
                tick = (long) tick;
            }

            double dx = tick - this.which.getX(this.getCurrent());

            for (UISheet sheet : this.getSheets())
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

    public void setValue(double value, boolean opposite)
    {
        if (this.isMultipleSelected())
        {
            double dy = value - this.which.getY(this.getCurrent());

            for (UISheet sheet : this.getSheets())
            {
                sheet.setValue(dy, this.which, opposite);
            }
        }
        else
        {
            this.which.setY(this.getCurrent(), value, opposite);
        }
    }

    public void setInterpolation(KeyframeInterpolation interp)
    {
        for (UISheet sheet : this.getSheets())
        {
            sheet.setInterpolation(interp);
        }
    }

    public void setEasing(KeyframeEasing easing)
    {
        for (UISheet sheet : this.getSheets())
        {
            sheet.setEasing(easing);
        }
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
        int c = 0;

        this.scaleX.set(0, 2);

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        /* Find minimum and maximum */
        for (UISheet sheet : this.sheets)
        {
            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                min = Integer.min((int) frame.getTick(), min);
                max = Integer.max((int) frame.getTick(), max);
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

    public Keyframe getCurrent()
    {
        UISheet current = this.getCurrentSheet();

        return current == null ? null : current.getKeyframe();
    }

    public List<UISheet> getSheets()
    {
        return this.current == null ? this.sheets : this.currentSheet;
    }

    public UISheet getSheet(int mouseY)
    {
        if (this.current != null)
        {
            return this.current;
        }

        List<UISheet> sheets = this.getSheets();
        int sheetCount = sheets.size();
        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int i = (mouseY - (this.area.ey() - h * sheetCount)) / h;

        return i < 0 || i >= sheetCount ? null : sheets.get(i);
    }

    @Override
    public void selectAll()
    {
        for (UISheet sheet : this.getSheets())
        {
            sheet.selectAll();
        }

        this.which = Selection.KEYFRAME;
        this.setKeyframe(this.getCurrent());
    }

    public UISheet getCurrentSheet()
    {
        if (this.current != null)
        {
            return this.current;
        }

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

        for (UISheet sheet : this.getSheets())
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
        UISheet sheet = this.getSheet(mouseY);

        if (sheet == null)
        {
            return;
        }

        long tick = Math.round(this.fromGraphX(mouseX));
        double value = this.current == null ? sheet.channel.interpolate(tick) : this.fromGraphY(mouseY);

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

        sheet.selected.clear();
        sheet.selected.add(sheet.channel.insert(tick, value));

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

        UISheet current = this.getCurrentSheet();

        current.channel.remove(current.selected.get(0));
        current.selected.clear();

        this.which = Selection.NOT_SELECTED;
    }

    @Override
    public void removeSelectedKeyframes()
    {
        for (UISheet sheet : this.getSheets())
        {
            sheet.removeSelectedKeyframes();
        }

        this.setKeyframe(null);

        this.which = Selection.NOT_SELECTED;
    }

    @Override
    public boolean isSelected()
    {
        return this.which == Selection.KEYFRAME;
    }

    @Override
    public void doubleClick(int mouseX, int mouseY)
    {
        if (this.which == Selection.NOT_SELECTED)
        {
            this.addCurrent(mouseX, mouseY);
        }
        else if (this.which == Selection.KEYFRAME && !this.isMultipleSelected())
        {
            this.removeCurrent();
        }
    }

    /* Mouse input handling */

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        List<UISheet> sheets = this.getSheets();
        int sheetCount = sheets.size();

        if (this.area.isInside(context) && context.mouseButton == 0 && sheetCount > 0)
        {
            int h = (this.area.h - TOP_MARGIN) / sheetCount;
            int y = this.area.ey() - h * sheetCount;

            for (UISheet sheet : sheets)
            {
                this.editArea.set(this.area.x, y, 20, h);

                if (this.editArea.isInside(context))
                {
                    this.editSheet(this.current == null ? sheet : null);

                    return true;
                }

                y += h;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected void duplicateKeyframe(UIContext context, int mouseX, int mouseY)
    {
        long offset = (long) this.fromGraphX(mouseX);

        for (UISheet sheet : this.getSheets())
        {
            sheet.duplicate(offset);
        }

        this.setKeyframe(this.getCurrent());
    }

    @Override
    protected boolean pickKeyframe(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        return this.current == null
            ? this.pickKeyframeDopeSheet(context, mouseX, mouseY, shift)
            : this.pickKeyframeGraph(context, mouseX, mouseY, shift);
    }

    private boolean pickKeyframeDopeSheet(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        List<UISheet> sheets = this.getSheets();
        int sheetCount = sheets.size();
        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int y = this.area.ey() - h * sheetCount;
        boolean alt = Window.isAltPressed();
        boolean finished = false;
        boolean isMultiSelect = this.isMultipleSelected();

        for (UISheet sheet : sheets)
        {
            int index = 0;
            int count = sheet.channel.getKeyframes().size();
            Keyframe prev = null;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                boolean left = prev != null && prev.getInterpolation().isBezier() && this.isInside(this.toGraphX(frame.getTick() - frame.getLx()), y + h / 2, mouseX, mouseY);
                boolean right = frame.getInterpolation().isBezier() && this.isInside(this.toGraphX(frame.getTick() + frame.getRx()), y + h / 2, mouseX, mouseY) && index != count - 1;
                boolean point = this.isInside(this.toGraphX(frame.getTick()), alt ? mouseY : y + h / 2, mouseX, mouseY);

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
                        this.lastT = left ? frame.getTick() - frame.getLx() : (right ? frame.getTick() + frame.getRx() : frame.getTick());
                        this.lastV = left ? frame.getValue() + frame.getLy() : (right ? frame.getValue() + frame.getRy() : frame.getValue());
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

    private boolean pickKeyframeGraph(UIContext context, int mouseX, int mouseY, boolean shift)
    {
        UISheet sheet = this.current;
        int index = 0;
        int count = sheet.channel.getKeyframes().size();
        Keyframe prev = null;

        for (Keyframe frame : sheet.channel.getKeyframes())
        {
            boolean left = prev != null && prev.getInterpolation().isBezier() && this.isInsideTickValue(frame.getTick() - frame.getLx(), frame.getValue() + frame.getLy(), mouseX, mouseY);
            boolean right = frame.getInterpolation().isBezier() && this.isInsideTickValue(frame.getTick() + frame.getRx(), frame.getValue() + frame.getRy(), mouseX, mouseY) && index != count - 1;
            boolean point = this.isInsideTickValue(frame.getTick(), frame.getValue(), mouseX, mouseY);

            if (left || right || point)
            {
                int key = sheet.selected.indexOf(index);

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
                        sheet.selected.remove(key);
                        frame = this.getCurrent();
                    }
                    else if (key == -1)
                    {
                        sheet.selected.add(index);
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

    private boolean isInside(double x, double y, int mouseX, int mouseY)
    {
        double d = Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2);

        return d < 16;
    }

    private boolean isInsideTickValue(double tick, double value, int mouseX, int mouseY)
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
        if (this.current == null)
        {
            super.zoom(context, scroll);

            return;
        }

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
        if (this.which == Selection.KEYFRAME)
        {
            if (this.sliding)
            {
                /* Resort after dragging the tick thing */
                for (UISheet sheet : this.getSheets())
                {
                    if (!sheet.selected.isEmpty())
                    {
                        sheet.sort();
                    }
                }

                this.sliding = false;
            }
        }

        if (this.isGrabbing())
        {
            if (this.current == null)
            {
                /* Multi select */
                Area area = new Area();

                area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY, 3);

                List<UISheet> sheets = this.getSheets();
                int count = sheets.size();
                int h = (this.area.h - TOP_MARGIN) / count;
                int y = this.area.ey() - h * count;
                int c = 0;

                for (UISheet sheet : sheets)
                {
                    int i = 0;

                    for (Keyframe keyframe : sheet.channel.getKeyframes())
                    {
                        if (area.isInside(this.toGraphX(keyframe.getTick()), y + h / 2) && !sheet.selected.contains(i))
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
            else
            {
                /* Multi select */
                UISheet sheet = this.current;
                Area area = new Area();
                KeyframeChannel channel = sheet.channel;

                area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY, 3);

                for (int i = 0, c = channel.getKeyframes().size(); i < c; i ++)
                {
                    Keyframe keyframe = channel.get(i);

                    if (area.isInside(this.toGraphX(keyframe.getTick()), this.toGraphY(keyframe.getValue())) && !sheet.selected.contains(i))
                    {
                        sheet.selected.add(i);
                    }
                }

                if (!sheet.selected.isEmpty())
                {
                    this.which = Selection.KEYFRAME;
                    this.setKeyframe(this.getCurrent());
                }
            }
        }

        super.resetMouseReleased(context);
    }

    /* Rendering */

    @Override
    protected void renderGrid(UIContext context)
    {
        super.renderGrid(context);

        if (this.current == null)
        {
            return;
        }

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

    @Override
    protected void renderGraph(UIContext context)
    {
        if (this.current == null)
        {
            this.renderDopeSheetGraph(context);
        }
        else
        {
            this.renderGraphGraph(context, this.current);
        }
    }

    private void renderDopeSheetGraph(UIContext context)
    {
        /* Draw dope sheet */
        List<UISheet> sheets = this.getSheets();
        int sheetCount = sheets.size();

        if (sheetCount == 0)
        {
            return;
        }

        int h = (this.area.h - TOP_MARGIN) / sheetCount;
        int y = this.area.ey() - h * sheetCount;

        for (UISheet sheet : sheets)
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
                this.renderRect(context, this.toGraphX(frame.getTick()), y + h / 2, 3, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);

                if (frame.getInterpolation().isBezier() && index != count - 1)
                {
                    this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), y + h / 2, 2, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);
                }

                if (prev != null && prev.getInterpolation().isBezier())
                {
                    this.renderRect(context, this.toGraphX(frame.getTick() - frame.getLx()), y + h / 2, 2, sheet.hasSelected(index) ? Colors.WHITE : sheet.color);
                }

                prev = frame;
                index++;
            }

            index = 0;
            prev = null;

            for (Keyframe frame : sheet.channel.getKeyframes())
            {
                this.renderRect(context, this.toGraphX(frame.getTick()), y + h / 2, 2, this.which == Selection.KEYFRAME && sheet.hasSelected(index) ? Colors.ACTIVE : 0);

                if (frame.getInterpolation().isBezier() && index != count - 1)
                {
                    this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), y + h / 2, 1, this.which == Selection.RIGHT_HANDLE && sheet.hasSelected(index) ? Colors.ACTIVE : 0);
                }

                if (prev != null && prev.getInterpolation().isBezier())
                {
                    this.renderRect(context, this.toGraphX(frame.getTick() - frame.getLx()), y + h / 2, 1, this.which == Selection.LEFT_HANDLE && sheet.hasSelected(index) ? Colors.ACTIVE : 0);
                }

                prev = frame;
                index++;
            }

            int lw = context.font.getWidth(sheet.title.get()) + 10;
            context.batcher.gradientHBox(this.area.ex() - lw - 10, y, this.area.ex(), y + h, sheet.color, Colors.A75 | sheet.color);
            context.batcher.textShadow(sheet.title.get(), this.area.ex() - lw + 5, y + (h - context.font.getHeight()) / 2);

            this.editArea.set(this.area.x, y, 20, h);

            if (this.editArea.isInside(context))
            {
                context.batcher.icon(Icons.EDIT, Colors.WHITE, this.area.x + 4, y + h / 2, 0F, 0.5F);
            }

            y += h;
        }
    }

    private void renderGraphGraph(UIContext context, UISheet sheet)
    {
        if (sheet == null || sheet.channel == null || sheet.channel.isEmpty())
        {
            return;
        }

        KeyframeChannel channel = sheet.channel;
        LineBuilder lines = new LineBuilder(0.75F);
        Line main = new Line();

        /* Colorize the graph for given channel */
        COLOR.set(sheet.color, false);
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

                if (prev.getInterpolation().isBezier())
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

            if (frame.getInterpolation().isBezier() && index != count - 1)
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

            if (frame.getInterpolation().isBezier() && index != count - 1)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), this.toGraphY(frame.getValue() + frame.getRy()), 3, Colors.WHITE);
            }

            if (prev != null && prev.getInterpolation().isBezier())
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
            boolean has = sheet.selected.contains(index);

            this.renderRect(context, this.toGraphX(frame.getTick()), this.toGraphY(frame.getValue()), 2, has && this.which == Selection.KEYFRAME ? Colors.ACTIVE : 0);

            if (frame.getInterpolation().isBezier() && index != count - 1)
            {
                this.renderRect(context, this.toGraphX(frame.getTick() + frame.getRx()), this.toGraphY(frame.getValue() + frame.getRy()), 2, has && this.which == Selection.RIGHT_HANDLE ? Colors.ACTIVE : 0);
            }

            if (prev != null && prev.getInterpolation().isBezier())
            {
                this.renderRect(context, this.toGraphX(frame.getTick() - frame.getLx()), this.toGraphY(frame.getValue() + frame.getLy()), 2, has && this.which == Selection.LEFT_HANDLE ? Colors.ACTIVE : 0);
            }

            prev = frame;
            index++;
        }

        int y = this.area.y + TOP_MARGIN;
        int h = this.area.ey() - y;

        this.editArea.set(this.area.x, y, 20, h);

        if (this.editArea.isInside(context))
        {
            context.batcher.icon(Icons.CLOSE, Colors.WHITE, this.area.x + 4, y + h / 2, 0F, 0.5F);
        }
    }

    /* Handling dragging */

    @Override
    protected void scrolling(int mouseX, int mouseY)
    {
        super.scrolling(mouseX, mouseY);

        if (this.current != null)
        {
            this.scaleY.setShift((mouseY - this.lastY) / this.scaleY.getZoom() + this.lastV);
        }
    }

    @Override
    protected Keyframe moving(UIContext context, int mouseX, int mouseY)
    {
        return this.current == null
            ? this.movingDopeSheet(context, mouseX, mouseY)
            : this.movingGraph(context, mouseX, mouseY);
    }

    private Keyframe movingDopeSheet(UIContext context, int mouseX, int mouseY)
    {
        Keyframe frame = this.getCurrent();
        double x = this.fromGraphX(mouseX);

        if (this.which == Selection.NOT_SELECTED)
        {
            this.moveNoKeyframe(context, x, 0);
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
                x = (int) -(x - frame.getTick());
            }
            else if (this.which == Selection.RIGHT_HANDLE)
            {
                x = (int) x - frame.getTick();
            }

            this.setTick(x, !Window.isAltPressed());
        }

        return frame;
    }

    private Keyframe movingGraph(UIContext context, int mouseX, int mouseY)
    {
        Keyframe frame = this.getCurrent();
        double x = this.fromGraphX(mouseX);
        double y = this.fromGraphY(mouseY);

        if (this.which == Selection.NOT_SELECTED)
        {
            this.moveNoKeyframe(context, x, y);
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