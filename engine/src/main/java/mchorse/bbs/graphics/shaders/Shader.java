package mchorse.bbs.graphics.shaders;

import mchorse.bbs.BBS;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.graphics.shaders.uniforms.Uniform;
import mchorse.bbs.graphics.ubo.UBO;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.resources.Link;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL31;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Shader program class
 */
public final class Shader implements IDisposable
{
    private static Shader bindShader;

    /**
     * Shader's name 
     */
    public final Link name;

    /**
     * Attributes layout that is supported by this shader.
     */
    public final VBOAttributes attributes;

    /**
     * OpenGL ID of this shader program
     */
    private int program;

    /**
     * Registered uniform list
     */
    private Set<Uniform> dirtyUniforms = new HashSet<>();

    /**
     * Shader uniforms that were attached outside the subclasses
     */
    private Map<String, Uniform> uniforms = new HashMap<>();

    /**
     * UBOs (Uniform Buffer Object) map
     */
    private Map<String, UBO> ubos = new HashMap<>();

    /**
     * Parsed defines
     */
    private Map<String, String> defines = new HashMap<>();

    /**
     * Imported shaders
     */
    private Set<Link> imported = new HashSet<>();

    /**
     * Initialize callback
     */
    private Consumer<Shader> onInitialize;

    private boolean constructed;

    public static boolean isBind(Shader shader)
    {
        return bindShader == shader;
    }

    /**
     * Creates a program and checks whether the program was created 
     * successively 
     */
    public Shader(Link name, VBOAttributes attributes)
    {
        this.name = name;
        this.attributes = attributes;

        this.createProgram();
    }

    public void setDefines(Map<String, String> defines)
    {
        this.defines.clear();
        this.defines.putAll(defines);
    }

    public Map<String, String> getDefines()
    {
        return this.defines;
    }

    public void setImported(Set<Link> imported)
    {
        this.imported.clear();
        this.imported.addAll(imported);
    }

    public Set<Link> getImported()
    {
        return this.imported;
    }

    private void createProgram()
    {
        this.program = GL20.glCreateProgram();

        if (this.program == 0)
        {
            throw new RuntimeException("Could not create shader " + this.name);
        }
    }

    public Shader onInitialize(Consumer<Shader> consumer)
    {
        this.onInitialize = consumer;

        return this;
    }

    public void reload()
    {
        this.delete();
        this.createProgram();

        this.constructed = false;

        this.uniforms.clear();
        this.dirtyUniforms.clear();
        this.tryConstructing();
    }

    /**
     * Get shader program ID
     */
    public int getProgram()
    {
        return this.program;
    }

    public void changeUniform(Uniform uniform)
    {
        this.dirtyUniforms.add(uniform);
    }

    public void registerUniforms(Collection<Uniform> uniforms)
    {
        for (Uniform uniform : uniforms)
        {
            this.registerUniform(uniform);
        }
    }

    public void registerUniform(Uniform uniform)
    {
        uniform.setProgram(this);
        this.uniforms.put(uniform.name, uniform);
    }

    public Uniform getUniform(String name)
    {
        this.tryConstructing();

        return this.uniforms.get(name);
    }

    public <T extends Uniform> T getUniform(String name, Class<T> clazz)
    {
        Uniform uniform = this.getUniform(name);

        return uniform == null ? null : clazz.cast(uniform);
    }

    /**
     * Link the program and clean up attached shaders
     */
    public void link(int vertexShaderId, int fragmentShaderId) throws Exception
    {
        GL20.glLinkProgram(this.program);

        if (GL20.glGetProgrami(this.program, GL20.GL_LINK_STATUS) == 0)
        {
            throw new Exception("Error linking shader code: " + GL20.glGetProgramInfoLog(this.program, 1024));
        }

        GL20.glDetachShader(this.program, vertexShaderId);
        GL20.glDetachShader(this.program, fragmentShaderId);

        GL20.glValidateProgram(this.program);

        if (GL20.glGetProgrami(this.program, GL20.GL_VALIDATE_STATUS) == 0)
        {
            System.err.println("Warning validating shader code: " + GL20.glGetProgramInfoLog(this.program, 1024));
        }

        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);

        this.bind();

        /* Attach uniforms */
        for (Uniform uniform : this.uniforms.values())
        {
            uniform.attach();
        }

        /* Attach UBOs */
        for (Map.Entry<String, UBO> entry : this.ubos.entrySet())
        {
            int block = GL31.glGetUniformBlockIndex(this.program, entry.getKey());

            if (block < 0)
            {
                System.err.println("Shader \"" + this.name + "\" doesn't have uniform block named \"" + entry.getKey() + "\"");
            }

            GL31.glUniformBlockBinding(this.program, block, entry.getValue().unit);
        }

        if (this.onInitialize != null)
        {
            this.onInitialize.accept(this);
        }
    }

    /**
     * Bind this shader program to current context
     */
    public void bind()
    {
        this.tryConstructing();

        if (bindShader != this)
        {
            GL20.glUseProgram(this.program);
        }

        bindShader = this;

        for (Uniform uniform : this.dirtyUniforms)
        {
            uniform.submit();
        }

        this.dirtyUniforms.clear();
    }


    private void tryConstructing()
    {
        if (this.constructed)
        {
            return;
        }

        this.constructed = true;

        try
        {
            BBS.getShaders().buildShader(this, this.name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Unbind this shader program to current context
     */
    public void unbind()
    {
        bindShader = null;
        
        GL20.glUseProgram(0);
    }

    /**
     * Attach a UBO to a given block name
     */
    public void attachUBO(UBO ubo, String blockName)
    {
        this.ubos.put(blockName, ubo);
    }

    /**
     * Free up the memory occupied by the shader when no longer needed
     */
    @Override
    public void delete()
    {
        unbind();

        if (this.program != 0)
        {
            GL20.glDeleteProgram(this.program);
        }
    }
}