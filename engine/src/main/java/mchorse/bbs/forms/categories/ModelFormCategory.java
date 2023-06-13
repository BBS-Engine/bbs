package mchorse.bbs.forms.categories;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.forms.categories.UIFormCategory;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;

import java.io.File;

public class ModelFormCategory extends FormCategory
{
    public ModelFormCategory()
    {
        super(UIKeys.FORMS_CATEGORIES_MODELS);
    }

    @Override
    public void update()
    {
        super.update();

        this.forms.clear();

        File folder = BBS.getAssetsPath("models");

        folder.mkdirs();

        File[] files = folder.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isDirectory())
            {
                ModelForm form = new ModelForm();

                form.setModel(file.getName());
                this.forms.add(form);
            }
        }
    }

    @Override
    public UIFormCategory createUI(UIFormList list)
    {
        UIFormCategory category = super.createUI(list);

        category.context((menu) ->
        {
            menu.action(Icons.FOLDER, UIKeys.FORMS_CATEGORIES_CONTEXT_OPEN_MODEL_FOLDER, () ->
            {
                ModelForm form = (ModelForm) category.selected;

                UIUtils.openFolder(BBS.getAssetsPath("models/" + form.getModelKey() + "/"));
            });
        });

        return category;
    }
}