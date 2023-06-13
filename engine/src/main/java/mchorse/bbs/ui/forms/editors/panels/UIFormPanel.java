package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.UI;

public abstract class UIFormPanel <T extends Form> extends UIElement
{
    protected UIForm editor;
    protected T form;

    public UIScrollView options;

    public UIFormPanel(UIForm editor)
    {
        this.editor = editor;

        this.options = UI.scrollView(5, 10);
        this.options.scroll.cancelScrolling().opposite();
        this.options.relative(this).w(160).h(1F);

        this.add(this.options);
    }

    public void startEdit(T form)
    {
        this.form = form;
    }

    public void finishEdit()
    {}

    public void pickBone(String bone)
    {}
}