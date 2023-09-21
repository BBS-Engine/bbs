package mchorse.bbs.ui.film;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.audio.AudioRenderer;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.camera.controller.CameraController;
import mchorse.bbs.camera.controller.RunnerCameraController;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.film.Film;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.graphics.Framebuffer;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.film.clips.UIClip;
import mchorse.bbs.ui.film.controller.UIFilmController;
import mchorse.bbs.ui.film.replays.UIReplaysEditor;
import mchorse.bbs.ui.film.screenplay.UIScreenplayEditor;
import mchorse.bbs.ui.film.utils.undo.ValueChangeUndo;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UIDraggable;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;
import mchorse.bbs.utils.undo.UndoManager;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class UIFilmPanel extends UIDataDashboardPanel<Film> implements IFlightSupported, IUIClipsDelegate
{
    private static Map<Class, Integer> scrolls = new HashMap<>();

    private RunnerCameraController runner;
    private boolean lastRunning;
    private final Position position = new Position(0, 0, 0, 0, 0);
    private final Position lastPosition = new Position(0, 0, 0, 0, 0);

    public UIElement main;
    public UIFilmRecorder recorder;

    public UIElement clips;
    public UIClips timeline;
    public UIReplaysEditor replays;

    public UIScreenplayEditor screenplay;

    public UIIcon plause;
    public UIIcon record;
    public UIIcon openVideos;
    public UIIcon openCamera;
    public UIIcon openReplays;
    public UIIcon openScreenplay;

    public UIClip panel;
    public UIRenderable renderableOverlay;
    public UIDraggable draggable;

    private Camera camera = new Camera();

    /* Entity control */
    private UIFilmController controller = new UIFilmController(this);

    private UndoManager<ValueGroup> undoManager;
    private List<Integer> cachedSelection = new ArrayList<>();
    private List<List<Integer>> cachedKeyframeSelection = new ArrayList<>();
    private Vector2i cachedKeyframeSelected = new Vector2i(-1, -1);
    private Map<BaseValue, BaseType> cachedUndo = new HashMap<>();

    /**
     * Initialize the camera editor with a camera profile.
     */
    public UIFilmPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.runner = new RunnerCameraController(dashboard.bridge);

        this.main = new UIElement();
        this.main.relative(this.editor).w(0.75F).h(1F);

        this.clips = new UIElement();
        this.clips.relative(this.main).full();

        this.recorder = new UIFilmRecorder(this);
        this.timeline = new UIClips(this, BBS.getFactoryCameraClips());
        this.timeline.relative(this.clips).full();

        this.replays = new UIReplaysEditor(this);
        this.replays.relative(this.main).full();
        this.replays.setVisible(false);

        this.screenplay = new UIScreenplayEditor(this);
        this.screenplay.relative(this.main).full();
        this.screenplay.setVisible(false);

        /* Setup elements */
        this.plause = new UIIcon(Icons.PLAY, (b) -> this.togglePlayback());
        this.plause.tooltip(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PLAUSE, Direction.BOTTOM);
        this.record = new UIIcon(Icons.SPHERE, (b) -> this.recorder.startRecording(this.data.camera.calculateDuration(), this.getFramebuffer()));
        this.record.tooltip(UIKeys.CAMERA_TOOLTIPS_RECORD, Direction.LEFT);
        this.openVideos = new UIIcon(Icons.FILM, (b) -> this.recorder.openMovies());
        this.openVideos.tooltip(UIKeys.CAMERA_TOOLTIPS_OPEN_VIDEOS, Direction.LEFT);
        this.openCamera = new UIIcon(Icons.FRUSTUM, (b) -> this.showPanel(this.clips));
        this.openReplays = new UIIcon(Icons.SCENE, (b) -> this.showPanel(this.replays));
        this.openScreenplay = new UIIcon(Icons.FILE, (b) -> this.showPanel(this.screenplay));

        this.draggable = new UIDraggable((context) ->
        {
            float value = (context.mouseX - this.main.area.x) / (float) this.editor.area.w;

            this.main.w(MathUtils.clamp(value, 0.05F, 0.95F), 0);
            this.resize();
        });
        this.draggable.hoverOnly().relative(this.main).x(1F, -3).y(0.5F, -40).wh(6, 80);

        this.iconBar.add(this.plause, this.record, this.openVideos, this.openCamera, this.openReplays, this.openScreenplay);

        /* Adding everything */
        UIRenderable renderable = new UIRenderable(this::renderIcons);

        this.editor.add(this.main, renderable);
        this.clips.add(this.timeline);
        this.main.add(this.clips, this.replays, this.screenplay, this.draggable);
        this.add(this.controller);
        this.overlay.namesList.setFileIcon(Icons.FILM);

        /* Register keybinds */
        IKey modes = UIKeys.CAMERA_EDITOR_KEYS_MODES_TITLE;
        IKey editor = UIKeys.CAMERA_EDITOR_KEYS_EDITOR_TITLE;
        IKey looping = UIKeys.CAMERA_EDITOR_KEYS_LOOPING_TITLE;
        Supplier<Boolean> active = this::isFlightDisabled;

        this.keys().register(Keys.PLAUSE, () -> this.plause.clickItself()).active(active).category(editor);
        this.keys().register(Keys.NEXT_CLIP, () -> this.setCursor(this.data.camera.findNextTick(this.getCursor()))).active(active).category(editor);
        this.keys().register(Keys.PREV_CLIP, () -> this.setCursor(this.data.camera.findPreviousTick(this.getCursor()))).active(active).category(editor);
        this.keys().register(Keys.NEXT, () -> this.setCursor(this.getCursor() + 1)).active(active).category(editor);
        this.keys().register(Keys.PREV, () -> this.setCursor(this.getCursor() - 1)).active(active).category(editor);
        this.keys().register(Keys.UNDO, this::undo).category(editor);
        this.keys().register(Keys.REDO, this::redo).category(editor);
        this.keys().register(Keys.FLIGHT, () -> this.setFlight(this.isFlightDisabled())).active(() -> this.data != null).category(modes);
        this.keys().register(Keys.LOOPING, () -> BBSSettings.editorLoop.set(!BBSSettings.editorLoop.get())).active(active).category(looping);
        this.keys().register(Keys.LOOPING_SET_MIN, () -> this.timeline.setLoopMin()).active(active).category(looping);
        this.keys().register(Keys.LOOPING_SET_MAX, () -> this.timeline.setLoopMax()).active(active).category(looping);
        this.keys().register(Keys.JUMP_FORWARD, () -> this.setCursor(this.getCursor() + BBSSettings.editorJump.get())).active(active).category(editor);
        this.keys().register(Keys.JUMP_BACKWARD, () -> this.setCursor(this.getCursor() - BBSSettings.editorJump.get())).active(active).category(editor);

        this.fill(null);
    }

    public void showPanel(UIElement element)
    {
        this.clips.setVisible(false);
        this.replays.setVisible(false);
        this.screenplay.setVisible(false);

        element.setVisible(true);
    }

    public UIFilmController getController()
    {
        return this.controller;
    }

    public RunnerCameraController getRunner()
    {
        return this.runner;
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

    public Area getViewportArea()
    {
        return new Area(this.clips.area.ex(), this.area.y, this.iconBar.area.x - this.clips.area.ex(), this.area.h);
    }

    public Area getFramebufferArea(Area viewport)
    {
        int width = BBSSettings.videoWidth.get();
        int height = BBSSettings.videoHeight.get();

        Camera camera = new Camera();

        camera.copy(this.getWorldCamera());
        camera.updatePerspectiveProjection(width, height);

        Vector2i size = Vectors.resize(width / (float) height, viewport.w, viewport.h);
        Area area = new Area();

        area.setSize(size.x, size.y);
        area.setPos(viewport.mx() - area.w / 2, viewport.my() - area.h / 2);

        return area;
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

        CameraController cameraController = this.getCameraController();

        this.fillData();
        this.setFlight(false);
        cameraController.add(this.runner);
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

        CameraController cameraController = this.getCameraController();

        this.timeline.embedView(null);
        this.setFlight(false);
        cameraController.remove(this.runner);
        cameraController.remove(this.controller.orbit);

        this.disableContext();
    }

    @Override
    public void disappear()
    {
        super.disappear();

        this.setFlight(false);
        this.getCameraController().remove(this.runner);
        this.dashboard.getRoot().remove(this.renderableOverlay);

        this.disableContext();
    }

    private void disableContext()
    {
        this.runner.getContext().shutdown();
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
        return ContentType.FILMS;
    }

    @Override
    public IKey getTitle()
    {
        return IKey.lazy("Films");
    }

    @Override
    public void fill(Film data)
    {
        if (this.data != null)
        {
            this.disableContext();
        }

        if (data != null)
        {
            data.preCallback(this::handlePreValues);
            data.postCallback(this::handlePostValues);

            this.undoManager = new UndoManager<>(50);
            this.undoManager.setCallback(this::handleUndos);
        }
        else
        {
            this.undoManager = null;
        }

        super.fill(data);

        this.plause.setEnabled(data != null);
        this.record.setEnabled(data != null);
        this.openCamera.setEnabled(data != null);
        this.openReplays.setEnabled(data != null);
        this.openScreenplay.setEnabled(data != null);

        this.runner.setWork(data == null ? null : data.camera);
        this.timeline.setClips(data == null ? null : data.camera);
        this.replays.setFilm(data);
        this.pickClip(null);

        this.fillData();
        this.controller.createEntities();
    }

    private void handlePreValues(BaseValue baseValue)
    {
        if (this.cachedSelection.isEmpty())
        {
            this.cachedSelection.addAll(this.timeline.getSelection());
        }

        if (this.cachedKeyframeSelection.isEmpty())
        {
            this.cachedKeyframeSelection.addAll(this.replays.collectSelection());
            this.cachedKeyframeSelected.set(this.replays.findSelected());
        }

        if (!this.cachedUndo.containsKey(baseValue))
        {
            this.cachedUndo.put(baseValue, baseValue.toData());
        }
    }

    private void handlePostValues(BaseValue baseValue)
    {}

    private void submitUndo()
    {
        if (this.cachedUndo.isEmpty())
        {
            return;
        }

        Iterator<BaseValue> it = this.cachedUndo.keySet().iterator();

        while (it.hasNext())
        {
            BaseValue value = it.next().getParent();
            boolean remove = false;

            while (value != null)
            {
                if (this.cachedUndo.containsKey(value))
                {
                    remove = true;

                    break;
                }

                value = value.getParent();
            }

            if (remove)
            {
                it.remove();
            }
        }

        List<ValueChangeUndo> changeUndos = new ArrayList<>();

        for (Map.Entry<BaseValue, BaseType> entry : this.cachedUndo.entrySet())
        {
            BaseValue value = entry.getKey();
            ValueChangeUndo undo = new ValueChangeUndo(value.getPath(), entry.getValue(), value.toData());

            undo.editor(this);
            undo.selectedBefore(this.cachedSelection, this.cachedKeyframeSelection, this.cachedKeyframeSelected);
            changeUndos.add(undo);
        }

        if (changeUndos.size() == 1)
        {
            this.undoManager.pushUndo(changeUndos.get(0));
        }
        else
        {
            this.undoManager.pushUndo(new CompoundUndo<>(changeUndos.toArray(new IUndo[0])));
        }

        this.cachedUndo.clear();
    }

    private void handleUndos(IUndo<ValueGroup> undo, boolean redo)
    {
        IUndo<ValueGroup> anotherUndo = undo;

        if (anotherUndo instanceof CompoundUndo)
        {
            anotherUndo = ((CompoundUndo<ValueGroup>) anotherUndo).getFirst(ValueChangeUndo.class);
        }

        if (anotherUndo instanceof ValueChangeUndo)
        {
            ValueChangeUndo change = (ValueChangeUndo) anotherUndo;
            List<Integer> selection = change.getSelection(redo);

            this.showPanel(change.panel == 1 ? this.replays : (change.panel == 2 ? this.screenplay : this.clips));

            if (selection.isEmpty())
            {
                this.pickClip(null);
            }
            else
            {
                this.timeline.setSelection(selection);

                Clip last = this.data.camera.get(selection.get(selection.size() - 1));

                this.pickClip(last);
            }

            this.timeline.scale.view(change.viewMin, change.viewMax);
            this.setCursor(change.tick);
            this.timeline.vertical.scrollTo(change.scroll);
            this.controller.createEntities();
            this.replays.handleUndo(change, redo);
        }

        if (this.panel != null)
        {
            this.panel.handleUndo(undo, redo);
        }

        this.fillData();
    }

    public void undo()
    {
        if (this.data != null && this.undoManager.undo(this.data)) UIUtils.playClick();
    }

    public void redo()
    {
        if (this.data != null && this.undoManager.redo(this.data)) UIUtils.playClick();
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
        return this.panel == null ? -1 : this.data.camera.getIndex(this.panel.clip);
    }

    /**
     * Update display icon of the plause button
     */
    private void updatePlauseButton()
    {
        this.plause.both(this.isRunning() ? Icons.PAUSE : Icons.PLAY);
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        Area area = this.getFramebufferArea(this.getViewportArea());

        if (area.isInside(context) && this.replays.isVisible())
        {
            return this.replays.clickViewport(context, area);
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void update()
    {
        this.controller.update();

        super.update();
    }

    /* Rendering code */

    /**
     * Draw everything on the screen
     */
    @Override
    public void render(UIContext context)
    {
        this.submitUndo();
        this.updateLogic(context);
        this.renderOverlays(context);

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

            max = Math.min(max, this.data.camera.calculateDuration());

            if (min >= 0 && max >= 0 && min < max && (this.runner.ticks >= max - 1 || this.runner.ticks < min))
            {
                this.setCursor((int) min);
            }
        }

        /* Animate flight mode */
        if (this.dashboard.orbitUI.canControl())
        {
            this.dashboard.orbit.apply(this.position);

            Position current = new Position(this.getCamera());

            if (this.panel != null && this.clips.isVisible())
            {
                if (!this.lastPosition.equals(current))
                {
                    this.panel.editClip(current);
                }
            }

            this.lastPosition.set(current);
        }
        else
        {
            this.dashboard.orbit.setup(this.getCamera());
        }

        /* Rewind playback back to 0 */
        if (this.lastRunning && !this.isRunning())
        {
            this.lastRunning = this.runner.isRunning();
            this.setCursor(0);

            this.updatePlauseButton();
        }
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

        this.camera.copy(this.getWorldCamera());
        this.camera.updatePerspectiveProjection(width, height);

        /* Resize framebuffer if desired width and height changed */
        if (texture.width != width || texture.height != height)
        {
            framebuffer.resize(width, height);
        }

        Area area = this.getFramebufferArea(viewport);

        /* Render the scene to framebuffer */
        GLStates.setupDepthFunction3D();
        this.dashboard.bridge.get(IBridgeRender.class).renderSceneTo(this.camera, framebuffer, 0, true, 0, (fb) ->
        {
            UISubtitleRenderer.renderSubtitles(context, fb, SubtitleClip.getSubtitles(this.runner.getContext()));
        });
        GLStates.setupDepthFunction2D();

        viewport.render(context.batcher, Colors.A90);
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

        this.controller.renderHUD(context, area);

        if (this.replays.isVisible())
        {
            this.renderAudio(context, area);
        }
    }

    private void renderAudio(UIContext context, Area area)
    {
        int w = (int) (area.w * BBSSettings.audioWaveformWidth.get());
        int x = area.x(0.5F, w);

        AudioRenderer.renderAll(context.batcher, x, area.y + 40, w, BBSSettings.audioWaveformHeight.get(), this.dashboard.width, this.dashboard.height);
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        super.renderInWorld(context);

        this.controller.renderFrame(context);
    }

    /* IUICameraWorkDelegate implementation */

    @Override
    public Film getFilm()
    {
        return this.data;
    }

    @Override
    public Camera getCamera()
    {
        return this.camera;
    }

    public Camera getWorldCamera()
    {
        return this.dashboard.bridge.get(IBridgeCamera.class).getCamera();
    }

    public CameraController getCameraController()
    {
        return this.dashboard.bridge.get(IBridgeCamera.class).getCameraController();
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

            this.timeline.w(1F, 0);
            this.timeline.clearSelection();
            this.clips.resize();

            return;
        }

        try
        {
            if (this.panel != null)
            {
                scrolls.put(this.panel.getClass(), this.panel.panels.scroll.scroll);
            }

            this.timeline.embedView(null);

            UIClip panel = (UIClip) BBS.getFactoryCameraClips().getData(clip).panelUI.getConstructors()[0].newInstance(clip, this);

            this.panel = panel;
            this.panel.relative(this.clips).x(1F, -160).w(160).h(1F);
            this.clips.add(this.panel);

            this.panel.fillData();

            Integer scroll = scrolls.get(this.panel.getClass());

            if (scroll != null)
            {
                this.panel.panels.scroll.scroll = scroll;
                this.panel.panels.scroll.clamp();
            }

            if (!this.isFlightDisabled())
            {
                this.setCursor(clip.tick.get());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.timeline.w(1F, -160);
        this.clips.resize();
    }

    @Override
    public int getCursor()
    {
        return this.runner.ticks;
    }

    @Override
    public void setCursor(int value)
    {
        this.runner.ticks = Math.max(0, value);
    }

    @Override
    public boolean isRunning()
    {
        return this.runner.isRunning();
    }

    @Override
    public void togglePlayback()
    {
        this.setFlight(false);

        this.runner.toggle(this.getCursor());
        this.lastRunning = this.runner.isRunning();
        this.updatePlauseButton();
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

        if (this.data != null)
        {
            this.screenplay.setScreenplay(this.data.screenplay);
        }
    }

    @Override
    public void embedView(UIElement element)
    {
        this.timeline.embedView(element);
    }

    @Override
    public void markLastUndoNoMerging()
    {
        if (this.data == null)
        {
            return;
        }

        IUndo<ValueGroup> undo = this.undoManager.getCurrentUndo();

        if (undo != null)
        {
            undo.noMerging();
        }
    }

    @Override
    public void updateClipProperty(ValueInt property, int value)
    {
        this.timeline.updateClipProperty(property, value);
    }
}