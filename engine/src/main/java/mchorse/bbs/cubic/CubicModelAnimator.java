package mchorse.bbs.cubic;

import mchorse.bbs.cubic.data.animation.Animation;
import mchorse.bbs.cubic.data.animation.AnimationChannel;
import mchorse.bbs.cubic.data.animation.AnimationPart;
import mchorse.bbs.cubic.data.animation.AnimationVector;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.world.entities.Entity;
import org.joml.Vector3d;

import java.util.List;

public class CubicModelAnimator
{
    private static Vector3d p = new Vector3d();
    private static Vector3d s = new Vector3d();
    private static Vector3d r = new Vector3d();

    public static void resetPose(Model model)
    {
        for (ModelGroup group : model.topGroups)
        {
            resetGroup(group);
        }
    }

    private static void resetGroup(ModelGroup group)
    {
        group.current.translate.set(group.initial.translate);
        group.current.scale.set(group.initial.scale);
        group.current.rotate.set(group.initial.rotate);

        for (ModelGroup childGroup : group.children)
        {
            resetGroup(childGroup);
        }
    }

    public static void animate(Entity target, Model model, Animation animation, float frame, float blend, boolean skipInitial)
    {
        for (ModelGroup group : model.topGroups)
        {
            animateGroup(group, animation, frame, blend, skipInitial);
        }
    }

    private static void animateGroup(ModelGroup group, Animation animation, float frame, float blend, boolean skipInitial)
    {
        boolean applied = false;

        AnimationPart part = animation.parts.get(group.id);

        if (part != null)
        {
            applyGroupAnimation(group, part, frame, blend);

            applied = true;
        }

        if (!applied && !skipInitial)
        {
            Transform initial = group.initial;
            Transform current = group.current;

            current.translate.x = Interpolations.lerp(current.translate.x, initial.translate.x, blend);
            current.translate.y = Interpolations.lerp(current.translate.y, initial.translate.y, blend);
            current.translate.z = Interpolations.lerp(current.translate.z, initial.translate.z, blend);

            current.scale.x = Interpolations.lerp(current.scale.x, initial.scale.x, blend);
            current.scale.y = Interpolations.lerp(current.scale.y, initial.scale.y, blend);
            current.scale.z = Interpolations.lerp(current.scale.z, initial.scale.z, blend);

            current.rotate.x = Interpolations.lerp(current.rotate.x, initial.rotate.x, blend);
            current.rotate.y = Interpolations.lerp(current.rotate.y, initial.rotate.y, blend);
            current.rotate.z = Interpolations.lerp(current.rotate.z, initial.rotate.z, blend);
        }

        for (ModelGroup childGroup : group.children)
        {
            animateGroup(childGroup, animation, frame, blend, skipInitial);
        }
    }

    private static void applyGroupAnimation(ModelGroup group, AnimationPart animation, float frame, float blend)
    {
        Vector3d position = interpolateList(p, animation.position, frame, MolangHelper.Component.POSITION);
        Vector3d scale = interpolateList(s, animation.scale, frame, MolangHelper.Component.SCALE);
        Vector3d rotation = interpolateList(r, animation.rotation, frame, MolangHelper.Component.ROTATION);

        Transform initial = group.initial;
        Transform current = group.current;

        current.translate.x = Interpolations.lerp(current.translate.x, (float) position.x + initial.translate.x, blend);
        current.translate.y = Interpolations.lerp(current.translate.y, (float) position.y + initial.translate.y, blend);
        current.translate.z = Interpolations.lerp(current.translate.z, (float) position.z + initial.translate.z, blend);

        current.scale.x = Interpolations.lerp(current.scale.x, (float) scale.x + initial.scale.x, blend);
        current.scale.y = Interpolations.lerp(current.scale.y, (float) scale.y + initial.scale.y, blend);
        current.scale.z = Interpolations.lerp(current.scale.z, (float) scale.z + initial.scale.z, blend);

        current.rotate.x = Interpolations.lerp(current.rotate.x, (float) rotation.x + initial.rotate.x, blend);
        current.rotate.y = Interpolations.lerp(current.rotate.y, (float) rotation.y + initial.rotate.y, blend);
        current.rotate.z = Interpolations.lerp(current.rotate.z, (float) rotation.z + initial.rotate.z, blend);
    }

    private static Vector3d interpolateList(Vector3d vector, AnimationChannel channel, float frame, MolangHelper.Component component)
    {
        return interpolate(vector, channel, frame, component);
    }

    private static Vector3d interpolate(Vector3d output, AnimationChannel channel, float frame, MolangHelper.Component component)
    {
        List<AnimationVector> keyframes = channel.keyframes;

        if (keyframes.isEmpty())
        {
            output.set(0, 0, 0);

            return output;
        }

        AnimationVector first = keyframes.get(0);

        if (frame < first.time * 20)
        {
            output.x = MolangHelper.getValue(first.getStart(Axis.X), component, Axis.X);
            output.y = MolangHelper.getValue(first.getStart(Axis.Y), component, Axis.Y);
            output.z = MolangHelper.getValue(first.getStart(Axis.Z), component, Axis.Z);

            return output;
        }

        double duration = 0;

        for (AnimationVector vector : keyframes)
        {
            double length = vector.getLengthInTicks();

            if (frame >= duration && frame < duration + length)
            {
                double factor = (frame - duration) / length;

                output.x = vector.interp.interpolate(vector, component, Axis.X, factor);
                output.y = vector.interp.interpolate(vector, component, Axis.Y, factor);
                output.z = vector.interp.interpolate(vector, component, Axis.Z, factor);

                return output;
            }

            duration += length;
        }

        AnimationVector last = keyframes.get(keyframes.size() - 1);

        output.x = MolangHelper.getValue(last.getStart(Axis.X), component, Axis.X);
        output.y = MolangHelper.getValue(last.getStart(Axis.Y), component, Axis.Y);
        output.z = MolangHelper.getValue(last.getStart(Axis.Z), component, Axis.Z);

        return output;
    }
}
