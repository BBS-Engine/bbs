package mchorse.bbs.graphics.shaders.uniforms;

import mchorse.bbs.graphics.shaders.Shader;
import org.lwjgl.opengl.GL20;

public abstract class Uniform
{
    public final String name;

    protected int uniform = -1;
    protected Shader program;

    /**
     * Get a shader uniform
     */
    public static int getUniform(Shader program, String name)
    {
        int uniform = GL20.glGetUniformLocation(program.getProgram(), name);

        if (uniform < 0)
        {
            System.err.println("Couldn't create uniform in \"" + program.name + "\" shader: " + name);
        }

        return uniform;
    }

    public Uniform(String name)
    {
        this.name = name;
    }

    public void setProgram(Shader program)
    {
        this.program = program;
    }

    public void attach()
    {
        this.uniform = getUniform(this.program, this.name);

        this.submitUniform();
    }

    protected void setChanged()
    {
        if (Shader.isBind(this.program))
        {
            this.submit();
        }
        else
        {
            this.program.changeUniform(this);
        }
    }

    public void submit()
    {
        if (this.uniform < 0)
        {
            return;
        }

        this.submitUniform();
    }

    protected abstract void submitUniform();
}