package mchorse.bbs.ui.forms.categories;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIRecentFormCategory extends UIFormCategory
{
    public UIRecentFormCategory(FormCategory category, UIFormList list)
    {
        super(category, list);

        this.context((menu) ->
        {
            try
            {
                MapType data = Window.getClipboardMap();
                Form form = FormUtils.fromData(data);

                menu.action(Icons.PASTE, UIKeys.FORMS_CATEGORIES_CONTEXT_PASTE_FORM, () -> this.category.forms.add(form));
            }
            catch (Exception e)
            {}

            if (this.selected != null)
            {
                menu.action(Icons.REMOVE, UIKeys.FORMS_CATEGORIES_CONTEXT_REMOVE_FORM, () ->
                {
                    this.category.forms.remove(this.selected);
                    this.select(null, false);
                });
            }
        });
    }
}