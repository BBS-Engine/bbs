package mchorse.bbs.utils;

import mchorse.bbs.data.IDataSerializable;
import mchorse.bbs.data.types.ListType;
import org.joml.Intersectiond;
import org.joml.RayAabIntersection;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * Axis aligned bounding box class
 * 
 * This class, right here, represents a 3D box in the space.
 */
public class AABB implements IDataSerializable<ListType>
{
    private static final Vector2d farNear = new Vector2d();

    /* Minimum (position) */
    public double x;
    public double y;
    public double z;

    /* Dimensions */
    public double w;
    public double h;
    public double d;

    public static AABB fromTwoPoints(Vector3d a, Vector3d b)
    {
        return fromTwoPoints(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public static AABB fromTwoPoints(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        return new AABB().setFromTwoPoints(x1, y1, z1, x2, y2, z2);
    }

    public AABB()
    {}

    public AABB(double x, double y, double z, double w, double h, double d)
    {
        this.setPosition(x, y, z).setSize(w, h, d);
    }

    public double maxX()
    {
        return this.x + this.w;
    }

    public double maxY()
    {
        return this.y + this.h;
    }

    public double maxZ()
    {
        return this.z + this.d;
    }

    public AABB copy()
    {
        return new AABB(this.x, this.y, this.z, this.w, this.h, this.d);
    }

    public AABB set(AABB aabb)
    {
        this.x = aabb.x;
        this.y = aabb.y;
        this.z = aabb.z;
        this.w = aabb.w;
        this.h = aabb.h;
        this.d = aabb.d;

        return this;
    }

    public AABB setPosition(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public AABB setSize(double w, double h, double d)
    {
        this.w = w;
        this.h = h;
        this.d = d;

        return this;
    }

    public AABB setFromTwoPoints(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);

        this.setPosition(minX, minY, minZ);

        return this.setSize(Math.max(x1, x2) - minX, Math.max(y1, y2) - minY, Math.max(z1, z2) - minZ);
    }

    public boolean contains(Vector3i vector)
    {
        return vector.x >= this.x && vector.x <= this.x + this.w && vector.y >= this.y && vector.y <= this.y + this.h && vector.z >= this.z && vector.z <= this.z + this.d;
    }

    public boolean contains(Vector3f vector)
    {
        return vector.x >= this.x && vector.x <= this.x + this.w && vector.y >= this.y && vector.y <= this.y + this.h && vector.z >= this.z && vector.z <= this.z + this.d;
    }

    public boolean contains(Vector3d vector)
    {
        return vector.x >= this.x && vector.x <= this.x + this.w && vector.y >= this.y && vector.y <= this.y + this.h && vector.z >= this.z && vector.z <= this.z + this.d;
    }

    public boolean intersectsRay(RayAabIntersection intersection)
    {
        return intersection.test((float) this.x, (float) this.y, (float) this.z, (float) (this.x + this.w), (float) (this.y + this.h), (float) (this.z + this.d));
    }

    public boolean intersectsRay(Vector3d origin, Vector3f direction)
    {
        return this.intersectsRay(origin, direction, new Vector2d());
    }

    public boolean intersectsRay(Vector3d origin, Vector3f direction, Vector2d farNear)
    {
        return Intersectiond.intersectRayAab(origin.x, origin.y, origin.z, direction.x, direction.y, direction.z, this.x, this.y, this.z, this.maxX(), this.maxY(), this.maxZ(), farNear);
    }

    public boolean intersectsRayHitNormal(Vector3d origin, Vector3f direction, Vector3d hit, Vector3i normal)
    {
        if (!this.intersectsRay(origin, direction, farNear.set(0, 0)))
        {
            return false;
        }

        hit.set(direction.x, direction.y, direction.z).mul(farNear.x).add(origin);

        final double e = 0.0001;

        normal.set(0, 0, 0);

        if (Math.abs(hit.x - this.x) < e || Math.abs(hit.x - (this.x + this.w)) < e)
        {
            normal.x = direction.x > 0 ? -1 : 1;
        }
        else if (Math.abs(hit.y - this.y) < e || Math.abs(hit.y - (this.y + this.h)) < e)
        {
            normal.y = direction.y > 0 ? -1 : 1;
        }
        else
        {
            normal.z = direction.z > 0 ? -1 : 1;
        }

        return true;
    }

    public boolean intersects(AABB box)
    {
        return this.intersects(box.x, box.y, box.z, box.w, box.h, box.d);
    }

    public boolean intersects(double x, double y, double z, double w, double h, double d)
    {
        return this.x < x + w && this.x + this.w > x && this.y < y + h && this.y + this.h > y && this.z < z + d && this.z + this.d > z;
    }

    public AABB intersection(AABB box)
    {
        return this.intersection(box, new AABB());
    }

    public AABB intersection(AABB box, AABB result)
    {
        if (!this.intersects(box))
        {
            return null;
        }

        double minX = Math.max(this.x, box.d);
        double minY = Math.max(this.y, box.y);
        double minZ = Math.max(this.z, box.z);

        double w = minX - (this.x + this.w);
        double h = minY - (this.y + this.h);
        double d = minZ - (this.z + this.d);

        result.setPosition(minX, minY, minZ);
        result.setSize(w, h, d);

        return result;
    }

    public AABB expand(double x, double y, double z)
    {
        if (x >= 0)
        {
            this.w += x;
        }
        else
        {
            this.x += x;
            this.w -= x;
        }

        if (y >= 0)
        {
            this.h += y;
        }
        else
        {
            this.y += y;
            this.h -= y;
        }

        if (z >= 0)
        {
            this.d += z;
        }
        else
        {
            this.z += z;
            this.d -= z;
        }

        return this;
    }

    public AABB inflate(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w += x * 2;
        this.h += y * 2;
        this.d += z * 2;

        return this;
    }

    public AABB offset(double x, double y, double z)
    {
        return this.setPosition(this.x + x, this.y + y, this.z + z);
    }

    public double calculateOffset(Axis axis, AABB other, double offset)
    {
        if (!this.intersects(other))
        {
            double thisOffset = this.getOffset(axis);
            double thisSide = this.getSide(axis);
            double otherOffset = other.getOffset(axis);
            double otherSide = other.getSide(axis);

            double thisCenter = thisOffset + thisSide / 2;
            double otherCenter = otherOffset + otherSide / 2;

            if (otherCenter < thisCenter)
            {
                double output = (otherOffset + otherSide) - thisOffset;

                return output > offset ? offset : output;
            }

            double output = otherOffset - (thisOffset + thisSide);

            return output < offset ? offset : output;
        }

        return offset;
    }

    public double getOffset(Axis axis)
    {
        return axis == Axis.X ? this.x : (axis == Axis.Y ? this.y : this.z);
    }

    public double getSide(Axis axis)
    {
        return axis == Axis.X ? this.w : (axis == Axis.Y ? this.h : this.d);
    }

    @Override
    public ListType toData()
    {
        ListType list = new ListType();

        list.addDouble(this.x);
        list.addDouble(this.y);
        list.addDouble(this.z);
        list.addDouble(this.w);
        list.addDouble(this.h);
        list.addDouble(this.d);

        return list;
    }

    @Override
    public void fromData(ListType data)
    {
        if (data.size() < 6)
        {
            return;
        }

        this.setPosition(data.getDouble(0), data.getDouble(1), data.getDouble(2));
        this.setSize(data.getDouble(3), data.getDouble(4), data.getDouble(5));
    }
}