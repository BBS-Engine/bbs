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
        float z = group.current.rotate.z;
        float y = group.current.rotate.y;
        float x = group.current.rotate.x;

        if (z != 0F) stack.rotateZ(MathUtils.toRad(z));
        if (y != 0F) stack.rotateY(MathUtils.toRad(y));
        if (x != 0F) stack.rotateX(MathUtils.toRad(x));
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