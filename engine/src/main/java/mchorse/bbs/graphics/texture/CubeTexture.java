package mchorse.bbs.graphics.texture;

import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.io.InputStream;
import java.util.List;

/**
 * Cubed 3D texture thing 
 */
public class CubeTexture extends Texture
{
    public CubeTexture()
    {
        super();

        this.target = GL13.GL_TEXTURE_CUBE_MAP;
        this.setWrap(GL12.GL_CLAMP_TO_EDGE);
    }

    @Override
    public void setWrap(int mode)
    {
        super.setWrap(mode);
        this.setParameter(GL12.GL_TEXTURE_WRAP_R, mode);
    }

    /**
     * The input streams in the list must be exactly in the order of:
     * positive X, negative X; positive Y, negative Y; and positive Z 
     * and negative Z!
     */
    public boolean uploadCubemap(List<InputStream> faces)
    {
        /* Because it's cube */
        if (faces.size() != 6)
        {
            return false;
        }

        try
        {
            for (int i = 0; i < faces.size(); i++)
            {
                this.uploadTexture(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, Pixels.fromPNGStream(faces.get(i)));
            }

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}