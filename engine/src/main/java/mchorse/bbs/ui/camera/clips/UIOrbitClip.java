package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.clips.modifiers.OrbitClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.utils.UITextboxHelp;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;

public class UIOrbitClip extends UIClip<OrbitClip>
{
    public UITextboxHelp selector;
    public UIToggle copy;
    public UITrackpad yaw;
    public UITrackpad pitch;
    public UIPointModule offset;
    public UITrackpad distance;

    public UIOrbitClip(OrbitClip clip, IUICameraWorkDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.selector = new UITextboxHelp(500, (str) ->
        {
            this.editor.postUndo(this.undo(this.clip.selector, (selector) -> selector.set(str)));
            this.clip.tryFindingEntity(this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld());
        });
        this.selector.link(UILookClip.SELECTOR_HELP).tooltip(UIKeys.CAMERA_PANELS_SELECTOR_TOOLTIP);

        this.copy = new UIToggle(UIKeys.CAMERA_PANELS_COPY_ENTITY, false, (b) -> this.editor.postUndo(this.undo(this.clip.copy, (copy) -> copy.set(b.getValue()))));
        this.copy.tooltip(UIKeys.CAMERA_PANELS_COPY_ENTITY_TOOLTIP);

        this.yaw = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.yaw, (yaw) -> yaw.set(value.floatValue()))));
        this.yaw.tooltip(UIKeys.CAMERA_PANELS_YAW);

        this.pitch = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.pitch, (pitch) -> pitch.set(value.floatValue()))));
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_PITCH);

        this.offset = new UIPointModule(editor, UIKeys.CAMERA_PANELS_OFFSET).contextMenu();

        this.distance = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.distance, (distance) -> distance.set(value.floatValue()))));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_SELECTOR), this.selector);
        this.panels.add(this.copy.marginBottom(12));
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_DISTANCE), this.distance);
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_ANGLE));
        this.panels.add(UI.row(5, 0, 20, this.yaw, this.pitch));
        this.panels.add(this.offset);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.selector.setText(this.clip.selector.get());
        this.copy.setValue(this.clip.copy.get());
        this.yaw.setValue(this.clip.yaw.get());
        this.pitch.setValue(this.clip.pitch.get());
        this.offset.fill(this.clip.offset);
        this.distance.setValue(this.clip.distance.get());
    }
}