package mchorse.bbs.ui.game.triggers;

import mchorse.bbs.BBS;
import mchorse.bbs.game.triggers.blocks.TriggerBlock;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;
import java.util.function.Consumer;

public class UITriggerBlockList extends UIList<TriggerBlock>
{
    public UITriggerBlockList(Consumer<List<TriggerBlock>> callback)
    {
        super(callback);
    }

    @Override
    protected void renderElementPart(UIContext context, TriggerBlock element, int i, int x, int y, boolean hover, boolean selected)
    {
        int color = BBS.getFactoryTriggers().getData(element).color;

        context.batcher.box(x, y, x + 4, y + this.scroll.scrollItemSize, Colors.A100 + color);
        context.batcher.gradientHBox(x + 4, y, x + 24, y + this.scroll.scrollItemSize, Colors.A25 + color, color);

        super.renderElementPart(context, element, i, x + 4, y, hover, selected);
    }

    @Override
    protected String elementToString(int i, TriggerBlock element)
    {
        return element.stringify();
    }
}