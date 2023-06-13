package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValueExpression;
import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.math.Variable;

/**
 * Math modifier
 * 
 * Probably the most complex modifier in Aperture. This modifier accepts 
 * a math expression (which supports basic operators, variables and 
 * functions) written by user, and calculates the value based on that 
 * expression.
 * 
 * This modifier provides all essential input variables for math 
 * expressions, such as: position, angle, progress, progress offset from 
 * fixture, current value and more!
 */
public class MathClip extends ComponentClip
{
    private static Position next = new Position();

    public MathBuilder builder = new MathBuilder();

    public Variable varTicks;
    public Variable varOffset;
    public Variable varTransition;
    public Variable varDuration;
    public Variable varProgress;
    public Variable varFactor;
    public Variable varVelocity;

    public Variable varValue;

    public Variable varX;
    public Variable varY;
    public Variable varZ;

    public Variable varYaw;
    public Variable varPitch;
    public Variable varRoll;
    public Variable varFov;

    public final ValueExpression expression = new ValueExpression("expression", this.builder);

    public MathClip()
    {
        super();

        this.register(this.expression);

        this.varTicks = new Variable("t", 0);
        this.varOffset = new Variable("o", 0);
        this.varTransition = new Variable("pt", 0);
        this.varDuration = new Variable("d", 0);
        this.varProgress = new Variable("p", 0);
        this.varFactor = new Variable("f", 0);
        this.varVelocity = new Variable("v", 0);

        this.varValue = new Variable("value", 0);

        this.varX = new Variable("x", 0);
        this.varY = new Variable("y", 0);
        this.varZ = new Variable("z", 0);

        this.varYaw = new Variable("yaw", 0);
        this.varPitch = new Variable("pitch", 0);
        this.varRoll = new Variable("roll", 0);
        this.varFov = new Variable("fov", 0);

        this.builder.register(this.varTicks);
        this.builder.register(this.varOffset);
        this.builder.register(this.varTransition);
        this.builder.register(this.varDuration);
        this.builder.register(this.varProgress);
        this.builder.register(this.varFactor);
        this.builder.register(this.varVelocity);

        this.builder.register(this.varValue);

        this.builder.register(this.varX);
        this.builder.register(this.varY);
        this.builder.register(this.varZ);

        this.builder.register(this.varYaw);
        this.builder.register(this.varPitch);
        this.builder.register(this.varRoll);
        this.builder.register(this.varFov);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        IExpression expression = this.expression.get();

        if (expression != null)
        {
            context.applyUnderneath(context.ticks + 1, context.transition, next);

            int duration = this.duration.get();
            double dx = next.point.x - position.point.x;
            double dy = next.point.y - position.point.y;
            double dz = next.point.z - position.point.z;
            double velocity = Math.sqrt(dx * dx + dy * dy + dz * dz);

            this.varVelocity.set(velocity);

            this.varTicks.set(context.ticks);
            this.varOffset.set(context.relativeTick);
            this.varTransition.set(context.transition);
            this.varDuration.set(duration);
            this.varProgress.set(context.relativeTick + context.transition);
            this.varFactor.set((double) (context.relativeTick + context.transition) / duration);

            this.varX.set(position.point.x);
            this.varY.set(position.point.y);
            this.varZ.set(position.point.z);

            this.varYaw.set(position.angle.yaw);
            this.varPitch.set(position.angle.pitch);
            this.varRoll.set(position.angle.roll);
            this.varFov.set(position.angle.fov);

            if (this.isActive(0))
            {
                this.varValue.set(position.point.x);
                position.point.x = expression.get().doubleValue();
            }

            if (this.isActive(1))
            {
                this.varValue.set(position.point.y);
                position.point.y = expression.get().doubleValue();
            }

            if (this.isActive(2))
            {
                this.varValue.set(position.point.z);
                position.point.z = expression.get().doubleValue();
            }

            if (this.isActive(3))
            {
                this.varValue.set(position.angle.yaw);
                position.angle.yaw = (float) expression.get().doubleValue();
            }

            if (this.isActive(4))
            {
                this.varValue.set(position.angle.pitch);
                position.angle.pitch = (float) expression.get().doubleValue();
            }

            if (this.isActive(5))
            {
                this.varValue.set(position.angle.roll);
                position.angle.roll = (float) expression.get().doubleValue();
            }

            if (this.isActive(6))
            {
                this.varValue.set(position.angle.fov);
                position.angle.fov = (float) expression.get().doubleValue();
            }
        }
    }

    @Override
    public Clip create()
    {
        return new MathClip();
    }
}