package mchorse.bbs.graphics.line;

import mchorse.bbs.graphics.vao.VAOBuilder;

public interface ILineRenderer <T>
{
    public void render(VAOBuilder builder, LinePoint<T> point);
}