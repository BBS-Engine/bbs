package mchorse.bbs.world;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Color;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class WorldSettings implements IMapSerializable
{
    public boolean sky = true;
    public boolean fog = true;

    public Form skyForm;
    public Color lightmap00 = new Color(1F, 1F, 1F);
    public Color lightmap10 = new Color(1F, 0F, 0F);
    public final Vector3f shadingDirection = new Vector3f(0, -1, 0);
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

        this.lightmap00.set(data.getInt("lightmap00", this.lightmap00.getRGBColor()), false);
        this.lightmap10.set(data.getInt("lightmap10", this.lightmap10.getRGBColor()), false);
        this.shadingDirection.set(DataStorageUtils.vector3fFromData(data.getList("shadingDirection"), this.shadingDirection));
        this.dayCycle = data.getFloat("dayCycle");
        this.dayYaw = data.getFloat("dayYaw");

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

        data.putInt("lightmap00", this.lightmap00.getRGBColor());
        data.putInt("lightmap10", this.lightmap10.getRGBColor());
        data.put("shadingDirection", DataStorageUtils.vector3fToData(this.shadingDirection));
        data.putFloat("dayCycle", this.dayCycle);
        data.putFloat("dayYaw", this.dayYaw);

        data.put("cameraPosition", DataStorageUtils.vector3dToData(this.cameraPosition));
        data.put("cameraRotation", DataStorageUtils.vector3fToData(this.cameraRotation));
    }
}