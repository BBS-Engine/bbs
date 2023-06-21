package mchorse.bbs.voxel;

import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * Chunk builder
 * 
 * At the moment, this class can only construct chunks out of opaque 
 * blocks with no textures.
 */
public class ChunkBuilder
{
    private static Vector3i tmp = new Vector3i();
    private static Vector3i tmp2 = new Vector3i();

    /**
     * Chunks 
     */
    protected ChunkManager manager;

    /**
     * Currently constructing chunk 
     */
    protected ChunkDisplay chunk;

    /**
     * Current index of a face 
     */
    protected int index;

    /**
     * Edge of the current constructing chunk
     */
    protected Vector3i edge;

    /**
     * Block model manager 
     */
    public BlockSet models;

    public Color color = Color.white();

    public ChunkBuilder(BlockSet models)
    {
        this.models = models;
    }

    public VBOAttributes getAttributes()
    {
        return VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA;
    }

    public ChunkBuilder resetIndex()
    {
        this.chunk = null;
        this.index = 0;

        return this;
    }

    public ChunkBuilder buildBlock(IBlockVariant block, int nx, int ny, int nz, VAOBuilder builder)
    {
        this.generateBlock(block, nx, ny, nz, builder, Vectors.EMPTY_3I);

        return this;
    }

    /**
     * Build a VAO based on given chunk 
     */
    public VAO build(RenderingContext context, ChunkDisplay chunk, ChunkManager manager)
    {
        this.chunk = chunk;
        this.manager = manager;
        this.index = 0;

        if (manager != null)
        {
            this.edge = new Vector3i();

            if (manager.isOutside(chunk.x - 1, chunk.y, chunk.z)) this.edge.x = -1;
            else if (manager.isOutside(chunk.x + manager.s, chunk.y, chunk.z)) this.edge.x = 1;

            if (manager.isOutside(chunk.x, chunk.y - 1, chunk.z)) this.edge.y = -1;
            else if (manager.isOutside(chunk.x, chunk.y + manager.s, chunk.z)) this.edge.y = 1;

            if (manager.isOutside(chunk.x, chunk.y, chunk.z - 1)) this.edge.z = -1;
            else if (manager.isOutside(chunk.x, chunk.y, chunk.z + manager.s)) this.edge.z = 1;
        }

        VAO vao = chunk.display;
        VAOBuilder builder = context.getVAO().setup((VAO) null, VAO.INDICES);

        builder.begin();
        this.generateGeometry(builder);

        this.chunk = null;
        this.manager = null;
        this.edge = null;

        if (this.index == 0)
        {
            chunk.display = null;

            if (vao != null)
            {
                vao.delete();
            }

            builder.reset();

            return null;
        }

        if (vao == null)
        {
            vao = chunk.display = new VAO().register(this.getAttributes()).registerIndex();
        }

        builder.vao = vao;
        builder.flush();
        vao.unbind();

        return vao;
    }

    /**
     * Generate geometry
     * 
     * This method is basically goes over every block in a chunk and 
     * delegates construction logic to generateBlock method.
     */
    protected void generateGeometry(VAOBuilder builder)
    {
        Chunk chunk = this.chunk.chunk;
        IBlockVariant[] chunkData = chunk.getData();

        this.color.set(1, 1, 1, 1);

        for (int i = 0; i < chunkData.length; i++)
        {
            IBlockVariant block = chunk.data[i];

            if (!block.isAir())
            {
                MathUtils.toBlock(i, chunk.w, chunk.h, tmp);

                this.generateBlock(block, tmp.x, tmp.y, tmp.z, builder, this.getEdge(tmp));
            }
        }
    }

    /**
     * Get edge vector that is used by Block models to avoid
     * generating faces on the edge of render distance.
     */
    private Vector3i getEdge(Vector3i block)
    {
        if (this.edge == null)
        {
            return Vectors.EMPTY_3I;
        }

        tmp2.set(0, 0, 0);

        int s = this.manager.s - 1;

        if (this.edge.x > 0 && block.x == s) tmp2.x = 1;
        else if (this.edge.x < 0 && block.x == 0) tmp2.x = -1;

        if (this.edge.y > 0 && block.y == s) tmp2.y = 1;
        else if (this.edge.y < 0 && block.y == 0) tmp2.y = -1;

        if (this.edge.z > 0 && block.z == s) tmp2.z = 1;
        else if (this.edge.z < 0 && block.z == 0) tmp2.z = -1;

        return tmp2;
    }

    /**
     * Generate faces for given block 
     */
    protected void generateBlock(IBlockVariant block, int nx, int ny, int nz, VAOBuilder builder, Vector3i edge)
    {
        this.index = block.getModel().build(builder, this, block, this.index, nx, ny, nz, edge);
    }

    /* API methods for model things */

    public IBlockVariant block(int x, int y, int z)
    {
        if (this.chunk == null)
        {
            return this.models.air;
        }

        if (this.manager == null)
        {
            if (this.chunk.chunk.isOutside(x, y, z))
            {
                return this.models.air;
            }

            return this.chunk.chunk.getBlock(x, y, z);
        }

        x += this.chunk.x;
        y += this.chunk.y;
        z += this.chunk.z;

        ChunkCell cell = this.manager.getCell(x, y, z, false);

        if (cell != null)
        {
            return cell.getBlock(x, y, z);
        }

        return this.models.air;
    }

    public int lighting(int x, int y, int z)
    {
        if (this.chunk == null)
        {
            return 0;
        }

        if (this.manager == null)
        {
            Chunk c = this.chunk.chunk;

            x = MathUtils.clamp(x, 0, c.w - 1);
            y = MathUtils.clamp(y, 0, c.h - 1);
            z = MathUtils.clamp(z, 0, c.d - 1);

            return c.getLighting(x, y, z);
        }

        x += this.chunk.x;
        y += this.chunk.y;
        z += this.chunk.z;

        return this.manager.getLighting(x, y, z);
    }

    public boolean emitsAO(int x, int y, int z)
    {
        return this.block(x, y, z).getModel().ao;
    }

    public void renderInUI(UIContext context, IBlockVariant variant, int x, int y, int scale)
    {
        context.batcher.flush();

        Shader shader = context.render.getShaders().get(this.getAttributes());
        VAOBuilder builder = context.render.getVAO().setup(shader, VAO.INDICES);

        Matrix4f model = new Matrix4f();
        Matrix3f normal = new Matrix3f();
        Vector3f translate = context.render.stack.getModelMatrix().getTranslation(new Vector3f());

        model.scale(scale, -scale, scale);
        model.rotateX(MathUtils.PI / 5).rotateY(MathUtils.PI / 4);
        model.setTranslation(x + translate.x, y + translate.y, scale);
        normal.rotateX(MathUtils.PI / 5);

        CommonShaderAccess.setModelView(shader, model, normal);

        context.render.getTextures().bind(this.models.atlas);

        GLStates.setupDepthFunction3D();

        builder.begin(-0.5F, -0.5F, -0.5F);
        this.resetIndex().buildBlock(variant, 0, 0, 0, builder);
        builder.render();

        GLStates.setupDepthFunction2D();
    }
}