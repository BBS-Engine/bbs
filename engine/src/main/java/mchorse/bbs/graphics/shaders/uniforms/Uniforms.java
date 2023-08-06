package mchorse.bbs.graphics.shaders.uniforms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Uniforms
{
    private static Map<String, Class<? extends Uniform>> uniforms = new HashMap<>();

    static
    {
        register("float", UniformFloat.class);
        register("int", UniformInt.class);
        register("sampler2D", UniformInt.class);
        register("mat3", UniformMatrix3.class);
        register("mat4", UniformMatrix4.class);
        register("mat4[]", UniformMatrix4s.class);
        register("vec2", UniformVector2.class);
        register("ivec2", UniformVector2i.class);
        register("vec3", UniformVector3.class);
        register("ivec3", UniformVector3i.class);
        register("vec4", UniformVector4.class);
        register("ivec4", UniformVector4i.class);
    }

    public static void register(String type, Class<? extends Uniform> clazz)
    {
        uniforms.put(type, clazz);
    }

    public static List<Uniform> createFromMap(Map<String, String> map)
    {
        List<Uniform> uniforms = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet())
        {
            Uniform uniform = createFromType(entry.getKey(), entry.getValue());

            if (uniform != null)
            {
                uniforms.add(uniform);
            }
        }

        return uniforms;
    }

    public static Uniform createFromType(String name, String type)
    {
        Class<? extends Uniform> clazz = uniforms.get(type);

        if (clazz == null)
        {
            System.err.println("There is no such uniform of type: " + type);

            return null;
        }

        try
        {
            return clazz.getConstructor(String.class).newInstance(name);
        }
        catch (Exception e)
        {
            System.err.println("Failed to create uniform of type: " + type);
            e.printStackTrace();
        }

        return null;
    }
}