package mchorse.bbs.game.huds;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

public class HUDForm implements IMapSerializable
{
    public Form form;
    public boolean ortho;
    public float orthoX;
    public float orthoY;
    public int expire;
    public Transform transform = new Transform();

    private Entity entity;

    private int tick;

    public Entity getEntity()
    {
        if (this.entity == null)
        {
            this.entity = EntityArchitect.createDummy();
            this.entity.basic.grounded = true;
        }

        return this.entity;
    }

    public void render(RenderingContext context, int w, int h)
    {
        if (this.form == null)
        {
            return;
        }

        float tx = this.transform.translate.x;
        float ty = this.transform.translate.y;
        float tz = this.transform.translate.z;
        float sx = this.transform.scale.x;
        float sy = this.transform.scale.y;
        float sz = this.transform.scale.z;
        float rx = this.transform.rotate.x;
        float ry = this.transform.rotate.y;
        float rz = this.transform.rotate.z;

        if (this.ortho)
        {
            tx = w * this.orthoX + tx;
            ty = h * this.orthoY + ty;
        }

        context.stack.push();
        context.stack.translate(tx, ty, tz);
        context.stack.rotateZ(rz);
        context.stack.rotateY(ry);
        context.stack.rotateX(rx);
        context.stack.scale(sx, sy, sz);

        this.form.getRenderer().render(this.getEntity(), context);

        context.stack.pop();
    }

    public boolean update(boolean allowExpiring)
    {
        Entity entity = this.getEntity();

        if (this.form != null)
        {
            this.form.update(entity);
        }

        entity.basic.ticks += 1;
        this.tick += 1;

        if (!allowExpiring)
        {
            return false;
        }

        return this.expire > 0 && this.tick >= this.expire;
    }

    @Override
    public void toData(MapType data)
    {
        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.putBool("ortho", this.ortho);
        data.putFloat("orthoX", this.orthoX);
        data.putFloat("orthoY", this.orthoY);
        data.putInt("expire", this.expire);

        data.put("transform", this.transform.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        this.ortho = data.getBool("ortho");
        this.orthoX = data.getFloat("orthoX");
        this.orthoY = data.getFloat("orthoY");
        this.expire = data.getInt("expire");

        this.transform.fromData(data.getMap("transform"));
    }
}