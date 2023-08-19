package mchorse.studio;

import mchorse.bbs.BBS;
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
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.shaders.pipeline.ShaderPipeline;
import mchorse.bbs.graphics.shaders.uniforms.UniformFloat;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.shaders.uniforms.UniformMatrix4;
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
import mchorse.bbs.utils.joml.Matrices;
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
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;

public class StudioRenderer implements IComponent
{
    public StudioEngine engine;

    public RenderingContext context;

    /* Shaders */
    public ProjectionViewUBO ubo;

    /* VAOs */
    public VAO sky;
    public Shader finalShader;
    public Shader skyboxShader;

    public ChunkRenderer renderer = new ChunkRenderer();
    private ShaderPipeline pipeline = new ShaderPipeline();
    private StudioShaders shaders = new StudioShaders(this.pipeline);

    private RenderWorldEvent renderWorld;

    private int ticks;
    private int frames;

    private Matrix4f prevProjection = new Matrix4f();
    private Matrix4f prevView = new Matrix4f();
    private Vector3d prevPosition = new Vector3d();

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
        this.setupShaders();
        this.setupShaderPipeline();

        this.renderWorld = new RenderWorldEvent(this.context);
    }

    public void reloadShaders(boolean full)
    {}

    private void setupShaders()
    {
        ShaderRepository mainShaders = this.context.getMainShaders();
        Shader vertexRGBA = new Shader(Link.create("studio:shaders/default/world/vertex_rgba.glsl"), VBOAttributes.VERTEX_RGBA);
        Shader vertexUVRGBA = new Shader(Link.create("studio:shaders/default/world/vertex_uv_rgba.glsl"), VBOAttributes.VERTEX_UV_RGBA);
        Shader vertexNormalUVRGBA = new Shader(Link.create("studio:shaders/default/world/vertex_normal_uv_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA);
        Shader vertexNormalUVLightRGBA = new Shader(Link.create("studio:shaders/default/world/vertex_normal_uv_light_rgba.glsl"), VBOAttributes.VERTEX_NORMAL_UV_LIGHT_RGBA);
        Shader vertexNormalUVRGBABones = new Shader(Link.create("studio:shaders/default/world/vertex_normal_uv_rgba_bones.glsl"), VBOAttributes.VERTEX_NORMAL_UV_RGBA_BONES);

        vertexRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVLightRGBA.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");
        vertexNormalUVRGBABones.onInitialize(CommonShaderAccess::initializeTexture).attachUBO(this.ubo, "u_matrices");

        mainShaders.clear();
        mainShaders.register(vertexRGBA);
        mainShaders.register(vertexUVRGBA);
        mainShaders.register(vertexNormalUVRGBA);
        mainShaders.register(vertexNormalUVLightRGBA);
        mainShaders.register(vertexNormalUVRGBABones);

        this.skyboxShader = new Shader(Link.create("studio:shaders/default/world/vertex-skybox.glsl"), VBOAttributes.VERTEX);
        this.skyboxShader.attachUBO(this.context.getUBO(), "u_matrices");
    }

    private void setupShaderPipeline()
    {
        try
        {
            String string = IOUtils.readText(BBS.getProvider().getAsset(Link.create("studio:shaders/default/default.shader.json")));
            MapType data = DataToString.mapFromString(string);

            this.pipeline.fromData(data);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.shaders.reload();

        this.finalShader = new Shader(Link.create("studio:shaders/default/deferred/vertex_2d-final.glsl"), VBOAttributes.VERTEX_2D);
        this.finalShader.onInitialize(CommonShaderAccess::initializeTexture);

        for (StudioShaders.Stage stage : this.shaders.stages)
        {
            stage.shader.attachUBO(this.context.getLights(), "u_lights_block");
        }
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
            this.renderFrameTo(this.engine.cameraController.camera, this.shaders.gbuffer, 0, true);
            this.renderFinal(this.context.getCamera(), this.shaders);
        }

        this.renderFinalQuad();

        this.context.runRunnables();

        this.frames += 1;
    }

    private void renderFinal(Camera camera, StudioShaders shaders)
    {
        Link lightmap = this.engine.world.settings.lightmap;

        if (lightmap != null)
        {
            Texture texture = this.context.getTextures().getTexture(lightmap);

            texture.bind(0);
            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL12.GL_CLAMP_TO_EDGE);
        }

        for (StudioShaders.Stage stage : this.shaders.stages)
        {
            stage.framebuffer.applyClear();

            this.setupCompositeShader(stage.shader, camera, shaders.gbuffer);

            int i = 1;

            for (Texture texture : shaders.gbuffer.textures)
            {
                texture.bind(i++);
            }

            for (Texture texture : stage.inputs)
            {
                texture.bind(i++);
            }

            Framebuffer.renderToQuad(this.context, stage.shader);
            stage.framebuffer.unbind();
        }

        GLStates.activeTexture(0);
        GLStates.resetViewport();

        this.prevProjection.set(camera.projection);
        this.prevView.set(camera.view);
        this.prevPosition.set(camera.position);
    }

    private void setupCompositeShader(Shader shader, Camera camera, Framebuffer framebuffer)
    {
        UniformVector3 position = shader.getUniform("u_camera", UniformVector3.class);
        UniformMatrix4 projection = shader.getUniform("u_projection", UniformMatrix4.class);
        UniformMatrix4 view = shader.getUniform("u_view", UniformMatrix4.class);
        UniformMatrix4 projectionInverse = shader.getUniform("u_projection_inv", UniformMatrix4.class);
        UniformMatrix4 viewInverse = shader.getUniform("u_view_inv", UniformMatrix4.class);
        UniformInt frames = shader.getUniform("u_frames", UniformInt.class);
        UniformFloat far = shader.getUniform("u_far", UniformFloat.class);
        UniformFloat near = shader.getUniform("u_near", UniformFloat.class);
        UniformVector2 screenSize = shader.getUniform("u_screen_size", UniformVector2.class);

        UniformVector3 prevPosition = shader.getUniform("u_prev_camera", UniformVector3.class);
        UniformMatrix4 prevProjection = shader.getUniform("u_prev_projection", UniformMatrix4.class);
        UniformMatrix4 prevView = shader.getUniform("u_prev_view", UniformMatrix4.class);

        if (position != null) position.set(camera.position);
        if (projection != null) projection.set(camera.projection);
        if (view != null) view.set(camera.view);
        if (projectionInverse != null) projectionInverse.set(Matrices.TEMP_4F.set(camera.projection).invert());
        if (viewInverse != null) viewInverse.set(Matrices.TEMP_4F.set(camera.view).invert());
        if (frames != null) frames.set(this.frames);
        if (near != null) near.set(camera.near);
        if (far != null) far.set(camera.far);
        if (screenSize != null)
        {
            Texture mainTexture = framebuffer.getMainTexture();

            screenSize.set(mainTexture.width, mainTexture.height);
        }

        if (prevPosition != null) prevPosition.set(this.prevPosition);
        if (prevProjection != null) prevProjection.set(this.prevProjection);
        if (prevView != null) prevView.set(this.prevView);

        this.updateSky(shader, this.engine.world.settings);
    }

    private void renderFinalQuad()
    {
        GLStates.depthMask(false);

        Texture texture = this.shaders.stages.get(this.shaders.stages.size() - 1).framebuffer.getMainTexture();

        texture.bind(0);
        Framebuffer.renderToQuad(this.context, this.finalShader);

        GLStates.depthMask(true);
    }

    public void renderFrameToQuality(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen, float quality, Runnable rendering)
    {
        if (quality <= 0)
        {
            quality = StudioSettings.renderQuality.get();
        }

        Texture mainTexture = framebuffer.getMainTexture();

        int w = (int) (mainTexture.width * quality);
        int h = (int) (mainTexture.height * quality);

        /* Texture gbufferAlbedo = this.renderToGbufferFramebuffer.getMainTexture();

        int lastW = gbufferAlbedo.width;
        int lastH = gbufferAlbedo.height;

        if (lastW != w || lastH != h)
        {
            this.renderToGbufferFramebuffer.resize(w, h);
            this.renderToComposite1Framebuffer.resize(w, h);
        }

        this.renderFrameTo(camera, this.renderToGbufferFramebuffer, pass, renderScreen);
        this.renderFinal(camera, this.renderToComposite0Framebuffer, this.renderToComposite1Framebuffer, this.renderToGbufferFramebuffer);

        framebuffer.apply();
        framebuffer.clear();

        this.renderToComposite1Framebuffer.getMainTexture().bind(0);
        Framebuffer.renderToQuad(this.context, this.finalShader);

        if (rendering != null)
        {
            rendering.run();
        }

        framebuffer.unbind();
        GLStates.resetViewport(); */
    }

    public void renderFrameTo(Camera camera, Framebuffer framebuffer, int pass, boolean renderScreen)
    {
        framebuffer.applyClear();

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

        this.shaders.resize(width, height);
    }
}