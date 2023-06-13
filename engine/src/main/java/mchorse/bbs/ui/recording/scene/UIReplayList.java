package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;
import java.util.function.Consumer;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class UIReplayList extends UIList<Replay>
{
    private String hovered;
    private int hoverX;
    private int hoverY;

    public UIReplayList(Consumer<List<Replay>> callback)
    {
        super(callback);

        this.horizontal().sorting();
        this.scroll.scrollItemSize = 40;
    }

    @Override
    public void render(UIContext context)
    {
        this.hovered = null;

        super.render(context);

        if (this.hovered != null)
        {
            int w = context.font.getWidth(this.hovered);
            int x = this.hoverX + this.scroll.scrollItemSize / 2 - w / 2;

            context.draw.box(x - 2, this.hoverY - 1, x + w + 2, this.hoverY + 9, Colors.A50);
            context.font.renderWithShadow(context.render, this.hovered, x, this.hoverY);
        }
        else if (this.getList().isEmpty())
        {
            context.font.renderCentered(context.render, UIKeys.SCENE_NO_REPLAYS.get(), this.area.mx(), this.area.my() - 6);
        }
    }

    @Override
    public void renderElementPart(UIContext context, Replay replay, int i, int x, int y, boolean hover, boolean selected)
    {
        int w = this.scroll.scrollItemSize;
        int h = this.scroll.h;
        boolean isDragging = this.isDragging() && this.getDraggingIndex() == i;

        if (selected && !isDragging)
        {
            context.draw.box(x, y, x + w, y + h, Colors.A75 | BBSSettings.primaryColor.get());
            context.draw.clip(x, y, w, h, context);
        }

        if (replay.form != null)
        {
            replay.form.getRenderer().renderUI(context, x, y, x + w, y + this.scroll.h);
        }
        else
        {
            Icons.POSE.render(context.draw, x + w / 2 - 8, y + this.scroll.h / 2 - 8);
        }

        if (selected && !isDragging)
        {
            context.draw.outline(x, y, x + w, y + h, Colors.A100 | BBSSettings.primaryColor.get(), 2);
            context.draw.unclip(context);
        }

        if (hover && !replay.id.isEmpty() && this.hovered == null)
        {
            this.hovered = replay.id;
            this.hoverX = x;
            this.hoverY = y + this.scroll.h / 2;
        }
    }
}