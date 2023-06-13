package mchorse.bbs.forms.categories;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.forms.categories.UIFormCategory;
import mchorse.bbs.ui.forms.categories.UIUserFormCategory;

public class UserFormCategory extends FormCategory
{
    public UserFormCategory(IKey title)
    {
        super(title);
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIUserFormCategory(this, list);
    }
}