package mchorse.bbs.ui.game.triggers;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.game.triggers.blocks.TriggerBlock;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.framework.elements.overlay.UIEditorOverlayPanel;
import mchorse.bbs.ui.game.triggers.panels.UITriggerBlockPanel;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

public class UITriggerOverlayPanel extends UIEditorOverlayPanel<TriggerBlock>
{
    private Trigger trigger;

    public UITriggerOverlayPanel(Trigger trigger)
    {
        super(UIKeys.TRIGGERS_TITLE);

        this.trigger = trigger;

        this.list.sorting().setList(trigger.blocks);
        this.list.resetContext();
        this.list.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.TRIGGERS_CONTEXT_ADD, () ->
            {
                this.getContext().replaceContextMenu((adds) ->
                {
                    for (Link key : BBS.getFactoryTriggers().getKeys())
                    {
                        IKey label = UIKeys.TRIGGERS_CONTEXT_ADD_TRIGGER.format(UIKeys.C_TRIGGER.get(key));
                        int color = BBS.getFactoryTriggers().getData(key).color;

                        adds.shadow().action(Icons.ADD, label, color, () -> this.addBlock(key));
                    }
                });
            });

            if (this.list.isSelected())
            {
                menu.action(Icons.COPY, UIKeys.TRIGGERS_CONTEXT_COPY, this::copyTrigger);
            }

            MapType data = Window.getClipboardMap("_CopyTrigger");

            if (data != null)
            {
                menu.action(Icons.PASTE, UIKeys.TRIGGERS_CONTEXT_PASTE, () -> this.pasteTrigger(data));
            }

            if (this.list.isSelected())
            {
                menu.action(Icons.REMOVE, UIKeys.TRIGGERS_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeItem);
            }
        });

        this.pickItem(this.trigger.blocks.isEmpty() ? null : this.trigger.blocks.get(0), true);
    }

    @Override
    protected UIList<TriggerBlock> createList()
    {
        return new UITriggerBlockList((l) -> this.pickItem(l.get(0), false));
    }

    private void addBlock(Link type)
    {
        TriggerBlock block = BBS.getFactoryTriggers().create(type);

        this.trigger.blocks.add(block);
        this.pickItem(block, true);
        this.list.update();
    }

    private void copyTrigger()
    {
        TriggerBlock block = this.list.getCurrentFirst();

        Window.setClipboard(BBS.getFactoryTriggers().toData(block), "_CopyTrigger");
    }

    private void pasteTrigger(MapType data)
    {
        TriggerBlock block = BBS.getFactoryTriggers().fromData(data);
        
        this.trigger.blocks.add(block);
        this.list.update();

        this.pickItem(block, true);
    }

    @Override
    protected void fillData(TriggerBlock block)
    {
        this.editor.removeAll();

        try
        {
            this.editor.add((UITriggerBlockPanel) BBS.getFactoryTriggers().getData(block).panelUI.getConstructors()[0].newInstance(this, block));
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

        if (this.trigger.blocks.isEmpty())
        {
            UIDataUtils.renderRightClickHere(context, this.list.area);
        }
    }
}