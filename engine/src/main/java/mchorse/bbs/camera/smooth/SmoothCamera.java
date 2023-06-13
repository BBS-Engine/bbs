package mchorse.bbs.camera.smooth;

import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.utils.math.Interpolations;

/**
 * Smooth camera
 *
 * This class is responsible for doing cool shit!
 */
public class SmoothCamera
{
    public ValueBoolean enabled;

    public float yaw;
    public float pitch;

    public MouseFilter x = new MouseFilter();
    public MouseFilter y = new MouseFilter();

    public float accX;
    public float accY;

    public ValueFloat fricX;
    public ValueFloat fricY;

    public void update(float dx, float dy)
    {
        this.accX += dx / 10.0F;
        this.accY += dy / 10.0F;

        this.accX *= this.fricX.get();
        this.accY *= this.fricY.get();

        this.yaw += this.accX;
        this.pitch += this.accY;

        this.x.update(this.yaw);
        this.y.update(this.pitch);
    }

    public void set(float yaw, float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;

        this.accX = this.accY = 0.0F;

        this.x.set(yaw);
        this.y.set(pitch);
    }

    /**
     * Get interpolated yaw
     */
    public float getInterpYaw(float ticks)
    {
        return Interpolations.cubic(this.x.a, this.x.b, this.x.c, this.x.d, ticks);
    }

    /**
     * Get interpolated pitch
     */
    public float getInterpPitch(float ticks)
    {
        return Interpolations.cubic(this.y.a, this.y.b, this.y.c, this.y.d, ticks);
    }

    public class MouseFilter
    {
        public float a;
        public float b;
        public float c;
        public float d;

        /**
         * Update variables to simulate cubic acceleration
         */
        public void update(float x)
        {
            float a = this.a;

            this.a = x - (x - a) * 0.975F;
            this.b = x - (x - a) * 0.95F;
            this.c = x - (x - a) * 0.90F;
            this.d = x - (x - a) * 0.875F;
        }

        /**
         * Set all values to given float
         */
        public void set(float x)
        {
            this.a = this.b = this.c = this.d = x;
        }
    }
}