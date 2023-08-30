package mchorse.bbs.ui.film;

import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.values.ValueKeyframes;
import mchorse.bbs.film.values.ValueReplay;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.utils.UIReplayList;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

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

    /* Clips */
    private UIFilmPanel delegate;
    private Film film;
    private ValueReplay replay;

    private List<ValueKeyframeChannel> tempKeyframes = new ArrayList<>();
    private List<Integer> tempColors = new ArrayList<>();

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
        COLORS.put("trigger_r", Colors.RED);
        COLORS.put("trigger_l", Colors.GREEN);
    }

    public UIReplaysEditor(UIFilmPanel delegate)
    {
        this.delegate = delegate;

        this.replays = new UIReplayList((l) -> this.setReplay(l.get(0)), this.delegate);
        this.replays.relative(this).w(1F).h(80);

        /* Keyframes */
        this.keyframes = new UIElement();
        this.keyframes.relative(this).y(80).w(1F).h(1F, -80);

        this.channels = new UIStringList(this::selectChannels);
        this.channels.background(Colors.A75).multi().context((menu) ->
        {
            menu.action(Icons.ALL_DIRECTIONS, IKey.lazy("Select position"), () -> this.pick("x", "y", "z"));
            menu.action(Icons.REFRESH, IKey.lazy("Select rotations"), () -> this.pick("yaw", "pitch", "bodyYaw"));
            menu.action(Icons.LEFT_STICK, IKey.lazy("Select left stick"), () -> this.pick("stick_lx", "stick_ly"));
            menu.action(Icons.RIGHT_STICK, IKey.lazy("Select right stick"), () -> this.pick("stick_rx", "stick_ry"));
            menu.action(Icons.TRIGGER, IKey.lazy("Select triggers"), () -> this.pick("trigger_l", "trigger_r"));
        });

        this.channels.relative(this.keyframes).x(1F, -100).w(100).h(1F);
        this.keyframes.add(this.channels);

        this.add(this.replays, this.keyframes);

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

        this.collectChannels(l);

        if (this.tempKeyframes.size() > 1)
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
            this.keyframeEditor.relative(this.keyframes).wTo(this.channels.area).h(1F);
            this.keyframes.add(this.keyframeEditor);
        }

        this.resize();

        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.keyframes.duration = this.film.camera.calculateDuration();
            this.keyframeEditor.resetView();
        }
    }

    private void collectChannels(List<String> keys)
    {
        this.tempKeyframes.clear();
        this.tempColors.clear();

        for (String key : keys)
        {
            BaseValue value = this.replay.keyframes.get(key);

            if (value instanceof ValueKeyframeChannel)
            {
                this.tempKeyframes.add((ValueKeyframeChannel) value);
                this.tempColors.add(COLORS.getOrDefault(key, Colors.ACTIVE));
            }
        }
    }

    public void setFilm(Film film)
    {
        this.film = film;

        if (film != null)
        {
            this.replays.setList(film.replays.replays);

            this.setReplay(film.replays.replays.isEmpty() ? null : film.replays.replays.get(0));
        }
    }

    public void setReplay(ValueReplay replay)
    {
        this.replay = replay;

        this.channels.clear();

        this.keyframes.setVisible(replay != null);

        if (replay != null)
        {
            for (String key : ValueKeyframes.CURATED_CHANNELS)
            {
                this.channels.add(key);
            }

            this.channels.setIndex(0);

            this.selectChannels(this.channels.getCurrent());
        }

        this.replays.setCurrentScroll(replay);
    }
}