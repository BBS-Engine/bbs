package mchorse.bbs.utils;

import org.joml.RayAabIntersection;
import org.joml.Vector3d;
import org.joml.Vector3f;

/**
 * Axis aligned bounding box class (integer implementation)
 * 
 * This class, right here, represents a 3D box in the space.
 */
public class AABBi
{
    /* Minimum (position) */
    public int x;
    public int y;
    public int z;

    /* Dimensions */
    public int w;
    public int h;
    public int d;

    public static AABBi fromTwoPoints(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        return new AABBi().setFromTwoPoints(x1, y1, z1, x2, y2, z2);
    }

    public AABBi()
    {}

    public AABBi(int x, int y, int z, int w, int h, int d)
    {
        this.setPosition(x, y, z).setSize(w, h, d);
    }

    public int maxX()
    {
        return this.x + this.w;
    }

    public int maxY()
    {
        return this.y + this.h;
    }

    public int maxZ()
    {
        return this.z + this.d;
    }

    public AABBi copy()
    {
        return new AABBi(this.x, this.y, this.z, this.w, this.h, this.d);
    }

    public AABBi set(AABBi aabb)
    {
        this.x = aabb.x;
        this.y = aabb.y;
        this.z = aabb.z;
        this.w = aabb.w;
        this.h = aabb.h;
        this.d = aabb.d;

        return this;
    }

    public AABBi setPosition(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;

        return this;
    }

    public AABBi setSize(int w, int h, int d)
    {
        this.w = w;
        this.h = h;
        this.d = d;

        return this;
    }

    public AABBi setFromTwoPoints(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);

        this.setPosition(minX, minY, minZ);

        return this.setSize(Math.max(x1, x2) - minX, Math.max(y1, y2) - minY, Math.max(z1, z2) - minZ);
    }

    public boolean contains(Vector3f vector)
    {
        return vector.x >= this.x && vector.x <= this.x + this.w && vector.y >= this.y && vector.y <= this.y + this.h && vector.z >= this.z && vector.z <= this.z + this.d;
    }

    public boolean contains(Vector3d vector)
    {
        return vector.x >= this.x && vector.x <= this.x + this.w && vector.y >= this.y && vector.y <= this.y + this.h && vector.z >= this.z && vector.z <= this.z + this.d;
    }

    public boolean intersects(RayAabIntersection intersection)
    {
        return intersection.test((float) this.x, (float) this.y, (float) this.z, (float) (this.x + this.w), (float) (this.y + this.h), (float) (this.z + this.d));
    }

    public boolean intersects(AABBi box)
    {
        return this.intersects(box.x, box.y, box.z, box.w, box.h, box.d);
    }

    public boolean intersects(int x, int y, int z, int w, int h, int d)
    {
        return this.x < x + w && this.x + this.w > x && this.y < y + h && this.y + this.h > y && this.z < z + d && this.z + this.d > z;
    }

    public AABBi intersection(AABBi box)
    {
        return this.intersection(box, new AABBi());
    }

    public AABBi intersection(AABBi box, AABBi result)
    {
        if (!this.intersects(box))
        {
            return null;
        }

        int minX = Math.max(this.x, box.d);
        int minY = Math.max(this.y, box.y);
        int minZ = Math.max(this.z, box.z);

        int w = minX - (this.x + this.w);
        int h = minY - (this.y + this.h);
        int d = minZ - (this.z + this.d);

        result.setPosition(minX, minY, minZ);
        result.setSize(w, h, d);

        return result;
    }

    public AABBi expand(double x, double y, double z)
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

    public AABBi inflate(double x, double y, double z)
    {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        this.w += x * 2;
        this.h += y * 2;
        this.d += z * 2;

        return this;
    }

    public AABBi offset(int x, int y, int z)
    {
        return this.setPosition(this.x + x, this.y + y, this.z + z);
    }

    public double calculateOffset(Axis axis, AABBi other, double offset)
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
}