package mchorse.bbs.ui.framework.elements.input.keyframes.generic;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.properties.IFormProperty;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.utils.UICameraUtils;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.keyframes.IAxisConverter;
import mchorse.bbs.ui.framework.elements.input.keyframes.generic.factories.UIKeyframeFactory;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIPropertyEditor extends UIElement
{
    public UIElement frameButtons;
    public UIToggle instant;
    public UITrackpad tick;
    public UITrackpad duration;
    public UIIcon interp;
    public UIKeyframeFactory editor;

    public UIProperties properties;

    private int clicks;
    private long clickTimer;

    private IAxisConverter converter;

    protected List<BaseValue> valueChannels = new ArrayList<>();

    public UIPropertyEditor(IUIClipsDelegate delegate)
    {
        super();

        InterpolationTooltip tooltip = new InterpolationTooltip(0F, 1F, () ->
        {
            GenericKeyframe keyframe = this.properties.getCurrent();

            if (keyframe == null)
            {
                return null;
            }

            return keyframe.getInterpolation();
        });

        this.frameButtons = new UIElement();
        this.frameButtons.relative(this).x(1F).y(1F).w(120).anchor(1F).column().vertical().stretch().padding(5);
        this.frameButtons.setVisible(false);
        this.instant = new UIToggle(IKey.lazy("Instant"), (b) -> this.setInstant(b.getValue()));
        this.tick = new UITrackpad(this::setTick);
        this.tick.limit(Integer.MIN_VALUE, Integer.MAX_VALUE, true).tooltip(UIKeys.KEYFRAMES_TICK);
        this.duration = new UITrackpad((v) -> this.setDuration(v.intValue()));
        this.duration.limit(0, Integer.MAX_VALUE, true).tooltip(IKey.lazy("Forced duration"));
        this.interp = new UIIcon(Icons.GRAPH, (b) ->
        {
            UICameraUtils.interps(this.getContext(), (Interpolation) this.properties.getCurrent().getInterpolation(), this::pickInterpolation);
        });
        this.interp.tooltip(tooltip);

        this.properties = new UIProperties(delegate, this::fillData);
        this.properties.relative(this).full();

        /* Add all elements */
        this.add(this.properties, this.frameButtons);
        this.frameButtons.add(this.instant);
        this.frameButtons.add(UI.row(5, this.interp, this.tick, this.duration));

        this.context((menu) ->
        {
            menu.action(Icons.MAXIMIZE, UIKeys.KEYFRAMES_CONTEXT_MAXIMIZE, this::resetView);
            menu.action(Icons.FULLSCREEN, UIKeys.KEYFRAMES_CONTEXT_SELECT_ALL, this::selectAll);

            if (this.properties.selected)
            {
                menu.action(Icons.REMOVE, UIKeys.KEYFRAMES_CONTEXT_REMOVE, this::removeSelectedKeyframes);
                menu.action(Icons.COPY, UIKeys.KEYFRAMES_CONTEXT_COPY, this::copyKeyframes);
            }

            Map<String, PastedKeyframes> pasted = this.parseKeyframes();

            if (pasted != null)
            {
                UIContext context = this.getContext();
                final Map<String, PastedKeyframes> keyframes = pasted;
                double offset = this.properties.fromGraphX(context.mouseX);
                int mouseY = context.mouseY;

                menu.action(Icons.PASTE, UIKeys.KEYFRAMES_CONTEXT_PASTE, () -> this.pasteKeyframes(keyframes, (long) offset, mouseY));
            }
        });

        IKey category = UIKeys.KEYFRAMES_KEYS_CATEGORY;

        this.keys().register(Keys.KEYFRAMES_MAXIMIZE, this::resetView).inside().category(category);
        this.keys().register(Keys.KEYFRAMES_SELECT_ALL, this::selectAll).inside().category(category);
        this.keys().register(Keys.COPY, this::copyKeyframes).inside().category(category);
        this.keys().register(Keys.PASTE, () ->
        {
            Map<String, PastedKeyframes> pasted = this.parseKeyframes();

            if (pasted != null)
            {
                UIContext context = this.getContext();
                final Map<String, PastedKeyframes> keyframes = pasted;
                double offset = this.properties.fromGraphX(context.mouseX);
                int mouseY = context.mouseY;

                this.pasteKeyframes(keyframes, (long) offset, mouseY);
            }
        }).inside().category(category);

        this.interp.keys().register(Keys.KEYFRAMES_INTERP, this.interp::clickItself).category(category);

        this.updateConverter();
    }

    public void setChannels(List<GenericKeyframeChannel> properties, List<IFormProperty> property, List<Integer> colors)
    {
        List<UIProperty> sheets = this.properties.properties;

        sheets.clear();
        this.properties.clearSelection();

        this.valueChannels.clear();

        for (int i = 0; i < properties.size(); i++)
        {
            GenericKeyframeChannel channel = properties.get(i);

            this.valueChannels.add(channel);
            sheets.add(new UIProperty(channel.getId(), IKey.raw(channel.getId()), colors.get(i), channel, property.get(i)));
        }

        this.frameButtons.setVisible(false);
    }

    public void updateConverter()
    {
        this.setConverter(UICameraDopeSheetEditor.CONVERTER);
    }

    public void setConverter(IAxisConverter converter)
    {
        this.converter = converter;
        this.properties.setConverter(converter);

        if (converter != null)
        {
            converter.updateField(this.tick);
        }

        this.fillData(this.properties.getCurrent());
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;

        if (this.area.isInside(mouseX, mouseY))
        {
            /* On double-click add or remove a keyframe */
            if (context.mouseButton == 0)
            {
                long time = System.currentTimeMillis();

                if (time - this.clickTimer < 175)
                {
                    this.clicks++;

                    if (this.clicks >= 1)
                    {
                        this.clicks = 0;
                        this.doubleClick(mouseX, mouseY);
                    }
                }
                else
                {
                    this.clicks = 0;
                }

                this.clickTimer = time;
            }
        }

        return super.subMouseClicked(context);
    }

    /**
     * Parse keyframes from clipboard
     */
    private Map<String, PastedKeyframes> parseKeyframes()
    {
        MapType data = Window.getClipboardMap("_CopyProperties");

        if (data == null)
        {
            return null;
        }

        Map<String, PastedKeyframes> temp = new HashMap<>();

        for (String key : data.keys())
        {
            MapType map = data.getMap(key);
            ListType list = map.getList("keyframes");
            IGenericKeyframeFactory serializer = KeyframeFactories.FACTORIES.get(map.getString("type"));

            for (int i = 0, c = list.size(); i < c; i++)
            {
                PastedKeyframes pastedKeyframes = temp.computeIfAbsent(key, k -> new PastedKeyframes(serializer));
                GenericKeyframe keyframe = new GenericKeyframe("", serializer);

                keyframe.fromData(list.getMap(i));
                pastedKeyframes.keyframes.add(keyframe);
            }
        }

        return temp.isEmpty() ? null : temp;
    }

    /**
     * Copy keyframes to clipboard
     */
    private void copyKeyframes()
    {
        MapType keyframes = new MapType();

        for (UIProperty property : this.properties.getProperties())
        {
            int c = property.getSelectedCount();

            if (c > 0)
            {
                MapType data = new MapType();
                ListType list = new ListType();

                data.putString("type", CollectionUtils.getKey(KeyframeFactories.FACTORIES, property.channel.getFactory()));
                data.put("keyframes", list);

                for (int i = 0; i < c; i++)
                {
                    GenericKeyframe keyframe = property.channel.get(property.selected.get(i));

                    list.add(keyframe.toData());
                }

                if (!list.isEmpty())
                {
                    keyframes.put(property.id, data);
                }
            }
        }

        Window.setClipboard(keyframes, "_CopyProperties");
    }

    /**
     * Paste copied keyframes to clipboard
     */
    protected void pasteKeyframes(Map<String, PastedKeyframes> keyframes, long offset, int mouseY)
    {
        List<UIProperty> properties = this.properties.getProperties();

        this.properties.clearSelection();

        if (keyframes.size() == 1)
        {
            UIProperty current = this.properties.getProperty(mouseY);

            if (current == null)
            {
                current =  properties.get(0);
            }

            this.pasteKeyframesTo(current, keyframes.get(keyframes.keySet().iterator().next()), offset);

            return;
        }

        for (Map.Entry<String, PastedKeyframes> entry : keyframes.entrySet())
        {
            for (UIProperty property : properties)
            {
                if (!property.id.equals(entry.getKey()))
                {
                    continue;
                }

                this.pasteKeyframesTo(property, entry.getValue(), offset);
            }
        }
    }

    private void pasteKeyframesTo(UIProperty property, PastedKeyframes pastedKeyframes, long offset)
    {
        if (property.channel.getFactory() != pastedKeyframes.factory)
        {
            return;
        }

        long firstX = pastedKeyframes.keyframes.get(0).getTick();
        List<GenericKeyframe> toSelect = new ArrayList<>();

        for (GenericKeyframe keyframe : pastedKeyframes.keyframes)
        {
            keyframe.setTick(keyframe.getTick() - firstX + offset);

            int index = property.channel.insert(keyframe.getTick(), keyframe.getValue());
            GenericKeyframe inserted = property.channel.get(index);

            inserted.copy(keyframe);
            toSelect.add(inserted);
        }

        for (GenericKeyframe select : toSelect)
        {
            property.selected.add(property.channel.getKeyframes().indexOf(select));
        }

        this.properties.selected = true;
        this.properties.setKeyframe(this.properties.getCurrent());
    }

    protected void doubleClick(int mouseX, int mouseY)
    {
        this.properties.doubleClick(mouseX, mouseY);
        this.fillData(this.properties.getCurrent());
    }

    public void resetView()
    {
        this.properties.resetView();
    }

    public void selectAll()
    {
        this.properties.selectAll();
    }

    public void removeSelectedKeyframes()
    {
        this.properties.removeSelectedKeyframes();
    }

    private void setInstant(boolean instant)
    {
        this.properties.setInstant(instant);
    }

    public void setTick(double tick)
    {
        this.properties.setTick(this.converter == null ? tick : this.converter.from(tick));
    }

    public void setDuration(int value)
    {
        GenericKeyframe current = this.properties.getCurrent();

        if (current != null)
        {
            current.setDuration(value);
        }
    }

    public void setValue(Object value)
    {
        this.properties.setValue(value);
    }

    public void pickInterpolation(IInterpolation interp)
    {
        this.properties.setInterpolation(interp);
    }

    public void fillData(GenericKeyframe frame)
    {
        boolean show = frame != null;

        this.frameButtons.setVisible(show);

        if (!show)
        {
            return;
        }

        double tick = frame.getTick();
        float duration = frame.getDuration();

        if (this.editor != null)
        {
            this.editor.removeFromParent();
            this.editor = null;
        }

        this.editor = frame.getFactory().createUI(frame, this);

        if (this.editor != null)
        {
            this.frameButtons.add(this.editor);
        }

        this.instant.setValue(frame.isInstant());
        this.tick.setValue(this.converter == null ? tick : this.converter.to(tick));
        this.duration.setValue(this.converter == null ? duration : this.converter.to(duration));
        this.frameButtons.resize();
    }

    public void pickKeyframe(GenericKeyframe frame)
    {
        this.fillData(frame);

        this.properties.selected = true;

        main:
        for (UIProperty property : this.properties.getProperties())
        {
            int i = 0;

            for (Object object : property.channel.getKeyframes())
            {
                if (object == frame)
                {
                    property.selected.add(i);

                    break main;
                }

                i += 1;
            }
        }
    }

    public void select(List<List<Integer>> selection, Vector2i selected)
    {
        int i = 0;
        boolean deselect = true;

        for (UIProperty property : this.properties.getProperties())
        {
            List<Integer> sheetSelection = CollectionUtils.inRange(selection, i) ? selection.get(i) : null;

            if (sheetSelection != null)
            {
                property.selected.clear();
                property.selected.addAll(sheetSelection);
            }

            if (i == selected.x)
            {
                GenericKeyframe keyframe = property.channel.get(selected.y);

                if (keyframe != null)
                {
                    this.fillData(keyframe);

                    deselect = false;
                }
            }

            i += 1;
        }

        if (deselect)
        {
            this.fillData(null);
        }
    }

    private static class PastedKeyframes
    {
        public IGenericKeyframeFactory factory;
        public List<GenericKeyframe> keyframes = new ArrayList<>();

        public PastedKeyframes(IGenericKeyframeFactory factory)
        {
            this.factory = factory;
        }
    }
}