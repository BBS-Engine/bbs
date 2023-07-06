package mchorse.bbs.graphics.shaders;

import mchorse.bbs.graphics.vao.VBOAttributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ShaderRepository
{
    private Map<VBOAttributes, Shader> shaders = new HashMap<VBOAttributes, Shader>();

    public void clear()
    {
        this.shaders.clear();
    }

    public void register(Shader shader)
    {
        this.shaders.put(shader.attributes, shader);
    }

    /**
     * Get shader for attribute layout.
     */
    public Shader get(VBOAttributes attributes)
    {
        return this.shaders.get(attributes);
    }

    public Collection<Shader> getAll()
    {
        return this.shaders.values();
    }
}