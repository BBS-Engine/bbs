package mchorse.studio;

import mchorse.bbs.BBS;
import mchorse.bbs.animation.AnimationPlayer;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.IComponent;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.RenderWorldEvent;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.Renderbuffer;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.shaders.pipeline.ShaderFramebufferTexture;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector2;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector3;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.ubo.ProjectionViewUBO;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.ChunkRenderer;
import mchorse.bbs.voxel.storage.ChunkArrayManager;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldSettings;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import mchorse.bbs.world.objects.WorldObject;
import mchorse.studio.settings.StudioSettings;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class StudioRenderer implements IComponent
{
    public StudioEngine engine;

    public Animations animations = new Animations();

    public RenderingContext context;

    /* Shaders */
    public ProjectionViewUBO ubo;

    /* VAOs */
    public VAO sky;
    public Shader compositeShader;
    public Shader finalShader;
    public Shader skyboxShader;

    public ChunkRenderer renderer = new ChunkRenderer();
    public Framebuffer gbufferFramebuffer;
    public Framebuffer finalFramebuffer;
    public Framebuffer tmpFramebuffer;

    private ShaderPipeline pipeline;
    private RenderWorldEvent renderWorld;

    private int ticks;

    private Entity dummy = EntityArchitect.createDummy();

    public StudioRenderer(StudioEngine engine)
    {
        this.engine = engine;
    }

    @Override
    public void init() throws Exception
    {
        this.context = BBS.getRender();

        this.ubo = new ProjectionViewUBO(0);
        this.ubo.init();
        this.ubo.bindUnit();

        this.context.getLights().init();
        this.context.getLights().bindUnit();

        this.context.setup(BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round.json")), BBS.getVAOs(), BBS.getTextures());
        this.context.setCamera(this.engine.cameraController.camera);
        this.context.setUBO(this.ubo);

        this.createSkybox();
        this.setupShaderPipeline();

        this.renderWorld = new RenderWorldEvent(this.context);
    }

    private void setupShaderPipeline()
    {
        this.pipeline = new ShaderPipeline();

        try
        {
            InputStream asset = BBS.getProvider().getAsset(Studio.link("shaders/default/default.shader.json"));
            MapType data = DataToString.mapFromString(IOUtils.readText(asset));

            this.pipeline.fromData(data);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        ShaderRepository mainShaders = this.context.getMainShaders();

        Shader skyboxShader = new Shader(this.pipeline.shaders.get("skybox"), VBOAttributes.VERTEX);
        Shader vertexRGBA = new Shader(this.pipeline.shaders.get("solid"), VBOAttributes.VERTEX_RGBA);
        Shader vertexUVRGBA = new Shader(this.pipeline.shaders.get("textured"), VBOAttributes.VERTEX_UV_RGBA);
        Shader vertexNormalUVRGBA = new Shader(this.pipeline.shaders.get("model"), VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        Shader vertexNormalUVLightRGBA = new Shader(this.pipeline.shaders.get("terrain"), VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
        Shader vertexNormalUVRGBABones = new Shader(this.pipeline.shaders.get("model_animated"), VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

        Shader compositeShader = new Shader(this.pipeline.shaders.get("composite"), VBOAttributes.VERTEX_2D);
        Shader finalShader = new Shader(this.pipeline.shaders.get("final"), VBOAttributes.VERTEX_2D);

        skyboxShader.attachUBO(this.context.getUBO(), "u_matrices");
        vertexRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVLightRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVRGBABones.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");

        compositeShader.onInitialize((shader) ->
        {
            shader.getUniform("u_texture", UniformInt.class).set(0);
            shader.getUniform("u_position", UniformInt.class).set(1);
            shader.getUniform("u_normal", UniformInt.class).set(2);
            shader.getUniform("u_lighting", UniformInt.class).set(3);
            shader.getUniform("u_depth", UniformInt.class).set(4);
            shader.getUniform("u_lightmap", UniformInt.class).set(7);
        });
        compositeShader.attachUBO(this.context.getLights(), "u_lights_block");
        finalShader.onInitialize(CommonShaderAccess::initializeTexture);

        mainShaders.clear();
        mainShaders.register(vertexRGBA);
        mainShaders.register(vertexUVRGBA);
        mainShaders.register(vertexNormalUVRGBA);
        mainShaders.register(vertexNormalUVLightRGBA);
        mainShaders.register(vertexNormalUVRGBABones);

        this.skyboxShader = skyboxShader;
        this.compositeShader = compositeShader;
        this.finalShader = finalShader;

        this.finalFramebuffer = BBS.getFramebuffers().getFramebuffer(Link.bbs("final"), (framebuffer) ->
        {
            Texture texture = new Texture();

            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);

            framebuffer.deleteTextures().attach(texture, GL30.GL_COLOR_ATTACHMENT0);
            framebuffer.unbind();
        });

        this.tmpFramebuffer = BBS.getFramebuffers().getFramebuffer(Link.bbs("tmp"), (framebuffer) ->
        {
            Texture texture = new Texture();

            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);

            framebuffer.deleteTextures().attach(texture, GL30.GL_COLOR_ATTACHMENT0);
            framebuffer.unbind();
        });

        this.gbufferFramebuffer = BBS.getFramebuffers().getFramebuffer(Link.bbs("gbuffer"), (framebuffer) ->
        {
            int colors = 0;
            boolean hasDepth = false;

            for (ShaderFramebufferTexture t : this.pipeline.gbuffer.textures)
            {
                if (t.format.isDepth())
                {
                    hasDepth = true;
                }
                else
                {
                    colors += 1;
                }
            }

            int color = 0;
            int[] colorAttachments = new int[colors];

            for (ShaderFramebufferTexture t : this.pipeline.gbuffer.textures)
            {
                Texture texture = new Texture();

                texture.setFilter(GL11.GL_NEAREST);
                texture.setWrap(GL13.GL_CLAMP_TO_EDGE);
                texture.setFormat(t.format);

                if (t.format.isDepth())
                {
                    framebuffer.attach(texture, t.format.attachment);
                }
                else
                {
                    framebuffer.attach(texture, t.format.attachment + color);

                    colorAttachments[color] = t.format.attachment + color;
                    color += 1;
                }
            }

            if (!hasDepth)
            {
                Renderbuffer renderbuffer = new Renderbuffer();

                framebuffer.attach(renderbuffer);
            }

            framebuffer.deleteTextures();

            GL30.glDrawBuffers(colorAttachments);

            framebuffer.unbind();
        });
    }

    private void createSkybox()
    {
        ByteBuffer data = VAO.DATA;
        float v[] = new float[] {
            /* Back (-Z) */
            -1F, 1F, -1F, -1F, -1F, -1F, 1F, -1F, -1F, 1F, -1F, -1F, 1F, 1F, -1F, -1F, 1F, -1F,
            /* Left (-X) */
            -1F, -1F, 1F, -1F, -1F, -1F, -1F, 1F, -1F, -1F, 1F, -1F, -1F, 1F, 1F, -1F, -1F, 1F,
            /* Right (X) */
            1F, -1F, -1F, 1F, -1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, -1F, 1F, -1F, -1F,
            /* Front (Z) */
            -1F, -1F, 1F, -1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, 1F, -1F, 1F, -1F, -1F, 1F,
            /* Top (Y) */
            -1F, 1F, -1F, 1F, 1F, -1F, 1F, 1F, 1F, 1F, 1F, 1F, -1F, 1F, 1F, -1F, 1F, -1F,
            /* Bottom (-Y) */
            -1F, -1F, -1F, -1F, -1F, 1F, 1F, -1F, -1F, 1F, -1F, -1F, -1F, -1F, 1F, 1F, -1F, 1F
        };

        data.clear();

        for (float value : v)
        {
            data.putFloat(value);
        }

        data.flip();

        this.sky = BBS.getVAOs().create().register(VBOAttributes.VERTEX).uploadData(data);
        this.sky.unbind();
    }

    @Override
    public void delete()
    {
        this.ubo.delete();
    }

    @Override
    public void update()
    {
        Iterator<AnimationPlayer> it = this.animations.animations.values().iterator();

        while (it.hasNext())
        {
            AnimationPlayer player = it.next();

            if (player != null)
            {
                player.update();

                if (player.canRemove())
                {
                    it.remove();
                }
            }
        }

        this.dummy.basic.ticks += 1;
        this.ticks += 1;

        Form skyForm = this.engine.world.settings.skyForm;

        if (skyForm != null)
        {
            this.dummy.setWorld(this.engine.world);
            skyForm.update(this.dummy);
        }
    }

    @Override
    public void render(float transition)
    {
        for (FontRenderer fontRenderer : BBS.getFonts().fontRenderers.values())
        {
            if (fontRenderer != null)
            {
                fontRenderer.setTime(this.ticks + transition);
            }
        }

        this.context.setTransition(transition);
        this.context.setWorld(this.engine.world);

        if (this.engine.screen.canRefresh())
        {
            this.renderFrameTo(this.engine.cameraController.camera, this.gbufferFramebuffer, 0, true);
            this.renderFinal(this.finalFramebuffer);
        }

        this.renderFinalQuad();

        this.context.runRunnables();
    }

    private void renderFinal(Framebuffer framebuffer)
    {
        this.updateSky(this.compositeShader, this.engine.world.settings);

        framebuffer.apply();

        Link lightmap = this.engine.world.settings.lightmap;

        if (lightmap != null)
        {
            Texture texture = this.context.getTextures().getTexture(lightmap);

            texture.bind(7);
            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL12.GL_CLAMP_TO_EDGE);
        }

        for (int i = this.gbufferFramebuffer.textures.size() - 1; i >= 0; i--)
        {
            this.gbufferFramebuffer.textures.get(i).bind(i);
        }

        Framebuffer.renderToQuad(this.context, this.compositeShader);

        framebuffer.unbind();
        GLStates.resetViewport();
    }

    private void renderFinalQuad()
    {
        GLStates.depthMask(false);

        this.finalFramebuffer.getMainTexture().bind(0);
        Framebuffer.renderToQuad(this.context, this.finalShader);

        GLStates.depthMask(true);
    }

    public void renderFrameToQuality(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality)
    {
        if (quality <= 0)
        {
            quality = StudioSettings.renderQuality.get();
        }

        Texture mainTexture = framebuffer.getMainTexture();

        int w = (int) (mainTexture.width * quality);
        int h = (int) (mainTexture.height * quality);

        Texture gbufferAlbedo = this.gbufferFramebuffer.getMainTexture();

        int lastW = gbufferAlbedo.width;
        int lastH = gbufferAlbedo.height;

        if (lastW != w || lastH != h)
        {
            this.gbufferFramebuffer.resize(w, h);
            this.tmpFramebuffer.resize(w, h);
        }

        this.renderFrameTo(camera, this.gbufferFramebuffer, pass, renderScreen);
        this.renderFinal(this.tmpFramebuffer);

        framebuffer.apply();

        this.tmpFramebuffer.getMainTexture().bind(0);
        Framebuffer.renderToQuad(this.context, this.finalShader);

        framebuffer.unbind();
        GLStates.resetViewport();

        if (lastW != w || lastH != h)
        {
            this.gbufferFramebuffer.resize(lastW, lastH);
        }
    }

    public void renderFrameTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen)
    {
        framebuffer.apply();

        /* Update context state */
        camera.updateView();
        this.context.setCamera(camera);
        this.context.setPass(pass);

        this.renderFrameCommon(camera, this.context);

        if (renderScreen)
        {
            this.engine.screen.renderWorld(this.context);
        }

        framebuffer.unbind();

        GLStates.resetViewport();
    }

    private void renderFrameCommon(Camera camera, RenderingContext context)
    {
        camera.updateView();
        context.stack.reset();
        context.getUBO().update(camera.projection, camera.view);

        if (this.engine.world.settings.sky)
        {
            this.renderSkybox();
        }

        this.renderScene(context);

        context.getLights().submitLights();
    }

    private void renderSkybox()
    {
        WorldSettings settings = this.engine.world.settings;

        GLStates.depthMask(false);

        this.updateSky(this.skyboxShader, settings);

        this.skyboxShader.bind();

        this.sky.bindForRender();
        this.sky.renderTriangles(36);
        this.sky.unbindForRender();

        GLStates.depthMask(true);

        Form skyForm = this.engine.world.settings.skyForm;

        if (skyForm != null)
        {
            this.dummy.setWorld(this.engine.world);
            skyForm.getRenderer().render(this.dummy, this.context);
        }

        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
    }

    private void updateSky(Shader shader, WorldSettings settings)
    {
        UniformVector3 zenith = shader.getUniform("u_zenith", UniformVector3.class);

        if (zenith != null)
        {
            UniformVector3 horizon = shader.getUniform("u_horizon", UniformVector3.class);
            UniformVector3 bottom = shader.getUniform("u_bottom", UniformVector3.class);

            zenith.set(settings.zenith);
            horizon.set(settings.horizon);
            bottom.set(settings.bottom);
        }

        UniformVector3 shadingDirection = shader.getUniform("u_shading", UniformVector3.class);

        if (shadingDirection != null)
        {
            Vector3f vector3f = Vectors.TEMP_3F.set(settings.shadingDirection);

            shadingDirection.set(vector3f.mul(-1).normalize());
        }

        UniformInt fog = shader.getUniform("u_fog", UniformInt.class);

        if (fog != null)
        {
            int distance = (int) (this.engine.world.chunks.s * (this.engine.world.chunks.getW() - 1) / 2F);

            fog.set(settings.fog ? distance : 0);
        }
    }

    private void renderScene(RenderingContext context)
    {
        World world = context.getWorld();

        if (StudioSettings.renderTerrain.get())
        {
            ChunkArrayManager manager = (ChunkArrayManager) world.chunks;

            this.renderer.bindTexture(manager);

            if (world.view.getThread().isIdling())
            {
                manager.buildChunks(this.context, this.engine.video != null && this.engine.video.isRecording());
            }

            this.renderer.render(manager, context);

            if (context.isDebug() && StudioSettings.renderTerrainDebug.get())
            {
                this.renderDebugChunks(context.getCamera(), manager);
            }
        }

        for (Entity entity : world.entities)
        {
            if (this.canRenderEntity(entity))
            {
                entity.render(this.context);
            }
        }

        for (WorldObject object : world.objects)
        {
            object.render(this.context);
        }

        for (AnimationPlayer animation : this.animations.animations.values())
        {
            if (animation != null)
            {
                animation.render(context);
            }
        }

        BBS.events.post(this.renderWorld);
    }

    private boolean canRenderEntity(Entity entity)
    {
        return true;
    }

    private void renderDebugChunks(Camera camera, ChunkManager chunks)
    {
        int x = MathUtils.toChunk(camera.position.x, chunks.s);
        int y = MathUtils.toChunk(camera.position.y, chunks.s);
        int z = MathUtils.toChunk(camera.position.z, chunks.s);
        int r = 5;
        int r2 = r / 2;

        for (int i = 0; i < r; i++)
        {
            for (int j = 0; j < r; j++)
            {
                for (int k = 0; k < r; k++)
                {
                    ChunkDisplay display = chunks.getDisplay((x - r2 + i) * chunks.s, (y - r2 + j) * chunks.s, (z - r2 + k) * chunks.s);

                    if (display != null)
                    {
                        this.renderDebugChunk(camera, display);
                    }
                }
            }
        }
    }

    private void renderDebugChunk(Camera camera, ChunkDisplay display)
    {
        int x = display.x;
        int y = display.y;
        int z = display.z;
        int w = display.chunk.w;
        int h = display.chunk.h;
        int d = display.chunk.d;
        int s = display.parent.manager.s;

        Draw.renderBox(this.context, x, y, z, w, h, d, 1, display.parent.generated ? 1 : 0, display.dirty ? 0 : 1);

        MatrixStack stack = this.context.stack;
        Shader shader = this.context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA);
        VAOBuilder builder = this.context.getVAO().setup(shader);
        String label = "(" + MathUtils.toChunk(x, s) + ", " + MathUtils.toChunk(y, s) + ", " + MathUtils.toChunk(z, s) + ")";
        float scale = 1 / 16F;

        stack.push();
        stack.translateRelative(camera, x + w / 2, y + h / 2, z + d / 2);
        stack.scale(scale, -scale, scale);
        stack.rotateY(-camera.rotation.y);
        stack.rotateX(camera.rotation.x);

        CommonShaderAccess.setModelView(shader, stack);

        stack.pop();

        FontRenderer text = this.context.getFont();

        text.bindTexture(this.context);

        builder.begin();
        text.buildVAO(-text.getWidth(label) / 2, -text.getHeight() / 2, label, builder, ITextBuilder.colored3D.setup(Colors.A100));
        builder.render();
    }

    @Override
    public void resize(int width, int height)
    {
        float renderQuality = StudioSettings.renderQuality.get();

        width *= renderQuality;
        height *= renderQuality;

        this.gbufferFramebuffer.resize(width, height);
        this.finalFramebuffer.resize(width, height);

        UniformVector2 screenSize = this.compositeShader.getUniform("u_screen_size", UniformVector2.class);

        if (screenSize != null)
        {
            screenSize.set(width, height);
        }
    }
}