package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.conditions.blocks.ConditionBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIConditionBlockPanel <T extends ConditionBlock> extends UIElement
{
    public UIElement icons;
    public UIIcon not;
    public UIIcon or;

    protected UIConditionOverlayPanel overlay;
    protected T block;

    public UIConditionBlockPanel(UIConditionOverlayPanel overlay, T block)
    {
        super();

        this.overlay = overlay;
        this.block = block;

        UILabel label = UI.label(UIKeys.C_CONDITION.get(BBS.getFactoryConditions().getType(block)));

        this.not = new UIIcon(Icons.EXCLAMATION, (b) -> this.block.not = !this.block.not);
        this.not.tooltip(UIKeys.CONDITIONS_NOT).wh(16, 16);
        this.or = new UIIcon(Icons.REVERSE, (b) -> this.block.or = !this.block.or);
        this.or.tooltip(UIKeys.CONDITIONS_OR).wh(16, 16);
        this.icons = new UIElement();
        this.icons.relative(label).x(1F).y(-4).anchorX(1F).row(0).resize().reverse();
        this.icons.add(this.or, this.not);
        label.add(this.icons);

        this.column().vertical().stretch();
        this.add(label);
    }

    @Override
    public void render(UIContext context)
    {
        int primary = BBSSettings.primaryColor.get();

        if (this.block.not)
        {
            this.not.area.render(context.batcher, Colors.A50 | primary);
        }

        if (this.block.or)
        {
            this.or.area.render(context.batcher, Colors.A50 | primary);
        }

        super.render(context);
    }
}