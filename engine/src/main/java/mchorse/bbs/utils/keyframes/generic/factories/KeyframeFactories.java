package mchorse.bbs.utils.keyframes.generic.factories;

import java.util.HashMap;
import java.util.Map;

public class KeyframeFactories
{
    public static final Map<String, IGenericKeyframeFactory> SERIALIZERS = new HashMap<>();
    public static final ColorKeyframeFactory COLOR = new ColorKeyframeFactory();
    public static final TransformKeyframeFactory TRANSFORM = new TransformKeyframeFactory();
    public static final PoseKeyframeFactory POSE = new PoseKeyframeFactory();
    public static final BooleanKeyframeFactory BOOLEAN = new BooleanKeyframeFactory();
    public static final StringKeyframeFactory STRING = new StringKeyframeFactory();
    public static final FloatKeyframeFactory FLOAT = new FloatKeyframeFactory();
    public static final IntegerKeyframeFactory INTEGER = new IntegerKeyframeFactory();
    public static final LinkKeyframeFactory LINK = new LinkKeyframeFactory();
    public static final Vector4fKeyframeFactory VECTOR4F = new Vector4fKeyframeFactory();

    static
    {
        SERIALIZERS.put("color", COLOR);
        SERIALIZERS.put("transform", TRANSFORM);
        SERIALIZERS.put("pose", POSE);
        SERIALIZERS.put("boolean", BOOLEAN);
        SERIALIZERS.put("string", STRING);
        SERIALIZERS.put("float", FLOAT);
        SERIALIZERS.put("integer", INTEGER);
        SERIALIZERS.put("link", LINK);
        SERIALIZERS.put("vector4f", VECTOR4F);
    }
}