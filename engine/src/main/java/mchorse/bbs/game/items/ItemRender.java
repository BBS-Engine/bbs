package mchorse.bbs.game.items;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector2i;

public class ItemRender implements IMapSerializable
{
    public Vector2i uv = new Vector2i();
    public Form form;
    public boolean extruded = true;
    public int frameColor = Colors.LIGHTER_GRAY;

    @Override
    public void toData(MapType data)
    {
        data.put("uv", DataStorageUtils.vector2iToData(this.uv));

        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.putBool("extruded", this.extruded);
        data.putInt("frameColor", this.frameColor);
    }

    @Override
    public void fromData(MapType data)
    {
        this.uv.set(DataStorageUtils.vector2iFromData(data.getList("uv"), this.uv));

        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        this.extruded = data.getBool("extruded", this.extruded);
        this.frameColor = data.getInt("frameColor", this.frameColor);
    }
}