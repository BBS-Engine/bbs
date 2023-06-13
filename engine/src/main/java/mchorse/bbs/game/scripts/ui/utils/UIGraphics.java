package mchorse.bbs.game.scripts.ui.utils;

import mchorse.bbs.game.scripts.ui.graphics.Graphic;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.UIContext;

import java.util.ArrayList;
import java.util.List;

public class UIGraphics extends UIElement
{
    public List<Graphic> graphics = new ArrayList<Graphic>();

    public UIGraphics()
    {
        super();
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        for (Graphic graphic : this.graphics)
        {
            graphic.render(context, this.area);
        }
    }
}