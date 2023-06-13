package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;
import org.joml.Matrix4f;

/**
 * Post render processor
 *
 * This render processors is responsible for applying given group's transformation
 * onto OpenGL's matrix stack. Make sure you push before and pop after the matrix
 * stack when you using this!
 */
public class CubicPostRenderer implements ICubicRenderer
{
    private Matrix4f matrix = new Matrix4f();
    private String groupName = "";

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public void resetMatrix()
    {
        this.matrix = null;
    }

    public Matrix4f getMatrix()
    {
        return this.matrix;
    }

    @Override
    public boolean renderGroup(VAOBuilder builder, MatrixStack stack, ModelGroup group, Model model)
    {
        if (group.id.equals(this.groupName))
        {
            this.matrix = new Matrix4f(stack.getModelMatrix());

            return true;
        }

        return false;
    }
}