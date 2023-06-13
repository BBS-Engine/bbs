package mchorse.app;

import mchorse.app.settings.AppSettings;
import mchorse.app.shaders.ShadersWorld;
import mchorse.bbs.BBS;
import mchorse.bbs.animation.AnimationPlayer;
import mchorse.bbs.animation.Animations;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.IComponent;
import mchorse.bbs.events.RenderWorldEvent;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.huds.HUDStage;
import mchorse.bbs.game.misc.WorldForm;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.Renderbuffer;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector3;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
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
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class GameRenderer implements IComponent
{
    public GameEngine engine;

    public HUDStage mainStage = new HUDStage();
    public HUDStage currentStage;
    public Animations animations = new Animations();

    public RenderingContext context;

    /* Shaders */
    public ShadersWorld shaders;

    /* VAOs */
    public VAO sky;
    public Shader compositeShader;
    public Shader finalShader;
    public Shader skyboxShader;

    public ChunkRenderer renderer = new ChunkRenderer();
    public Framebuffer gbufferFramebuffer;
    public Framebuffer finalFramebuffer;

    private RenderWorldEvent renderWorld;

    private int ticks;

    private Entity dummy = EntityArchitect.createDummy();

    public GameRenderer(GameEngine engine)
    {
        this.engine = engine;
    }

    @Override
    public void init() throws Exception
    {
        this.shaders = new ShadersWorld();
        this.context = BBS.getRender();

        FontRenderer fontDefault = BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round.json"));
        FontRenderer fontMonoDefault = BBS.getFonts().getRenderer(Link.assets("fonts/bbs_round_mono.json"));

        this.context.setup(fontDefault, fontMonoDefault, BBS.getVAOs(), BBS.getTextures());
        this.context.setCamera(this.engine.cameraController.camera);
        this.context.setUBO(this.shaders.ubo);

        this.context.getMainShaders().register(this.shaders.vertex);
        this.context.getMainShaders().register(this.shaders.vertexRGBA);
        this.context.getMainShaders().register(this.shaders.vertexUVRGBA);
        this.context.getMainShaders().register(this.shaders.vertexNormalUVRGBA);
        this.context.getMainShaders().register(this.shaders.vertexNormalUVLightRGBA);
        this.context.getMainShaders().register(this.shaders.vertexNormalUVRGBABones);

        this.compositeShader = new Shader(Link.create("app:shaders/world/vertex_2d-composite.glsl"), VBOAttributes.VERTEX_2D).onInitialize((shader) ->
        {
            shader.getUniform("u_texture", UniformInt.class).set(0);
            shader.getUniform("u_position", UniformInt.class).set(1);
            shader.getUniform("u_normal", UniformInt.class).set(2);
            shader.getUniform("u_lighting", UniformInt.class).set(3);
            shader.getUniform("u_lightmap", UniformInt.class).set(7);
        });
        this.finalShader = new Shader(Link.create("app:shaders/world/vertex_2d-final.glsl"), VBOAttributes.VERTEX_2D).onInitialize((shader) ->
        {
            shader.getUniform("u_texture", UniformInt.class).set(0);
        });;
        this.skyboxShader = new Shader(Link.create("app:shaders/world/vertex-skybox.glsl"), VBOAttributes.VERTEX);
        this.skyboxShader.attachUBO(this.context.getUBO(), "u_matrices");

        this.finalFramebuffer = BBS.getFramebuffers().getFramebuffer(Link.bbs("final"), (framebuffer) ->
        {
            Texture texture = BBS.getTextures().createTexture(Link.create("main:final"));

            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);

            framebuffer.attach(texture, GL30.GL_COLOR_ATTACHMENT0);
            framebuffer.unbind();
        });

        this.gbufferFramebuffer = BBS.getFramebuffers().getFramebuffer(Link.bbs("gbuffer"), (framebuffer) ->
        {
            Texture albedo = BBS.getTextures().createTexture(Link.create("main:albedo"));

            albedo.setFilter(GL11.GL_NEAREST);
            albedo.setWrap(GL13.GL_CLAMP_TO_EDGE);

            Texture position = BBS.getTextures().createTexture(Link.create("main:position"));

            position.setFilter(GL11.GL_NEAREST);
            position.setWrap(GL13.GL_CLAMP_TO_EDGE);
            position.setFormat(GL30.GL_RGBA16F, GL11.GL_RGBA, GL11.GL_FLOAT);

            Texture normal = BBS.getTextures().createTexture(Link.create("main:normal"));

            normal.setFilter(GL11.GL_NEAREST);
            normal.setWrap(GL13.GL_CLAMP_TO_EDGE);
            normal.setFormat(GL30.GL_RGBA16F, GL11.GL_RGBA, GL11.GL_FLOAT);

            Texture lighting = BBS.getTextures().createTexture(Link.create("main:lighting"));

            lighting.setFilter(GL11.GL_NEAREST);
            lighting.setWrap(GL13.GL_CLAMP_TO_EDGE);

            Renderbuffer depth = new Renderbuffer();

            framebuffer.attach(albedo, GL30.GL_COLOR_ATTACHMENT0);
            framebuffer.attach(position, GL30.GL_COLOR_ATTACHMENT1);
            framebuffer.attach(normal, GL30.GL_COLOR_ATTACHMENT2);
            framebuffer.attach(lighting, GL30.GL_COLOR_ATTACHMENT3);
            framebuffer.attach(depth);

            GL30.glDrawBuffers(new int[] {GL30.GL_COLOR_ATTACHMENT0, GL30.GL_COLOR_ATTACHMENT1, GL30.GL_COLOR_ATTACHMENT2, GL30.GL_COLOR_ATTACHMENT3});

            framebuffer.unbind();
        });

        this.createSkybox();

        this.renderWorld = new RenderWorldEvent(this.context);
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

    public HUDStage getStage()
    {
        return this.currentStage == null ? this.mainStage : this.currentStage;
    }

    @Override
    public void delete()
    {
        this.shaders.ubo.delete();
    }

    @Override
    public void update()
    {
        HUDStage stage = this.getStage();

        stage.update(stage == this.mainStage);

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

        this.gbufferFramebuffer.textures.get(3).bind(3);
        this.gbufferFramebuffer.textures.get(2).bind(2);
        this.gbufferFramebuffer.textures.get(1).bind(1);
        this.gbufferFramebuffer.textures.get(0).bind(0);

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
            quality = AppSettings.renderQuality.get();
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
        }

        this.renderFrameTo(camera, this.gbufferFramebuffer, pass, renderScreen);
        this.renderFinal(framebuffer);

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

        if (AppSettings.renderTerrain.get())
        {
            ChunkArrayManager manager = (ChunkArrayManager) world.chunks;

            this.renderer.bindTexture(manager);

            if (world.view.getThread().isIdling())
            {
                manager.buildChunks(this.context, this.engine.video != null && this.engine.video.isRecording());
            }

            this.renderer.render(manager, context);

            if (context.isDebug() && AppSettings.renderTerrainDebug.get())
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

        for (WorldForm form : world.worldForms)
        {
            form.render(this.context);
        }

        for (AnimationPlayer animation : this.animations.animations.values())
        {
            if (animation != null)
            {
                animation.render(context);
            }
        }

        this.engine.playerData.getGameController().renderInWorld(context);

        BBS.events.post(this.renderWorld);
    }

    private boolean canRenderEntity(Entity entity)
    {
        return this.engine.playerData.getGameController().canRenderEntity(entity, this.context);
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
        VAOBuilder builder = this.context.getVAO().setup(this.shaders.vertexUVRGBA);
        String label = "(" + MathUtils.toChunk(x, s) + ", " + MathUtils.toChunk(y, s) + ", " + MathUtils.toChunk(z, s) + ")";
        float scale = 1 / 16F;

        stack.push();
        stack.translateRelative(camera, x + w / 2, y + h / 2, z + d / 2);
        stack.scale(scale, -scale, scale);
        stack.rotateY(-camera.rotation.y);
        stack.rotateX(camera.rotation.x);

        Shader shader = this.shaders.vertexUVRGBA;

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
        float renderQuality = AppSettings.renderQuality.get();

        width *= renderQuality;
        height *= renderQuality;

        this.gbufferFramebuffer.resize(width, height);
        this.finalFramebuffer.resize(width, height);
    }
}