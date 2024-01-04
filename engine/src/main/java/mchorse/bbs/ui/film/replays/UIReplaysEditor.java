package mchorse.bbs.ui.film.replays;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.replays.Replay;
import mchorse.bbs.film.replays.ReplayKeyframes;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanels;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.film.utils.undo.ValueChangeUndo;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIProperty;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIPoseKeyframeFactory;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.StencilFormFramebuffer;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIReplaysEditor extends UIElement
{
    private static final Map<String, Integer> COLORS = new HashMap<>();

    public UIIcon toggleKeyframes;
    public UIIcon toggleProperties;
    public UIIcon record;
    public UIIcon keyframe;
    public UIElement icons;
    public UIReplayList replays;

    /* Keyframes */
    public UIElement keyframes;
    public UICameraDopeSheetEditor keyframeEditor;
    public UIPropertyEditor propertyEditor;

    /* Clips */
    private UIFilmPanel filmPanel;
    private Film film;
    private Replay replay;

    static
    {
        COLORS.put("x", Colors.RED);
        COLORS.put("y", Colors.GREEN);
        COLORS.put("z", Colors.BLUE);
        COLORS.put("yaw", Colors.YELLOW);
        COLORS.put("pitch", Colors.CYAN);
        COLORS.put("bodyYaw", Colors.MAGENTA);

        COLORS.put("stick_lx", Colors.RED);
        COLORS.put("stick_ly", Colors.GREEN);
        COLORS.put("stick_rx", Colors.RED);
        COLORS.put("stick_ry", Colors.GREEN);
        COLORS.put("trigger_l", Colors.RED);
        COLORS.put("trigger_r", Colors.GREEN);

        COLORS.put("pose", Colors.RED);
        COLORS.put("transform", Colors.GREEN);
        COLORS.put("color", Colors.INACTIVE);
    }

    public UIReplaysEditor(UIFilmPanel filmPanel)
    {
        this.filmPanel = filmPanel;

        int w = 120;

        this.record = new UIIcon(Icons.SPHERE, (b) -> this.filmPanel.getController().pickRecording());
        this.record.tooltip(UIKeys.FILM_REPLAY_RECORD);
        this.keyframe = new UIIcon(Icons.KEY, (b) -> this.filmPanel.getController().insertFrame());
        this.keyframe.tooltip(UIKeys.FILM_REPLAY_INSERT);
        this.toggleKeyframes = new UIIcon(Icons.GRAPH, (b) -> this.toggleProperties(false));
        this.toggleKeyframes.tooltip(UIKeys.FILM_REPLAY_ENTITY_KEYFRAMES);
        this.toggleProperties = new UIIcon(Icons.MORE, (b) -> this.toggleProperties(true));
        this.toggleProperties.tooltip(UIKeys.FILM_REPLAY_FORM_KEYFRAMES);

        this.keyframes = new UIElement();
        this.keyframes.relative(this).x(w).w(1F, -w).h(1F);

        this.replays = new UIReplayList((l) -> this.setReplay(l.get(0)), this.filmPanel);
        this.replays.relative(this).y(20).w(w).h(1F, -20);

        this.icons = UI.row(0, this.record, this.keyframe, this.toggleKeyframes, this.toggleProperties);
        this.icons.relative(this.replays).y(-20).w(60).h(20);

        this.add(this.replays, this.icons, this.keyframes);

        this.markContainer();
    }

    private void toggleProperties(boolean properties)
    {
        if (this.propertyEditor != null)
        {
            this.keyframeEditor.setVisible(!properties);
            this.propertyEditor.setVisible(properties);
        }
    }

    public void handleUndo(ValueChangeUndo change, boolean redo)
    {
        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.keyframes.applySelection(change.getKeyframeSelection(redo));
        }

        if (this.propertyEditor != null)
        {
            this.propertyEditor.properties.applySelection(change.getPropertiesSelection(redo));
        }
    }

    public void setFilm(Film film)
    {
        this.film = film;

        if (film != null)
        {
            List<Replay> replays = film.replays.getList();

            this.replays.setList(replays);
            this.setReplay(replays.isEmpty() ? null : replays.get(0));
        }
    }

    public void setReplay(Replay replay)
    {
        this.replay = replay;

        this.keyframes.setVisible(replay != null);
        this.updateChannelsList();

        this.replays.setCurrentScroll(replay);
    }

    public void moveReplay(double x, double y, double z)
    {
        if (this.replay != null)
        {
            int cursor = this.filmPanel.getCursor();

            this.replay.keyframes.x.insert(cursor, x);
            this.replay.keyframes.y.insert(cursor, y);
            this.replay.keyframes.z.insert(cursor, z);
        }
    }

    public void updateChannelsList()
    {
        if (this.keyframeEditor != null) this.keyframeEditor.removeFromParent();
        if (this.propertyEditor != null) this.propertyEditor.removeFromParent();

        if (this.replay == null)
        {
            return;
        }

        int duration = this.film.camera.calculateDuration();

        /* Replay keyframes */
        List<KeyframeChannel> keyframes = new ArrayList<>();
        List<Integer> tempKeyframesColors = new ArrayList<>();

        for (String key : ReplayKeyframes.CURATED_CHANNELS)
        {
            BaseValue value = this.replay.keyframes.get(key);

            keyframes.add((KeyframeChannel) value);
            tempKeyframesColors.add(COLORS.getOrDefault(key, Colors.ACTIVE));
        }

        this.keyframeEditor = new UICameraDopeSheetEditor(this.filmPanel.cameraClips);
        this.keyframeEditor.setChannels(keyframes, tempKeyframesColors);
        this.keyframeEditor.relative(this.keyframes).full();

        this.keyframeEditor.keyframes.absolute();
        this.keyframeEditor.keyframes.duration = duration;

        this.keyframes.add(this.keyframeEditor);

        /* Form properties */
        List<GenericKeyframeChannel> properties = new ArrayList<>();
        List<Integer> propertiesColors = new ArrayList<>();
        List<IFormProperty> formProperties = new ArrayList<>();

        for (String key : FormUtils.collectPropertyPaths(this.replay.form.get()))
        {
            GenericKeyframeChannel property = this.replay.properties.getOrCreate(this.replay.form.get(), key);

            if (property != null)
            {
                IFormProperty formProperty = FormUtils.getProperty(this.replay.form.get(), key);

                properties.add(property);
                propertiesColors.add(COLORS.getOrDefault(StringUtils.fileName(key), Colors.ACTIVE));
                formProperties.add(formProperty);
            }
        }

        if (!properties.isEmpty())
        {
            this.propertyEditor = new UIPropertyEditor(this.filmPanel.cameraClips);
            this.propertyEditor.setChannels(properties, formProperties, propertiesColors);
            this.propertyEditor.relative(this.keyframes).full();
            this.propertyEditor.setVisible(false);

            this.propertyEditor.properties.duration = duration;

            this.keyframes.add(this.propertyEditor);
        }

        this.toggleProperties.setEnabled(this.propertyEditor != null);
        this.keyframes.resize();

        if (this.keyframeEditor != null) this.keyframeEditor.resetView();
        if (this.propertyEditor != null) this.propertyEditor.resetView();
    }

    public void pickForm(Form form, String bone)
    {
        String path = FormUtils.getPath(form);

        if (!bone.isEmpty())
        {
            if (this.propertyEditor == null)
            {
                return;
            }

            this.toggleProperties(true);
            this.pickProperty(bone, StringUtils.combinePaths(path, "pose"), false);
        }
    }

    public void pickFormProperty(Form form, String bone)
    {
        String path = FormUtils.getPath(form);
        boolean shift = Window.isShiftPressed();
        ContextMenuManager manager = new ContextMenuManager();

        for (IFormProperty formProperty : form.getProperties().values())
        {
            if (!formProperty.canCreateChannel())
            {
                continue;
            }

            manager.action(Icons.POINTER, IKey.raw(formProperty.getKey()), () ->
            {
                this.toggleProperties(true);
                this.pickProperty(bone, StringUtils.combinePaths(path, formProperty.getKey()), shift);
            });
        }

        this.getContext().replaceContextMenu(manager.create());
    }

    private void pickProperty(String bone, String key, boolean insert)
    {
        List<UIProperty> properties = this.propertyEditor.properties.getProperties();

        for (UIProperty property : properties)
        {
            if (FormUtils.getPropertyPath(property.property).equals(key))
            {
                this.pickProperty(bone, property, insert);

                break;
            }
        }
    }

    private void pickProperty(String bone, UIProperty property, boolean insert)
    {
        int tick = this.filmPanel.getRunner().ticks;

        if (insert)
        {
            this.propertyEditor.properties.addCurrent(property, tick);
            this.propertyEditor.fillData(this.propertyEditor.properties.getCurrent());

            return;
        }

        Pair segment = property.channel.findSegment(tick);

        if (segment != null)
        {
            GenericKeyframe a = (GenericKeyframe) segment.a;
            GenericKeyframe b = (GenericKeyframe) segment.b;
            GenericKeyframe closest = Math.abs(a.getTick() - tick) > Math.abs(b.getTick() - tick) ? b : a;

            this.propertyEditor.pickKeyframe(closest);

            if (this.propertyEditor.editor instanceof UIPoseKeyframeFactory)
            {
                ((UIPoseKeyframeFactory) this.propertyEditor.editor).selectBone(bone);
            }

            this.filmPanel.setCursor((int) closest.getTick());
        }
    }

    public boolean clickViewport(UIContext context, Area area)
    {
        StencilFormFramebuffer stencil = this.filmPanel.getController().getStencil();

        if (stencil.hasPicked())
        {
            Pair<Form, String> pair = stencil.getPicked();

            if (pair != null && context.mouseButton < 2)
            {
                if (!this.isVisible())
                {
                    this.filmPanel.showPanel(this);
                }

                if (context.mouseButton == 0)
                {
                    this.pickForm(pair.a, pair.b);

                    return true;
                }
                else if (context.mouseButton == 1)
                {
                    this.pickFormProperty(pair.a, pair.b);

                    return true;
                }
            }
        }
        else if (context.mouseButton == 1 && this.isVisible())
        {
            RayTraceResult traceResult = new RayTraceResult();
            World world = context.menu.bridge.get(IBridgeWorld.class).getWorld();
            Camera camera = this.filmPanel.getCamera();

            RayTracer.trace(traceResult, world.chunks, camera.position, camera.getMouseDirection(context.mouseX, context.mouseY, area), 64F);

            if (traceResult.type == RayTraceType.BLOCK)
            {
                context.replaceContextMenu((menu) ->
                {
                    float pitch = camera.rotation.x;
                    float yaw = camera.rotation.y + MathUtils.PI;

                    menu.action(Icons.ADD, UIKeys.FILM_REPLAY_CONTEXT_ADD, () -> this.replays.addReplay(traceResult.hit, pitch, yaw));
                    menu.action(Icons.POINTER, UIKeys.FILM_REPLAY_CONTEXT_MOVE_HERE, () -> this.moveReplay(traceResult.hit.x, traceResult.hit.y, traceResult.hit.z));
                });

                return true;
            }
        }

        return false;
    }

    @Override
    public void render(UIContext context)
    {
        context.batcher.box(this.icons.area.x, this.icons.area.y, this.replays.area.ex(), this.icons.area.ey(), Colors.CONTROL_BAR);

        if (this.keyframeEditor != null && this.keyframeEditor.isVisible())
        {
            UIDashboardPanels.renderHighlight(context.batcher, this.toggleKeyframes.area);
        }
        else if (this.propertyEditor != null && this.propertyEditor.isVisible())
        {
            UIDashboardPanels.renderHighlight(context.batcher, this.toggleProperties.area);
        }

        super.render(context);
    }
}