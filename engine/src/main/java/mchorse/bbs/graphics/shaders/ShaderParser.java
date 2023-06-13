package mchorse.bbs.graphics.shaders;

import java.util.HashMap;
import java.util.Map;

public class ShaderParser
{
    public static final String SECTION_MARKER = "//@";
    public static final String UNIFORM = "\nuniform";

    public String vertex;
    public String fragment;
    public Map<String, String> uniforms = new HashMap<String, String>();

    /**
     * Parse vertex and fragment shader code out of merged shader code.  
     */
    public void parse(String code)
    {
        String lastKey = "";
        Map<String, String> sections = new HashMap<String, String>();

        int index = 0;

        while (index >= 0)
        {
            int nextIndex = code.indexOf(SECTION_MARKER, index);

            if (nextIndex >= 0)
            {
                sections.put(lastKey, code.substring(index, nextIndex));

                int nameIndex = code.indexOf('\n', nextIndex);

                lastKey = code.substring(nextIndex + SECTION_MARKER.length(), nameIndex).trim();
                index = nameIndex + 1;
            }
            else
            {
                sections.put(lastKey, code.substring(index));

                break;
            }
        }

        this.parseUniforms(code);

        String common = sections.get("");

        this.vertex = common + sections.get("vertex");
        this.fragment = common + sections.get("fragment");
    }

    /**
     * Parse uniforms out of the shader's code
     */
    public void parseUniforms(String code)
    {
        int index = 0;

        while (index >= 0)
        {
            int nextIndex = code.indexOf(UNIFORM, index);

            if (nextIndex < 0)
            {
                break;
            }

            int endIndex = code.indexOf(';', nextIndex);
            String line = code.substring(nextIndex + UNIFORM.length() + 1, endIndex);

            int space = line.lastIndexOf(' ');
            String name = line.substring(space + 1);
            String type = line.substring(0, space);

            this.uniforms.put(name, this.processType(type));

            index = endIndex + 1;
        }
    }

    /**
     * Process uniform type (if a uniform array is provided, strip the size)
     */
    private String processType(String type)
    {
        int bracket = type.indexOf('[');

        if (bracket >= 0)
        {
            return type.substring(0, bracket) + "[]";
        }

        return type;
    }
}