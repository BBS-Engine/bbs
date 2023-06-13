package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.events.UITriggerHotkeysOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.objects.TriggerObject;

public class UITriggerWorldObject extends UIWorldObject<TriggerObject>
{
    public UIButton edit;
    public UIVector3d hitbox;

    public UITriggerWorldObject()
    {
        super();

        this.edit = new UIButton(UIKeys.SETTINGS_HOTKEYS, this::openHotkeysEditor);
        this.hitbox = new UIVector3d((v) -> this.object.hitbox.set(v));

        this.add(this.edit);
        this.add(UI.label(UIKeys.WORLD_OBJECTS_OBJECTS_PROP_HITBOX).marginTop(6), this.hitbox);
    }

    private void openHotkeysEditor(UIButton b)
    {
        UITriggerHotkeysOverlayPanel overlay = new UITriggerHotkeysOverlayPanel(this.object.hotkeys.hotkeys);

        UIOverlay.addOverlay(this.getContext(), overlay, 0.5F, 0.7F);
    }

    @Override
    public void fillData(TriggerObject object)
    {
        super.fillData(object);

        this.hitbox.fill(object.hitbox);
    }
}