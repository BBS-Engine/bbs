package mchorse.bbs.graphics.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public enum TextureFormat
{
    RGBA_U8(GL11.GL_RGBA8, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, GL30.GL_COLOR_ATTACHMENT0),
    RGB_U8(GL11.GL_RGB8, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, GL30.GL_COLOR_ATTACHMENT0),
    RGBA_F16(GL30.GL_RGBA16F, GL11.GL_RGBA, GL11.GL_FLOAT, GL30.GL_COLOR_ATTACHMENT0),
    DEPTH_F24(GL30.GL_DEPTH_COMPONENT24, GL30.GL_DEPTH_COMPONENT, GL30.GL_FLOAT, GL30.GL_DEPTH_ATTACHMENT);

    public final int internal;
    public final int format;
    public final int type;
    public final int attachment;

    public static TextureFormat getByName(String name)
    {
        try
        {
            return valueOf(name.toUpperCase());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return RGBA_U8;
    }

    private TextureFormat(int internal, int format, int type, int attachment)
    {
        this.internal = internal;
        this.format = format;
        this.type = type;
        this.attachment = attachment;
    }

    public boolean isDepth()
    {
        return this.format == GL30.GL_DEPTH_COMPONENT;
    }

    public boolean isColor()
    {
        return this.attachment == GL30.GL_COLOR_ATTACHMENT0;
    }
}
