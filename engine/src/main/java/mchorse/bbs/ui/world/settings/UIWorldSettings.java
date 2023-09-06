package mchorse.bbs.ui.world.settings;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.world.WorldSettings;
import org.joml.Vector3f;

public class UIWorldSettings extends UIElement
{
    public UIToggle sky;
    public UIToggle fog;
    public UINestedEdit pickSkyForm;
    public UIColor zenith;
    public UIColor horizon;
    public UIColor bottom;
    public UITrackpad dayCycle;
    public UITrackpad dayYaw;
    public UIVector3d shadingDirection;
    public UIButton pickLightmap;

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

        this.zenith = new UIColor((c) -> this.settings.zenith.set(c, false));
        this.zenith.tooltip(UIKeys.WORLD_SETTINGS_ZENITH);
        this.zenith.setColor(this.settings.zenith.getRGBColor());
        this.horizon = new UIColor((c) -> this.settings.horizon.set(c, false));
        this.horizon.tooltip(UIKeys.WORLD_SETTINGS_HORIZON);
        this.horizon.setColor(this.settings.horizon.getRGBColor());
        this.bottom = new UIColor((c) -> this.settings.bottom.set(c, false));
        this.bottom.tooltip(UIKeys.WORLD_SETTINGS_BOTTOM);
        this.bottom.setColor(this.settings.bottom.getRGBColor());
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
        this.pickLightmap = new UIButton(UIKeys.WORLD_SETTINGS_PICK_LIGHTMAP, (b) ->
        {
            UITexturePicker.open(this.getParentContainer(), this.settings.lightmap, (l) -> this.settings.lightmap = LinkUtils.copy(l == null ? WorldSettings.DEFAULT_LIGHTMAP : l));
        });

        this.add(UI.label(UIKeys.WORLD_SETTINGS_PICK_SKYBOX).background(), this.pickSkyForm);
        this.add(this.sky.marginTop(8), this.fog, this.zenith, this.horizon, this.bottom);
        this.add(UI.label(IKey.lazy("Day cycle and yaw")).marginTop(8), this.dayCycle, this.dayYaw);
        this.add(UI.label(UIKeys.WORLD_SETTINGS_SHADING).marginTop(8), this.shadingDirection, this.pickLightmap);

        this.column().vertical().stretch();
    }
}