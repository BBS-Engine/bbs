package mchorse.bbs.ui.forms.categories;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.categories.UserFormCategory;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIUserFormCategory extends UIFormCategory
{
    public UIUserFormCategory(FormCategory category, UIFormList list)
    {
        super(category, list);

        this.context((menu) ->
        {
            menu.action(Icons.EDIT, UIKeys.FORMS_CATEGORIES_CONTEXT_RENAME_CATEGORY, () ->
            {
                UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
                    UIKeys.FORMS_CATEGORIES_RENAME_CATEGORY_TITLE,
                    UIKeys.FORMS_CATEGORIES_RENAME_CATEGORY_DESCRIPTION,
                    (str) ->
                    {
                        this.getCategory().title = IKey.raw(str);
                    }
                );

                panel.text.setText(this.getCategory().title.get());

                UIOverlay.addOverlay(this.getContext(), panel);
            });

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

    private UserFormCategory getCategory()
    {
        return (UserFormCategory) this.category;
    }
}