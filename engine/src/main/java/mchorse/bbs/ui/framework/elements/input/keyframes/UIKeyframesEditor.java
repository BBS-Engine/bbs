package mchorse.bbs.ui.framework.elements.input.keyframes;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.keyframes.Keyframe;
import mchorse.bbs.utils.keyframes.KeyframeEasing;
import mchorse.bbs.utils.keyframes.KeyframeInterpolation;
import mchorse.bbs.utils.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UIKeyframesEditor <T extends UIKeyframes> extends UIElement
{
    public UIElement frameButtons;
    public UITrackpad tick;
    public UITrackpad value;
    public UIIcon interp;
    public UIIcon easing;

    public T keyframes;

    private int clicks;
    private long clickTimer;

    private KeyframeEasing e = KeyframeEasing.IN;
    private IAxisConverter converter;

    public UIKeyframesEditor()
    {
        super();

        InterpolationTooltip tooltip = new InterpolationTooltip(0F, 0F, () ->
        {
            Keyframe keyframe = this.keyframes.getCurrent();

            if (keyframe == null)
            {
                return null;
            }

            return keyframe.interp.from(keyframe.easing);
        }, null);

        this.frameButtons = new UIElement();
        this.frameButtons.relative(this).x(1F).y(1F).w(100).anchor(1F).column().vertical().stretch().padding(5);
        this.frameButtons.setVisible(false);
        this.tick = new UITrackpad(this::setTick);
        this.tick.limit(Integer.MIN_VALUE, Integer.MAX_VALUE, true).tooltip(UIKeys.KEYFRAMES_TICK);
        this.value = new UITrackpad(this::setValue);
        this.value.tooltip(UIKeys.KEYFRAMES_VALUE);
        this.interp = new UIIcon(Icons.GRAPH, (b) ->
        {
            UI.keyframeInterps(this.getContext(), this.keyframes.getCurrent().interp, (i) ->
            {
                this.keyframes.setInterpolation(i);
            });
        });
        this.interp.tooltip(tooltip);

        this.easing = new UIIcon(Icons.CURVES, (b) ->
        {
            KeyframeEasing[] values = KeyframeEasing.values();
            this.e = values[MathUtils.cycler(this.e.ordinal() + 1, 0, values.length - 1)];

            this.changeEasing();
        });
        this.easing.tooltip(tooltip);

        this.keyframes = this.createElement();

        /* Position the elements */
        this.tick.w(70);
        this.value.w(70);
        this.keyframes.relative(this).set(0, 0, 0, 0).w(1, 0).h(1, 0);

        /* Add all elements */
        this.add(this.keyframes, this.frameButtons);
        this.frameButtons.add(UI.row(0, this.interp, this.tick), UI.row(0, this.easing, this.value));

        this.context((menu) ->
        {
            menu.action(Icons.MAXIMIZE, UIKeys.KEYFRAMES_CONTEXT_MAXIMIZE, this::resetView);
            menu.action(Icons.FULLSCREEN, UIKeys.KEYFRAMES_CONTEXT_SELECT_ALL, this::selectAll);
            menu.action(Icons.MINIMIZE, IKey.lazy("Simplify"), this::simplify);

            if (this.keyframes.which != Selection.NOT_SELECTED)
            {
                menu.action(Icons.REMOVE, UIKeys.KEYFRAMES_CONTEXT_REMOVE, this::removeSelectedKeyframes);
                menu.action(Icons.COPY, UIKeys.KEYFRAMES_CONTEXT_COPY, this::copyKeyframes);
            }

            Map<String, List<Keyframe>> pasted = this.parseKeyframes();

            if (pasted != null)
            {
                UIContext context = this.getContext();
                final Map<String, List<Keyframe>> keyframes = pasted;
                double offset = this.keyframes.scaleX.from(context.mouseX);
                int mouseY = context.mouseY;

                menu.action(Icons.PASTE, UIKeys.KEYFRAMES_CONTEXT_PASTE, () -> this.pasteKeyframes(keyframes, (long) offset, mouseY));
            }

            if (this.keyframes.which != Selection.NOT_SELECTED && this.keyframes.isMultipleSelected())
            {
                menu.action(Icons.LEFT_HANDLE, UIKeys.KEYFRAMES_CONTEXT_TO_LEFT, () -> this.keyframes.which = Selection.LEFT_HANDLE);
                menu.action(Icons.MAIN_HANDLE, UIKeys.KEYFRAMES_CONTEXT_TO_MAIN, () -> this.keyframes.which = Selection.KEYFRAME);
                menu.action(Icons.RIGHT_HANDLE, UIKeys.KEYFRAMES_CONTEXT_TO_RIGHT, () -> this.keyframes.which = Selection.RIGHT_HANDLE);
            }
        });

        IKey category = UIKeys.KEYFRAMES_KEYS_CATEGORY;

        this.keys().register(Keys.KEYFRAMES_MAXIMIZE, this::resetView).inside().category(category);
        this.keys().register(Keys.KEYFRAMES_SELECT_ALL, this::selectAll).inside().category(category);

        this.interp.keys().register(Keys.KEYFRAMES_INTERP, this::toggleInterpolation).category(category);
        this.easing.keys().register(Keys.KEYFRAMES_EASING, this::toggleEasing).category(category);
    }

    protected abstract T createElement();

    protected void toggleInterpolation()
    {
        this.interp.clickItself();
    }

    protected void toggleEasing()
    {
        this.easing.clickItself(this.getContext(), Window.isShiftPressed() ? 1 : 0);
    }

    public void setConverter(IAxisConverter converter)
    {
        this.converter = converter;
        this.keyframes.setConverter(converter);

        if (converter != null)
        {
            converter.updateField(this.tick);
        }

        this.fillData(this.keyframes.getCurrent());
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
    private Map<String, List<Keyframe>> parseKeyframes()
    {
        MapType data = Window.getClipboardMap("_CopyKeyframes");

        if (data == null)
        {
            return null;
        }

        Map<String, List<Keyframe>> temp = new HashMap<>();

        for (String key : data.keys())
        {
            ListType list = data.getList(key);

            for (int i = 0, c = list.size(); i < c; i++)
            {
                List<Keyframe> keyframes = temp.computeIfAbsent(key, k -> new ArrayList<>());
                Keyframe keyframe = new Keyframe();

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

        for (UISheet sheet : this.keyframes.getSheets())
        {
            int c = sheet.getSelectedCount();

            if (c > 0)
            {
                ListType list = new ListType();

                for (int i = 0; i < c; i++)
                {
                    Keyframe keyframe = sheet.channel.get(sheet.selected.get(i));

                    list.add(keyframe.toData());
                }

                if (!list.isEmpty())
                {
                    keyframes.put(sheet.id, list);
                }
            }
        }

        Window.setClipboard(keyframes, "_CopyKeyframes");
    }

    /**
     * Paste copied keyframes to clipboard
     */
    protected void pasteKeyframes(Map<String, List<Keyframe>> keyframes, long offset, int mouseY)
    {
        List<UISheet> sheets = this.keyframes.getSheets();

        this.keyframes.clearSelection();

        if (keyframes.size() == 1)
        {
            UISheet current = this.keyframes.getSheet(mouseY);

            if (current == null)
            {
                current =  sheets.get(0);
            }

            this.pasteKeyframesTo(current, keyframes.get(keyframes.keySet().iterator().next()), offset);

            return;
        }

        for (Map.Entry<String, List<Keyframe>> entry : keyframes.entrySet())
        {
            for (UISheet sheet : sheets)
            {
                if (!sheet.id.equals(entry.getKey()))
                {
                    continue;
                }

                this.pasteKeyframesTo(sheet, entry.getValue(), offset);
            }
        }
    }

    private void pasteKeyframesTo(UISheet sheet, List<Keyframe> keyframes, long offset)
    {
        long firstX = keyframes.get(0).tick;
        List<Keyframe> toSelect = new ArrayList<>();

        if (Window.isCtrlPressed())
        {
            offset = firstX;
        }

        for (Keyframe keyframe : keyframes)
        {
            keyframe.tick = keyframe.tick - firstX + offset;

            int index = sheet.channel.insert(keyframe.tick, keyframe.value);
            Keyframe inserted = sheet.channel.get(index);

            inserted.copy(keyframe);
            toSelect.add(inserted);
        }

        for (Keyframe select : toSelect)
        {
            sheet.selected.add(sheet.channel.getKeyframes().indexOf(select));
        }

        this.keyframes.which = Selection.KEYFRAME;
        this.keyframes.setKeyframe(this.keyframes.getCurrent());
    }

    protected void doubleClick(int mouseX, int mouseY)
    {
        this.keyframes.doubleClick(mouseX, mouseY);
        this.fillData(this.keyframes.getCurrent());
    }

    public void resetView()
    {
        this.keyframes.resetView();
    }

    public void selectAll()
    {
        this.keyframes.selectAll();
    }

    public void simplify()
    {
        for (UISheet sheet : this.keyframes.getSheets())
        {
            sheet.channel.simplify();
        }
    }

    public void removeSelectedKeyframes()
    {
        this.keyframes.removeSelectedKeyframes();
    }

    public void setTick(double tick)
    {
        this.keyframes.setTick(this.converter == null ? tick : this.converter.from(tick), false);
    }

    public void setValue(double value)
    {
        this.keyframes.setValue(value, false);
    }

    public void pickInterpolation(KeyframeInterpolation interp)
    {
        this.keyframes.setInterpolation(interp);
    }

    public void changeEasing()
    {
        this.keyframes.setEasing(this.e);
    }

    public void fillData(Keyframe frame)
    {
        boolean show = frame != null && this.keyframes.which != Selection.NOT_SELECTED;

        this.frameButtons.setVisible(show);

        if (!show)
        {
            return;
        }

        double tick = this.keyframes.which.getX(frame);
        boolean forceInteger = this.keyframes.which == Selection.KEYFRAME;

        this.tick.integer = this.converter == null ? forceInteger : this.converter.forceInteger(frame, this.keyframes.which, forceInteger);
        this.tick.setValue(this.converter == null ? tick : this.converter.to(tick));
        this.value.setValue(this.keyframes.which.getY(frame));
        this.e = frame.easing;
    }
}