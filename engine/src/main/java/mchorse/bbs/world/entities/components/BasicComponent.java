package mchorse.bbs.world.entities.components;

import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class BasicComponent extends Component
{
    /* Position and orientation */
    public Vector3d position = new Vector3d();
    public Vector3d prevPosition = new Vector3d();

    public Vector3f rotation = new Vector3f();
    public Vector3f prevRotation = new Vector3f();

    public Vector3f velocity = new Vector3f();
    public float speed = 0.5F;

    /* Hitbox */
    public AABB hitbox = new AABB(0, 0, 0, 0.5F, 1.8F, 0.5F);
    public float hitboxWidth = 0.5F;
    public float hitboxHeight = 1.8F;
    public float sneakMultiplier = 0.9F;
    public float eyeHeight = 0.9F;

    public int hitTimer;

    public String name = "";

    /* Entity states */
    public float fall;
    public boolean grounded;
    public boolean sneak;
    public int ticks;
    public boolean manualControl;

    public float prevPrevRotationZ;

    public void setPosition(double x, double y, double z)
    {
        this.position.set(x, y, z);
        this.hitbox.setPosition(x - this.hitbox.w / 2, y, z - this.hitbox.d / 2);
    }

    public void setRotation(float pitch, float yaw)
    {
        this.rotation.x = pitch;
        this.rotation.y = yaw;
    }

    public Vector3f getLook()
    {
        return Matrices.rotation(this.rotation.x, MathUtils.PI - this.rotation.y);
    }

    /* Hitbox size */

    public void setHitboxSize(float width, float height)
    {
        this.hitboxWidth = width;
        this.hitboxHeight = height;

        this.hitbox.w = this.hitbox.d = this.hitboxWidth;
        this.hitbox.h = this.hitboxWidth;
    }

    public float getHeight()
    {
        return this.hitboxHeight * (this.sneak ? this.sneakMultiplier : 1F);
    }

    public float getEyeHeight()
    {
        return this.getHeight() * this.eyeHeight;
    }

    @Override
    public void preUpdate()
    {
        this.hitbox.w = this.hitbox.d = this.hitboxWidth;
        this.hitbox.h = this.getHeight();

        super.preUpdate();

        this.prevPrevRotationZ = this.prevRotation.z;
        this.prevPosition.set(this.position);
        this.prevRotation.set(this.rotation);

        this.updateBodyYaw();

        if (this.manualControl)
        {
            return;
        }

        if (!this.grounded)
        {
            this.velocity.y -= 0.06F;

            if (this.velocity.y < 0)
            {
                this.fall -= this.velocity.y;
            }
        }

        float slowdown = this.grounded ? 0.7F : 0.95F;

        if (this.velocity.x != 0)
        {
            if (Math.abs(this.velocity.x) < 0.001F) this.velocity.x = 0;
            else this.velocity.x *= slowdown;
        }

        if (this.velocity.z != 0)
        {
            if (Math.abs(this.velocity.z) < 0.001F) this.velocity.z = 0;
            else this.velocity.z *= slowdown;
        }
    }

    @Override
    public void postUpdate()
    {
        super.postUpdate();

        if (!this.manualControl)
        {
            this.position.add(this.velocity);
        }

        this.hitbox.setPosition(this.position.x - this.hitbox.w / 2, this.position.y, this.position.z - this.hitbox.d / 2);

        this.ticks++;
    }

    protected void updateBodyYaw()
    {
        Vector2f a = new Vector2f(this.velocity.x, this.velocity.z);
        float z = this.rotation.z;

        if (a.lengthSquared() > 0.001F)
        {
            float v = MathUtils.wrapToCircle(-a.normalize().angle(new Vector2f(0, -1)));
            float z2 = MathUtils.wrapToCircle(z);
            float v2 = v - z2 + z;

            if (Math.abs(v2 - z) > MathUtils.PI)
            {
                v2 -= Math.copySign(MathUtils.PI * 2, v2 - z);
            }

            this.rotation.z = Interpolations.lerp(this.rotation.z, v2, 0.5F);
        }
        else
        {
            this.rotation.z = Interpolations.lerp(z, this.rotation.y, 0.5F);
        }

        float bodyYawDiff = this.rotation.z - this.rotation.y;

        if (Math.abs(bodyYawDiff) >= MathUtils.PI / 3)
        {
            this.rotation.z = this.rotation.y + Math.copySign(MathUtils.PI / 3, bodyYawDiff);
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.putString("name", this.name);
        data.put("position", DataStorageUtils.vector3dToData(this.position));
        data.put("rotation", DataStorageUtils.vector3fToData(this.rotation));
        data.put("velocity", DataStorageUtils.vector3fToData(this.velocity));
        data.putFloat("speed", this.speed);
        data.putBool("sneak", this.sneak);
    }

    @Override
    public void fromData(MapType data)
    {
        this.name = data.getString("name");

        Vector3d position = DataStorageUtils.vector3dFromData(data.getList("position"));
        Vector3f rotation = DataStorageUtils.vector3fFromData(data.getList("rotation"));

        this.position.set(position);
        this.prevPosition.set(position);

        this.rotation.set(rotation);
        this.prevRotation.set(rotation);

        this.velocity.set(DataStorageUtils.vector3fFromData(data.getList("velocity")));
        this.speed = data.getFloat("speed", this.speed);

        this.sneak = data.getBool("sneak");
    }
}