package mchorse.bbs.forms.categories;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.forms.categories.UIFormCategory;
import mchorse.bbs.ui.forms.categories.UIRecentFormCategory;

public class RecentFormCategory extends FormCategory
{
    public RecentFormCategory()
    {
        super(UIKeys.FORMS_CATEGORIES_RECENT);
    }

    @Override
    public boolean canModify(Form form)
    {
        return true;
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        return new UIRecentFormCategory(this, list);
    }
}