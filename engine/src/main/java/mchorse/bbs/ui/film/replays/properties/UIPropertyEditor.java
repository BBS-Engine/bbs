package mchorse.bbs.ui.film.replays.properties;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.replays.properties.factories.UIKeyframeFactory;
import mchorse.bbs.ui.film.utils.UICameraUtils;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.keyframes.IAxisConverter;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.CollectionUtils;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframe;
import mchorse.bbs.utils.keyframes.generic.factories.IGenericKeyframeFactory;
import mchorse.bbs.utils.keyframes.generic.factories.KeyframeFactories;
import mchorse.bbs.utils.math.Interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIPropertyEditor extends UIElement
{
    public UIElement frameButtons;
    public UITrackpad tick;
    public UIIcon interp;
    public UIKeyframeFactory editor;

    public UIMultiProperties properties;

    private int clicks;
    private long clickTimer;

    private IAxisConverter converter;

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

            return keyframe.interp;
        }, null);

        this.frameButtons = new UIElement();
        this.frameButtons.relative(this).x(1F).y(1F).w(120).anchor(1F).column().vertical().stretch().padding(5);
        this.frameButtons.setVisible(false);
        this.tick = new UITrackpad(this::setTick);
        this.tick.limit(Integer.MIN_VALUE, Integer.MAX_VALUE, true).tooltip(UIKeys.KEYFRAMES_TICK);
        this.interp = new UIIcon(Icons.GRAPH, (b) ->
        {
            UICameraUtils.interps(this.getContext(), (Interpolation) this.properties.getCurrent().interp, (i) ->
            {
                this.properties.setInterpolation(i);
            });
        });
        this.interp.tooltip(tooltip);

        this.properties = new UIMultiProperties(delegate, this::fillData);

        /* Position the elements */
        this.tick.w(70);
        this.properties.relative(this).full();

        /* Add all elements */
        this.add(this.properties, this.frameButtons);
        this.frameButtons.add(UI.row(0, this.interp, this.tick));

        this.context((menu) ->
        {
            menu.action(Icons.MAXIMIZE, UIKeys.KEYFRAMES_CONTEXT_MAXIMIZE, this::resetView);
            menu.action(Icons.FULLSCREEN, UIKeys.KEYFRAMES_CONTEXT_SELECT_ALL, this::selectAll);

            if (this.properties.selected)
            {
                menu.action(Icons.REMOVE, UIKeys.KEYFRAMES_CONTEXT_REMOVE, this::removeSelectedKeyframes);
                menu.action(Icons.COPY, UIKeys.KEYFRAMES_CONTEXT_COPY, this::copyKeyframes);
            }

            Map<String, List<GenericKeyframe>> pasted = this.parseKeyframes();

            if (pasted != null)
            {
                UIContext context = this.getContext();
                final Map<String, List<GenericKeyframe>> keyframes = pasted;
                double offset = this.properties.scaleX.from(context.mouseX);
                int mouseY = context.mouseY;

                menu.action(Icons.PASTE, UIKeys.KEYFRAMES_CONTEXT_PASTE, () -> this.pasteKeyframes(keyframes, (long) offset, mouseY));
            }
        });

        IKey category = UIKeys.KEYFRAMES_KEYS_CATEGORY;

        this.keys().register(Keys.KEYFRAMES_MAXIMIZE, this::resetView).inside().category(category);
        this.keys().register(Keys.KEYFRAMES_SELECT_ALL, this::selectAll).inside().category(category);

        this.interp.keys().register(Keys.KEYFRAMES_INTERP, this.interp::clickItself).category(category);
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
    private Map<String, List<GenericKeyframe>> parseKeyframes()
    {
        MapType data = Window.getClipboardMap("_CopyProperties");

        if (data == null)
        {
            return null;
        }

        Map<String, List<GenericKeyframe>> temp = new HashMap<>();

        for (String key : data.keys())
        {
            MapType map = data.getMap(key);
            ListType list = map.getList("keyframes");
            IGenericKeyframeFactory serializer = KeyframeFactories.SERIALIZERS.get(map.getString("type"));

            for (int i = 0, c = list.size(); i < c; i++)
            {
                List<GenericKeyframe> keyframes = temp.computeIfAbsent(key, k -> new ArrayList<>());
                GenericKeyframe keyframe = new GenericKeyframe(serializer);

                keyframe.fromData(list.getMap(i));
                keyframes.add(keyframe);
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

                data.putString("type", CollectionUtils.getKey(KeyframeFactories.SERIALIZERS, property.channel.getFactory()));
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
    protected void pasteKeyframes(Map<String, List<GenericKeyframe>> keyframes, long offset, int mouseY)
    {
        List<UIProperty> properties = this.properties.getProperties();

        this.properties.clearSelection();

        if (keyframes.size() == 1)
        {
            UIProperty current = this.properties.getSheet(mouseY);

            if (current == null)
            {
                current =  properties.get(0);
            }

            this.pasteKeyframesTo(current, keyframes.get(keyframes.keySet().iterator().next()), offset);

            return;
        }

        for (Map.Entry<String, List<GenericKeyframe>> entry : keyframes.entrySet())
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

    private void pasteKeyframesTo(UIProperty property, List<GenericKeyframe> keyframes, long offset)
    {
        long firstX = keyframes.get(0).tick;
        List<GenericKeyframe> toSelect = new ArrayList<>();

        if (Window.isCtrlPressed())
        {
            offset = firstX;
        }

        for (GenericKeyframe keyframe : keyframes)
        {
            keyframe.tick = keyframe.tick - firstX + offset;

            int index = property.channel.insert(keyframe.tick, keyframe.value);
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

    public void setTick(double tick)
    {
        this.properties.setTick(this.converter == null ? tick : this.converter.from(tick));
    }

    public void setValue(Object value)
    {
        this.properties.setValue(value);
    }

    public void fillData(GenericKeyframe frame)
    {
        boolean show = frame != null;

        this.frameButtons.setVisible(show);

        if (!show)
        {
            return;
        }

        double tick = frame.tick;

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

        this.tick.setValue(this.converter == null ? tick : this.converter.to(tick));
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
}