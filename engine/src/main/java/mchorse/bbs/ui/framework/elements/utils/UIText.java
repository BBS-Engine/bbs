package mchorse.bbs.ui.framework.elements.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;

public class UIText extends UIElement implements ITextColoring
{
    private IKey temp = IKey.EMPTY;
    private List<String> text;
    private int lineHeight = 12;
    private int color = Colors.WHITE;
    private int hoverColor = Colors.WHITE;
    private boolean shadow = true;
    private int paddingH;
    private int paddingV;
    private float anchorX;

    private int lines;
    private boolean updates;
    private String lastString;

    public UIText(String text)
    {
        this();

        this.text(text);
    }

    public UIText(IKey text)
    {
        this();

        this.text(text);
    }

    public UIText()
    {
        super();
    }

    private int height()
    {
        FontRenderer font = BBS.getRender().getFont();
        int height = Math.max(this.lines, 1) * this.lineHeight - (this.lineHeight - font.getHeight());

        return height + this.paddingV * 2;
    }

    public IKey getText()
    {
        return this.temp;
    }

    public UIText text(String text)
    {
        return this.text(IKey.str(text));
    }

    public UIText text(IKey text)
    {
        this.temp = text;
        this.text = null;
        this.lines = 0;

        return this;
    }

    public UIText lineHeight(int lineHeight)
    {
        this.lineHeight = lineHeight;

        return this;
    }

    public UIText color(int color, boolean shadow)
    {
        this.color = this.hoverColor = color;
        this.shadow = shadow;

        return this;
    }

    public UIText hoverColor(int color)
    {
        this.hoverColor = color;

        return this;
    }

    public UIText padding(int padding)
    {
        return this.padding(padding, padding);
    }

    public UIText padding(int horizontal, int vertical)
    {
        this.paddingH = horizontal;
        this.paddingV = vertical;

        return this;
    }

    public UIText anchorX(float anchor)
    {
        this.anchorX = anchor;

        return this;
    }

    public UIText updates()
    {
        this.updates = true;

        return this;
    }

    @Override
    public void setColor(int color, boolean shadow)
    {
        this.color(color, shadow);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.text = null;
    }

    @Override
    public void render(UIContext context)
    {
        if (this.updates)
        {
            if (this.lastString == null)
            {
                this.lastString = this.temp.get();
            }
            else if (!this.lastString.equals(this.temp.get()))
            {
                this.text = null;
                this.lastString = this.temp.get();
            }
        }

        if (this.area.w > 0)
        {
            if (this.text == null)
            {
                List<String> text = context.font.split(this.temp.get(), this.area.w - this.paddingH * 2);

                this.lines = text.size();

                this.h(this.height());
                this.getParentContainer().resize();

                this.text = text;
                this.lines = text.size();
            }

            int y = this.paddingV;
            int color = this.area.isInside(context) ? this.hoverColor : this.color;

            for (String line : this.text)
            {
                int x = this.area.x + this.paddingH;

                if (this.anchorX != 0)
                {
                    x = x + (int) (((this.area.w - this.paddingH * 2) - (context.font.getWidth(line))) * this.anchorX);
                }

                if (this.shadow)
                {
                    context.font.renderWithShadow(context.render, line, x, this.area.y + y, color);
                }
                else
                {
                    context.font.render(context.render, line, x, this.area.y + y, color);
                }

                y += this.lineHeight;
            }
        }

        super.render(context);
    }
}