package mchorse.bbs.ui.world.entities.components;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.events.UIRemovedEvent;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.entities.components.FormComponent;

public class UIFormEntityComponent extends UIEntityComponent<FormComponent>
{
    public UINestedEdit form;
    public UINestedEdit firstPersonForm;
    public UIVector3d firstPersonOffset;

    public UIFormEntityComponent(FormComponent component)
    {
        super(component);

        this.form = new UINestedEdit((editing) ->
        {
            UIContext context = this.getContext();
            UIFormPalette palette = UIFormPalette.open(context.menu.overlay, editing, this.component.form, this::setForm);

            if (palette != null)
            {
                context.menu.main.setVisible(false);

                palette.getEvents().register(UIRemovedEvent.class, (e) ->
                {
                    context.menu.main.setVisible(true);
                });
            }
        });
        this.form.setForm(component.form);

        this.firstPersonForm = new UINestedEdit((editing) ->
        {
            UIContext context = this.getContext();
            UIFormPalette palette = UIFormPalette.open(context.menu.overlay, editing, this.component.firstPersonForm, this::setFirstPersonForm);

            if (palette != null)
            {
                context.menu.main.setVisible(false);

                palette.getEvents().register(UIRemovedEvent.class, (e) ->
                {
                    context.menu.main.setVisible(true);
                });
            }
        });
        this.firstPersonForm.setForm(component.firstPersonForm);
        this.firstPersonOffset = new UIVector3d((v) -> this.component.firstPersonOffset.set(v));
        this.firstPersonOffset.fill(this.component.firstPersonOffset);

        this.add(this.form);
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_FORM_FIRST_PERSON).marginTop(4), this.firstPersonForm);
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_FORM_FIRST_PERSON_OFFSET).marginTop(4), this.firstPersonOffset);
    }

    private void setForm(Form form)
    {
        this.component.form = form;
        this.form.setForm(form);
    }

    private void setFirstPersonForm(Form form)
    {
        this.component.firstPersonForm = form;
        this.firstPersonForm.setForm(form);
    }
}