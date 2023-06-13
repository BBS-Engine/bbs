package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.objects.WorldObject;

public abstract class UIWorldObject <T extends WorldObject> extends UIElement
{
    public UITextbox id;
    public UIVector3d position;

    protected T object;

    public UIWorldObject()
    {
        super();

        this.relative(this).column().vertical().stretch();

        this.id = new UITextbox(100, (t) -> this.object.id = t);
        this.id.tooltip(UIKeys.WORLD_OBJECTS_ID_TOOLTIP);
        this.position = new UIVector3d((v) -> this.object.position.set(v));

        this.add(UI.label(UIKeys.WORLD_OBJECTS_ID).background(), this.id);
        this.add(UI.label(UIKeys.WORLD_OBJECTS_POSITION).background().marginTop(8), this.position);
    }

    public void fillData(T object)
    {
        this.object = object;

        this.id.setText(object.id);
        this.position.fill(object.position);
    }
}