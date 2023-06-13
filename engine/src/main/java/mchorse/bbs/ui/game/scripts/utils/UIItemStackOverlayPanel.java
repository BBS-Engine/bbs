package mchorse.bbs.ui.game.scripts.utils;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.utils.UI;

public class UIItemStackOverlayPanel extends UIOverlayPanel
{
    public UISlot pick;
    public UIButton insert;

    private UITextEditor editor;
    private ItemStack stack;

    public UIItemStackOverlayPanel(IKey title, UITextEditor editor, ItemStack stack)
    {
        super(title);

        this.editor = editor;
        this.stack = stack;

        this.pick = new UISlot(0, this::pickItem);
        this.pick.wh(20, 20);
        this.pick.setStack(stack);
        this.insert = new UIButton(UIKeys.SCRIPTS_OVERLAY_INSERT, this::insert);

        UIElement row = UI.row(this.pick, this.insert);

        row.relative(this.content).y(1F, -30).w(1F).h(20).row(0).preferred(1);
        this.content.add(row);
    }

    private void pickItem(ItemStack stack)
    {
        this.stack = stack.copy();
    }

    private void insert(UIButton b)
    {
        this.close();

        if (!this.stack.isEmpty())
        {
            String data = this.stack.toData().toString();

            this.editor.pasteText(DataToString.escapeQuoted(data));
        }
    }
}