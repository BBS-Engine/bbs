package mchorse.bbs.voxel;

import mchorse.bbs.BBS;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.texture.TextureManager;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.AABBi;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.storage.data.ChunkCell;
import org.joml.FrustumIntersection;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ChunkRenderer
{
    private FrustumIntersection frustum = new FrustumIntersection();
    private Texture atlas;
    private Link link;

    /**
     * Bind texture.
     */
    public void bindTexture(ChunkArrayManager manager)
    {
        if (
            (this.atlas != null && !this.atlas.isValid()) ||
            (this.link != null && !this.link.equals(manager.builder.models.atlas))
        ) {
            this.atlas = null;
        }

        if (this.atlas == null)
        {
            TextureManager textures = BBS.getTextures();
            Texture texture = textures.getTexture(manager.builder.models.atlas);

            texture.bind();
            texture.setParameter(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            texture.setParameter(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            texture.setParameter(GL12.GL_TEXTURE_MAX_LEVEL, 1);
            texture.setWrap(GL12.GL_CLAMP_TO_EDGE);

            boolean mipmap1 = this.tryUploadingMipmap(texture, manager.builder.models.atlas, 1);
            boolean mipmap2 = this.tryUploadingMipmap(texture, manager.builder.models.atlas, 2);

            if (!mipmap1 && !mipmap2)
            {
                texture.generateMipmap();
            }

            this.atlas = texture;
            this.link = manager.builder.models.atlas;
        }

        this.atlas.bind();
    }

    private boolean tryUploadingMipmap(Texture texture, Link atlas, int level)
    {
        Pixels pixels = null;
        boolean success = false;

        try
        {
            Link mipmap = new Link(atlas.source, StringUtils.removeExtension(atlas.path) + "_mipmap" + level + ".png");

            pixels = Pixels.fromPNGStream(BBS.getProvider().getAsset(mipmap));

            texture.bind();
            texture.uploadTexture(texture.target, level, pixels);

            success = true;
        }
        catch (Exception e)
        {}
        finally
        {
            if (pixels != null)
            {
                pixels.delete();
            }
        }

        return success;
    }

    /**
     * Render chunks.
     */
    public void render(ChunkArrayManager manager, RenderingContext context)
    {
        Camera camera = context.getCamera();

        this.frustum.set(Matrices.TEMP_4F.set(camera.projection).mul(camera.view));

        Shader shader = context.getShaders().get(manager.builder.getAttributes());

        shader.bind();

        for (ChunkCell cell : manager.render)
        {
            AABBi bounds = cell.bounds;
            Vector3f relative = camera.getRelative(bounds.x, bounds.y, bounds.z);

            if (this.frustum.testAab(relative.x, relative.y, relative.z, relative.x + bounds.w, relative.y + bounds.h, relative.z + bounds.d))
            {
                context.stack.push();
                context.stack.translate(relative);

                cell.render(context.stack, shader);

                context.stack.pop();
            }
        }
    }
}
