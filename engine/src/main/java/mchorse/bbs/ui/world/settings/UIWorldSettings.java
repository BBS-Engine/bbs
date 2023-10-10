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
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.world.WorldSettings;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class UIWorldSettings extends UIElement
{
    public UIToggle terrain;
    public UIToggle sky;
    public UIToggle fog;
    public UINestedEdit pickSkyForm;
    public UIColor lightmap00;
    public UIColor lightmap10;
    public UITrackpad dayCycle;
    public UITrackpad dayYaw;

    public UIColor skySunrise1;
    public UIColor skySunrise2;
    public UIColor skySunrise3;
    public UIColor skyNoon1;
    public UIColor skyNoon2;
    public UIColor skyNoon3;
    public UIColor skySunset1;
    public UIColor skySunset2;
    public UIColor skySunset3;
    public UIColor skyMidnight1;
    public UIColor skyMidnight2;
    public UIColor skyMidnight3;

    public UIVector3d shadingDirection;

    private WorldSettings settings;

    public UIWorldSettings(WorldSettings settings)
    {
        this.settings = settings;

        this.terrain = new UIToggle(IKey.lazy("Terrain"), (b) -> this.settings.terrain = b.getValue());
        this.terrain.setValue(settings.terrain);
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

        this.skySunrise1 = new UIColor((c) -> this.setColor(c, this.settings.skySunrise, 0));
        this.skySunrise1.setColor(this.getColor(this.settings.skySunrise, 0));
        this.skySunrise2 = new UIColor((c) -> this.setColor(c, this.settings.skySunrise, 1));
        this.skySunrise2.setColor(this.getColor(this.settings.skySunrise, 1));
        this.skySunrise3 = new UIColor((c) -> this.setColor(c, this.settings.skySunrise, 2));
        this.skySunrise3.setColor(this.getColor(this.settings.skySunrise, 2));
        this.skyNoon1 = new UIColor((c) -> this.setColor(c, this.settings.skyNoon, 0));
        this.skyNoon1.setColor(this.getColor(this.settings.skyNoon, 0));
        this.skyNoon2 = new UIColor((c) -> this.setColor(c, this.settings.skyNoon, 1));
        this.skyNoon2.setColor(this.getColor(this.settings.skyNoon, 1));
        this.skyNoon3 = new UIColor((c) -> this.setColor(c, this.settings.skyNoon, 2));
        this.skyNoon3.setColor(this.getColor(this.settings.skyNoon, 2));
        this.skySunset1 = new UIColor((c) -> this.setColor(c, this.settings.skySunset, 0));
        this.skySunset1.setColor(this.getColor(this.settings.skySunset, 0));
        this.skySunset2 = new UIColor((c) -> this.setColor(c, this.settings.skySunset, 1));
        this.skySunset2.setColor(this.getColor(this.settings.skySunset, 1));
        this.skySunset3 = new UIColor((c) -> this.setColor(c, this.settings.skySunset, 2));
        this.skySunset3.setColor(this.getColor(this.settings.skySunset, 2));
        this.skyMidnight1 = new UIColor((c) -> this.setColor(c, this.settings.skyMidnight, 0));
        this.skyMidnight1.setColor(this.getColor(this.settings.skyMidnight, 0));
        this.skyMidnight2 = new UIColor((c) -> this.setColor(c, this.settings.skyMidnight, 1));
        this.skyMidnight2.setColor(this.getColor(this.settings.skyMidnight, 1));
        this.skyMidnight3 = new UIColor((c) -> this.setColor(c, this.settings.skyMidnight, 2));
        this.skyMidnight3.setColor(this.getColor(this.settings.skyMidnight, 2));

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
        this.add(this.terrain.marginTop(8), this.sky, this.fog, UI.row(this.lightmap00, this.lightmap10));
        this.add(UI.label(IKey.lazy("Day cycle and yaw")).marginTop(8), this.dayCycle, this.dayYaw);
        this.add(
            UI.label(IKey.lazy("Skybox colors")).marginTop(8),
            UI.row(this.skySunrise1, this.skySunrise2, this.skySunrise3).tooltip(IKey.lazy("Sunrise"), Direction.LEFT),
            UI.row(this.skyNoon1, this.skyNoon2, this.skyNoon3).tooltip(IKey.lazy("Noon"), Direction.LEFT),
            UI.row(this.skySunset1, this.skySunset2, this.skySunset3).tooltip(IKey.lazy("Sunset"), Direction.LEFT),
            UI.row(this.skyMidnight1, this.skyMidnight2, this.skyMidnight3).tooltip(IKey.lazy("Midnight"), Direction.LEFT)
        );
        this.add(UI.label(UIKeys.WORLD_SETTINGS_SHADING).marginTop(8), this.shadingDirection);

        this.column().vertical().stretch();
    }

    private int getColor(Matrix3f skySunrise, int row)
    {
        Vector3f vector = skySunrise.getColumn(row, new Vector3f());
        Color color = new Color(vector.x, vector.y, vector.z);

        return color.getRGBColor();
    }

    private void setColor(int c, Matrix3f matrix, int row)
    {
        Color color = new Color().set(c, false);

        matrix.setColumn(row, new Vector3f(color.r, color.g, color.b));
    }
}