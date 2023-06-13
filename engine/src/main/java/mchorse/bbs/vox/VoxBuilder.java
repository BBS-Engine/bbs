package mchorse.bbs.vox;

import mchorse.bbs.cubic.data.model.ModelMesh;
import mchorse.bbs.vox.data.Vox;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class VoxBuilder
{
    public Vector3f translation;
    public Matrix3f transform;
    public Vector3f vector = new Vector3f();

    private Vector3f right;
    private Vector3f left;
    private Vector3f front;
    private Vector3f back;
    private Vector3f bottom;
    private Vector3f top;

    public VoxBuilder(Vector3f translation, Matrix3f transform)
    {
        this.translation = translation;
        this.transform = transform;

        this.right = this.processNormal(new Vector3f(-1, 0, 0));
        this.left = this.processNormal(new Vector3f(1, 0, 0));
        this.front = this.processNormal(new Vector3f(0, 0, 1));
        this.back = this.processNormal(new Vector3f(0, 0, -1));
        this.bottom = this.processNormal(new Vector3f(0, -1, 0));
        this.top = this.processNormal(new Vector3f(0, 1, 0));
    }

    private Vector3f processNormal(Vector3f normal)
    {
        /* Transform the normal */
        normal.set(normal.x, normal.z, normal.y);
        this.transform.transform(normal);
        normal.set(normal.x, normal.z, normal.y);
        normal.normalize();

        return normal;
    }

    public ModelMesh build(Vox vox)
    {
        /* Worst case scenario */
        ModelMesh mesh = new ModelMesh();

        for (int x = 0; x < vox.w; x++)
        {
            for (int y = 0; y < vox.h; y++)
            {
                for (int z = 0; z < vox.d; z++)
                {
                    int voxel = vox.voxels[vox.toIndex(x, y, z)];

                    if (voxel != 0)
                    {
                        this.buildVertex(mesh, x, y, z, voxel, vox);
                    }
                }
            }
        }

        return mesh;
    }

    private void buildVertex(ModelMesh mesh, int x, int y, int z, int voxel, Vox vox)
    {
        boolean top = vox.has(x, y + 1, z);
        boolean bottom = vox.has(x, y - 1, z);
        boolean left = vox.has(x + 1, y, z);
        boolean right = vox.has(x - 1, y, z);
        boolean front = vox.has(x, y, z + 1);
        boolean back = vox.has(x, y, z - 1);

        if (!top)
        {
            Vector3f normal = this.top;

            this.add(mesh, vox, x, y + 1, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, -0.5F, 0.5F, normal);
        }

        if (!bottom)
        {
            Vector3f normal = this.bottom;

            this.add(mesh, vox, x + 1, y, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y, z, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, -0.5F, 0.5F, normal);
        }

        if (!left)
        {
            Vector3f normal = this.left;

            this.add(mesh, vox, x + 1, y + 1, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, 0.5F, -0.5F, normal);
        }

        if (!right)
        {
            Vector3f normal = this.right;

            this.add(mesh, vox, x, y, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y, z, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, 0.5F, -0.5F, normal);
        }

        if (!front)
        {
            Vector3f normal = this.front;

            this.add(mesh, vox, x + 1, y, z + 1, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y, z + 1, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y, z + 1, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z + 1, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x, y + 1, z + 1, voxel, -0.5F, 0.5F, normal);
        }

        if (!back)
        {
            Vector3f normal = this.back;

            this.add(mesh, vox, x, y + 1, z, voxel, -0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y, z, voxel, -0.5F, -0.5F, normal);
            this.add(mesh, vox, x + 1, y + 1, z, voxel, 0.5F, 0.5F, normal);
            this.add(mesh, vox, x + 1, y, z, voxel, 0.5F, -0.5F, normal);
            this.add(mesh, vox, x, y + 1, z, voxel, -0.5F, 0.5F, normal);
        }
    }

    private void add(ModelMesh mesh, Vox vox, int x, int y, int z, int voxel, float offsetU, float offsetV, Vector3f normal)
    {
        float u = voxel + 0.5F + offsetU;
        float v = 0.5F + offsetV;

        Vector3f vertex = this.process(x, y, z, vox);

        mesh.vertices.add(new Vector3f(vertex));

        /* mesh.normData[tris * 3] = normal.x;
        mesh.normData[tris * 3 + 1] = normal.y;
        mesh.normData[tris * 3 + 2] = normal.z; */

        mesh.uvs.add(new Vector2f(u, v));
    }

    private Vector3f process(int x, int y, int z, Vox vox)
    {
        int w = (int) (vox.w / 2F);
        int h = (int) (vox.h / 2F);
        int d = (int) (vox.d / 2F);

        this.vector.set(x - w, z - h, y - d);
        this.transform.transform(this.vector);
        this.vector.set(this.vector.x, this.vector.z, this.vector.y);
        this.vector.add(this.translation.x, this.translation.z, this.translation.y);

        return this.vector;
    }
}