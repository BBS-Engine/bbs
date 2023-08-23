package mchorse.bbs.graphics.text.builders;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.utils.colors.Color;

public interface ITextBuilder
{
    public static final ColoredTextBuilder2D colored2D = new ColoredTextBuilder2D();
    public static final ColoredTextBuilder3D colored3D = new ColoredTextBuilder3D();

    public void setMultiplicative(boolean multiplicative);

    public VBOAttributes getAttributes();

    public VAOBuilder put(VAOBuilder builder, float x, float y, float u, float v, float tw, float th, Color color);
}