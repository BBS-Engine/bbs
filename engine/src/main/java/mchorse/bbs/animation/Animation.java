package mchorse.bbs.animation;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.graphics.RenderingContext;

import java.util.HashMap;
import java.util.Map;

public class Animation extends AbstractData
{
    public int duration = 30;
    public Map<String, AnimationModel> models = new HashMap<>();

    public void render(RenderingContext context, float currentTime)
    {
        for (AnimationModel model : this.models.values())
        {
            model.render(context, currentTime);
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.putInt("duration", this.duration);

        MapType entries = new MapType();

        for (Map.Entry<String, AnimationModel> entry : this.models.entrySet())
        {
            entries.put(entry.getKey(), entry.getValue().toData());
        }

        data.put("models", entries);
    }

    @Override
    public void fromData(MapType data)
    {
        this.duration = data.getInt("duration");
        this.models.clear();

        MapType entries = data.getMap("models");

        for (String key : entries.keys())
        {
            MapType entryMap = entries.getMap(key);
            AnimationModel entry = new AnimationModel();

            entry.fromData(entryMap);
            this.models.put(key, entry);
        }
    }
}