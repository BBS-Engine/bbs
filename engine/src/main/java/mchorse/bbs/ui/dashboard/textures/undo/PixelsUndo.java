package mchorse.bbs.ui.dashboard.textures.undo;

import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.undo.IUndo;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

public class PixelsUndo implements IUndo<Pixels>
{
    public Map<Vector2i, Pair<Color, Color>> pixels = new HashMap<>();

    public void setColor(Pixels pixels, int x, int y, Color color)
    {
        if (x < 0 || y < 0 || x >= pixels.width || y >= pixels.height)
        {
            return;
        }

        Vector2i key = new Vector2i(x, y);
        Pair<Color, Color> pair = this.pixels.computeIfAbsent(key, (k) -> new Pair<>(pixels.getColor(x, y).copy(), null));

        pair.b = color.copy();
        pixels.setColor(x, y, color);
    }

    @Override
    public IUndo<Pixels> noMerging()
    {
        return this;
    }

    @Override
    public boolean isMergeable(IUndo<Pixels> undo)
    {
        return false;
    }

    @Override
    public void merge(IUndo<Pixels> undo)
    {}

    @Override
    public void undo(Pixels context)
    {
        for (Map.Entry<Vector2i, Pair<Color, Color>> entry : this.pixels.entrySet())
        {
            Vector2i key = entry.getKey();

            context.setColor(key.x, key.y, entry.getValue().a);
        }
    }

    @Override
    public void redo(Pixels context)
    {
        for (Map.Entry<Vector2i, Pair<Color, Color>> entry : this.pixels.entrySet())
        {
            Vector2i key = entry.getKey();

            context.setColor(key.x, key.y, entry.getValue().b);
        }
    }
}