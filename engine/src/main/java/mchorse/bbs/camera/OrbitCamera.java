package mchorse.bbs.camera;

import mchorse.bbs.camera.data.Position;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Matrix3f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;

/**
 * Orbit camera class.
 *
 * This generic orbit camera class can be used in multiple ways where orbit-like
 * camera control is needed.
 */
public class OrbitCamera
{
    public Vector3d position = new Vector3d();
    public Vector3f rotation = new Vector3f();
    public float distance;
    public float fov;

    protected int dragging = -1;
    protected int lastX;
    protected int lastY;

    protected float low = 0.05F;
    protected float normal = 0.25F;
    protected float high = 1F;

    protected Vector3d finalPosition = new Vector3d();

    protected Vector3i velocityPosition = new Vector3i();
    protected Vector4i velocityAngle = new Vector4i();

    public void reset()
    {
        this.velocityPosition.set(0);
        this.velocityAngle.set(0);
    }

    public void setup(Camera camera)
    {
        this.position.set(camera.position);
        this.rotation.set(camera.rotation);
        this.fov = camera.fov;

        this.distance = 0;
    }

    public Vector3i getVelocityPosition()
    {
        return this.velocityPosition;
    }

    public Vector4i getVelocityAngle()
    {
        return this.velocityAngle;
    }

    public boolean canStart(UIContext context)
    {
        if (context.mouseButton == 2)
        {
            return true;
        }

        return context.mouseButton == 0 && Window.isKeyPressed(GLFW.GLFW_KEY_SPACE);
    }

    public void from(Position position)
    {
        this.reset();

        this.position.set(position.point.x, position.point.y, position.point.z);
        this.rotation.set(MathUtils.toRad(position.angle.pitch), MathUtils.toRad(position.angle.yaw), MathUtils.toRad(position.angle.roll));
        this.fov = MathUtils.toRad(position.angle.fov);

        Vector3d vector = new Vector3d(this.getFinalPosition()).sub(this.position);

        this.position.sub(vector);

        vector = this.getFinalPosition();
        position.point.set(vector.x, vector.y, vector.z);
    }

    public void apply(Position position)
    {
        Vector3d vector = this.getFinalPosition();

        position.point.set(vector.x, vector.y, vector.z);
        position.angle.set(MathUtils.toDeg(this.rotation.y), MathUtils.toDeg(this.rotation.x), MathUtils.toDeg(this.rotation.z), MathUtils.toDeg(this.fov));
    }

    public void cache(int mouseX, int mouseY)
    {
        this.lastX = mouseX;
        this.lastY = mouseY;
    }

    public void start(int mouseX, int mouseY)
    {
        this.start(0, mouseX, mouseY);
    }

    public void start(int mouseButton, int mouseX, int mouseY)
    {
        this.dragging = mouseButton;
        this.lastX = mouseX;
        this.lastY = mouseY;
    }

    public void release()
    {
        this.dragging = -1;
    }

    public boolean isDragging()
    {
        return this.dragging >= 0;
    }

    /**
     * Calculate the final position.
     */
    public Vector3d getFinalPosition()
    {
        return this.finalPosition.set(this.position).add(this.rotateVector(0, 0, -1).mul(this.distance));
    }

    /**
     * Get looking direction vector.
     */
    public Vector3f getLook()
    {
        return Matrices.rotation(this.rotation.x, MathUtils.PI - this.rotation.y);
    }

    protected float getAngleSpeed()
    {
        return 1 / 180F;
    }

    protected float getSpeed()
    {
        return Window.isCtrlPressed() ? this.high : (Window.isAltPressed() ? this.low : this.normal);
    }

    protected Vector3f rotateVector(float x, float y, float z)
    {
        Matrix3f rotation = new Matrix3f();
        Vector3f rotate = new Vector3f(x, y, z);

        rotation.rotateY(MathUtils.PI - this.rotation.y);
        rotation.rotateX(this.rotation.x);
        rotation.transform(rotate);

        return rotate;
    }

    /**
     * Drag the mouse Should be called in rendering.
     */
    public boolean drag(int mouseX, int mouseY)
    {
        if (this.dragging == 0)
        {
            int x = mouseX - this.lastX;
            int y = mouseY - this.lastY;

            if (x != 0 || y != 0)
            {
                float angleFactor = this.getAngleSpeed();

                this.rotation.x += y * angleFactor;
                this.rotation.y += x * angleFactor;

                this.lastX = mouseX;
                this.lastY = mouseY;
            }

            return true;
        }

        return false;
    }

    public boolean scroll(int scroll)
    {
        float distance = MathUtils.clamp(this.distance - Math.copySign(1F, scroll), 0, 100);
        boolean hasChanged = this.distance != distance;

        if (hasChanged)
        {
            this.distance = distance;
        }

        return hasChanged;
    }

    public boolean keyPressed(UIContext context)
    {
        int x = this.getFactor(context, Keys.FLIGHT_LEFT, Keys.FLIGHT_RIGHT, this.velocityPosition.x);
        int y = this.getFactor(context, Keys.FLIGHT_UP, Keys.FLIGHT_DOWN, this.velocityPosition.y);
        int z = this.getFactor(context, Keys.FLIGHT_FORWARD, Keys.FLIGHT_BACKWARD, this.velocityPosition.z);
        boolean same = this.velocityPosition.x == x && this.velocityPosition.y == y && this.velocityPosition.z == z;

        int pitch = this.getFactor(context, Keys.FLIGHT_TILT_UP, Keys.FLIGHT_TILT_DOWN, this.velocityAngle.x);
        int yaw = this.getFactor(context, Keys.FLIGHT_PAN_LEFT, Keys.FLIGHT_PAN_RIGHT, this.velocityAngle.y);
        same = same && this.velocityAngle.x == pitch && this.velocityAngle.y == yaw;

        this.velocityPosition.set(x, y, z);
        this.velocityAngle.x = pitch;
        this.velocityAngle.y = yaw;

        return !same;
    }

    protected int getFactor(UIContext context, KeyCombo positive, KeyCombo negative, int x)
    {
        if (context.isPressed(positive.getMainKey()))
        {
            x = 1;
        }
        else if (context.isPressed(negative.getMainKey()))
        {
            x = -1;
        }
        else if (
            (context.isReleased(positive.getMainKey()) && x > 0) ||
            (context.isReleased(negative.getMainKey()) && x < 0)
        ) {
            x = 0;
        }

        return x;
    }

    protected int getFactor(UIContext context, int positive, int negative, int x)
    {
        if (context.isPressed(positive))
        {
            x = 1;
        }
        else if (context.isPressed(negative))
        {
            x = -1;
        }
        else if ((context.isReleased(positive) && x > 0) || (context.isReleased(negative) && x < 0))
        {
            x = 0;
        }

        return x;
    }

    public boolean update(UIContext context)
    {
        if (context.isFocused())
        {
            return false;
        }

        boolean changed = false;

        if (this.velocityPosition.lengthSquared() > 0)
        {
            this.position.add(this.rotateVector(this.velocityPosition.x, 0, this.velocityPosition.z).add(0, this.velocityPosition.y, 0).mul(this.getSpeed()));

            changed = true;
        }

        if (this.velocityAngle.lengthSquared() > 0)
        {
            float angleSpeed = -this.getAngleSpeed() * (this.getSpeed() * (1F / this.normal));

            this.rotation.x += this.velocityAngle.x * angleSpeed;
            this.rotation.y += this.velocityAngle.y * angleSpeed;

            changed = true;
        }

        return changed;
    }
}