package mchorse.bbs.ui.game.conditions;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.game.conditions.blocks.ConditionBlock;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.framework.elements.overlay.UIEditorOverlayPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIConditionBlockPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIConditionOverlayPanel extends UIEditorOverlayPanel<ConditionBlock>
{
    private Condition condition;

    public UIConditionOverlayPanel(Condition condition)
    {
        super(UIKeys.CONDITIONS_TITLE);

        this.condition = condition;

        this.list.sorting().setList(condition.blocks);
        this.list.resetContext();
        this.list.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.CONDITIONS_CONTEXT_ADD, () ->
            {
                this.getContext().replaceContextMenu((add) ->
                {
                    for (Link key : BBS.getFactoryConditions().getKeys())
                    {
                        IKey label = UIKeys.CONDITIONS_CONTEXT_ADD_CONDITION.format(UIKeys.C_CONDITION.get(key));
                        int color = BBS.getFactoryConditions().getData(key).color;

                        add.shadow().action(Icons.ADD, label, color, () -> this.addBlock(key));
                    }
                });
            });

            if (this.list.isSelected())
            {
                menu.action(Icons.COPY, UIKeys.CONDITIONS_CONTEXT_COPY, this::copyCondition);
            }

            MapType data = Window.getClipboardMap("_CopyCondition");

            if (data != null)
            {
                menu.action(Icons.PASTE, UIKeys.CONDITIONS_CONTEXT_PASTE, () -> this.pasteCondition(data));
            }

            if (this.list.isSelected())
            {
                menu.action(Icons.REMOVE, UIKeys.CONDITIONS_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeItem);
            }
        });

        this.pickItem(this.condition.blocks.isEmpty() ? null : this.condition.blocks.get(0), true);
    }

    @Override
    protected UIList<ConditionBlock> createList()
    {
        return new UIConditionBlockList((l) -> this.pickItem(l.get(0), false));
    }

    private void addBlock(Link type)
    {
        ConditionBlock block = BBS.getFactoryConditions().create(type);

        this.condition.blocks.add(block);
        this.pickItem(block, true);
        this.list.update();
    }

    private void copyCondition()
    {
        ConditionBlock block = this.list.getCurrentFirst();

        Window.setClipboard(BBS.getFactoryConditions().toData(block), "_CopyCondition");
    }

    private void pasteCondition(MapType data)
    {
        ConditionBlock block = BBS.getFactoryConditions().fromData(data);

        this.condition.blocks.add(block);
        this.list.update();

        this.pickItem(block, true);
    }

    @Override
    protected void fillData(ConditionBlock block)
    {
        this.editor.removeAll();

        try
        {
            this.editor.add((UIConditionBlockPanel) BBS.getFactoryConditions().getData(block).panelUI.getConstructors()[0].newInstance(this, block));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.condition.blocks.isEmpty())
        {
            UIDataUtils.renderRightClickHere(context, this.list.area);
        }
    }
}