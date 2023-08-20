package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.events.UIRemovedEvent;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.objects.PropObject;

public class UIPropWorldObject extends UIWorldObject<PropObject>
{
    public UINestedEdit form;
    public UIToggle collidable;
    public UIVector3d hitbox;

    public UIPropTransform transforms;

    public UIPropWorldObject()
    {
        super();

        this.form = new UINestedEdit((editing) ->
        {
            UIContext context = this.getContext();
            UIFormPalette palette = UIFormPalette.open(context.menu.overlay, editing, this.object.form, this::setForm);

            if (palette != null)
            {
                context.menu.main.setVisible(false);

                palette.getEvents().register(UIRemovedEvent.class, (e) ->
                {
                    context.menu.main.setVisible(true);
                });
            }
        }).alternativeKeybinds();

        this.transforms = new UIPropTransform();
        this.transforms.verticalCompact();

        this.collidable = new UIToggle(UIKeys.WORLD_OBJECTS_OBJECTS_PROP_HITBOX, (b) -> this.object.collidable = b.getValue());
        this.hitbox = new UIVector3d((v) -> this.object.hitbox.set(v));

        ((UIElement) this.getChildren().get(0)).marginTop(12);
        this.prepend(this.form);
        this.add(this.collidable, this.hitbox, this.transforms.marginTop(8));
    }

    private void setForm(Form form)
    {
        if (this.object != null)
        {
            this.object.form = form;
        }
    }

    @Override
    public void fillData(PropObject object)
    {
        super.fillData(object);

        this.collidable.setValue(object.collidable);
        this.hitbox.fill(object.hitbox);

        this.transforms.setTransform(object.transform);
    }
}