package mchorse.bbs.utils.keyframes.generic.serializers;

import java.util.HashMap;
import java.util.Map;

public class KeyframeSerializers
{
    public static final Map<String, IGenericKeyframeSerializer> SERIALIZERS = new HashMap<>();
    public static final ColorKeyframeSerializer COLOR = new ColorKeyframeSerializer();
    public static final TransformKeyframeSerializer TRANSFORM = new TransformKeyframeSerializer();
    public static final PoseKeyframeSerializer POSE = new PoseKeyframeSerializer();
    public static final BooleanKeyframeSerializer BOOLEAN = new BooleanKeyframeSerializer();
    public static final StringKeyframeSerializer STRING = new StringKeyframeSerializer();
    public static final FloatKeyframeSerializer FLOAT = new FloatKeyframeSerializer();
    public static final LinkKeyframeSerializer LINK = new LinkKeyframeSerializer();

    static
    {
        SERIALIZERS.put("color", COLOR);
        SERIALIZERS.put("transform", TRANSFORM);
        SERIALIZERS.put("pose", POSE);
        SERIALIZERS.put("boolean", BOOLEAN);
        SERIALIZERS.put("string", STRING);
        SERIALIZERS.put("float", FLOAT);
        SERIALIZERS.put("link", LINK);
    }
}