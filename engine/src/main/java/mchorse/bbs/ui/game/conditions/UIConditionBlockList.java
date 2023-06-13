package mchorse.bbs.ui.game.conditions;

import mchorse.bbs.BBS;
import mchorse.bbs.game.conditions.blocks.ConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;
import java.util.function.Consumer;

public class UIConditionBlockList extends UIList<ConditionBlock>
{
    public UIConditionBlockList(Consumer<List<ConditionBlock>> callback)
    {
        super(callback);

        this.postDraw = true;
        this.scroll.scrollItemSize = 24;
    }

    @Override
    public void renderPostListElement(UIContext context, ConditionBlock element, int i, int x, int y, boolean hover, boolean selected)
    {
        if (i > 0)
        {
            String label = (element.or ? UIKeys.CONDITIONS_LABEL_OR : UIKeys.CONDITIONS_LABEL_AND).get();

            y -= 4;
            int w = context.font.getWidth(label);

            context.draw.textCard(context.font, label, this.scroll.mx(w), y, Colors.WHITE, Colors.A50, 2);
        }
    }

    @Override
    protected void renderElementPart(UIContext context, ConditionBlock element, int i, int x, int y, boolean hover, boolean selected)
    {
        int color = BBS.getFactoryConditions().getData(element).color;

        context.draw.box(x, y, x + 4, y + this.scroll.scrollItemSize, Colors.A100 + color);
        context.draw.gradientHBox(x + 4, y, x + 24, y + this.scroll.scrollItemSize, Colors.A25 + color, color);

        if (element.not)
        {
            context.draw.textCard(context.font, "!", x + 6, y + this.scroll.scrollItemSize / 2 - context.font.getHeight() / 2,Colors.WHITE, Colors.A50, 2);
        }

        super.renderElementPart(context, element, i, x + 4, y, hover, selected);
    }

    @Override
    protected String elementToString(int i, ConditionBlock element)
    {
        return element.stringify();
    }
}