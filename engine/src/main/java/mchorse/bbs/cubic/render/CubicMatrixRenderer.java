package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class CubicMatrixRenderer implements ICubicRenderer
{
    public List<Matrix4f> matrices;

    public CubicMatrixRenderer(Model model)
    {
        this.matrices = new ArrayList<Matrix4f>();

        for (int i = 0; i < model.getAllGroupKeys().size(); i++)
        {
            this.matrices.add(new Matrix4f());
        }
    }

    @Override
    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        this.matrices.get(group.index).set(stack.getModelMatrix());

        return false;
    }
}