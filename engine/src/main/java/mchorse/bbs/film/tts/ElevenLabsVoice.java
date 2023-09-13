package mchorse.bbs.film.tts;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;

public class ElevenLabsVoice implements IMapSerializable
{
    public String id = "";
    public String name = "";
    public String category = "";

    public boolean isCloned()
    {
        return this.category.equals("cloned");
    }

    @Override
    public void fromData(MapType data)
    {
        this.id = data.getString("voice_id");
        this.name = data.getString("name");
        this.category = data.getString("category");
    }

    @Override
    public void toData(MapType data)
    {}
}