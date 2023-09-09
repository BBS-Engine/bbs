package mchorse.bbs.ui.world.settings;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.WorldSettings;
import org.joml.Vector3f;

public class UIWorldSettings extends UIElement
{
    public UIToggle sky;
    public UIToggle fog;
    public UINestedEdit pickSkyForm;
    public UIColor lightmap00;
    public UIColor lightmap10;
    public UITrackpad dayCycle;
    public UITrackpad dayYaw;
    public UIVector3d shadingDirection;

    private WorldSettings settings;

    public UIWorldSettings(WorldSettings settings)
    {
        this.settings = settings;

        this.sky = new UIToggle(UIKeys.WORLD_SETTINGS_SKY, (b) -> this.settings.sky = b.getValue());
        this.sky.setValue(settings.sky);
        this.fog = new UIToggle(UIKeys.WORLD_SETTINGS_FOG, (b) -> this.settings.fog = b.getValue());
        this.fog.setValue(settings.fog);

        this.pickSkyForm = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this.getParentContainer(), editing, this.settings.skyForm, (form) ->
            {
                this.settings.skyForm = form;
                this.pickSkyForm.setForm(form);
            });
        });
        this.pickSkyForm.setForm(settings.skyForm);

        this.lightmap00 = new UIColor((c) -> this.settings.lightmap00.set(c, false));
        this.lightmap00.tooltip(IKey.lazy("Lightmap (no lighting)"));
        this.lightmap00.setColor(this.settings.lightmap00.getRGBColor());
        this.lightmap10 = new UIColor((c) -> this.settings.lightmap10.set(c, false));
        this.lightmap10.tooltip(IKey.lazy("Lightmap (with lighting)"));
        this.lightmap10.setColor(this.settings.lightmap10.getRGBColor());
        this.dayCycle = new UITrackpad((v) -> this.settings.dayCycle = v.floatValue());
        this.dayCycle.setValue(this.settings.dayCycle);
        this.dayYaw = new UITrackpad((v) -> this.settings.dayYaw = v.floatValue());
        this.dayYaw.setValue(this.settings.dayYaw);
        this.shadingDirection = new UIVector3d((v) -> this.settings.shadingDirection.set(v));
        this.shadingDirection.fill(this.settings.shadingDirection);
        this.shadingDirection.context((menu) ->
        {
            menu.action(Icons.FRUSTUM, UIKeys.WORLD_SETTINGS_CONTEXT_SHADING_FROM_CAMERA, () ->
            {
                Vector3f direction = this.getContext().menu.bridge.get(IBridgeCamera.class).getCamera().getLookDirection();

                this.settings.shadingDirection.set(direction.normalize());
                this.shadingDirection.fill(direction);
            });
        });

        this.add(UI.label(UIKeys.WORLD_SETTINGS_PICK_SKYBOX).background(), this.pickSkyForm);
        this.add(this.sky.marginTop(8), this.fog, UI.row(this.lightmap00, this.lightmap10));
        this.add(UI.label(IKey.lazy("Day cycle and yaw")).marginTop(8), this.dayCycle, this.dayYaw);
        this.add(UI.label(UIKeys.WORLD_SETTINGS_SHADING).marginTop(8), this.shadingDirection);

        this.column().vertical().stretch();
    }
}