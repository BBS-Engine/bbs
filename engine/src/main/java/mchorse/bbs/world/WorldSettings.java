package mchorse.bbs.world;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Color;
import org.joml.Matrix3f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WorldSettings implements IMapSerializable
{
    public boolean terrain = true;
    public boolean sky = true;
    public boolean fog = true;

    public Form skyForm;
    public Color lightmap00 = new Color(1F, 1F, 1F);
    public Color lightmap10 = new Color(1F, 0F, 0F);
    public final Vector3f shadingDirection = new Vector3f(0, -1, 0);
    public float dayCycle;
    public float dayYaw;

    public final Matrix3f skySunrise = new Matrix3f(0.710F, 0.188F, 0.122F, 0.980F, 0.588F, 0.078F, 0.553F, 0.788F, 0.969F);
    public final Matrix3f skyNoon = new Matrix3f(0.800F, 0.914F, 1.000F, 0.459F, 0.776F, 1.000F, 0.243F, 0.494F, 0.690F);
    public final Matrix3f skySunset = new Matrix3f(0.710F, 0.188F, 0.122F, 0.831F, 0.416F, 0.075F, 0.553F, 0.788F, 0.969F);
    public final Matrix3f skyMidnight = new Matrix3f(0.030F, 0.069F, 0.108F, 0.001F, 0.007F, 0.031F, 0.001F, 0.007F, 0.031F);

    public final Vector3d cameraPosition = new Vector3d(0.5, 0.5, 0.5);
    public final Vector3f cameraRotation = new Vector3f();

    @Override
    public void fromData(MapType data)
    {
        this.terrain = data.getBool("terrain", this.terrain);
        this.sky = data.getBool("sky", this.sky);
        this.fog = data.getBool("fog", this.fog);

        if (data.has("skyForm"))
        {
            this.skyForm = FormUtils.fromData(data.getMap("skyForm"));
        }

        this.lightmap00.set(data.getInt("lightmap00", this.lightmap00.getRGBColor()), false);
        this.lightmap10.set(data.getInt("lightmap10", this.lightmap10.getRGBColor()), false);
        this.shadingDirection.set(DataStorageUtils.vector3fFromData(data.getList("shadingDirection"), this.shadingDirection));
        this.dayCycle = data.getFloat("dayCycle");
        this.dayYaw = data.getFloat("dayYaw");

        this.skySunrise.set(DataStorageUtils.matrix3fFromData(data.getList("skySunrise"), this.skySunrise));
        this.skyNoon.set(DataStorageUtils.matrix3fFromData(data.getList("skyNoon"), this.skyNoon));
        this.skySunset.set(DataStorageUtils.matrix3fFromData(data.getList("skySunset"), this.skySunset));
        this.skyMidnight.set(DataStorageUtils.matrix3fFromData(data.getList("skyMidnight"), this.skyMidnight));

        this.cameraPosition.set(DataStorageUtils.vector3dFromData(data.getList("cameraPosition"), this.cameraPosition));
        this.cameraRotation.set(DataStorageUtils.vector3fFromData(data.getList("cameraRotation"), this.cameraRotation));
    }

    @Override
    public void toData(MapType data)
    {
        data.putBool("terrain", this.terrain);
        data.putBool("sky", this.sky);
        data.putBool("fog", this.fog);

        if (this.skyForm != null)
        {
            data.put("skyForm", FormUtils.toData(this.skyForm));
        }

        data.putInt("lightmap00", this.lightmap00.getRGBColor());
        data.putInt("lightmap10", this.lightmap10.getRGBColor());
        data.put("shadingDirection", DataStorageUtils.vector3fToData(this.shadingDirection));
        data.putFloat("dayCycle", this.dayCycle);
        data.putFloat("dayYaw", this.dayYaw);

        data.put("skySunrise", DataStorageUtils.matrix3fToData(this.skySunrise));
        data.put("skyNoon", DataStorageUtils.matrix3fToData(this.skyNoon));
        data.put("skySunset", DataStorageUtils.matrix3fToData(this.skySunset));
        data.put("skyMidnight", DataStorageUtils.matrix3fToData(this.skyMidnight));

        data.put("cameraPosition", DataStorageUtils.vector3dToData(this.cameraPosition));
        data.put("cameraRotation", DataStorageUtils.vector3fToData(this.cameraRotation));
    }
}