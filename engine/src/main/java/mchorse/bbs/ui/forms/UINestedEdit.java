package mchorse.bbs.ui.forms;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;

import java.util.function.Consumer;

public class UINestedEdit extends UIElement
{
    public UIButton pick;
    public UIButton edit;

    public UINestedEdit(Consumer<Boolean> callback)
    {
        super();

        this.edit = new UIButton(UIKeys.GENERAL_EDIT, (b) -> callback.accept(true));
        this.pick = new UIButton(UIKeys.GENERAL_PICK, (b) -> callback.accept(false));

        this.edit.relative(this).h(1F);
        this.pick.relative(this).h(1F);

        this.h(20).row(0);
        this.add(this.pick, this.edit);
    }

    public UINestedEdit keybinds()
    {
        this.keys().register(Keys.FORMS_PICK, () -> this.pick.clickItself());
        this.keys().register(Keys.FORMS_EDIT, () -> this.edit.clickItself());

        return this;
    }

    public UINestedEdit alternativeKeybinds()
    {
        this.keys().register(Keys.FORMS_PICK_ALT, () -> this.pick.clickItself());
        this.keys().register(Keys.FORMS_EDIT_ALT, () -> this.edit.clickItself());

        return this;
    }

    public void setForm(Form form)
    {
        this.edit.setEnabled(form != null);
    }
}