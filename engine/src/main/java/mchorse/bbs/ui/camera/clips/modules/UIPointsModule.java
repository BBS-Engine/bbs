package mchorse.bbs.ui.camera.clips.modules;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.overwrite.PathClip;
import mchorse.bbs.camera.values.ValuePositions;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.IUndo;

import java.util.function.Consumer;

/**
 * Points GUI module
 *
 * This module is responsible for displaying "buttons" for picking up path
 * clip's points, and also an ability to add or remove them.
 */
public class UIPointsModule extends UIAbstractModule
{
    /* Input */
    public PathClip path;
    public Consumer<Integer> picker;

    /* GUI */
    public ScrollArea scroll = new ScrollArea(this.area, 20);

    /**
     * Currently selected button (shouldn't be deselected, i.e. can't be -1)
     */
    public int index = 0;

    public UIPointsModule(UICameraPanel editor, Consumer<Integer> picker)
    {
        super(editor);

        this.picker = picker;

        this.scroll.direction = ScrollDirection.HORIZONTAL;
        this.scroll.cancelScrolling();

        this.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.CAMERA_PANELS_POINTS_CONTEXT_ADD, this::addPoint);
            menu.action(Icons.REMOVE, UIKeys.CAMERA_PANELS_POINTS_CONTEXT_REMOVE, this::removePoint);
            menu.action(Icons.SHIFT_BACKWARD, UIKeys.CAMERA_PANELS_POINTS_CONTEXT_MOVE_BACK, this::moveBack);
            menu.action(Icons.SHIFT_FORWARD, UIKeys.CAMERA_PANELS_POINTS_CONTEXT_MOVE_FORWARD, this::moveForward);
        });
    }

    private IUndo<CameraWork> undo(UICameraPanel editor, int index, int nextIndex, BaseType positions)
    {
        return UIClip.undo(editor, this.path.points, positions).noMerging();
    }

    private ValuePositions create()
    {
        ValuePositions positions = new ValuePositions("");

        positions.fromData(this.path.points.toData());

        return positions;
    }

    public void setIndex(int index)
    {
        this.index = index;
        this.scroll.scrollIntoView(index * this.scroll.scrollItemSize);
    }

    public void moveBack()
    {
        if (this.index == 0)
        {
            return;
        }

        ValuePositions positions = this.create();

        positions.get().add(this.index, positions.get().remove(this.index - 1));

        int nextIndex = this.index - 1;

        this.editor.postUndo(this.undo(this.editor, this.index, nextIndex, positions.toData()));
        this.index = nextIndex;
    }

    public void moveForward()
    {
        if (this.index >= this.path.size() - 1)
        {
            return;
        }

        ValuePositions positions = this.create();

        positions.get().add(this.index, positions.get().remove(this.index + 1));

        int nextIndex = this.index - 1;

        this.editor.postUndo(this.undo(this.editor, this.index, nextIndex, positions.toData()));
        this.index = nextIndex;
    }

    public void addPoint()
    {
        ValuePositions positions = this.create();

        if (this.index + 1 >= this.path.size())
        {
            positions.get().add(this.editor.getPosition());

            int nextIndex = MathUtils.clamp(this.index + 1, 0, positions.size() - 1);

            this.editor.postUndo(this.undo(this.editor, this.index, nextIndex, positions.toData()));
            this.index = nextIndex;
        }
        else
        {
            positions.get().add(this.index + 1, this.editor.getPosition());

            int nextIndex = this.index + 1;

            this.editor.postUndo(this.undo(this.editor, this.index, nextIndex, positions.toData()));
            this.index = nextIndex;
        }

        this.scroll.setSize(this.path.size());
        this.scroll.scrollTo(this.index * this.scroll.scrollItemSize);

        if (this.picker != null)
        {
            this.picker.accept(this.index);
        }
    }

    public void removePoint()
    {
        if (this.path.points.size() == 1 && this.index >= 0)
        {
            return;
        }

        ValuePositions positions = this.create();

        positions.get().remove(this.index);

        int nextIndex = this.index > 0 ? this.index - 1 : this.index;

        this.editor.postUndo(this.undo(this.editor, this.index, nextIndex, positions.toData()));

        this.index = nextIndex;
        this.scroll.setSize(this.path.size());
        this.scroll.scrollTo(this.index * this.scroll.scrollItemSize);

        if (this.picker != null)
        {
            this.picker.accept(this.index);
        }
    }

    /**
     * Setup the path clip and also fill or reset this module's fields based
     * on the path clip.
     */
    public void fill(PathClip path)
    {
        this.path = path;
        this.index = 0;
        this.scroll.setSize(path.size());
        this.scroll.clamp();
    }

    /**
     * Mouse was clicked
     *
     * This method responsible for adding and removing points in the path
     * clip and initiating scrolling.
     */
    @Override
    public boolean subMouseClicked(UIContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.area.isInside(context))
        {
            if (context.mouseButton == 2 || (context.mouseButton == 0 && Window.isCtrlPressed()))
            {
                this.scroll.dragging = true;

                return true;
            }
            else if (context.mouseButton == 0)
            {
                int index = this.scroll.getIndex(mouseX, mouseY);
                int size = this.path.size();

                if (index >= 0 && index < size)
                {
                    /* Pick a point */
                    this.index = index;

                    if (this.picker != null)
                    {
                        this.picker.accept(index);
                    }
                }

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        return this.scroll.mouseScroll(context);
    }

    /**
     * Mouse button was released
     *
     * If scrolling was initiated on click, this method will be responsible for
     * selecting a point in the path or shifting the playback timeline to the
     * location of the of current path point.
     */
    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.scroll.mouseReleased(context);

        return super.subMouseReleased(context);
    }

    /**
     * Draw the module
     *
     * This method will draw the background, button labels (+/-) and also alls
     * the buttons. It also responsible for scrolling.
     */
    @Override
    public void render(UIContext context)
    {
        /* Scroll this view */
        this.scroll.drag(context);

        int x = this.area.x;
        int y = this.area.y;
        int c = this.path.size();

        /* Draw background and buttons */
        context.batcher.box(x, y, x + this.area.w, y + this.area.h, Colors.A50);
        context.batcher.clip(this.area, context);

        for (int i = 0; i < c; i++)
        {
            String label = String.valueOf(i);
            int xx = this.area.x + i * this.scroll.scrollItemSize - this.scroll.scroll;
            int w = context.font.getWidth(label);

            context.batcher.box(xx, y, xx + 20, y + 20, this.index == i ? 0xffcc1170 : 0xffff2280);
            context.batcher.box(xx + 19, y, xx + 20, y + 20, Colors.A12);
            context.batcher.textShadow(label, xx + 10 - w / 2, y + 6);
        }

        context.batcher.unclip(context);

        /* Display scroll bar */
        int mw = this.area.w;
        int scroll = this.scroll.getScrollBar(mw);

        if (scroll != 0)
        {
            int bx = this.area.x + (int) (this.scroll.scroll / (float) (this.scroll.scrollSize - this.area.w) * (mw - scroll));
            int by = y + this.area.h + 2;

            context.batcher.box(bx, by, bx + scroll, by + 2, Colors.A50);
        }

        /* Overlay "shadows" for informing the user that  */
        if (this.scroll.scroll > 0 && this.scroll.scrollSize >= this.area.w - 40)
        {
            context.batcher.gradientHBox(x, y, x + 4, y + this.area.h, Colors.A50, 0);
        }

        if (this.scroll.scroll < this.scroll.scrollSize - this.area.w && this.scroll.scrollSize >= this.area.w)
        {
            context.batcher.gradientHBox(x + this.area.w - 4, y, x + this.area.w, y + this.area.h, 0, Colors.A50);
        }

        super.render(context);
    }
}