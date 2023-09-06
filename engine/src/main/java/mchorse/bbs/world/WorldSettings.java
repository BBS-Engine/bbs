package mchorse.bbs.world;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.LinkUtils;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WorldSettings implements IMapSerializable
{
    public static final Link DEFAULT_LIGHTMAP = Link.assets("textures/lightmap.png");

    public boolean sky = true;
    public boolean fog = true;

    public Form skyForm;
    public Color zenith = new Color(50F / 255F, 88F / 255F, 187F / 255F, 1F);
    public Color horizon = new Color(187F / 255F, 237F / 255F, 255F / 255F, 1F);
    public Color bottom = new Color(69F / 255F, 61F / 255F, 120F / 255F, 1F);
    public final Vector3f shadingDirection = new Vector3f(0, -1, 0);
    public Link lightmap = DEFAULT_LIGHTMAP;
    public float dayCycle;
    public float dayYaw;

    public final Vector3d cameraPosition = new Vector3d(0.5, 0.5, 0.5);
    public final Vector3f cameraRotation = new Vector3f();

    @Override
    public void fromData(MapType data)
    {
        this.sky = data.getBool("sky", this.sky);
        this.fog = data.getBool("fog", this.fog);

        if (data.has("skyForm"))
        {
            this.skyForm = FormUtils.fromData(data.getMap("skyForm"));
        }

        this.zenith.set(data.getInt("zenith", this.zenith.getRGBColor()), false);
        this.horizon.set(data.getInt("horizon", this.horizon.getRGBColor()), false);
        this.bottom.set(data.getInt("bottom", this.bottom.getRGBColor()), false);
        this.shadingDirection.set(DataStorageUtils.vector3fFromData(data.getList("shadingDirection"), this.shadingDirection));
        this.lightmap = LinkUtils.create(data.get("lightmap"));
        this.dayCycle = data.getFloat("dayCycle");
        this.dayYaw = data.getFloat("dayYaw");

        if (this.lightmap == null)
        {
            this.lightmap = DEFAULT_LIGHTMAP;
        }

        this.cameraPosition.set(DataStorageUtils.vector3dFromData(data.getList("cameraPosition"), this.cameraPosition));
        this.cameraRotation.set(DataStorageUtils.vector3fFromData(data.getList("cameraRotation"), this.cameraRotation));
    }

    @Override
    public void toData(MapType data)
    {
        data.putBool("sky", this.sky);
        data.putBool("fog", this.fog);

        if (this.skyForm != null)
        {
            data.put("skyForm", FormUtils.toData(this.skyForm));
        }

        data.putInt("zenith", this.zenith.getRGBColor());
        data.putInt("horizon", this.horizon.getRGBColor());
        data.putInt("bottom", this.bottom.getRGBColor());
        data.put("shadingDirection", DataStorageUtils.vector3fToData(this.shadingDirection));
        data.put("lightmap", LinkUtils.toData(this.lightmap == null ? DEFAULT_LIGHTMAP : this.lightmap));
        data.putFloat("dayCycle", this.dayCycle);
        data.putFloat("dayYaw", this.dayYaw);

        data.put("cameraPosition", DataStorageUtils.vector3dToData(this.cameraPosition));
        data.put("cameraRotation", DataStorageUtils.vector3fToData(this.cameraRotation));
    }
}