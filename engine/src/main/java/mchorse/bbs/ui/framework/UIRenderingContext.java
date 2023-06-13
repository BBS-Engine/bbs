package mchorse.bbs.ui.framework;

import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.texture.TextureManager;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.ui.framework.elements.utils.StencilMap;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.world.World;
import org.joml.Matrix4f;

public class UIRenderingContext extends RenderingContext
{
    /**
     * Orthographic projection.
     */
    public Matrix4f projection;

    public UIDraw draw;

    private RenderingContext render;
    private ShaderRepository pickingShaders = new ShaderRepository();
    private StencilMap stencil = new StencilMap();

    public UIRenderingContext(RenderingContext render, Matrix4f projection)
    {
        this.render = render;
        this.projection = projection;
        this.draw = new UIDraw(this);
    }

    public ShaderRepository getPickingShaders()
    {
        return this.pickingShaders;
    }

    public StencilMap getStencil()
    {
        return this.stencil;
    }

    /* Rendering context implementations */

    @Override
    public boolean isDebug()
    {
        return this.render.isDebug();
    }

    @Override
    public World getWorld()
    {
        return this.render.getWorld();
    }

    @Override
    public FontRenderer getFont()
    {
        return this.render.getFont();
    }

    @Override
    public FontRenderer getFontMono()
    {
        return this.render.getFontMono();
    }

    @Override
    public VAOBuilder getVAO()
    {
        return this.render.getVAO();
    }

    @Override
    public TextureManager getTextures()
    {
        return this.render.getTextures();
    }
}
