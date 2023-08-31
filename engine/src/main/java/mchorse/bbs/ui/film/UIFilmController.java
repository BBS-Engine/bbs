package mchorse.bbs.ui.film;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.controller.RunnerCameraController;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.values.ValueReplay;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.film.replays.UIRecordOverlayPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.StencilFormFramebuffer;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyAction;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.FormComponent;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class UIFilmController extends UIElement
{
    private UIFilmPanel panel;

    private List<Entity> entities = new ArrayList<>();

    private Entity controlled;
    private final Vector3f direction = new Vector3f();
    private final Vector2f walkDirection = new Vector2f();
    private final Vector2i lastMouse = new Vector2i();

    private int mouseMode;
    private final Vector2f mouseStick = new Vector2f();

    private boolean recording;
    private int recordingCountdown;
    private List<String> recordingGroups;

    private Camera stencilCamera = new Camera();
    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();

    public UIFilmController(UIFilmPanel panel)
    {
        this.panel = panel;

        this.noCulling();
    }

    private int getTick()
    {
        return this.panel.getRunner().ticks;
    }

    private int getMouseMode()
    {
        return this.mouseMode % 4;
    }

    private boolean isMouseLookMode()
    {
        return this.getMouseMode() == 0;
    }

    public void updateEntities()
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
            for (ValueReplay replay : film.replays.replays)
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

    public void toggleControl()
    {
        if (this.controlled != null)
        {
            this.controlled = null;
        }
        else if (this.panel.replays.replays.isSelected())
        {
            this.controlled = this.entities.get(this.panel.replays.replays.getIndex());
        }

        Window.toggleMousePointer(this.controlled != null);
    }

    public void startRecording(List<String> groups)
    {
        this.recording = true;
        this.recordingCountdown = 30;
        this.recordingGroups = groups;

        if (this.controlled == null)
        {
            this.toggleControl();
        }
    }

    public void stopRecording()
    {
        this.recording = false;
        this.recordingGroups = null;

        if (this.controlled != null)
        {
            this.toggleControl();
        }
    }

    private boolean canControl()
    {
        UIContext context = this.getContext();

        return this.controlled != null && context != null && !UIOverlay.has(context);
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.stencil.hasPicked() && context.mouseButton == 0)
        {
            Pair<Form, String> pair = this.stencil.getPicked();

            if (pair != null)
            {
                this.panel.replays.pickForm(pair.a, pair.b);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_R) && Window.isCtrlPressed())
        {
            Window.toggleMousePointer(false);

            UIOverlay.addOverlay(this.getContext(), new UIRecordOverlayPanel(
                IKey.lazy("Record"),
                IKey.lazy("Pick a keyframe group that you want to record:"),
                this::startRecording
            ));

            return true;
        }
        else if (context.isPressed(GLFW.GLFW_KEY_H) && !context.isFocused())
        {
            this.toggleControl();

            return true;
        }
        else if (this.canControl())
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
                this.controlled.basic.velocity.y = this.controlled.basic.sneak ? 0.4F : 0.5F;
                this.controlled.basic.velocity.x *= 1.2F;
                this.controlled.basic.velocity.z *= 1.2F;
                this.controlled.basic.grounded = false;

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_I) && Window.isCtrlPressed())
            {
                Window.toggleMousePointer(false);

                UIRecordOverlayPanel panel = new UIRecordOverlayPanel(
                    IKey.lazy("Record"),
                    IKey.lazy("Pick a keyframe group that you want to insert:"),
                    (groups) ->
                    {
                        int index = this.entities.indexOf(this.controlled);

                        if (index >= 0 && CollectionUtils.inRange(this.panel.getData().replays.replays, index))
                        {
                            this.panel.getData().replays.replays.get(index).keyframes.record(this.getTick(), this.controlled, groups);
                        }

                        Window.toggleMousePointer(true);
                    }
                );

                panel.onClose((event) -> Window.toggleMousePointer(true));

                UIOverlay.addOverlay(this.getContext(), panel);

                return true;
            }
            else if (context.getKeyAction() == KeyAction.PRESSED && context.getKeyCode() >= GLFW.GLFW_KEY_1 && context.getKeyCode() <= GLFW.GLFW_KEY_4)
            {
                this.mouseMode = context.getKeyCode() - GLFW.GLFW_KEY_1;

                /* Restore value of the mouse stick */
                int index = this.getMouseMode() - 1;

                if (index >= 0)
                {
                    PlayerComponent component = this.controlled.get(PlayerComponent.class);

                    this.mouseStick.set(component.sticks[index * 2 + 1], component.sticks[index * 2]);
                }

                return true;
            }
        }

        return super.subKeyPressed(context);
    }

    public void update()
    {
        Film film = this.panel.getData();

        if (film == null)
        {
            return;
        }

        RunnerCameraController runner = this.panel.getRunner();
        UIContext context = this.getContext();

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

        for (int i = 0; i < this.entities.size(); i++)
        {
            Entity entity = this.entities.get(i);

            if (context == null || !UIOverlay.has(context))
            {
                entity.update();
            }

            if (CollectionUtils.inRange(film.replays.replays, i))
            {
                ValueReplay replay = film.replays.replays.get(i);
                int ticks = runner.ticks;

                if (entity != this.controlled || (this.recording && this.recordingCountdown <= 0 && this.recordingGroups != null))
                {
                    replay.applyFrame(ticks, entity, this.recordingGroups);
                }

                if (entity == this.controlled && this.recording && runner.isRunning())
                {
                    replay.keyframes.record(ticks, entity, this.recordingGroups);
                }

                replay.applyProperties(ticks, entity, runner.isRunning());
            }
        }

        if (this.canControl())
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
    }

    public void renderHUD(UIContext context, Area area)
    {
        int mode = this.getMouseMode();

        /* Render helpful guides for sticks and triggers controls */
        if (mode > 0 && this.controlled != null)
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
        if (this.recording && this.controlled != null)
        {
            int x = area.x + 5 + 15;
            int y = area.y + 5;

            context.batcher.icon(Icons.SPHERE, Colors.RED | Colors.A100, x - 18, y - 4, 0F, 0F);

            if (this.recordingCountdown <= 0)
            {
                context.batcher.textCard(context.font, String.valueOf(this.getTick()), x, y, Colors.WHITE, Colors.A50);
            }
            else
            {
                context.batcher.textCard(context.font, String.valueOf(this.recordingCountdown / 20F), x, y, Colors.WHITE, Colors.A50);
            }
        }

        this.renderPicking(context, area);
    }

    private void renderPicking(UIContext context, Area area)
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
            entity.render(context);
        }

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

    private void renderStencil(UIContext context)
    {
        int index = this.panel.replays.replays.getIndex();

        if (!CollectionUtils.inRange(this.entities, index))
        {
            return;
        }

        Entity entity = this.entities.get(index);
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