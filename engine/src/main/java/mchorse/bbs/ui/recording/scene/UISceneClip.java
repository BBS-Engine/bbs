package mchorse.bbs.ui.recording.scene;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.Replay;
import mchorse.bbs.recording.scene.Scene;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.recording.editor.UIDedicatedRecordEditor;

import java.util.HashSet;
import java.util.Set;

public class UISceneClip extends UIClip<SceneClip>
{
    public UIButton pickScene;
    public UITrackpad offset;
    public UIButton editRecord;

    public static void openRecordEditor(IUICameraWorkDelegate editor, SceneClip clip, Record record)
    {
        UIDedicatedRecordEditor recordEditor = new UIDedicatedRecordEditor(editor, clip);

        recordEditor.fill(record);
        recordEditor.setTick(editor.getCursor());
        editor.embedView(recordEditor);
    }

    public UISceneClip(SceneClip clip, IUICameraWorkDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.pickScene = new UIButton(ContentType.SCENES.getPickLabel(), (b) ->
        {
            UIDataUtils.openPicker(this.getContext(), ContentType.SCENES, this.clip.scene.get(), (name) ->
            {
                editor.postUndo(this.undo(this.clip.scene, (scene) -> scene.set(name)));
            });
        });

        this.offset = new UITrackpad((v) -> {
            this.editor.updateClipProperty(this.clip.offset, TimeUtils.fromTime(v));
        });
        this.offset.limit(0);

        this.editRecord = new UIButton(ContentType.RECORDS.getPickLabel(), (b) ->
        {
            Scene scene = BBSData.getScenes().get(this.clip.scene.get(), this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld());

            if (scene == null)
            {
                return;
            }

            Set<String> ids = new HashSet<>();

            for (Replay replay : scene.getAllReplays())
            {
                if (BBSData.getRecords().records.containsKey(replay.id))
                {
                    ids.add(replay.id);
                }
            }

            UIStringOverlayPanel panel = new UIStringOverlayPanel(UIKeys.CAMERA_PANELS_EDIT_PLAYER_RECORDING, ids, null);

            panel.callback((s) ->
            {
                Record record = BBSData.getRecords().records.get(s);

                if (record != null)
                {
                    openRecordEditor(this.editor, this.clip, record);
                }

                panel.close();
            });

            UIOverlay.addOverlay(this.getContext(), panel);
        });
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(IKey.lazy("Scene")).marginTop(12), this.pickScene, this.offset, this.editRecord);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.offset.setValue(TimeUtils.toTime(this.clip.offset.get()));
    }
}