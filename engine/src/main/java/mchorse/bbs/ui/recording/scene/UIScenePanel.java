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
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.recording.scene.Scene;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
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
    private UIReplayList replays;
    private UIScrollView replayEditor;

    /* Settings fields */
    public UIButton audio;

    /* Replay fields */
    public UITextbox id;
    public UIToggle enabled;

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

        this.replays = new UIReplayList((l) -> this.setReplay(l.get(0)), this);
        this.replays.background(0x44000000).relative(this.editor).y(1F, -80).w(1F).h(80);
        this.replayEditor = UI.scrollView(5, 10);
        this.replayEditor.relative(this.editor).x(1F, -140).w(140).h(1F, -80);

        this.editor.add(this.replayEditor, this.replays);

        /* Settings options */
        this.audio = new UIButton(IKey.lazy("Pick audio..."), (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), new UISoundOverlayPanel((l) ->
            {
                this.data.audio = l;
            }).set(this.data.audio));
        });

        /* Replay options */
        this.id = new UITextbox(120, (str) ->
        {
            this.replay.id = str;

            this.updateLabel();
        }).filename();
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
        this.replayEditor.add(this.enabled);

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

        this.addOptions();
        this.options.fields.add(this.audio);

        this.iconBar.add(this.record, this.edit, this.rename, this.teleport);
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
                        float yaw = camera.rotation.y;

                        entity.setPosition(camera.position.x, camera.position.y, camera.position.z);
                        entity.basic.rotation.set(camera.rotation.x, yaw, yaw);
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
        this.record.setEnabled(data != null);
        this.edit.setEnabled(data != null);
        this.rename.setEnabled(data != null);
        this.teleport.setEnabled(data != null);

        if (this.data != null)
        {
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

            this.replays.setList(replays);
        }
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

        this.replays.setCurrentScroll(replay);

        this.fillReplayData();
    }

    private void fillReplayData()
    {
        this.replayEditor.setVisible(this.replay != null);

        if (this.replay == null)
        {
            return;
        }

        this.id.setText(this.replay.id);
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

            double x = record.keyframes.x.interpolate(0);
            double y = record.keyframes.y.interpolate(0);
            double z = record.keyframes.z.interpolate(0);
            float yaw = (float) record.keyframes.yaw.interpolate(0);
            float pitch = (float) record.keyframes.pitch.interpolate(0);

            this.dashboard.orbit.position.set(x, y + 1, z);
            this.dashboard.orbit.rotation.set(pitch, yaw, 0);
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