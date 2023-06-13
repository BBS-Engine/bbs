package mchorse.bbs.cubic.render;

import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.cubic.data.model.ModelVertex;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.vao.VAOBuilder;

public class CubicVAORenderer extends CubicCubeRenderer
{
    private boolean normals;

    public CubicVAORenderer(boolean normals)
    {
        this.normals = normals;
    }

    @Override
    public void applyGroupTransformations(MatrixStack stack, ModelGroup group)
    {
        /* No scale or rotation, because otherwise it would get applied twice during rendering */
        ICubicRenderer.translateGroup(stack, group);
        ICubicRenderer.moveToGroupPivot(stack, group);
        ICubicRenderer.moveBackFromGroupPivot(stack, group);
    }

    @Override
    protected void writeVertex(VAOBuilder builder, MatrixStack stack, ModelGroup group, ModelVertex vertex)
    {
        this.vertex.set(vertex.vertex.x, vertex.vertex.y, vertex.vertex.z, 1);
        stack.getModelMatrix().transform(this.vertex);

        /* Vertex */
        builder.xyz(this.vertex.x, this.vertex.y, this.vertex.z);

        if (this.normals)
        {
            /* Normal */
            builder.normal(this.normal.x, this.normal.y, this.normal.z);
        }
        else
        {
            builder.normal(0F, 1F, 0F);
        }

        builder.uv(vertex.uv.x, vertex.uv.y)
            .rgba(this.r, this.g, this.b, this.a)
            /* Bone indices */
            .xyzw(group.index + 1, 0, 0, 0)
            /* Bone weights */
            .xyzw(1, 0, 0, 0);
    }
}