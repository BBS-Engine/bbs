package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3f;

public interface ICubicRenderer
{
    public static void translateGroup(MatrixStack stack, ModelGroup group)
    {
        Vector3f translate = group.current.translate;
        Vector3f pivot = group.initial.translate;

        stack.translate(-(translate.x - pivot.x) / 16F, (translate.y - pivot.y) / 16F, (translate.z - pivot.z) / 16F);
    }

    public static void moveToGroupPivot(MatrixStack stack, ModelGroup group)
    {
        Vector3f pivot = group.initial.translate;

        stack.translate(pivot.x / 16F, pivot.y / 16F, pivot.z / 16F);
    }

    public static void rotateGroup(MatrixStack stack, ModelGroup group)
    {
        if (group.current.rotate.z != 0F) stack.rotateZ(MathUtils.toRad(group.current.rotate.z));
        if (group.current.rotate.y != 0F) stack.rotateY(MathUtils.toRad(group.current.rotate.y));
        if (group.current.rotate.x != 0F) stack.rotateX(MathUtils.toRad(group.current.rotate.x));

        if (group.current.rotate2.z != 0F) stack.rotateZ(MathUtils.toRad(group.current.rotate2.z));
        if (group.current.rotate2.y != 0F) stack.rotateY(MathUtils.toRad(group.current.rotate2.y));
        if (group.current.rotate2.x != 0F) stack.rotateX(MathUtils.toRad(group.current.rotate2.x));
    }

    public static void scaleGroup(MatrixStack stack, ModelGroup group)
    {
        stack.scale(group.current.scale.x, group.current.scale.y, group.current.scale.z);
    }

    public static void moveBackFromGroupPivot(MatrixStack stack, ModelGroup group)
    {
        Vector3f pivot = group.initial.translate;

        stack.translate(-pivot.x / 16F, -pivot.y / 16F, -pivot.z / 16F);
    }

    public default void applyGroupTransformations(MatrixStack stack, ModelGroup group)
    {
        translateGroup(stack, group);
        moveToGroupPivot(stack, group);
        rotateGroup(stack, group);
        scaleGroup(stack, group);
        moveBackFromGroupPivot(stack, group);
    }

    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model);
}