package mchorse.bbs.ui.framework.elements;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel base GUI
 * 
 * With this base class, you can add multi panel elements which could be 
 * switched between using buttons.
 */
public class UIPanelBase <T extends UIElement> extends UIElement
{
    public T view;
    public UIScrollView buttons;
    public List<T> panels = new ArrayList<T>();
    public Direction direction;

    public UIPanelBase()
    {
        this(Direction.BOTTOM);
    }

    public UIPanelBase(Direction direction)
    {
        super();

        this.direction = direction == null ? Direction.BOTTOM : direction;
        this.buttons = new UIScrollView();
        this.buttons.scroll.cancelScrolling().noScrollbar();
        this.buttons.scroll.scrollSpeed = 5;
        this.buttons.preRender((context) ->
        {
            for (int i = 0, c = this.panels.size(); i < c; i++)
            {
                if (this.view == this.panels.get(i))
                {
                    Area area = ((UIIcon) this.buttons.getChildren().get(i)).area;

                    area.render(context.batcher, Colors.A75 | BBSSettings.primaryColor.get());
                }
            }
        });

        this.setButtonsPlacement();

        this.add(new UIRenderable(this::renderOverlay), this.buttons);
    }

    public void changeDirection(Direction direction)
    {
        this.direction = direction == null ? Direction.BOTTOM : direction;

        this.setButtonsPlacement();

        if (this.view != null)
        {
            this.setPanelPlacement(this.view);
        }

        for (UIElement element : this.buttons.getChildren(UIElement.class))
        {
            if (element.tooltip != null)
            {
                element.tooltip(element.tooltip.getLabel(), this.direction.opposite());
            }
        }

        this.resize();
    }

    private void setButtonsPlacement()
    {
        this.buttons.scroll.direction = this.direction.factorX == 0 ? ScrollDirection.HORIZONTAL : ScrollDirection.VERTICAL;
        this.buttons.resetFlex();

        if (this.direction == Direction.TOP)
        {
            this.buttons.relative(this).w(1F).h(20).column(0).scroll();
        }
        else if (this.direction == Direction.LEFT)
        {
            this.buttons.relative(this).w(20).h(1F).column(0).scroll().vertical();
        }
        else if (this.direction == Direction.BOTTOM)
        {
            this.buttons.relative(this).y(1F, -20).w(1F).h(20).column(0).scroll();
        }
        else
        {
            this.buttons.relative(this).x(1F, -20).w(20).h(1F).column(0).scroll().vertical();
        }
    }

    private void setPanelPlacement(UIElement panel)
    {
        panel.resetFlex();

        if (this.direction == Direction.TOP)
        {
            panel.relative(this).y(20).w(1F).h(1F, -20);
        }
        else if (this.direction == Direction.LEFT)
        {
            panel.relative(this).x(20).w(1F, -20).h(1F);
        }
        else if (this.direction == Direction.RIGHT)
        {
            panel.relative(this).w(1F, -20).h(1F);
        }
        else
        {
            panel.relative(this).w(1F).h(1F, -20);
        }
    }

    public UIIcon getButton(T panel)
    {
        int index = this.panels.indexOf(panel);

        return index < 0 ? null : (UIIcon) this.buttons.getChildren().get(index);
    }

    /**
     * Register a panel with given texture and tooltip
     */
    public UIIcon registerPanel(T panel, IKey tooltip, Icon icon)
    {
        UIIcon button = new UIIcon(icon, (b) -> this.setPanel(panel));

        if (tooltip != null && !tooltip.get().isEmpty())
        {
            button.tooltip(tooltip, this.direction.opposite());
        }

        panel.markContainer();
        this.panels.add(panel);
        this.buttons.add(button);

        return button;
    }

    /**
     * Switch current panel to given one
     */
    public void setPanel(T panel)
    {
        if (this.view != null)
        {
            this.view.removeFromParent();
        }

        this.view = panel;

        if (this.view != null)
        {
            this.setPanelPlacement(panel);

            this.view.resize();
            this.prepend(this.view);
        }
    }

    protected void renderOverlay(UIContext context)
    {
        if (this.direction == Direction.TOP)
        {
            this.renderBackground(context, this.area.x, this.area.y, this.area.w, 20);
        }
        else if (this.direction == Direction.BOTTOM)
        {
            this.renderBackground(context, this.area.x, this.area.ey() - 20, this.area.w, 20);
        }
        else if (this.direction == Direction.LEFT)
        {
            this.renderBackground(context, this.area.x, this.area.y, 20, this.area.h);
        }
        else
        {
            this.renderBackground(context, this.area.ex() - 20, this.area.y, 20, this.area.h);
        }
    }

    protected void renderBackground(UIContext context, int x, int y, int w, int h)
    {}
}