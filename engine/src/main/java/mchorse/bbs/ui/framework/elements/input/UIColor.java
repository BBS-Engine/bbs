package mchorse.bbs.ui.framework.elements.input;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.color.UIColorPicker;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

/**
 * Color GUI element
 *
 * This class is responsible for providing a way to edit colors, this element
 * itself is not editing the color, the picker element is the one that does color editing
 */
public class UIColor extends UIElement
{
    public UIColorPicker picker;
    public boolean label = true;
    public Direction direction;

    public UIColor(Consumer<Integer> callback)
    {
        super();

        this.picker = new UIColorPicker(callback);
        this.picker.wh(200, 85).bounds(this, 2);

        this.direction(Direction.BOTTOM).h(20);
    }

    public UIColor withAlpha()
    {
        this.picker.editAlpha();

        return this;
    }

    public UIColor direction(Direction direction)
    {
        this.direction = direction;
        this.picker.anchor(1 - direction.anchorX, 1 - direction.anchorY);

        return this;
    }

    public UIColor onTop()
    {
        return this.direction(Direction.TOP);
    }

    public UIColor noLabel()
    {
        this.label = false;

        return this;
    }

    public void setColor(int color)
    {
        this.picker.setColor(color);
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            if (!this.picker.hasParent())
            {
                int x = context.globalX(this.area.x(this.direction.anchorX) + 2 * this.direction.factorX);
                int y = context.globalY(this.area.y(this.direction.anchorY) + 2 * this.direction.factorY);

                context.menu.overlay.add(this.picker);
                this.picker.setup(x, y);
                this.picker.resize();
            }
            else
            {
                this.picker.removeFromParent();
            }

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        int padding = 0;

        this.picker.renderRect(context.batcher, this.area.x, this.area.y, this.area.ex(), this.area.ey());

        if (this.area.isInside(context))
        {
            this.area.render(context.batcher, Colors.A12, padding);
        }

        if (this.label)
        {
            String label = this.picker.color.stringify(this.picker.editAlpha);

            context.batcher.textCard(context.font, label, this.area.mx(context.font.getWidth(label)), this.area.my(context.font.getHeight() - 1), Colors.WHITE, Colors.A25, 1);
        }

        this.renderLockedArea(context);

        super.render(context);
    }
}