package mchorse.bbs.graphics;

import mchorse.bbs.camera.Camera;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.shaders.lighting.LightsUBO;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.texture.TextureManager;
import mchorse.bbs.graphics.ubo.ProjectionViewUBO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VAOManager;
import mchorse.bbs.world.World;

import java.util.ArrayList;
import java.util.List;

public class RenderingContext
{
    private boolean debug;
    private float transition;
    private int pass;

    public MatrixStack stack = new MatrixStack();
    private Camera camera;
    private World world;

    private ProjectionViewUBO ubo;

    private FontRenderer font;

    private VAOBuilder vao;
    private TextureManager textures;

    private ShaderRepository shaders = new ShaderRepository();
    private ShaderRepository active;

    private LightsUBO lights = new LightsUBO(2);

    private List<Runnable> scheduledRunnables = new ArrayList<>();

    public void setup(FontRenderer font, VAOManager vaos, TextureManager textures)
    {
        this.font = font;
        this.vao = new VAOBuilder(vaos);
        this.textures = textures;
    }

    public int getPass()
    {
        return this.pass;
    }

    public void setPass(int pass)
    {
        this.pass = pass;
    }

    public boolean isDebug()
    {
        return this.debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public float getTransition()
    {
        return this.transition;
    }

    public void setTransition(float transition)
    {
        this.transition = transition;
    }

    public MatrixStack getStack()
    {
        return this.stack;
    }

    public Camera getCamera()
    {
        return this.camera;
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }

    public World getWorld()
    {
        return this.world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public ProjectionViewUBO getUBO()
    {
        return this.ubo;
    }

    public void setUBO(ProjectionViewUBO ubo)
    {
        this.ubo = ubo;
    }

    public FontRenderer getFont()
    {
        return this.font;
    }

    public VAOBuilder getVAO()
    {
        return this.vao;
    }

    public TextureManager getTextures()
    {
        return this.textures;
    }

    public void setShaders(ShaderRepository shaders)
    {
        this.active = shaders;
    }

    public ShaderRepository getShaders()
    {
        return this.active == null ? this.shaders : this.active;
    }

    public ShaderRepository getMainShaders()
    {
        return this.shaders;
    }

    public LightsUBO getLights()
    {
        return this.lights;
    }

    public void postRunnable(Runnable runnable)
    {
        if (runnable != null)
        {
            this.scheduledRunnables.add(runnable);
        }
    }

    public void runRunnables()
    {
        for (int i = 0; i < this.scheduledRunnables.size(); i++)
        {
            Runnable runnable = i < this.scheduledRunnables.size() ? this.scheduledRunnables.get(i) : null;

            if (runnable != null)
            {
                runnable.run();
            }
        }

        this.scheduledRunnables.clear();
    }
}