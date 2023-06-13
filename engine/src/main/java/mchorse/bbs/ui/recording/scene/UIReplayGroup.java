package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.recording.scene.ReplayGroup;
import mchorse.bbs.recording.scene.Scene;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.math.MathUtils;

public class UIReplayGroup extends UIElement
{
    public UITextbox title;
    public UIToggle enabled;
    public UIScenePanel panel;

    public UIReplayList replays;

    private ReplayGroup group;

    public UIReplayGroup(UIScenePanel panel)
    {
        this.panel = panel;

        this.title = new UITextbox(1000, (t) -> this.group.title = t);
        this.title.relative(this).w(120);
        this.enabled = new UIToggle(UIKeys.SCENE_REPLAYS_ENABLED, (b) -> this.group.enabled = b.getValue());
        this.enabled.relative(this).x(125).w(1F, -125).h(20);
        this.replays = new UIReplayList((l) -> this.panel.setReplay(l.get(0)));

        this.replays.relative(this).y(20).w(1F, -20).h(60);
        this.add(this.title, this.enabled, this.replays);

        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.SCENE_REPLAYS_CONTEXT_ADD, this::addReplay);

            if (this.replays.isSelected())
            {
                menu.action(Icons.DUPE, UIKeys.SCENE_REPLAYS_CONTEXT_DUPE, this::dupeReplay);
                menu.action(Icons.REMOVE, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE, this::removeReplay);
            }

            menu.action(Icons.TRASH, UIKeys.SCENE_REPLAYS_CONTEXT_REMOVE_GROUP, this::removeGroup);
        });

        this.h(80);
    }

    public void setGroup(ReplayGroup group)
    {
        this.group = group;

        this.title.setText(group.title);
        this.enabled.setValue(group.enabled);
        this.replays.setList(this.group.replays);
    }

    public void select(Replay replay)
    {
        this.replays.setIndex(this.group.replays.indexOf(replay));
    }

    private void addReplay()
    {
        Replay replay = new Replay("");
        Scene scene = this.group.scene;

        replay.id = scene.getNextBaseSuffix(scene.getId());

        this.group.replays.add(replay);
        this.panel.setReplay(replay);
        this.replays.update();
    }

    private void dupeReplay()
    {
        if (this.replays.isDeselected())
        {
            return;
        }

        if (this.group.dupe(this.group.replays.indexOf(this.panel.getReplay())))
        {
            this.replays.update();
            this.replays.scroll.scrollTo(this.replays.getIndex() * this.replays.scroll.scrollItemSize);
            this.panel.setReplay(this.group.replays.get(this.group.replays.size() - 1));
        }
    }

    private void removeReplay()
    {
        if (this.replays.isDeselected())
        {
            return;
        }

        int index = this.replays.getIndex();

        this.group.replays.remove(this.panel.getReplay());

        int size = this.group.replays.size();
        index = MathUtils.clamp(index, 0, size - 1);

        this.panel.setReplay(size == 0 ? null : this.group.replays.get(index));
        this.replays.update();
    }

    private void removeGroup()
    {
        UIElement parent = this.getParent();

        this.panel.getData().groups.remove(this.group);
        this.removeFromParent();

        parent.resize();
    }
}