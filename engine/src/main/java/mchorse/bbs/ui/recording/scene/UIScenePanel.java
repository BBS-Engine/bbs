package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.recording.scene.ReplayGroup;
import mchorse.bbs.recording.scene.Scene;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.recording.editor.UIRecordPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.util.Collections;
import java.util.List;

public class UIScenePanel extends UIDataDashboardPanel<Scene>
{
    private UIElement groups;
    private UIElement replays;
    private UIScrollView replayEditor;

    /* Settings fields */
    public UITrigger onStart;
    public UITrigger onStop;
    public UIToggle loops;

    /* Replay fields */
    public UITextbox id;
    public UITextbox name;
    public UITextbox target;
    public UIToggle invisible;
    public UIToggle enabled;

    public UIIcon add;
    public UIIcon record;
    public UIIcon edit;
    public UIIcon rename;
    public UIIcon teleport;

    public UILabel recordingId;
    public UINestedEdit pickMorph;

    private Replay replay;

    public UIScenePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.groups = UI.scrollView(5, 10);
        this.groups.relative(this.editor).w(1F, -140).h(1F);
        this.replays = new UIElement().noCulling();
        this.replayEditor = UI.scrollView(5, 10);
        this.replayEditor.relative(this.editor).x(1F, -140).w(140).h(1F);

        this.editor.add(this.replays);

        /* Settings options */
        this.onStart = new UITrigger();
        this.onStop = new UITrigger();
        this.loops = new UIToggle(UIKeys.SCENE_LOOPS, false, (b) -> this.data.loops = b.getValue());

        /* Replay options */
        this.id = new UITextbox(120, (str) ->
        {
            this.replay.id = str;

            this.updateLabel();
        }).filename();
        this.name = new UITextbox(80, (str) -> this.replay.name = str);
        this.name.tooltip(UIKeys.SCENE_NAME_TOOLTIP, Direction.RIGHT);
        this.target = new UITextbox(80, (str) -> this.replay.target = str);
        this.target.tooltip(UIKeys.SCENE_TARGET_TOOLTIP, Direction.LEFT);
        this.invisible = new UIToggle(UIKeys.SCENE_INVISIBLE, false, (b) -> this.replay.invisible = b.getValue());
        this.enabled = new UIToggle(UIKeys.SCENE_ENABLED, false, (b) -> this.replay.enabled = b.getValue());
        this.recordingId = UI.label(UIKeys.SCENE_ID).color(Colors.LIGHTEST_GRAY);

        this.pickMorph = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this, editing, this.replay.form, (form) ->
            {
                if (this.replay != null)
                {
                    this.replay.form = FormUtils.copy(form);
                }

                this.pickMorph.setForm(this.replay.form);
            }).updatable();
        });

        this.replayEditor.add(this.pickMorph, this.recordingId, this.id);
        this.replayEditor.add(UI.label(UIKeys.SCENE_NAME).color(Colors.LIGHTEST_GRAY), this.name);
        this.replayEditor.add(this.invisible, this.enabled);
        this.replays.add(this.groups, this.replayEditor);

        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            this.data.groups.add(new ReplayGroup(this.data));

            this.fillGroups();
        });
        this.add.tooltip(UIKeys.SCENE_ADD_GROUP, Direction.LEFT);
        this.record = new UIIcon(Icons.SPHERE, (b) ->
        {
            this.record();
        });
        this.record.tooltip(UIKeys.SCENE_RECORD, Direction.LEFT);
        this.edit = new UIIcon(Icons.EDITOR, (b) -> this.openRecordEditor());
        this.edit.tooltip(UIKeys.SCENE_EDIT_RECORD, Direction.LEFT);
        this.rename = new UIIcon(Icons.EDIT, (b) -> this.renamePrefix());
        this.rename.tooltip(UIKeys.SCENE_RENAME_PREFIX, Direction.LEFT);
        this.teleport = new UIIcon(Icons.MOVE_TO, (b) -> this.teleport());
        this.teleport.tooltip(UIKeys.SCENE_TP, Direction.LEFT);

        this.replayEditor.add(UI.label(UIKeys.SCENE_TARGET).color(Colors.LIGHTEST_GRAY).marginTop(12), this.target);

        this.addOptions();
        this.options.fields.add(this.loops);
        this.options.fields.add(UI.label(UIKeys.SCENE_START_COMMAND).marginTop(8), this.onStart);
        this.options.fields.add(UI.label(UIKeys.SCENE_STOP_COMMAND).marginTop(8), this.onStop);

        this.iconBar.add(this.add, this.record, this.edit, this.rename, this.teleport);
        this.overlay.namesList.setFileIcon(Icons.SCENE);

        this.fill(null);
    }

    public void plause()
    {
        if (this.data != null)
        {
            BBSData.getScenes().toggle(this.data.getId(), this.dashboard.bridge.get(IBridgeWorld.class).getWorld());
        }
    }

    public void record()
    {
        UIContext context = this.getContext();

        if (context == null)
        {
            return;
        }

        this.save();

        IBridge bridge = context.menu.bridge;

        if (this.replay != null)
        {
            if (BBSData.getRecords().recorders.isEmpty())
            {
                UIRecordOverlayPanel panel = new UIRecordOverlayPanel(
                    UIKeys.SCENE_RECORD_TITLE,
                    UIKeys.SCENE_RECORD_DESCRIPTION,
                    (l) ->
                    {
                        World world = bridge.get(IBridgeWorld.class).getWorld();
                        Entity entity = world.architect.create(Link.bbs("player"));
                        Camera camera = bridge.get(IBridgeCamera.class).getCamera();

                        entity.setPosition(camera.position.x, camera.position.y, camera.position.z);
                        entity.basic.rotation.set(camera.rotation.x, camera.rotation.y, camera.rotation.y);
                        entity.basic.prevRotation.set(entity.basic.rotation);
                        entity.setWorld(world);
                        entity.canBeSaved = false;

                        world.addEntitySafe(entity);
                        bridge.get(IBridgePlayer.class).setController(entity);

                        BBSData.getScenes().record(this.data.getId(), this.replay.id, 0, entity, world, l);

                        bridge.get(IBridgeMenu.class).closeMenu();
                    }
                );

                UIOverlay.addOverlay(this.getContext(), panel);
            }
            else
            {
                Entity entity = BBSData.getRecords().recorders.keySet().iterator().next();
                World world = bridge.get(IBridgeWorld.class).getWorld();

                BBSData.getScenes().record(this.data.getId(), this.replay.id, 0, entity, world, Collections.emptyList());

                world.removeEntity(entity);
                bridge.get(IBridgeMenu.class).showMenu(this.dashboard);
            }
        }
    }

    public Replay getReplay()
    {
        return this.replay;
    }

    @Override
    public void open()
    {
        super.open();

        /* Stop recording */
        if (!BBSData.getRecords().recorders.isEmpty())
        {
            this.record();
        }
    }

    @Override
    public ContentType getType()
    {
        return ContentType.SCENES;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_SCENES;
    }

    @Override
    public void fill(Scene data)
    {
        super.fill(data);

        this.editor.setVisible(data != null);
        this.add.setEnabled(data != null);
        this.record.setEnabled(data != null);
        this.edit.setEnabled(data != null);
        this.rename.setEnabled(data != null);
        this.teleport.setEnabled(data != null);

        if (this.data != null)
        {
            this.fillGroups();

            List<Replay> replays = this.data.getAllReplays();

            if (!replays.isEmpty())
            {
                Replay id = null;

                if (this.replay != null)
                {
                    for (Replay replay : replays)
                    {
                        if (replay.id.equals(this.replay.id))
                        {
                            id = replay;

                            break;
                        }
                    }
                }

                if (id != null)
                {
                    this.setReplay(id);
                }
                else
                {
                    this.setReplay(replays.get(0));
                }
            }
            else
            {
                this.setReplay(null);
            }

            this.onStart.set(this.data.onStart);
            this.onStop.set(this.data.onStop);
            this.loops.setValue(this.data.loops);
        }
    }

    private void fillGroups()
    {
        this.groups.removeAll();

        for (ReplayGroup group : this.data.groups)
        {
            UIReplayGroup element = new UIReplayGroup(this);

            element.setGroup(group);
            this.groups.add(element);
        }

        this.resize();
    }

    public void setReplay(Replay replay)
    {
        if (this.replay != null)
        {
            this.replay.form = FormUtils.copy(this.replay.form);
        }

        this.record.setEnabled(replay != null);
        this.edit.setEnabled(replay != null);
        this.teleport.setEnabled(replay != null);

        this.replay = replay;

        this.fillReplayData();

        for (UIReplayGroup group : this.groups.getChildren(UIReplayGroup.class))
        {
            group.select(replay);
        }
    }

    private void fillReplayData()
    {
        this.replayEditor.setEnabled(this.replay != null);

        if (this.replay == null)
        {
            return;
        }

        this.id.setText(this.replay.id);
        this.name.setText(this.replay.name);
        this.target.setText(this.replay.target);
        this.invisible.setValue(this.replay.invisible);
        this.enabled.setValue(this.replay.enabled);
        this.pickMorph.setForm(this.replay.form);

        this.updateLabel();
    }

    private void updateLabel()
    {
        boolean error = this.replay != null && this.replay.id.isEmpty();

        this.recordingId.color(error ? Colors.RED : Colors.LIGHTEST_GRAY);
    }

    private void openRecordEditor()
    {
        if (this.replay != null && !this.replay.id.isEmpty())
        {
            UIRecordPanel panel = this.dashboard.getPanel(UIRecordPanel.class);

            this.dashboard.setPanel(panel);
            panel.pickData(this.replay.id);
        }
    }

    private void teleport()
    {
        if (this.replay == null)
        {
            return;
        }

        Record record = BBSData.getRecords().load(this.replay.id);
        UIContext context = this.getContext();
        Entity player = context.menu.bridge.get(IBridgePlayer.class).getController();

        if (record != null)
        {
            if (player != null)
            {
                record.applyFrame(0, player);
            }

            Frame frame = record.getFrame(0);

            if (frame != null)
            {
                this.dashboard.orbit.position.set(frame.x, frame.y + 1, frame.z);
                this.dashboard.orbit.rotation.set(frame.pitch, frame.yaw, 0);
            }
        }
    }

    private void renamePrefix()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.RENAME,
            UIKeys.SCENE_RENAME_PREFIX_POPUP,
            this::renamePrefix
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void renamePrefix(String newPrefix)
    {
        this.data.renamePrefix(newPrefix);
        this.fillReplayData();
    }
}