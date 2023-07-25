package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.recording.scene.Scene;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * This GUI is responsible for drawing replays available in the 
 * director thing
 */
public class UIReplayList extends UIList<Replay>
{
    public UIScenePanel panel;

    private String hovered;
    private int hoverX;
    private int hoverY;

    public UIReplayList(Consumer<List<Replay>> callback, UIScenePanel panel)
    {
        super(callback);

        this.panel = panel;

        this.horizontal().sorting();
        this.scroll.scrollItemSize = 40;

        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SCENE_REPLAYS_CONTEXT_ADD, this::addReplay);

            if (this.isSelected())
            {
                menu.action(Icons.DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, this::dupeReplay);
                menu.action(Icons.REMOVE, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE, this::removeReplay);
            }
        });
    }

    private void addReplay()
    {
        Replay replay = new Replay("");
        Scene scene = this.panel.getData();

        replay.id = scene.getNextBaseSuffix(scene.getId());

        this.add(replay);
        this.panel.setReplay(replay);
        this.update();
    }

    private void dupeReplay()
    {
        if (this.isDeselected())
        {
            return;
        }

        Replay copy = this.getCurrentFirst().copy();

        copy.id = this.panel.getData().getNextSuffix(copy.id);

        this.list.add(copy);

        this.update();
        this.scroll.scrollTo(this.getIndex() * this.scroll.scrollItemSize);
        this.panel.setReplay(this.list.get(this.list.size() - 1));
    }

    private void removeReplay()
    {
        if (this.isDeselected())
        {
            return;
        }

        int index = this.getIndex();

        this.remove(this.panel.getReplay());

        int size = this.list.size();
        index = MathUtils.clamp(index, 0, size - 1);

        this.panel.setReplay(size == 0 ? null : this.list.get(index));
        this.update();
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

            context.batcher.box(x - 2, this.hoverY - 1, x + w + 2, this.hoverY + 9, Colors.A50);
            context.batcher.textShadow(this.hovered, x, this.hoverY);
        }
        else if (this.getList().isEmpty())
        {
            String label = UIKeys.SCENE_NO_REPLAYS.get();
            int x = this.area.mx(context.font.getWidth(label));
            int y = this.area.my() - 6;

            context.batcher.text(label, x, y);
        }
    }

    @Override
    public void renderElementPart(UIContext context, Replay replay, int i, int x, int y, boolean hover, boolean selected)
    {
        int w = this.scroll.scrollItemSize;
        int h = this.area.h;
        boolean isDragging = this.isDragging() && this.getDraggingIndex() == i;

        if (selected && !isDragging)
        {
            context.batcher.box(x, y, x + w, y + h, Colors.A75 | BBSSettings.primaryColor.get());
            context.batcher.clip(x, y, w, h, context);
        }

        if (replay.form != null)
        {
            replay.form.getRenderer().renderUI(context, x, y, x + w, y + this.area.h);
        }
        else
        {
            context.batcher.icon(Icons.POSE, x + w / 2 - 8, y + this.area.h / 2 - 8);
        }

        if (selected && !isDragging)
        {
            context.batcher.outline(x, y, x + w, y + h, Colors.A100 | BBSSettings.primaryColor.get(), 2);
            context.batcher.unclip(context);
        }

        if (hover && !replay.id.isEmpty() && this.hovered == null)
        {
            this.hovered = replay.id;
            this.hoverX = x;
            this.hoverY = y + this.area.h / 2;
        }
    }
}