package mchorse.bbs.ui.film;

import mchorse.bbs.camera.values.ValueKeyframeChannel;
import mchorse.bbs.film.Film;
import mchorse.bbs.film.values.ValueReplay;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.film.utils.UIReplayList;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.film.utils.keyframes.UICameraGraphEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.keyframes.UIKeyframesEditor;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.utils.Label;
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
    public UILabelList<ValueKeyframeChannel> channels;
    public UIKeyframesEditor keyframeEditor;

    /* Clips */
    private UIFilmPanel delegate;
    private Film film;
    private ValueReplay replay;

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

        this.channels = new UILabelList<>(this::selectChannels);
        this.channels.background(Colors.A75).multi().context((menu) ->
        {
            menu.action(Icons.SPHERE, IKey.lazy("Select position"), () -> this.pick("x", "y", "z"));
            menu.action(Icons.FRUSTUM, IKey.lazy("Select rotations"), () -> this.pick("yaw", "pitch", "bodyYaw"));
            menu.action(Icons.LEFT_HANDLE, IKey.lazy("Select left stick"), () -> this.pick("stick_lx", "stick_ly"));
            menu.action(Icons.RIGHT_HANDLE, IKey.lazy("Select right stick"), () -> this.pick("stick_rx", "stick_ry"));
            menu.action(Icons.DOWNLOAD, IKey.lazy("Select triggers"), () -> this.pick("trigger_l", "trigger_r"));
        });

        this.channels.relative(this.keyframes).x(1F, -100).w(100).h(1F);
        this.keyframes.add(this.channels);

        this.add(this.replays, this.keyframes);

        this.markContainer();
    }

    private void pick(String... channels)
    {
        this.channels.deselect();

        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.removeFromParent();
            this.keyframeEditor = null;
        }

        UICameraDopeSheetEditor editor = new UICameraDopeSheetEditor(this.delegate);
        List<ValueKeyframeChannel> keyframes = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        this.keyframeEditor = editor;

        for (String key : channels)
        {
            int i = 0;

            for (Label<ValueKeyframeChannel> c : this.channels.getList())
            {
                if (c.value.getId().equals(key))
                {
                    this.channels.addIndex(i);
                    colors.add(COLORS.getOrDefault(key, Colors.ACTIVE));
                    keyframes.add(c.value);

                    break;
                }

                i += 1;
            }
        }

        editor.setChannels(keyframes, colors);

        this.keyframeEditor.relative(this.keyframes).wTo(this.channels.area).h(1F);
        this.keyframes.add(this.keyframeEditor);

        this.resize();

        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.keyframes.duration = this.film.camera.calculateDuration();
            this.keyframeEditor.resetView();
        }
    }

    private void selectChannels(List<Label<ValueKeyframeChannel>> l)
    {
        if (this.keyframeEditor != null)
        {
            this.keyframeEditor.removeFromParent();
            this.keyframeEditor = null;
        }

        if (l.size() > 1)
        {
            UICameraDopeSheetEditor editor = new UICameraDopeSheetEditor(this.delegate);
            List<ValueKeyframeChannel> keyframes = new ArrayList<>();
            List<Integer> colors = new ArrayList<>();

            for (Label<ValueKeyframeChannel> e : l)
            {
                String key = e.title.get();

                keyframes.add(e.value);
                colors.add(COLORS.getOrDefault(key, Colors.ACTIVE));
            }

            editor.setChannels(keyframes, colors);

            this.keyframeEditor = editor;
        }
        else if (!l.isEmpty())
        {
            UICameraGraphEditor editor = new UICameraGraphEditor(this.delegate);

            editor.setChannel(l.get(0).value, COLORS.getOrDefault(l.get(0).title.get(), Colors.ACTIVE));

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
            List<BaseValue> all = replay.keyframes.getAll();

            for (BaseValue key : all)
            {
                this.channels.add(IKey.lazy(key.getId()), (ValueKeyframeChannel) key);
            }

            this.channels.sort();
            this.channels.setIndex(0);

            this.selectChannels(this.channels.getCurrent());
        }

        this.replays.setCurrentScroll(replay);
    }
}