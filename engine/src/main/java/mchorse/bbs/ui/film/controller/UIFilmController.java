package mchorse.bbs.ui.film.controller;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.RunnerCameraController;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.replays.Replay;
import mchorse.bbs.film.replays.ReplayKeyframes;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.film.replays.UIRecordOverlayPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.StencilFormFramebuffer;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyAction;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.FormComponent;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class UIFilmController extends UIElement
{
    public final UIFilmPanel panel;

    public final List<Entity> entities = new ArrayList<>();

    /* Character control */
    private Entity controlled;
    private final Vector3f direction = new Vector3f();
    private final Vector2f walkDirection = new Vector2f();
    private final Vector2i lastMouse = new Vector2i();
    private int mouseMode;
    private final Vector2f mouseStick = new Vector2f();

    /* Recording state */
    private int recordingTick;
    private boolean recording;
    private int recordingCountdown;
    private List<String> recordingGroups;
    private BaseType recordingOld;

    /* Replay and group picking */
    private Entity hoveredEntity;
    private Camera stencilCamera = new Camera();
    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();

    public final OrbitFilmCameraController orbit = new OrbitFilmCameraController(this);
    private int pov;
    private RayTraceResult result = new RayTraceResult();

    public UIFilmController(UIFilmPanel panel)
    {
        this.panel = panel;

        IKey category = IKey.lazy("Player controller");

        this.keys().register(new KeyCombo(IKey.lazy("Start recording"), GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_LEFT_CONTROL), this::pickRecording).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Insert keyframe"), GLFW.GLFW_KEY_I), this::insertFrame).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Toggle orbit"), GLFW.GLFW_KEY_O), this::toggleOrbit).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Toggle actor control"), GLFW.GLFW_KEY_H), this::toggleControl).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Toggle orbit mode"), GLFW.GLFW_KEY_P), () -> this.setPov(this.pov + 1)).category(category);

        this.noCulling();
    }

    private int getTick()
    {
        return this.panel.getRunner().ticks;
    }

    private Replay getReplay()
    {
        return this.panel.replays.replays.getCurrentFirst();
    }

    public StencilFormFramebuffer getStencil()
    {
        return this.stencil;
    }

    public Entity getCurrentEntity()
    {
        int index = this.panel.replays.replays.getIndex();

        if (CollectionUtils.inRange(this.entities, index))
        {
            return this.entities.get(index);
        }

        return null;
    }

    private int getPovMode()
    {
        return this.pov % 4;
    }

    public void setPov(int pov)
    {
        this.pov = pov;
    }

    private int getMouseMode()
    {
        return this.mouseMode % 6;
    }

    private void setMouseMode(int mode)
    {
        this.mouseMode = mode;

        if (this.controlled != null)
        {
            /* Restore value of the mouse stick */
            int index = this.getMouseMode() - 1;

            if (index >= 0)
            {
                PlayerComponent component = this.controlled.get(PlayerComponent.class);

                this.mouseStick.set(component.sticks[index * 2 + 1], component.sticks[index * 2]);
            }
        }
    }

    private boolean isMouseLookMode()
    {
        return this.getMouseMode() == 0;
    }

    public void createEntities()
    {
        this.stopRecording();

        if (this.controlled != null)
        {
            this.toggleControl();
        }

        UIContext context = this.panel.dashboard.context;

        this.entities.clear();

        Film film = this.panel.getData();

        if (context != null && film != null)
        {
            for (Replay replay : film.replays.getList())
            {
                World world = context.menu.bridge.get(IBridgeWorld.class).getWorld();
                Entity entity = world.architect.create(Link.bbs("player"));

                entity.setWorld(world);
                entity.get(FormComponent.class).setForm(FormUtils.copy(replay.form.get()));
                replay.applyFrame(this.getTick(), entity);
                entity.basic.prevPosition.set(entity.basic.position);
                entity.basic.prevRotation.set(entity.basic.rotation);

                this.entities.add(entity);
            }
        }
    }

    /* Character control state */

    public void toggleControl()
    {
        if (this.controlled != null)
        {
            this.controlled = null;
        }
        else if (this.panel.replays.replays.isSelected())
        {
            this.controlled = this.getCurrentEntity();
        }

        this.walkDirection.set(0, 0);

        Window.toggleMousePointer(this.controlled != null);

        if (this.controlled == null && this.recording)
        {
            this.stopRecording();
        }
    }

    private boolean canControl()
    {
        UIContext context = this.getContext();

        return this.controlled != null && context != null && !UIOverlay.has(context);
    }

    /* Recording */

    public void startRecording(List<String> groups)
    {
        this.recordingTick = this.getTick();
        this.recording = true;
        this.recordingCountdown = 30;
        this.recordingGroups = groups;

        this.recordingOld = this.getReplay().keyframes.toData();

        if (groups != null)
        {
            if (groups.contains(ReplayKeyframes.GROUP_LEFT_STICK))
            {
                this.setMouseMode(1);
            }
            else if (groups.contains(ReplayKeyframes.GROUP_RIGHT_STICK))
            {
                this.setMouseMode(2);
            }
            else if (groups.contains(ReplayKeyframes.GROUP_TRIGGERS))
            {
                this.setMouseMode(3);
            }
        }

        if (this.controlled == null)
        {
            this.toggleControl();
        }

        Window.toggleMousePointer(this.controlled != null);
    }

    public void stopRecording()
    {
        if (!this.recording)
        {
            return;
        }

        this.recording = false;
        this.recordingGroups = null;

        if (this.controlled != null)
        {
            this.toggleControl();
        }

        this.panel.setCursor(this.recordingTick);

        if (this.panel.getRunner().isRunning())
        {
            this.panel.togglePlayback();
        }

        if (this.recordingCountdown > 0)
        {
            return;
        }

        Replay replay = this.getReplay();

        if (replay != null && this.recordingOld != null)
        {
            for (BaseValue value : replay.keyframes.getAll())
            {
                if (value instanceof KeyframeChannel)
                {
                    ((KeyframeChannel) value).simplify();
                }
            }

            BaseType newData = replay.keyframes.toData();

            replay.keyframes.fromData(this.recordingOld);
            replay.keyframes.preNotifyParent();
            replay.keyframes.fromData(newData);
            replay.keyframes.postNotifyParent();

            this.recordingOld = null;
        }

        this.setMouseMode(0);
    }

    /* Input handling */

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (context.mouseButton == 0)
        {
            if (this.hoveredEntity != null)
            {
                int index = this.entities.indexOf(this.hoveredEntity);

                this.panel.replays.setReplay(this.panel.getData().replays.getList().get(index));

                if (!this.panel.replays.isVisible())
                {
                    this.panel.showPanel(this.panel.replays);
                }

                return true;
            }
        }
        else if (context.mouseButton == 2)
        {
            Area area = this.panel.getFramebufferArea(this.panel.getViewportArea());

            if (area.isInside(context) && this.orbit.enabled)
            {
                this.orbit.start(context);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected boolean subMouseScrolled(UIContext context)
    {
        Area area = this.panel.getFramebufferArea(this.panel.getViewportArea());

        if (area.isInside(context) && this.orbit.enabled)
        {
            this.orbit.handleDistance(context);

            return true;
        }

        return super.subMouseScrolled(context);
    }

    @Override
    protected boolean subMouseReleased(UIContext context)
    {
        this.orbit.stop();

        return super.subMouseReleased(context);
    }

    @Override
    protected boolean subKeyPressed(UIContext context)
    {
        if (this.canControl())
        {
            int key = context.getKeyCode();

            if (key == GLFW.GLFW_KEY_A || key == GLFW.GLFW_KEY_S || key == GLFW.GLFW_KEY_D || key == GLFW.GLFW_KEY_W)
            {
                this.walkDirection.set(
                    Window.isKeyPressed(GLFW.GLFW_KEY_A) ? -1 : (Window.isKeyPressed(GLFW.GLFW_KEY_D) ? 1 : 0),
                    Window.isKeyPressed(GLFW.GLFW_KEY_W) ? -1 : (Window.isKeyPressed(GLFW.GLFW_KEY_S) ? 1 : 0)
                );

                return true;
            }
            else if (key == GLFW.GLFW_KEY_LEFT_SHIFT)
            {
                this.controlled.basic.sneak = Window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_SPACE))
            {
                this.jump();

                return true;
            }
            else if (context.getKeyAction() == KeyAction.PRESSED && context.getKeyCode() >= GLFW.GLFW_KEY_1 && context.getKeyCode() <= GLFW.GLFW_KEY_6)
            {
                /* Switch mouse input mode */
                this.setMouseMode(context.getKeyCode() - GLFW.GLFW_KEY_1);

                return true;
            }
        }

        return super.subKeyPressed(context);
    }

    public void pickRecording()
    {
        Window.toggleMousePointer(false);

        UIOverlay.addOverlay(this.getContext(), new UIRecordOverlayPanel(
            IKey.lazy("Record"),
            IKey.lazy("Pick a keyframe group that you want to record:"),
            this::startRecording
        ));
    }

    private void toggleOrbit()
    {
        this.orbit.enabled = !this.orbit.enabled;
    }

    public void handleCamera(Camera camera, float transition)
    {
        if (this.orbit.enabled)
        {
            int mode = this.getPovMode();

            if (mode == 0)
            {
                this.orbit.setup(camera, transition);
            }
            else
            {
                this.handleFirstThirdPerson(camera, transition, mode);
            }
        }
    }

    private void handleFirstThirdPerson(Camera camera, float transition, int mode)
    {
        Entity controller = this.getCurrentEntity();
        Vector3d position = new Vector3d();
        Vector3f rotation = new Vector3f();
        float distance = this.orbit.getDistance();
        boolean back = mode == 2;

        BasicComponent basic = controller.basic;

        position.set(basic.prevPosition);
        position.lerp(basic.position, transition);
        position.y += basic.getEyeHeight();

        rotation.set(basic.prevRotation);
        rotation.lerp(basic.rotation, transition);

        camera.fov = BBSSettings.getFov();

        if (mode == 1)
        {
            camera.position.set(position);
            camera.rotation.set(rotation.x, rotation.y, 0F);

            return;
        }

        Vector3f rotate = Matrices.rotation(-rotation.x, (back ? 0F : MathUtils.PI) - rotation.y);
        World world = this.panel.dashboard.bridge.get(IBridgeWorld.class).getWorld();

        RayTracer.trace(this.result, world.chunks, position, rotate, distance, true, (b) ->
        {
            IBlockVariant block = world.chunks.getBlock(b.block.x, b.block.y, b.block.z);

            return block.getModel().opaque;
        });

        if (this.result.type == RayTraceType.BLOCK)
        {
            distance = (float) position.distance(this.result.hit) - 0.1F;
        }

        rotate.mul(distance);
        position.add(rotate);

        camera.position.set(position);

        if (back)
        {
            camera.rotation.set(rotation.x, rotation.y, 0);
        }
        else
        {
            camera.rotation.set(-rotation.x, MathUtils.PI + rotation.y, 0);
        }
    }

    private void jump()
    {
        this.controlled.basic.velocity.y = this.controlled.basic.sneak ? 0.4F : 0.5F;
        this.controlled.basic.velocity.x *= 1.2F;
        this.controlled.basic.velocity.z *= 1.2F;
        this.controlled.basic.grounded = false;
    }

    public void insertFrame()
    {
        Window.toggleMousePointer(false);

        UIRecordOverlayPanel panel = new UIRecordOverlayPanel(
            IKey.lazy("Insert keyframe"),
            IKey.lazy("Pick a keyframe group that you want to insert:"),
            (groups) ->
            {
                Replay replay = this.getReplay();

                if (replay == null)
                {
                    return;
                }

                BaseValue.edit(replay.keyframes, (keyframes) ->
                {
                    Entity entity = this.getCurrentEntity();

                    keyframes.record(this.getTick(), entity, groups);
                });
            }
        );

        panel.onClose((event) -> Window.toggleMousePointer(this.controlled != null));

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    /* Update */

    public void update()
    {
        Film film = this.panel.getData();

        if (film == null)
        {
            return;
        }

        RunnerCameraController runner = this.panel.getRunner();
        UIContext context = this.getContext();

        this.handleRecording(runner);
        this.updateEntities(film, runner, context);

        if (this.canControl())
        {
            this.updateControls();
        }
    }

    private void handleRecording(RunnerCameraController runner)
    {
        if (this.recording)
        {
            if (this.recordingCountdown > 0)
            {
                this.recordingCountdown -= 1;

                if (this.recordingCountdown <= 0)
                {
                    this.panel.togglePlayback();
                }
            }

            if (this.recordingCountdown <= 0 && !runner.isRunning())
            {
                this.stopRecording();
            }
        }
    }

    private void updateEntities(Film film, RunnerCameraController runner, UIContext context)
    {
        for (int i = 0; i < this.entities.size(); i++)
        {
            Entity entity = this.entities.get(i);

            if (context == null || !UIOverlay.has(context))
            {
                entity.update();
            }

            List<Replay> replays = film.replays.getList();

            if (CollectionUtils.inRange(replays, i))
            {
                Replay replay = replays.get(i);
                int ticks = runner.ticks;

                if (entity != this.controlled || (this.recording && this.recordingCountdown <= 0 && this.recordingGroups != null))
                {
                    replay.applyFrame(ticks, entity, entity == this.controlled ? this.recordingGroups : null);
                }

                if (entity == this.controlled && this.recording && runner.isRunning())
                {
                    replay.keyframes.record(ticks, entity, this.recordingGroups);
                }

                replay.applyProperties(ticks, entity, runner.isRunning());
            }
        }
    }

    private void updateControls()
    {
        Entity controller = this.controlled;
        float moveX = this.walkDirection.x;
        float moveZ = this.walkDirection.y;

        if (moveZ != 0 || moveX != 0)
        {
            this.direction.set(moveX, 0, moveZ).normalize().mul(0.25F);

            BasicComponent basic = controller.basic;

            Matrices.rotate(this.direction, 0, -basic.rotation.y);
            this.direction.mul((basic.sneak ? 0.33F : 1F) * (basic.grounded ? 1F : 0.05F));
            this.direction.mul(basic.speed);

            basic.velocity.x += this.direction.x;
            basic.velocity.z += this.direction.z;
        }

        if (!this.isMouseLookMode())
        {
            PlayerComponent component = controller.get(PlayerComponent.class);

            if (component != null)
            {
                int index = this.getMouseMode() - 1;

                component.sticks[index * 2] = this.mouseStick.y;
                component.sticks[index * 2 + 1] = this.mouseStick.x;
            }
        }
    }

    /* Render */

    public void renderHUD(UIContext context, Area area)
    {
        int mode = this.getMouseMode();

        if (this.controlled != null)
        {
            /* Render helpful guides for sticks and triggers controls */
            if (mode > 0)
            {
                String label = "Left stick";

                if (mode == 2)
                {
                    label = "Right stick";
                }
                else if (mode == 3)
                {
                    label = "Triggers";
                }
                else if (mode == 4)
                {
                    label = "Extra 1";
                }
                else if (mode == 5)
                {
                    label = "Extra 2";
                }

                context.batcher.textCard(context.font, label, area.x + 5, area.ey() - 5 - context.font.getHeight(), Colors.WHITE, BBSSettings.primaryColor(Colors.A100));

                int ww = (int) (Math.min(area.w, area.h) * 0.75F);
                int hh = ww;
                int x = area.x + (area.w - ww) / 2;
                int y = area.y + (area.h - hh) / 2;
                int color = Colors.setA(Colors.WHITE, 0.5F);

                context.batcher.outline(x, y, x + ww, y + hh, color);

                int bx = area.x + area.w / 2 + (int) ((this.mouseStick.y) * ww / 2);
                int by = area.y + area.h / 2 + (int) ((this.mouseStick.x) * hh / 2);

                context.batcher.box(bx - 4, by - 4, bx + 4, by + 4, color);
            }

            /* Render reording overlay */
            if (this.recording)
            {
                int x = area.x + 5 + 16;
                int y = area.y + 5;

                context.batcher.icon(Icons.SPHERE, Colors.RED | Colors.A100, x, y, 1F, 0F);

                if (this.recordingCountdown <= 0)
                {
                    context.batcher.textCard(context.font, this.getTick() + " ticks", x + 3, y + 4, Colors.WHITE, Colors.A50);
                }
                else
                {
                    context.batcher.textCard(context.font, String.valueOf(this.recordingCountdown / 20F), x + 3, y + 4, Colors.WHITE, Colors.A50);
                }
            }

            context.batcher.outlinedIcon(Icons.POSE, area.ex() - 5, area.y + 5, 1F, 0F);
        }

        this.renderPickingPreview(context, area);

        this.orbit.handleOrbiting(context);
    }

    private void renderPickingPreview(UIContext context, Area area)
    {
        if (!this.stencil.hasPicked())
        {
            return;
        }

        int index = this.stencil.getIndex();
        Texture texture = this.stencil.getFramebuffer().getMainTexture();
        Pair<Form, String> pair = this.stencil.getPicked();
        int w = texture.width;
        int h = texture.height;

        Shader shader = context.render.getPickingShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);

        CommonShaderAccess.setTarget(shader, index);
        context.batcher.texturedBox(shader, texture, Colors.WHITE, area.x, area.y, area.w, area.h, 0, h, w, 0, w, h);

        if (pair != null)
        {
            String label = pair.a.getIdOrName();

            if (!pair.b.isEmpty())
            {
                label += " - " + pair.b;
            }

            context.batcher.textCard(context.font, label, context.mouseX + 12, context.mouseY + 8);
        }
    }

    public void renderFrame(RenderingContext context)
    {
        for (Entity entity : this.entities)
        {
            if (!(this.getPovMode() == 1 && entity == getCurrentEntity() && this.orbit.enabled))
            {
                entity.render(context);
            }
        }

        this.rayTraceEntity(context);
        this.renderStencil(this.getContext());

        MouseInput mouse = BBS.getEngine().mouse;
        int x = mouse.x;
        int y = mouse.y;

        if (this.canControl())
        {
            float sensitivity = 400F;
            Entity controller = this.controlled;

            if (this.isMouseLookMode())
            {
                /* Control head direction */
                BasicComponent basic = controller.basic;

                float xx = (y - this.lastMouse.y) / sensitivity;
                float yy = (x - this.lastMouse.x) / sensitivity;

                if (xx != 0 || yy != 0)
                {
                    basic.rotation.x += xx;
                    basic.rotation.y += yy;
                    basic.rotation.x = MathUtils.clamp(basic.rotation.x, -MathUtils.PI / 2, MathUtils.PI / 2);
                    basic.prevRotation.x = basic.rotation.x;
                    basic.prevRotation.y = basic.rotation.y;
                }
            }
            else
            {
                /* Control sticks and triggers variables */
                sensitivity = 50F;

                float xx = (y - this.lastMouse.y) / sensitivity;
                float yy = (x - this.lastMouse.x) / sensitivity;

                this.mouseStick.add(xx, yy);
                this.mouseStick.x = MathUtils.clamp(this.mouseStick.x, -1F, 1F);
                this.mouseStick.y = MathUtils.clamp(this.mouseStick.y, -1F, 1F);
            }
        }

        this.lastMouse.set(x, y);
    }

    private void rayTraceEntity(RenderingContext context)
    {
        this.hoveredEntity = null;

        if (!Window.isAltPressed())
        {
            return;
        }

        UIContext c = this.getContext();
        Area area = this.panel.getFramebufferArea(this.panel.getViewportArea());

        if (!area.isInside(c))
        {
            return;
        }

        List<Entity> entities = new ArrayList<>();
        Camera camera = this.panel.getCamera();
        Vector3f mouseDirection = camera.getMouseDirection(c.mouseX, c.mouseY, area);

        for (Entity entity : this.entities)
        {
            AABB aabb = entity.getPickingHitbox();

            if (aabb.intersectsRay(camera.position, mouseDirection))
            {
                entities.add(entity);
            }
        }

        if (!entities.isEmpty())
        {
            entities.sort((a, b) -> (int) (a.basic.position.distanceSquared(camera.position) - b.basic.position.distanceSquared(camera.position)));

            this.hoveredEntity = entities.get(0);
        }

        if (this.hoveredEntity != null)
        {
            AABB aabb = this.hoveredEntity.getPickingHitbox();

            Draw.renderBox(context, aabb.x, aabb.y, aabb.z, aabb.w, aabb.h, aabb.d, 0F, 0.5F, 1F);
        }
    }

    private void renderStencil(UIContext context)
    {
        Area viewport = this.panel.getFramebufferArea(this.panel.getViewportArea());

        if (!viewport.isInside(context) || this.controlled != null)
        {
            this.stencil.clearPicking();

            return;
        }

        Entity entity = this.getCurrentEntity();

        if (entity != null)
        {
            return;
        }

        Camera camera = context.render.getCamera();

        this.ensureStencilFramebuffer();

        Texture mainTexture = this.stencil.getFramebuffer().getMainTexture();

        this.stencilCamera.copy(this.panel.getCamera());
        this.stencilCamera.updatePerspectiveProjection(mainTexture.width, mainTexture.height);
        context.render.getUBO().update(this.stencilCamera.projection, this.stencilCamera.view);

        this.stencil.apply(context);
        context.render.setCamera(this.stencilCamera);
        entity.render(context.render);
        context.render.setCamera(camera);

        Area area = this.panel.getFramebufferArea(this.panel.getViewportArea());
        int x = (int) ((context.mouseX - area.x) / (float) area.w * mainTexture.width);
        int y = (int) ((1F - (context.mouseY - area.y) / (float) area.h) * mainTexture.height);

        this.stencil.pick(x, y);
        this.stencil.unbind(context);

        context.render.getUBO().update(context.render.projection, Matrices.EMPTY_4F);
    }

    private void ensureStencilFramebuffer()
    {
        this.stencil.setup(Link.bbs("stencil_film"));

        Texture mainTexture = this.stencil.getFramebuffer().getMainTexture();
        int w = BBSSettings.videoWidth.get();
        int h = BBSSettings.videoHeight.get();

        if (mainTexture.width != w || mainTexture.height != h)
        {
            this.stencil.resizeGUI(w, h);
        }
    }
}