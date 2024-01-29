package mchorse.bbs.world.objects;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.pose.Transform;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.List;

public class PropObject extends WorldObject
{
    public Form form;
    public final Transform transform = new Transform();

    public boolean collidable = true;
    public final Vector3f hitbox = new Vector3f(1, 1, 1);

    private Entity entity;

    private void ensureEntity(World world)
    {
        if (this.entity == null)
        {
            this.entity = EntityArchitect.createDummy();
            this.entity.world = world;
        }
    }

    @Override
    public void addCollisionBoxes(List<AABB> boxes)
    {
        if (this.collidable)
        {
            boxes.add(this.getPickingHitbox());
        }
    }

    @Override
    public AABB getPickingHitbox()
    {
        AABB hitbox = super.getPickingHitbox();

        hitbox.x -= this.hitbox.x / 2;
        hitbox.z -= this.hitbox.z / 2;
        hitbox.w = this.hitbox.x;
        hitbox.h = this.hitbox.y;
        hitbox.d = this.hitbox.z;

        return hitbox;
    }

    @Override
    public void update(World world)
    {
        if (this.form == null)
        {
            return;
        }

        this.ensureEntity(world);
        this.form.update(this.entity);
    }

    @Override
    public void render(RenderingContext context)
    {
        super.render(context);

        if (this.form == null)
        {
            return;
        }

        this.ensureEntity(context.getWorld());

        Vector3d position = this.position;
        Vector2f lighting = this.entity.world.getLighting(position.x, position.y + this.hitbox.y / 2, position.z);

        for (Shader shader : context.getShaders().getAll())
        {
            CommonShaderAccess.setLightMapCoords(shader, lighting.x, lighting.y);
        }

        context.stack.push();
        context.stack.translateRelative(context.getCamera(), this.position);
        context.stack.multiply(this.transform.createMatrix());

        this.form.getRenderer().render(this.entity, context);
        context.stack.pop();
    }

    @Override
    public String toString()
    {
        return super.toString() + (this.form == null ? "" : " " + this.form.getId());
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        if (this.form != null)
        {
            data.put("form", FormUtils.toData(this.form));
        }

        data.put("transform", this.transform.toData());
        data.putBool("collidable", this.collidable);
        data.put("hitbox", DataStorageUtils.vector3fToData(this.hitbox));
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("form"))
        {
            this.form = FormUtils.fromData(data.getMap("form"));
        }

        this.transform.fromData(data.getMap("transform"));
        this.collidable = data.getBool("collidable", true);
        this.hitbox.set(DataStorageUtils.vector3fFromData(data.getList("hitbox"), new Vector3f(1, 1, 1)));
    }
}