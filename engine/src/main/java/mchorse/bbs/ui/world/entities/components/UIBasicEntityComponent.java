package mchorse.bbs.ui.world.entities.components;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector3d;

public class UIBasicEntityComponent extends UIEntityComponent<BasicComponent>
{
    public UITextbox name;
    public UIVector3d position;
    public UIVector3d rotation;
    public UIVector3d velocity;
    public UITrackpad speed;

    public UIBasicEntityComponent(BasicComponent component)
    {
        super(component);

        this.name = new UITextbox(1000, (t) -> this.component.name = t);
        this.position = new UIVector3d((v) ->
        {
            this.component.setPosition(v.x, v.y, v.z);
            this.component.prevPosition.set(this.component.position);
        });
        this.rotation = new UIVector3d((v) ->
        {
            this.component.rotation.set((float) Math.toRadians(v.x), (float) Math.toRadians(v.y), (float) Math.toRadians(v.z));
            this.component.prevRotation.set(this.component.rotation);
        });
        this.velocity = new UIVector3d((v) -> this.component.velocity.set(v));
        this.speed = new UITrackpad((v) -> this.component.speed = v.floatValue());

        this.name.setText(component.name);
        this.name.textbox.setPlaceholder(UIKeys.ENTITIES_COMPONENTS_BASIC_NAME);
        this.position.fill(component.position);
        this.rotation.fill(new Vector3d(Math.toDegrees(component.rotation.x), Math.toDegrees(component.rotation.y), Math.toDegrees(component.rotation.z)));
        this.velocity.fill(component.velocity);
        this.speed.setValue(component.speed);

        this.add(this.name.marginBottom(8));
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_BASIC_POSITION), this.position);
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_BASIC_ROTATION), this.rotation);
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_BASIC_VELOCITY), this.velocity);
        this.add(UI.label(UIKeys.ENTITIES_COMPONENTS_BASIC_SPEED), this.speed);
    }
}