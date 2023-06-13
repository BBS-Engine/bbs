package mchorse.bbs.ui.game.scripts.utils;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.utils.UI;

public class UIFormOverlayPanel extends UIOverlayPanel
{
    public UIButton pick;
    public UIButton insert;

    private UITextEditor editor;
    private Form form;

    public UIFormOverlayPanel(IKey title, UITextEditor editor, Form form)
    {
        super(title);

        this.editor = editor;
        this.form = form;

        this.pick = new UIButton(UIKeys.SCRIPTS_OVERLAY_PICK_FORM, this::pickForm);
        this.insert = new UIButton(UIKeys.SCRIPTS_OVERLAY_INSERT, this::insert);

        UIElement row = UI.row(this.pick, this.insert);

        row.relative(this.content).y(1F, -30).w(1F).h(20);
        this.content.add(row);
    }

    private void pickForm(UIButton b)
    {
        UIFormPalette.open(this.getParent(), false, this.form, this::setForm);
    }

    private void setForm(Form form)
    {
        this.form = FormUtils.copy(form);
    }

    private void insert(UIButton b)
    {
        this.close();

        if (this.form != null)
        {
            String data = FormUtils.toData(this.form).toString();

            this.editor.pasteText(DataToString.escapeQuoted(data));
        }
    }
}