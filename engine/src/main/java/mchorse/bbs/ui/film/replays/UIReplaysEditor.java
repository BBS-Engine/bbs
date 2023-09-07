package mchorse.bbs.ui.film.replays;

import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.replays.FormProperty;
import mchorse.bbs.film.replays.Replay;
import mchorse.bbs.film.replays.ReplayKeyframes;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.film.replays.properties.UIProperty;
import mchorse.bbs.ui.film.replays.properties.UIPropertyEditor;
import mchorse.bbs.ui.film.replays.properties.factories.UIPoseKeyframeFactory;
import mchorse.bbs.ui.film.replays.properties.undo.UIUndoPropertyEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIReplaysEditor extends UIElement
{
    private static final Map<String, Integer> COLORS = new HashMap<>();

    public UIReplayList replays;

    /* Keyframes */
    public UIElement keyframes;
    public UIStringList channels;
    public UIKeyframesEditor keyframeEditor;
    public UIPropertyEditor propertyEditor;

    /* Clips */
    private UIFilmPanel delegate;
    private Film film;
    private Replay replay;

    private List<ValueKeyframeChannel> tempKeyframes = new ArrayList<>();
    private List<Integer> tempColors = new ArrayList<>();
    private List<FormProperty> tempProperties = new ArrayList<>();
    private List<IFormProperty> tempFormProperties = new ArrayList<>();

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
    }

    public UIReplaysEditor(UIFilmPanel delegate)
    {
        this.delegate = delegate;

        int w = 120;
        int h = 140;

        this.keyframes = new UIElement();
        this.keyframes.relative(this).w(1F, -w).h(1F);

        this.replays = new UIReplayList((l) -> this.setReplay(l.get(0)), this.delegate);
        this.replays.relative(this).x(1F, -w).w(w).h(h);

        this.channels = new UIStringList(this::selectChannels);
        this.channels.background(Colors.A75).multi().context((menu) ->
        {
            menu.action(Icons.ALL_DIRECTIONS, IKey.lazy("Select position"), () -> this.pick("x", "y", "z"));
            menu.action(Icons.REFRESH, IKey.lazy("Select rotations"), () -> this.pick("yaw", "pitch", "bodyYaw"));
            menu.action(Icons.LEFT_STICK, IKey.lazy("Select left stick"), () -> this.pick("stick_lx", "stick_ly"));
            menu.action(Icons.RIGHT_STICK, IKey.lazy("Select right stick"), () -> this.pick("stick_rx", "stick_ry"));
            menu.action(Icons.TRIGGER, IKey.lazy("Select triggers"), () -> this.pick("trigger_l", "trigger_r"));
        });

        this.channels.relative(this).x(1F, -w).y(h).w(w).h(1F, -h);

        this.add(this.replays, this.channels, this.keyframes);

        this.markContainer();
    }

    private void pick(String... channels)
    {
        this.channels.deselect();

        List<String> channelsList = this.channels.getList();

        for (int i = 0; i < channelsList.size(); i++)
        {
            for (String key : channels)
            {
                if (key.equals(channelsList.get(i)))
                {
                    this.channels.addIndex(i);
                }
            }
        }

        this.selectChannels(this.channels.getCurrent());
    }

    private void selectChannels(List<String> l)
    {
        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.removeFromParent();
            this.keyframeEditor = null;
        }

        if (this.propertyEditor != null)
        {
            this.propertyEditor.removeFromParent();
            this.propertyEditor = null;
        }

        this.collectChannels(l);

        if (!this.tempProperties.isEmpty())
        {
            UIUndoPropertyEditor editor = new UIUndoPropertyEditor(this.delegate);

            editor.setChannels(this.tempProperties, this.tempFormProperties, this.tempColors);

            this.propertyEditor = editor;
        }
        else if (this.tempKeyframes.size() > 1)
        {
            UICameraDopeSheetEditor editor = new UICameraDopeSheetEditor(this.delegate);

            editor.keyframes.absolute();
            editor.setChannels(this.tempKeyframes, this.tempColors);

            this.keyframeEditor = editor;
        }
        else if (!this.tempKeyframes.isEmpty())
        {
            UICameraGraphEditor editor = new UICameraGraphEditor(this.delegate);

            editor.keyframes.absolute();
            editor.setChannel(this.tempKeyframes.get(0), this.tempColors.get(0));

            this.keyframeEditor = editor;
        }

        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.relative(this.keyframes).full();
            this.keyframes.add(this.keyframeEditor);
        }

        if (this.propertyEditor != null)
        {
            this.propertyEditor.relative(this.keyframes).full();
            this.keyframes.add(this.propertyEditor);
        }

        this.resize();

        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.keyframes.duration = this.film.camera.calculateDuration();
            this.keyframeEditor.resetView();
        }

        if (this.propertyEditor != null)
        {
            this.propertyEditor.properties.duration = this.film.camera.calculateDuration();
            this.propertyEditor.resetView();
        }
    }

    private void collectChannels(List<String> keys)
    {
        this.tempKeyframes.clear();
        this.tempColors.clear();
        this.tempProperties.clear();
        this.tempFormProperties.clear();

        for (String key : keys)
        {
            BaseValue value = this.replay.keyframes.get(key);

            if (value instanceof ValueKeyframeChannel)
            {
                this.tempKeyframes.add((ValueKeyframeChannel) value);
                this.tempColors.add(COLORS.getOrDefault(key, Colors.ACTIVE));
            }
            else
            {
                FormProperty property = this.replay.properties.getOrCreate(this.replay.form.get(), key);

                if (property != null)
                {
                    IFormProperty formProperty = FormUtils.getProperty(this.replay.form.get(), key);

                    this.tempProperties.add(property);
                    this.tempColors.add(COLORS.getOrDefault(StringUtils.fileName(key), Colors.ACTIVE));
                    this.tempFormProperties.add(formProperty);
                }
            }
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

    public void updateChannelsList()
    {
        this.channels.clear();

        if (this.replay != null)
        {
            for (String key : ReplayKeyframes.CURATED_CHANNELS)
            {
                this.channels.add(key);
            }

            this.channels.add(FormUtils.collectPropertyPaths(replay.form.get()));
            this.channels.setIndex(0);

            this.selectChannels(this.channels.getCurrent());
        }
    }

    public void pickForm(Form form, String bone)
    {
        String path = FormUtils.getPath(form);

        if (Window.isCtrlPressed())
        {
            ContextMenuManager manager = new ContextMenuManager();

            for (IFormProperty property : form.getProperties().values())
            {
                if (property.canCreateChannel())
                {
                    manager.action(Icons.POINTER, IKey.raw(property.getKey()), () ->
                    {
                        this.pick(StringUtils.combinePaths(path, property.getKey()));
                    });
                }
            }

            this.getContext().replaceContextMenu(manager.create());
        }
        else if (!bone.isEmpty())
        {
            String key = StringUtils.combinePaths(path, "pose");

            if (this.propertyEditor == null)
            {
                this.pick(key);
            }

            List<UIProperty> properties = this.propertyEditor.properties.getProperties();
            UIProperty poseProperty = null;

            for (UIProperty property : properties)
            {
                if (FormUtils.getPropertyPath(property.property).equals(key))
                {
                    poseProperty = property;

                    break;
                }
            }

            if (poseProperty == null)
            {
                this.pick(key);

                poseProperty = this.propertyEditor.properties.getProperties().get(0);
            }

            int ticks = this.delegate.getRunner().ticks;

            Pair segment = poseProperty.channel.findSegment(ticks);

            if (segment != null)
            {
                GenericKeyframe a = (GenericKeyframe) segment.a;
                GenericKeyframe b = (GenericKeyframe) segment.b;
                GenericKeyframe closest = Math.abs(a.tick - ticks) > Math.abs(b.tick - ticks) ? b : a;

                this.propertyEditor.pickKeyframe(closest);

                if (this.propertyEditor.editor instanceof UIPoseKeyframeFactory)
                {
                    ((UIPoseKeyframeFactory) this.propertyEditor.editor).selectBone(bone);
                }

                this.delegate.setCursor((int) closest.tick);
            }
        }
    }
}