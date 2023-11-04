package mchorse.bbs.film.tts;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.utils.colors.Colors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueVoiceColors extends BaseValue implements IValueUIProvider
{
    private Map<String, Integer> colors = new HashMap<>();

    public ValueVoiceColors(String id)
    {
        super(id);
    }

    public void setColor(String voice, int color)
    {
        this.preNotifyParent();
        this.colors.put(voice.toLowerCase(), color);
        this.postNotifyParent();
    }

    public int getColor(String voice)
    {
        Integer color = this.colors.get(voice.toLowerCase());

        return color == null ? Colors.WHITE : color;
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton button = new UIButton(UIKeys.VOICE_COLORS_OPEN, (b) ->
        {
            UIOverlay.addOverlay(ui.getContext(), new UIVoiceColorsOverlayPanel(this));
        });

        return Arrays.asList(button);
    }

    @Override
    public BaseType toData()
    {
        MapType data = new MapType();

        for (Map.Entry<String, Integer> entry : colors.entrySet())
        {
            data.putInt(entry.getKey(), entry.getValue());
        }

        return data;
    }

    @Override
    public void fromData(BaseType data)
    {
        this.colors.clear();

        if (!data.isMap())
        {
            return;
        }

        MapType map = data.asMap();

        for (String key : map.keys())
        {
            this.colors.put(key.toLowerCase(), map.getInt(key));
        }
    }
}