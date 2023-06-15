package mchorse.bbs.ui.framework.elements.input.color;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.math.MathUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * Color palette GUI element
 *
 * This element allows to provide a way to select a color from a grid like
 * list
 */
public class UIColorPalette extends UIElement
{
    public List<Color> colors;
    public Consumer<Color> callback;
    public int cellSize = 10;

    public UIColorPalette(Consumer<Color> callback)
    {
        super();

        this.callback = callback;
    }

    public UIColorPalette colors(List<Color> colors)
    {
        this.colors = colors;

        return this;
    }

    public UIColorPalette cellSize(int cellSize)
    {
        this.cellSize = cellSize;

        return this;
    }

    public int getHeight(int width)
    {
        return MathUtils.gridRows(this.colors.size(), this.cellSize, width) * this.cellSize;
    }

    public boolean hasColor(int index)
    {
        return index >= 0 && index < this.colors.size();
    }

    public int getIndex(UIContext context)
    {
        return this.colors.size() - 1 - this.area.getIndex(context.mouseX, context.mouseY, this.cellSize);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            int index = this.getIndex(context);

            if (this.hasColor(index) && this.callback != null)
            {
                this.callback.accept(this.colors.get(index));
            }

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        /* Draw recent colors panel */
        int count = this.colors.size();

        if (count > 0)
        {
            int elements = this.area.w / this.cellSize;

            if (this.area.h > this.cellSize)
            {
                context.batcher.iconArea(Icons.CHECKBOARD, this.area.x, this.area.y, this.area.w, this.area.h - this.cellSize);
            }

            context.batcher.iconArea(Icons.CHECKBOARD, this.area.x, this.area.ey() - this.cellSize, count % elements * this.cellSize, this.cellSize);

            for (int i = count - 1, j = 0; i >= 0; i--, j++)
            {
                Color c = this.colors.get(i);
                int x = this.area.x + j % elements * this.cellSize;
                int y = this.area.y + j / elements * this.cellSize;

                UIColorPicker.renderAlphaPreviewQuad(context.batcher, x, y, x + this.cellSize, y + this.cellSize, c);
            }
        }

        super.render(context);
    }
}
