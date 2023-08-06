package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.cubic.data.model.ModelCube;
import mchorse.bbs.cubic.data.model.ModelQuad;
import mchorse.bbs.cubic.data.model.ModelVertex;
import mchorse.bbs.cubic.render.CubicCubeRenderer;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.tilesets.geometry.CombinedGeometry;
import mchorse.bbs.voxel.tilesets.geometry.QuadGeometry;
import mchorse.bbs.voxel.tilesets.models.BlockModel;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class BlockModelCombined extends BlockModelFactory
{
    private static Vector4f vector = new Vector4f();

    public List<ModelCube> cubes = new ArrayList<>();
    public Vector3f offset = new Vector3f(0.5F, 0F, 0.5F);
    public boolean rotations;

    @Override
    public void compile()
    {
        for (int i = 0, c = this.rotations ? 4 : 1; i < c; i++)
        {
            this.models.add(i, this.createModel(i));
        }
    }

    private BlockModel createModel(int rotation)
    {
        BlockModel model = this.createModel();
        CombinedGeometry combined = new CombinedGeometry();

        for (ModelCube cube : this.cubes)
        {
            cube.generateQuads(256, 256);

            MatrixStack stack = new MatrixStack();

            stack.translate(this.offset);
            stack.rotateY(MathUtils.PI / 2F * rotation);

            for (ModelQuad quad : cube.quads)
            {
                stack.push();
                CubicCubeRenderer.moveToPivot(stack, cube.pivot);
                CubicCubeRenderer.rotate(stack, cube.rotate);
                CubicCubeRenderer.moveBackFromPivot(stack, cube.pivot);

                ModelVertex p1 = quad.vertices.get(0);
                ModelVertex p2 = quad.vertices.get(1);
                ModelVertex p3 = quad.vertices.get(2);
                ModelVertex p4 = quad.vertices.get(3);

                Vector3f normal = new Vector3f(quad.normal);

                stack.getNormalMatrix().transform(normal);

                normal.normalize();

                QuadGeometry geometry = new QuadGeometry(normal.x, normal.y, normal.z);
                geometry.t1.set(p4.uv).mul(256);
                geometry.t2.set(p2.uv).mul(256);
                geometry.p1.set(this.applyTransform(stack, p2.vertex));
                geometry.p2.set(this.applyTransform(stack, p1.vertex));
                geometry.p3.set(this.applyTransform(stack, p3.vertex));
                geometry.p4.set(this.applyTransform(stack, p4.vertex));

                if (cube.size.x == 0 || cube.size.z == 0)
                {
                    geometry.n.set(0, 1, 0);
                }

                geometry.ao = this.ao;
                combined.geometries.add(geometry);

                stack.pop();
            }
        }

        model.all = combined.geometries.isEmpty() ? null : combined;

        return model;
    }

    private Vector3f applyTransform(MatrixStack stack, Vector3f vector)
    {
        BlockModelCombined.vector.set(vector, 1);

        stack.getModelMatrix().transform(BlockModelCombined.vector);

        return new Vector3f(BlockModelCombined.vector.x, BlockModelCombined.vector.y, BlockModelCombined.vector.z);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.remove("all");

        if (!this.cubes.isEmpty())
        {
            ListType cubesList = new ListType();

            for (ModelCube cube : this.cubes)
            {
                cubesList.add(cube.toData());
            }

            data.put("cubes", cubesList);
        }

        data.put("offset", DataStorageUtils.vector3fToData(this.offset));
        data.putBool("rotations", this.rotations);
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("cubes"))
        {
            for (BaseType cubeBase : data.getList("cubes"))
            {
                ModelCube cube = new ModelCube();

                cube.fromData((MapType) cubeBase);
                this.cubes.add(cube);
            }
        }

        this.offset.set(data.has("offset") ? DataStorageUtils.vector3fFromData(data.getList("offset")) : Vectors.EMPTY_3F);
        this.rotations = data.getBool("rotations");
    }
}