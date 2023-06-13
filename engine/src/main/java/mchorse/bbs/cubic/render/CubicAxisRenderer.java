package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class CubicAxisRenderer implements ICubicRenderer
{
    private Vector4f vector = new Vector4f();

    @Override
    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        stack.push();
        stack.translate(group.initial.translate.x / 16, group.initial.translate.y / 16, group.initial.translate.z / 16);

        Matrix4f matrix = stack.getModelMatrix();
        float f = 0.1F;

        matrix.transform(this.vector.set(0, 0, 0, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(1, 0, 0, 1);

        matrix.transform(this.vector.set(f, 0, 0, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(1, 0, 0, 1);

        matrix.transform(this.vector.set(0, 0, 0, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, 1);

        matrix.transform(this.vector.set(0, f, 0, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 1, 0, 1);

        matrix.transform(this.vector.set(0, 0, 0, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 0, 1, 1);

        matrix.transform(this.vector.set(0, 0, f, 1));
        builder.xyz(this.vector.x, this.vector.y, this.vector.z).rgba(0, 0, 1, 1);

        stack.pop();

        return false;
    }
}