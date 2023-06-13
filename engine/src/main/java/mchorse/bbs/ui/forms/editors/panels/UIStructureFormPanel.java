package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.StructureForm;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.UI;

public class UIStructureFormPanel extends UIFormPanel<StructureForm>
{
    public UIStringList structures;

    public UIStructureFormPanel(UIForm<StructureForm> editor)
    {
        super(editor);

        this.structures = new UIStringList((l) -> this.form.structure.set(l.get(0)));
        this.structures.background().h(120);

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_STRUCTURE_TITLE), this.structures);
    }

    @Override
    public void startEdit(StructureForm form)
    {
        super.startEdit(form);

        this.structures.clear();
        this.structures.add(BBS.getStructures().getIds(false));
        this.structures.sort();
        this.structures.setCurrentScroll(form.structure.get());
    }
}