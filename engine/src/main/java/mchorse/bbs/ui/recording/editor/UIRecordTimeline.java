package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.actions.FormAction;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.recording.editor.tools.AddTool;
import mchorse.bbs.ui.recording.editor.tools.ApplyTool;
import mchorse.bbs.ui.recording.editor.tools.CameraTool;
import mchorse.bbs.ui.recording.editor.tools.CaptureTool;
import mchorse.bbs.ui.recording.editor.tools.CopyTool;
import mchorse.bbs.ui.recording.editor.tools.CutTool;
import mchorse.bbs.ui.recording.editor.tools.InsertTool;
import mchorse.bbs.ui.recording.editor.tools.LerpTool;
import mchorse.bbs.ui.recording.editor.tools.PasteTool;
import mchorse.bbs.ui.recording.editor.tools.ProcessTool;
import mchorse.bbs.ui.recording.editor.tools.RemoveTool;
import mchorse.bbs.ui.recording.editor.tools.ReverseTool;
import mchorse.bbs.ui.recording.editor.tools.TeleportTool;
import mchorse.bbs.ui.recording.editor.tools.Tool;
import mchorse.bbs.ui.recording.editor.tools.ToolContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.Range;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class UIRecordTimeline extends UIElement
{
    public IRecordEditor editor;
    public Area frames;
    public ScrollArea scroll;
    public ScrollArea vertical;

    public Offset current = new Offset(-1, -1);
    public Offset selection = new Offset(-1, -1);
    public boolean frame;

    public boolean lastDragging = false;
    public int lastX;
    public int lastY;
    public int lastH;
    public int lastV;

    public boolean dragging;
    public boolean moving;
    public int cursor = -1;

    private int adaptiveMaxIndex;

    private Record record;
    private ToolContext context;
    private Range range = new Range(0, 0);

    private List<Tool> tools = new ArrayList<Tool>();

    public UIRecordTimeline(IRecordEditor editor)
    {
        super();

        int defaultSize = 17;

        this.editor = editor;
        this.context = new ToolContext(this);

        this.frames = new Area();
        this.scroll = new ScrollArea(defaultSize, ScrollDirection.HORIZONTAL);
        this.scroll.scrollSpeed = defaultSize * 4;
        this.vertical = new ScrollArea(20);

        this.tools.add(new AddTool());
        this.tools.add(new ApplyTool());
        this.tools.add(new InsertTool());
        this.tools.add(new CameraTool());
        this.tools.add(new CutTool());
        this.tools.add(new CopyTool());
        this.tools.add(new PasteTool());
        this.tools.add(new ProcessTool());
        this.tools.add(new LerpTool());
        this.tools.add(new ReverseTool());
        this.tools.add(new CaptureTool());
        this.tools.add(new TeleportTool());
        this.tools.add(new RemoveTool());

        this.context((menu) ->
        {
            for (Tool tool : this.tools)
            {
                if (tool.canApply(this.context))
                {
                    tool.addContext(this.context, menu);
                }
            }
        });

        this.keys().register(Keys.RECORD_SELECT_ALL, this::selectAll).category(Tool.CATEGORY_KEY);

        for (Tool tool : this.tools)
        {
            tool.addKey(this.context, this.keys());
        }

        this.keys().register(Keys.RECORD_JUMP_NEXT_ACTION, () -> this.jumpAction(1)).category(Tool.CATEGORY_KEY);
        this.keys().register(Keys.RECORD_JUMP_PREV_ACTION, () -> this.jumpAction(-1)).category(Tool.CATEGORY_KEY);
    }

    private void jumpAction(int direction)
    {
        int size =this.record.frames.size();

        if (size == 0)
        {
            return;
        }

        int tick = this.calculateRange().min + direction;

        while (tick >= 0 && tick < size)
        {
            Frame frame = this.record.getFrame(tick);

            if (frame != null && !frame.actions.isEmpty())
            {
                this.selectAction(tick, 0);
                this.openAction(frame.actions.get(0));
                this.scroll.scrollTo(this.scroll.scrollItemSize * tick - this.scroll.w / 2);

                return;
            }

            tick += direction;
        }
    }

    public void openAction(Action action)
    {
        this.editor.openAction(action);
        this.editor.openFrame(null);
    }

    public void openFrame(Frame frame)
    {
        this.editor.openAction(null);
        this.editor.openFrame(frame);
    }

    public void moveTo(int tick)
    {
        if (tick < 0 || tick >= this.record.frames.size())
        {
            return;
        }

        Action action = this.getAction();
        Frame frame = this.record.getFrame(tick);

        new RemoveTool().apply(this.context);
        frame.actions.add(action);
        this.recalculateVertical();
        this.current.tick = tick;
        this.current.index = frame.actions.indexOf(action);
        this.selection.tick = this.current.tick;
        this.selection.index = this.current.index;
        this.editor.openAction(action);
    }

    public void openCurrentFrame()
    {
        int tick = this.current.tick;
        Frame frame = tick >= 0 && tick < this.record.frames.size() ? this.record.frames.get(tick) : null;

        this.editor.openFrame(frame);
    }

    /* Getters / setters */

    public Action getAction()
    {
        return this.record.getAction(this.current.tick, this.current.index);
    }

    public void setRecord(Record record)
    {
        this.record = record;
        this.context.record = record;
        this.current.set(-1, -1);
        this.selection.set(-1, -1);
        this.frame = false;

        this.update();
    }

    /* Selection methods */

    public boolean hasSelectionRange()
    {
        return !this.current.isEmpty() && !this.selection.isEmpty() && this.current.tick - this.selection.tick != 0;
    }

    public boolean isActionSelected()
    {
        Frame frame = this.record.getFrame(this.current.tick);

        return frame != null && CollectionUtils.inRange(frame.actions, this.current.index);
    }

    public boolean isFrameSelected()
    {
        return this.frame && !this.hasSelectionRange() && !this.current.isEmpty();
    }

    public boolean areFramesSelected()
    {
        return this.frame && this.hasSelectionRange();
    }

    public void deselect()
    {
        this.openFrame(null);
        this.current.set(-1, -1);
        this.selection.set(-1, -1);
    }

    public void selectAll()
    {
        int size = this.record.frames.size() - 1;

        if (size == 0)
        {
            this.select(-1, -1, false);
        }
        else
        {
            this.select(0, size, true);
            this.openFrame(this.record.frames.get(0));
        }
    }

    public Range calculateRange()
    {
        int mn = -1;
        int mx = -1;

        if (this.current.tick >= 0 && this.selection.tick >= 0)
        {
            mn = Math.min(this.current.tick, this.selection.tick);
            mx = Math.max(this.current.tick, this.selection.tick);
        }
        else if (this.current.tick >= 0)
        {
            mn = mx = this.current.tick;
        }

        this.range.set(mn, mx);

        return this.range;
    }

    public void selectAction(int tick, int index)
    {
        this.select(tick, tick, false);

        this.current.index = index;
    }

    public void selectFrame(int tick)
    {
        this.select(tick, tick, true);
    }

    public void selectFrames(int min, int max)
    {
        this.select(min, max, true);
    }

    public void select(int min, int max, boolean frame)
    {
        int a = min;
        int b = max;

        min = Math.min(a, b);
        max = Math.max(a, b);

        this.frame = frame;
        this.current.set(min, -1);
        this.selection.set(max, -1);

        if (min == max)
        {
            this.editor.clickTick(min);
        }
    }

    @Override
    public void resize()
    {
        super.resize();

        this.frames.copy(this.area);
        this.frames.h = 20;
        this.scroll.copy(this.area);
        this.vertical.copy(this.area);
        this.vertical.y += 20;
        this.vertical.h -= 20;
    }

    public void update()
    {
        if (this.record != null)
        {
            int count = this.record.frames.size();

            this.scroll.setSize(count);
            this.scroll.clamp();

            this.recalculateVertical();
        }
    }

    public void recalculateVertical()
    {
        int max = 0;

        if (this.record != null)
        {
            for (Frame frame : this.record.frames)
            {
                max = Math.max(frame.actions.size(), max);
            }

            max += 1;
        }

        this.vertical.setSize(max);
        this.vertical.clamp();
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        this.lastX = context.mouseX;
        this.lastY = context.mouseY;

        if (context.mouseButton == 2 && this.area.isInside(context))
        {
            this.lastDragging = true;
            this.lastH = this.scroll.scroll;
            this.lastV = this.vertical.scroll;

            return true;
        }

        if (this.scroll.mouseClicked(context) || this.vertical.mouseClicked(context))
        {
            return true;
        }

        if (this.area.isInside(context) && !this.moving && context.mouseButton == 0)
        {
            int tick = this.scroll.getIndex(context.mouseX, context.mouseY);
            int index = this.vertical.getIndex(context.mouseX, context.mouseY);

            if (tick >= 0 && tick < this.record.frames.size())
            {
                this.selection.set(-1, -1);

                if (Window.isShiftPressed())
                {
                    this.selection.set(tick, -1);
                }
                else if (index == -1)
                {
                    this.openFrame(this.record.frames.get(tick));

                    this.selectFrame(tick);
                }
                else
                {
                    Action action = this.record.getAction(tick, index);

                    this.openAction(action);
                    this.selectAction(tick, action == null ? -1 : index);

                    if (this.current.index != -1)
                    {
                        this.dragging = true;
                        this.moving = false;
                    }
                }
            }
            else
            {
                this.current.set(-1, -1);
                this.frame = false;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        boolean shift = Window.isShiftPressed();
        boolean alt = Window.isAltPressed();

        if (shift && !alt)
        {
            return this.vertical.mouseScroll(context);
        }
        else if (alt && !shift)
        {
            int scale = this.scroll.scrollItemSize;

            this.scroll.scrollItemSize = MathUtils.clamp(this.scroll.scrollItemSize + (int) Math.copySign(2, context.mouseWheel), 6, 50);
            this.scroll.setSize(this.record.frames.size());

            if (this.scroll.scrollItemSize != scale)
            {
                int value = this.scroll.scroll + (context.mouseX - this.area.x);

                this.scroll.scroll = (int) ((value - (value - this.scroll.scroll) * (scale / (float) this.scroll.scrollItemSize)) * (this.scroll.scrollItemSize / (float) scale));
            }

            this.scroll.clamp();

            return true;
        }

        return this.scroll.mouseScroll(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.moving)
        {
            this.moveTo(this.scroll.getIndex(context.mouseX, context.mouseY));
        }

        this.lastDragging = false;
        this.dragging = false;
        this.moving = false;
        this.scroll.mouseReleased(context);
        this.vertical.mouseReleased(context);

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.record == null)
        {
            return;
        }

        int w = this.scroll.scrollItemSize;

        this.handleLogic(context);

        this.area.render(context.draw, Colors.A50);
        context.draw.gradientHBox(this.area.ex() - 8, this.area.y, this.area.ex(), this.area.ey(), 0, Colors.A50);
        context.draw.gradientHBox(this.area.x, this.area.y, this.area.x + 8, this.area.ey(), Colors.A50, 0);

        this.renderTimeline(context);

        this.scroll.renderScrollbar(context.draw);
        this.vertical.renderScrollbar(context.draw);

        /* Draw cursor (tick indicator) */
        if (this.cursor >= 0 && this.cursor < this.record.frames.size())
        {
            int x = this.scroll.x - this.scroll.scroll + this.cursor * w;
            int cursorX = x + 2;

            String label = this.cursor + "/" + this.record.frames.size();
            int width = context.font.getWidth(label);
            int height = 4 + context.font.getHeight();
            int offsetY = this.scroll.ey() - height;

            if (cursorX + width + 4 > this.scroll.ex())
            {
                cursorX -= width + 4 + 2;
            }

            context.draw.clip(this.area, context);
            context.draw.box(x, this.scroll.y, x + 2, this.scroll.ey(), Colors.CURSOR);
            context.draw.box(cursorX, offsetY, cursorX + width + 4, offsetY + height, Colors.setA(Colors.CURSOR, 0.75F));
            context.font.renderWithShadow(context.render, label, cursorX + 2, offsetY + 2);
            context.draw.unclip(context);
        }

        if (this.moving)
        {
            int x = context.mouseX - w / 2;
            int y = context.mouseY;

            this.renderAction(context, this.getAction(), x, y, true);
        }

        super.render(context);
    }

    private void handleLogic(UIContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.lastDragging)
        {
            this.scroll.scroll = this.lastH + (this.lastX - mouseX);
            this.scroll.clamp();
            this.vertical.scroll = this.lastV + (this.lastY - mouseY);
            this.vertical.clamp();
        }

        if (this.dragging && !this.moving && (Math.abs(mouseX - this.lastX) > 2 || Math.abs(mouseY - this.lastY) > 2))
        {
            this.moving = true;
        }

        this.scroll.drag(mouseX, mouseY);
        this.vertical.drag(mouseX, mouseY);
    }

    private void renderTimeline(UIContext context)
    {
        Range range = this.calculateRange();
        int count = this.record.frames.size();
        int max = this.scroll.x + this.scroll.scrollItemSize * count;

        if (max < this.area.ex())
        {
            context.draw.box(max, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);
        }

        context.draw.clip(this.area, context);

        int w = this.scroll.scrollItemSize;
        int index = this.scroll.scroll / w;
        int diff = index;

        index -= this.adaptiveMaxIndex;
        index = index < 0 ? 0 : index;
        diff = diff - index;

        this.adaptiveMaxIndex = 0;

        for (int i = index, c = i + this.area.w / w + 2 + diff; i < c; i++)
        {
            int x = this.scroll.x - this.scroll.scroll + i * w;

            if (i < count)
            {
                /* Draw tick separators */
                context.draw.box(x, this.area.y, x + 1, this.area.ey(), 0x22ffffff);
            }

            if (i >= range.min && i <= range.max)
            {
                /* Draw selected highlight */
                context.draw.box(x, this.area.y, x + w + 1, this.area.ey(), 0x440088ff);
            }

            if (i >= 0 && i < count)
            {
                /* Draw frame */
                int y = this.area.y - this.vertical.scroll;
                Frame frame = this.record.frames.get(i);

                Icons.CHECKBOARD.renderArea(context.draw, x + 2, y + 1, w - 3, 18, 0xff000000 + frame.color);

                if (this.frame && i == this.current.tick)
                {
                    context.draw.outline(x + 2, y + 1, x + w - 1, y + 19, 0xffffffff);
                }

                List<Action> actions = frame.actions;

                if (actions != null)
                {
                    int j = 0;

                    for (Action action : actions)
                    {
                        y += 20;

                        this.renderAction(context, action, x, y, i == this.current.tick && j == this.current.index);

                        j++;
                    }
                }
            }
        }

        int divisor = this.scroll.scrollItemSize < 5 ? 10 : 5;

        for (int i = index, c = i + this.area.w / w + 2 + diff; i < c; i++)
        {
            if (i % divisor == 0 && i < count && i != this.cursor)
            {
                int x = this.scroll.x - this.scroll.scroll + i * w;
                int y = this.scroll.ey() - 12;

                String str = String.valueOf(i);
                int bottomColor = Colors.mulRGB(Colors.A50 | BBSSettings.primaryColor.get(), 0.5F);

                context.draw.gradientVBox(x + 1, y - 6, x + w, y + 12, 0, bottomColor);
                context.font.renderWithShadow(context.render, str, x + (this.scroll.scrollItemSize - context.font.getWidth(str) + 2) / 2, y);
            }
        }

        context.draw.unclip(context);
    }

    private void renderAction(UIContext context, Action action, int x, int y, boolean selected)
    {
        int w = this.scroll.scrollItemSize;
        int color = BBS.getFactoryActions().getData(action).color;

        this.renderAnimationLength(context, action, x, y, color, selected);

        context.draw.box(x + 2, y + 1, x + w - 1, y + this.vertical.scrollItemSize - 2, color + Colors.A75);

        if (selected)
        {
            context.draw.outline(x + 2, y + 1, x + w - 1, y + this.vertical.scrollItemSize - 2, Colors.WHITE);
        }
    }

    private void renderAnimationLength(UIContext context, Action action, int x, int y, int color, boolean selected)
    {
        if (action instanceof FormAction)
        {
            FormAction morphAction = (FormAction) action;
            int ticks = morphAction.duration;

            if (ticks > 1)
            {
                ticks -= 1;

                int offset = x + this.scroll.scrollItemSize;

                context.draw.box(offset - 1, y + 7, offset + ticks * this.scroll.scrollItemSize, y + 12, selected ? Colors.WHITE : Colors.A25 | color);
                context.draw.box(offset + ticks * this.scroll.scrollItemSize - 1, y + 1, offset + ticks * this.scroll.scrollItemSize, y + this.vertical.scrollItemSize - 2, selected ? Colors.WHITE : Colors.A100 | color);
            }

            this.adaptiveMaxIndex = Math.max(ticks, this.adaptiveMaxIndex);
        }
    }


    public static class Offset
    {
        public int tick;
        public int index;

        public Offset(int tick, int index)
        {
            this.set(tick, index);
        }

        public boolean isEmpty()
        {
            return this.tick == -1;
        }

        public void set(int tick, int index)
        {
            this.tick = tick;
            this.index = index;
        }
    }
}