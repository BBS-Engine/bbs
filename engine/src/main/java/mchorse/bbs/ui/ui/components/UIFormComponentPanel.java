package mchorse.bbs.ui.ui.components;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.scripts.ui.components.UIFormComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;

public class UIFormComponentPanel extends UIComponentPanel<UIFormComponent>
{
    public UINestedEdit pickForm;
    public UIToggle editing;

    public UIVector3d position;
    public UITrackpad pitch;
    public UITrackpad yaw;

    public UITrackpad distance;
    public UITrackpad fov;

    public UIFormComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.pickForm = new UINestedEdit((editing) ->
        {
            Form form = BBS.getForms().fromData(this.component.form);

            UIFormPalette.open(this.panel, editing, form, (f) ->
            {
                this.component.form = FormUtils.toData(f);
                this.panel.needsUpdate();
            });
        });

        this.editing = new UIToggle(UIKeys.UI_COMPONENTS_FORM_EDITING, (b) ->
        {
            this.component.editing = b.getValue();
            this.panel.needsUpdate();
        });

        this.position = new UIVector3d((v) ->
        {
            this.component.pos.set(v);
            this.panel.needsUpdate();
        });

        this.pitch = new UITrackpad((v) ->
        {
            this.component.rot.x = v.floatValue();
            this.panel.needsUpdate();
        });

        this.yaw = new UITrackpad((v) ->
        {
            this.component.rot.y = v.floatValue();
            this.panel.needsUpdate();
        });

        this.distance = new UITrackpad((v) ->
        {
            this.component.distance = v.floatValue();
            this.panel.needsUpdate();
        });

        this.fov = new UITrackpad((v) ->
        {
            this.component.fov = v.floatValue();
            this.panel.needsUpdate();
        });

        this.prepend(this.fov.marginBottom(8));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_FORM_FOV).marginTop(4));
        this.prepend(this.distance);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_FORM_DISTANCE).marginTop(4));
        this.prepend(UI.row(this.pitch, this.yaw));
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_FORM_ROTATION).marginTop(4));
        this.prepend(this.position);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_FORM_POSITION).marginTop(4));
        this.prepend(this.editing);
        this.prepend(this.pickForm);
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_FORM_TITLE));
    }

    @Override
    public void fill(UIFormComponent component)
    {
        super.fill(component);

        Form form = BBS.getForms().fromData(component.form);

        this.pickForm.setForm(form);
        this.editing.setValue(component.editing);

        this.position.fill(component.pos);
        this.pitch.setValue(component.rot.x);
        this.yaw.setValue(component.rot.y);

        this.distance.setValue(component.distance);
        this.fov.setValue(component.fov);
    }
}