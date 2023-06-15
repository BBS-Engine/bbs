package mchorse.bbs.graphics.line;

import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.ui.framework.elements.utils.Batcher2D;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Line builder 2D
 *
 * This class provides a neat way to construct 2D line
 * segments that is thicker than default OpenGL3 line renderer.
 */
public class LineBuilder <T>
{
    public float thickness;
    public List<Line<T>> lines = new ArrayList<Line<T>>();

    public LineBuilder(float thickness)
    {
        this.thickness = thickness;
    }

    public LineBuilder<T> add(float x, float y)
    {
        return this.add(x, y, null);
    }

    public LineBuilder<T> add(float x, float y, T user)
    {
        if (this.lines.isEmpty())
        {
            this.push();
        }

        Line line = this.lines.get(this.lines.size() - 1);

        line.add(x, y, user);

        return this;
    }

    public LineBuilder<T> push()
    {
        return this.push(new Line<T>());
    }

    public LineBuilder<T> push(Line<T> line)
    {
        this.lines.add(line);

        return this;
    }

    public List<List<LinePoint<T>>> build()
    {
        List<List<LinePoint<T>>> output = new ArrayList<List<LinePoint<T>>>();

        for (Line line : this.lines)
        {
            List<LinePoint<T>> compiled = line.build(this.thickness);

            if (!compiled.isEmpty())
            {
                output.add(compiled);
            }
        }

        return output;
    }

    public void render(Batcher2D batcher2D, ILineRenderer<T> renderer)
    {
        List<List<LinePoint<T>>> build = this.build();

        for (List<LinePoint<T>> points : build)
        {
            VAOBuilder builder = batcher2D.begin(GL11.GL_TRIANGLE_STRIP, VBOAttributes.VERTEX_RGBA_2D, null);

            for (LinePoint<T> point : points)
            {
                renderer.render(builder, point);
            }
        }
    }
}