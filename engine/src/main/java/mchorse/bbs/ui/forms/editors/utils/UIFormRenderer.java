package mchorse.bbs.ui.forms.editors.utils;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIModelRenderer;

public class UIFormRenderer extends UIModelRenderer
{
    public Form form;

    @Override
    protected void renderUserModel(UIContext context)
    {
        if (this.form == null)
        {
            return;
        }

        this.form.getRenderer().render(this.entity, context.render);
    }
}