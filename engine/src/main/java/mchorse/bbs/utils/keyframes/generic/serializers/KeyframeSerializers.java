package mchorse.bbs.utils.keyframes.generic.serializers;

import java.util.HashMap;
import java.util.Map;

public class KeyframeSerializers
{
    public static final Map<String, IGenericKeyframeSerializer> SERIALIZERS = new HashMap<>();
    public static final ColorKeyframeSerializer COLOR = new ColorKeyframeSerializer();

    static
    {
        SERIALIZERS.put("color", COLOR);
    }
}