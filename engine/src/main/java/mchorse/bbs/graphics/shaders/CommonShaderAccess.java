package mchorse.bbs.graphics.shaders;

import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.shaders.uniforms.UniformInt;
import mchorse.bbs.graphics.shaders.uniforms.UniformMatrix3;
import mchorse.bbs.graphics.shaders.uniforms.UniformMatrix4;
import mchorse.bbs.graphics.shaders.uniforms.UniformMatrix4s;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector2;
import mchorse.bbs.graphics.shaders.uniforms.UniformVector4;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.joml.Matrices;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

public class CommonShaderAccess
{
    public static void initializeTexture(Shader shader)
    {
        setTexture(shader, 0);
        resetColor(shader);
    }

    public static void setModelView(Shader shader, MatrixStack stack)
    {
        setModelView(shader, stack.getModelMatrix(), stack.getNormalMatrix());
    }

    public static void setModelView(Shader shader)
    {
        setModelView(shader, Matrices.EMPTY_4F, Matrices.EMPTY_3F);
    }

    public static void setModelView(Shader shader, Matrix4f matModel, Matrix3f matNormal)
    {
        UniformMatrix4 model = shader.getUniform("u_model", UniformMatrix4.class);
        UniformMatrix3 normal = shader.getUniform("u_normal", UniformMatrix3.class);

        if (model != null)
        {
            model.set(matModel);
        }

        if (normal != null)
        {
            normal.set(matNormal);
        }
    }

    public static void setColor(Shader shader, Color c)
    {
        setColor(shader, c.r, c.g, c.b, c.a);
    }

    public static void resetColor(Shader shader)
    {
        setColor(shader, 1F, 1F, 1F, 1F);
    }

    public static void setColor(Shader shader, float r, float g, float b, float a)
    {
        UniformVector4 color = shader.getUniform("u_color", UniformVector4.class);

        if (color != null)
        {
            color.set(r, g, b, a);
        }
    }

    public static void setTexture(Shader shader, int index)
    {
        UniformInt texture = shader.getUniform("u_texture", UniformInt.class);

        if (texture != null)
        {
            texture.set(index);
        }
    }

    public static void setTarget(Shader shader, int index)
    {
        UniformInt texture = shader.getUniform("u_target", UniformInt.class);

        if (texture != null)
        {
            texture.set(index);
        }
    }

    public static void setBones(Shader shader, List<Matrix4f> transformations)
    {
        UniformMatrix4s bones = shader.getUniform("u_bones", UniformMatrix4s.class);

        if (bones != null)
        {
            bones.set(transformations);
        }
    }

    public static void setMultiLink(Shader shader, int w, int h, int pixelate, boolean erase)
    {
        UniformInt textureBackground = shader.getUniform("u_texture_background", UniformInt.class);
        UniformVector2 size = shader.getUniform("u_size", UniformVector2.class);
        UniformVector4 filters = shader.getUniform("u_filters", UniformVector4.class);

        if (textureBackground != null)
        {
            textureBackground.set(5);
        }

        if (size != null)
        {
            size.set(w, h);
        }

        if (filters != null)
        {
            filters.set(pixelate, erase ? 1 : 0, 0, 0);
        }
    }

    public static void setLightMapCoords(Shader shader, float x, float y)
    {
        UniformVector2 coords = shader.getUniform("u_lightmap_coords", UniformVector2.class);

        if (coords != null)
        {
            coords.set(x, y);
        }
    }
}