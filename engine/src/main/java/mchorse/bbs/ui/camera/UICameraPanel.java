package mchorse.bbs.ui.camera;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.clips.misc.Subtitle;
import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.camera.controller.RunnerCameraController;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.RecordComponent;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.resources.Link;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.camera.utils.undo.ValueChangeUndo;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UIDraggable;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.recording.scene.UISceneClip;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.resizers.IResizer;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.VectorUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;
import mchorse.bbs.utils.undo.UndoManager;
import mchorse.bbs.world.entities.Entity;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Camera editor GUI
 *
 * This GUI provides tools for managing camera profiles.
 */
public class UICameraPanel extends UIDataDashboardPanel<CameraWork> implements IFlightSupported, IUICameraWorkDelegate
{
    private static Map<Class, Integer> scrolls = new HashMap<>();

    /**
     * Profile runner
     */
    private RunnerCameraController runner;

    private boolean lastRunning;

    /**
     * Position
     */
    public Position position = new Position(0, 0, 0, 0, 0);

    /**
     * Last position
     */
    public Position lastPosition = new Position(0, 0, 0, 0, 0);

    /* GUI fields */

    public UICameraRecorder recorder;
    public UICameraWork timeline;

    public UIIcon plause;
    public UIIcon record;
    public UIIcon openVideos;

    /* Widgets */
    public UIClip panel;
    public UIRenderable renderableOverlay;

    public UIDraggable draggable;

    private Camera camera = new Camera();
    private Entity hoveredEntity;

    private UndoManager<StructureBase> undoManager;

    /**
     * Initialize the camera editor with a camera profile.
     */
    public UICameraPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.runner = new RunnerCameraController(dashboard.bridge);

        this.recorder = new UICameraRecorder(this);
        this.timeline = new UICameraWork(this);
        this.timeline.relative(this.editor).y(1F).w(1F).h(150).anchorY(1F);

        /* Setup elements */
        this.plause = new UIIcon(Icons.PLAY, (b) -> this.togglePlayback());
        this.plause.tooltip(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PLAUSE, Direction.BOTTOM);
        this.record = new UIIcon(Icons.SPHERE, (b) -> this.recorder.startRecording());
        this.record.tooltip(UIKeys.CAMERA_TOOLTIPS_RECORD, Direction.LEFT);
        this.openVideos = new UIIcon(Icons.FILM, (b) -> this.recorder.openMovies());
        this.openVideos.tooltip(UIKeys.CAMERA_TOOLTIPS_OPEN_VIDEOS, Direction.LEFT);

        this.draggable = new UIDraggable((context) ->
        {
            int diff = this.area.ey() - context.mouseY;
            int max = MathUtils.clamp(diff - diff % 10, 70, Math.min(this.editor.area.h - 70, 410));
            int h = this.timeline.area.h;

            if (max != h)
            {
                ScrollArea vertical = this.timeline.vertical;
                int bottom = vertical.scroll + vertical.area.h;

                this.timeline.h(max);
                this.resize();

                if (!this.timeline.hasEmbeddedView())
                {
                    vertical.scroll = bottom - vertical.area.h;
                    vertical.clamp();
                }
            }
        });
        this.draggable.hoverOnly().relative(this.timeline).x(0.5F, -40).y(-3).wh(80, 6);

        this.iconBar.add(this.plause, this.record, this.openVideos);

        /* Adding everything */
        UIRenderable renderable = new UIRenderable((context) ->
        {
            /* Display position variables */
            if (this.isFlightEnabled() && BBSSettings.editorDisplayPosition.get())
            {
                this.renderPosition(context, this.timeline.area);
            }

            this.renderIcons(context);
        });

        this.editor.add(this.timeline, renderable);
        this.timeline.add(this.draggable);
        this.overlay.namesList.setFileIcon(Icons.FRUSTUM);

        /* Register keybinds */
        IKey clips = UICameraWork.KEYS_CATEGORY;
        IKey modes = UIKeys.CAMERA_EDITOR_KEYS_MODES_TITLE;
        IKey editor = UIKeys.CAMERA_EDITOR_KEYS_EDITOR_TITLE;
        IKey looping = UIKeys.CAMERA_EDITOR_KEYS_LOOPING_TITLE;
        Supplier<Boolean> active = this::isFlightDisabled;

        this.keys().register(Keys.PLAUSE, () -> this.plause.clickItself()).active(active).category(editor);
        this.keys().register(Keys.NEXT_CLIP, this::jumpToNextClip).active(active).category(editor);
        this.keys().register(Keys.PREV_CLIP, this::jumpToPrevClip).active(active).category(editor);
        this.keys().register(Keys.NEXT, () -> this.timeline.setTickAndNotify(this.timeline.tick + 1)).active(active).category(editor);
        this.keys().register(Keys.PREV, () -> this.timeline.setTickAndNotify(this.timeline.tick - 1)).active(active).category(editor);
        this.keys().register(Keys.UNDO, this::undo).category(editor);
        this.keys().register(Keys.REDO, this::redo).category(editor);
        this.keys().register(Keys.DESELECT, () -> this.pickClip(null)).active(active).category(clips);
        this.keys().register(Keys.FLIGHT, () -> this.setFlight(this.isFlightDisabled())).active(() -> this.data != null).category(modes);
        this.keys().register(Keys.LOOPING, () -> BBSSettings.editorLoop.set(!BBSSettings.editorLoop.get())).active(active).category(looping);
        this.keys().register(Keys.LOOPING_SET_MIN, () -> this.timeline.setLoopMin()).active(active).category(looping);
        this.keys().register(Keys.LOOPING_SET_MAX, () -> this.timeline.setLoopMax()).active(active).category(looping);
        this.keys().register(Keys.JUMP_FORWARD, () -> this.timeline.setTickAndNotify(this.timeline.tick + BBSSettings.editorJump.get())).active(active).category(editor);
        this.keys().register(Keys.JUMP_BACKWARD, () -> this.timeline.setTickAndNotify(this.timeline.tick - BBSSettings.editorJump.get())).active(active).category(editor);

        this.fill(null);
    }

    public Framebuffer getFramebuffer()
    {
        return BBS.getFramebuffers().getFramebuffer(Link.bbs("camera"), (framebuffer) ->
        {
            Texture texture = new Texture();
            int width = BBSSettings.videoWidth.get();
            int height = BBSSettings.videoHeight.get();

            texture.setFilter(GL11.GL_LINEAR);
            texture.setWrap(GL13.GL_CLAMP_TO_EDGE);
            texture.setSize(width, height);

            framebuffer.deleteTextures().attach(texture, GL30.GL_COLOR_ATTACHMENT0);
        });
    }

    private Area getViewportArea()
    {
        return new Area(this.area.x, this.area.y, this.iconBar.area.x - this.area.x, this.timeline.area.y - this.area.y);
    }

    private Area getFramebufferArea()
    {
        return this.getFramebufferArea(this.getViewportArea());
    }

    private Area getFramebufferArea(Area viewport)
    {
        int width = BBSSettings.videoWidth.get();
        int height = BBSSettings.videoHeight.get();

        Camera camera = new Camera();

        camera.copy(this.dashboard.bridge.get(IBridgeCamera.class).getCamera());
        camera.updatePerspectiveProjection(width, height);

        Vector2i size = VectorUtils.resize(width / (float) height, viewport.w, viewport.h);
        Area area = new Area();

        area.setSize(size.x, size.y);
        area.setPos(viewport.mx() - area.w / 2, viewport.my() - area.h / 2);

        return area;
    }

    public RunnerCameraController getRunner()
    {
        return this.runner;
    }

    @Override
    public void open()
    {
        super.open();

        if (this.panel != null)
        {
            this.panel.cameraEditorWasOpened();
        }
    }

    @Override
    public void appear()
    {
        super.appear();

        this.fillData();
        this.setFlight(false);
        this.dashboard.bridge.get(IBridgeCamera.class).getCameraController().add(this.runner);
        this.dashboard.getRoot().prepend(this.renderableOverlay);

        if (this.dashboard.isWalkMode())
        {
            this.dashboard.toggleWalkMode();
        }
    }

    @Override
    public void close()
    {
        super.close();

        this.timeline.embedView(null);
        this.setFlight(false);
        this.dashboard.bridge.get(IBridgeCamera.class).getCameraController().remove(this.runner);

        this.disableContext();
    }

    @Override
    public void disappear()
    {
        super.disappear();

        this.setFlight(false);
        this.dashboard.bridge.get(IBridgeCamera.class).getCameraController().remove(this.runner);
        this.dashboard.getRoot().remove(this.renderableOverlay);

        this.disableContext();
    }

    private void disableContext()
    {
        ClipContext context = this.runner.getContext();

        context.shutdown();
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public boolean canRefresh()
    {
        return false;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.CAMERAS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_CAMERAS;
    }

    @Override
    public void fill(CameraWork data)
    {
        if (this.data != null)
        {
            this.disableContext();
        }

        this.undoManager = data == null ? null : new UndoManager<>(30);

        super.fill(data);

        this.plause.setEnabled(data != null);
        this.record.setEnabled(data != null);

        this.runner.setWork(data);
        this.timeline.setClips(data == null ? null : data.clips);
        this.pickClip(null);
    }

    private void handleUndos(IUndo<StructureBase> undo, boolean redo)
    {
        IUndo<StructureBase> anotherUndo = undo;

        if (anotherUndo instanceof CompoundUndo)
        {
            anotherUndo = ((CompoundUndo<StructureBase>) anotherUndo).getFirst(ValueChangeUndo.class);
        }

        if (anotherUndo instanceof ValueChangeUndo)
        {
            ValueChangeUndo change = (ValueChangeUndo) anotherUndo;
            List<Integer> selection = change.getSelection(redo);

            if (selection.isEmpty())
            {
                this.pickClip(null);
            }
            else
            {
                this.timeline.setSelection(selection);

                Clip last = this.data.clips.get(selection.get(selection.size() - 1));

                this.pickClip(last);
            }

            this.timeline.scale.view(change.viewMin, change.viewMax);
            this.timeline.setTickAndNotify(change.tick);
            this.timeline.vertical.scrollTo(change.scroll);
        }

        if (this.panel != null)
        {
            this.panel.handleUndo(undo, redo);
        }
    }

    public void markLastUndoNoMerging()
    {
        if (this.data == null)
        {
            return;
        }

        IUndo<StructureBase> undo = this.undoManager.getCurrentUndo();

        if (undo != null)
        {
            undo.noMerging();
        }
    }

    public void undo()
    {
        CameraWork work = this.data;

        if (work != null && this.undoManager.undo(work))
        {
            UIUtils.playClick();
        }
    }

    public void redo()
    {
        CameraWork work = this.data;

        if (work != null && this.undoManager.redo(work))
        {
            UIUtils.playClick();
        }
    }

    public CameraWork getWork()
    {
        return this.data;
    }

    public boolean isRunning()
    {
        return this.runner.isRunning();
    }

    public boolean isFlightEnabled()
    {
        return this.dashboard.orbitUI.canControl();
    }

    public boolean isFlightDisabled()
    {
        return !this.dashboard.orbitUI.canControl();
    }

    /**
     * Set flight mode
     */
    public void setFlight(boolean flight)
    {
        this.runner.setManual(flight ? this.position : null);

        if (!this.isRunning() || !flight)
        {
            this.dashboard.orbitUI.setControl(flight);

            /* Marking the latest undo as unmergeable */
            if (!flight)
            {
                this.markLastUndoNoMerging();
            }
            else
            {
                this.lastPosition.set(Position.ZERO);
            }
        }
    }

    public int getSelected()
    {
        return this.panel == null ? -1 : this.data.clips.getIndex(this.panel.clip);
    }

    /**
     * Update display icon of the plause button
     */
    private void updatePlauseButton()
    {
        this.plause.both(this.isRunning() ? Icons.PAUSE : Icons.PLAY);
    }

    private void editClip()
    {
        Position current = new Position(this.getCamera());

        if (this.panel != null)
        {
            if (!this.lastPosition.equals(current))
            {
                this.panel.editClip(current);
            }
        }

        this.lastPosition.set(current);
    }

    private void jumpToNextClip()
    {
        this.timeline.setTickAndNotify(this.data.findNextTick(this.timeline.tick));
    }

    private void jumpToPrevClip()
    {
        this.timeline.setTickAndNotify(this.data.findPreviousTick(this.timeline.tick));
    }

    public void togglePlayback()
    {
        this.setFlight(false);

        this.runner.toggle(this.timeline.tick);
        this.lastRunning = this.runner.isRunning();
        this.updatePlauseButton();
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (context.mouseButton == 0 && this.hoveredEntity != null && this.getClip() instanceof SceneClip)
        {
            Area area = this.getFramebufferArea();

            if (area.isInside(context))
            {
                RecordComponent component = this.hoveredEntity.get(RecordComponent.class);

                UISceneClip.openRecordEditor(this, (SceneClip) this.getClip(), component.player.record);
            }
        }

        return super.subMouseClicked(context);
    }

    /* Rendering code */

    @Override
    public void renderInWorld(RenderingContext context)
    {
        super.renderInWorld(context);

        UIContext c = this.getContext();
        Camera camera = this.camera;
        Area area = this.getFramebufferArea();

        this.hoveredEntity = null;

        if (!area.isInside(c) || !(this.getClip() instanceof SceneClip))
        {
            return;
        }

        List<Entity> entities = new ArrayList<>();

        for (Entity entity : context.getWorld().entities)
        {
            AABB aabb = entity.getPickingHitbox();
            RecordComponent record = entity.get(RecordComponent.class);

            if (record != null && record.player != null && aabb.intersectsRay(camera.position, camera.getMouseDirection(c.mouseX, c.mouseY, area)))
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

    /**
     * Draw everything on the screen
     */
    @Override
    public void render(UIContext context)
    {
        this.updateLogic(context);
        this.renderOverlays(context);
        this.renderEditorsBackground(context);

        super.render(context);
    }

    /**
     * Update logic for such components as repeat fixture, minema recording,
     * sync mode, flight mode, etc.
     */
    private void updateLogic(UIContext context)
    {
        Clip clip = this.getClip();

        /* Loop fixture */
        if (BBSSettings.editorLoop.get() && this.isRunning())
        {
            long min = -1;
            long max = -1;

            if (clip != null)
            {
                min = clip.tick.get();
                max = min + clip.duration.get();
            }

            if (this.timeline.loopMin != this.timeline.loopMax && this.timeline.loopMin >= 0 && this.timeline.loopMin < this.timeline.loopMax)
            {
                min = this.timeline.loopMin;
                max = this.timeline.loopMax;
            }

            max = Math.min(max, this.data.clips.calculateDuration());

            if (min >= 0 && max >= 0 && min < max && (this.runner.ticks >= max - 1 || this.runner.ticks < min))
            {
                this.timeline.setTickAndNotify((int) min);
            }
        }

        /* Animate flight mode */
        if (this.dashboard.orbitUI.canControl())
        {
            this.dashboard.orbit.apply(this.position);

            this.editClip();
        }
        else
        {
            this.dashboard.orbit.setup(this.getCamera());
        }

        /* Update playback timeline */
        if (this.isRunning())
        {
            this.timeline.setTick(this.runner.ticks);
        }

        /* Rewind playback back to 0 */
        if (this.lastRunning && !this.isRunning())
        {
            this.lastRunning = this.runner.isRunning();
            this.timeline.setTickAndNotify(0);

            this.updatePlauseButton();
        }
    }

    /**
     * Draw little squares behind some visible elements
     */
    private void renderEditorsBackground(UIContext context)
    {
        context.batcher.gradientVBox(this.area.x, this.area.y, this.area.ex(), this.area.y + 20, Colors.A50, 0);
    }

    /**
     * Draw icons for indicating different active states (like syncing
     * or flight mode)
     */
    private void renderIcons(UIContext context)
    {
        int x = this.iconBar.area.ex() - 18;
        int y = this.iconBar.area.ey() - 18;

        if (this.dashboard.orbitUI.canControl())
        {
            context.batcher.icon(Icons.ORBIT, x, y);
            y -= 20;
        }

        if (BBSSettings.editorLoop.get())
        {
            context.batcher.icon(Icons.REFRESH, x, y);
        }
    }

    /**
     * Draw information about current camera's position
     */
    private void renderPosition(UIContext context, IResizer panel)
    {
        Camera camera = this.getCamera();
        String[] labels = {
            UIKeys.X + ": " + camera.position.x,
            UIKeys.Y + ": " + camera.position.y,
            UIKeys.Z + ": " + camera.position.z,
            UIKeys.CAMERA_PANELS_YAW + ": " + MathUtils.toDeg(camera.rotation.y),
            UIKeys.CAMERA_PANELS_PITCH + ": " + MathUtils.toDeg(camera.rotation.x),
            UIKeys.CAMERA_PANELS_ROLL + ": " + MathUtils.toDeg(camera.rotation.z),
            UIKeys.CAMERA_PANELS_FOV + ": " + camera.fov
        };

        for (int i = 0; i < labels.length; i++)
        {
            String label = labels[i];
            int width = context.font.getWidth(label);
            int y = panel.getY() - 20 - 15 * (labels.length - i - 1);
            int x = panel.getX() + 10;

            context.batcher.box(x, y - 3, x + width + 4, y + 10, Colors.A75);
            context.batcher.textShadow(label, x + 2, y);
        }
    }

    /**
     * Draw different camera type overlays (custom texture overlay, letterbox,
     * rule of thirds and crosshair)
     */
    private void renderOverlays(UIContext context)
    {
        if (this.data == null)
        {
            return;
        }

        context.batcher.flush();

        Area viewport = this.getViewportArea();

        /* Setup framebuffer */
        Framebuffer framebuffer = this.getFramebuffer();
        Texture texture = framebuffer.getMainTexture();
        int width = BBSSettings.videoWidth.get();
        int height = BBSSettings.videoHeight.get();

        this.camera.copy(this.getCamera());
        this.camera.updatePerspectiveProjection(width, height);

        /* Resize framebuffer if desired width and height changed */
        if (texture.width != width || texture.height != height)
        {
            framebuffer.resize(width, height);
        }

        Area area = this.getFramebufferArea(viewport);

        /* Render the scene to framebuffer */
        GLStates.setupDepthFunction3D();
        this.dashboard.bridge.get(IBridgeRender.class).renderSceneTo(this.camera, framebuffer, 0, true, 0, () ->
        {
            this.renderSubtitles(context, width, height);
        });
        GLStates.setupDepthFunction2D();

        viewport.render(context.batcher, Colors.A75);
        context.batcher.texturedBox(texture, Colors.WHITE, area.x, area.y, area.w, area.h, 0, height, width, 0, width, height);

        /* Render rule of thirds */
        if (BBSSettings.editorRuleOfThirds.get())
        {
            int color = BBSSettings.editorGuidesColor.get();

            context.batcher.box(area.x + area.w / 3 - 1, area.y, area.x + area.w / 3, area.y + area.h, color);
            context.batcher.box(area.x + area.w - area.w / 3, area.y, area.x + area.w - area.w / 3 + 1, area.y + area.h, color);

            context.batcher.box(area.x, area.y + area.h / 3 - 1, area.x + area.w, area.y + area.h / 3, color);
            context.batcher.box(area.x, area.y + area.h - area.h / 3, area.x + area.w, area.y + area.h - area.h / 3 + 1, color);
        }

        if (BBSSettings.editorCenterLines.get())
        {
            int color = BBSSettings.editorGuidesColor.get();
            int x = area.mx();
            int y = area.my();

            context.batcher.box(area.x, y, area.ex(), y + 1, color);
            context.batcher.box(x, area.y, x + 1, area.ey(), color);
        }

        if (BBSSettings.editorCrosshair.get())
        {
            int x = area.mx() + 1;
            int y = area.my() + 1;

            context.batcher.box(x - 4, y - 1, x + 3, y, Colors.setA(Colors.WHITE, 0.5F));
            context.batcher.box(x - 1, y - 4, x, y + 3, Colors.setA(Colors.WHITE, 0.5F));
        }
    }

    private void renderSubtitles(UIContext context, int width, int height)
    {
        width /= 2;
        height /= 2;

        List<Subtitle> subtitles = SubtitleClip.getSubtitles(this.runner.getContext());

        if (subtitles.isEmpty())
        {
            return;
        }

        Matrix4f ortho = new Matrix4f().ortho(0, width, height, 0, -100, 100);

        context.render.getUBO().update(ortho, Matrices.EMPTY_4F);

        for (Subtitle subtitle : subtitles)
        {
            float alpha = Colors.getAlpha(subtitle.color);

            if (alpha <= 0)
            {
                continue;
            }

            String label = TextUtils.processColoredText(subtitle.label);
            int w = context.font.getWidth(label);
            int h = context.font.getHeight();
            int x = (int) (width * subtitle.windowX + subtitle.x);
            int y = (int) (height * subtitle.windowY + subtitle.y);
            float scale = subtitle.size;

            context.render.stack.push();
            context.render.stack.translate(x, y, 0);
            context.render.stack.scale(scale, scale, 1F);

            if (Colors.getAlpha(subtitle.backgroundColor) > 0)
            {
                context.batcher.textCard(context.font, label, (int) (-w * subtitle.anchorX), (int) (-h * subtitle.anchorY), subtitle.color, Colors.mulA(subtitle.backgroundColor, alpha), subtitle.backgroundOffset);
            }
            else
            {
                context.batcher.textShadow(context.font, label, (int) (-w * subtitle.anchorX), (int) (-h * subtitle.anchorY), subtitle.color);
            }

            context.render.stack.pop();
        }

        context.batcher.flush();
        context.render.getUBO().update(context.render.projection, Matrices.EMPTY_4F);
    }

    /* IUICameraWorkDelegate implementation */

    @Override
    public Camera getCamera()
    {
        return this.dashboard.bridge.get(IBridgeCamera.class).getCamera();
    }

    @Override
    public Clip getClip()
    {
        return this.panel == null ? null : this.panel.clip;
    }

    @Override
    public void pickClip(Clip clip)
    {
        if (this.panel != null)
        {
            if (this.panel.clip == clip)
            {
                this.panel.fillData();

                return;
            }
            else
            {
                this.panel.removeFromParent();
            }
        }

        if (clip == null)
        {
            this.panel = null;
            this.timeline.clearSelection();

            return;
        }

        try
        {
            if (this.panel != null)
            {
                scrolls.put(this.panel.getClass(), this.panel.panels.scroll.scroll);
            }

            this.timeline.embedView(null);

            UIClip panel = (UIClip) BBS.getFactoryClips().getData(clip).panelUI.getConstructors()[0].newInstance(clip, this);

            this.panel = panel;
            this.panel.relative(this.editor).w(1F).hTo(this.timeline.area);
            this.editor.addAfter(this.timeline, this.panel);

            this.panel.fillData();
            this.panel.resize();

            Integer scroll = scrolls.get(this.panel.getClass());

            if (scroll != null)
            {
                this.panel.panels.scroll.scroll = scroll;
                this.panel.panels.scroll.clamp();
            }

            if (this.isFlightEnabled())
            {
                this.timeline.setTick(clip.tick.get(), true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getCursor()
    {
        return this.timeline.tick;
    }

    @Override
    public void setCursor(int value, boolean notify)
    {
        this.runner.ticks = value;
    }

    @Override
    public void setTickAndNotify(int tick)
    {
        this.timeline.setTickAndNotify(tick);
    }

    @Override
    public boolean canUseKeybinds()
    {
        return this.isFlightDisabled();
    }

    @Override
    public void fillData()
    {
        if (this.panel != null)
        {
            this.panel.fillData();
        }
    }

    @Override
    public void embedView(UIElement element)
    {
        this.timeline.embedView(element);
    }

    @Override
    public <T extends BaseValue> IUndo createUndo(T property, Consumer<T> consumer)
    {
        BaseType oldValue = property.toData();

        consumer.accept(property);

        BaseType newValue = property.toData();
        ValueChangeUndo undo = new ValueChangeUndo(property.getPath(), oldValue, newValue);

        undo.editor(this);

        return undo;
    }

    @Override
    public <T extends BaseValue> IUndo createUndo(T property, BaseType oldValue, BaseType newValue)
    {
        ValueChangeUndo undo = new ValueChangeUndo(property.getPath(), oldValue, newValue);

        undo.editor(this);

        return undo;
    }

    @Override
    public void postUndo(IUndo undo, boolean apply, boolean callback)
    {
        if (undo == null)
        {
            throw new RuntimeException("Given undo is null!");
        }

        CameraWork work = this.data;
        UndoManager<StructureBase> undoManager = this.undoManager;

        undoManager.setCallback(callback ? this::handleUndos : null);

        if (apply)
        {
            undoManager.pushApplyUndo(undo, work);
        }
        else
        {
            undoManager.pushUndo(undo);
        }

        undoManager.setCallback(this::handleUndos);
    }

    @Override
    public void updateClipProperty(ValueInt property, int value)
    {
        this.timeline.updateClipProperty(property, value);
    }
}