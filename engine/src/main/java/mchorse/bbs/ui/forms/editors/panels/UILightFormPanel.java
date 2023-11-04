package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.forms.forms.LightForm;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Color;

public class UILightFormPanel extends UIFormPanel<LightForm>
{
    public UIColor color;
    public UITrackpad distance;

    public UILightFormPanel(UIForm<LightForm> editor)
    {
        super(editor);

        this.color = new UIColor((c) -> this.form.color.set(new Color().set(c, false)));
        this.distance = new UITrackpad((v) -> this.form.distance.set(v.floatValue()));

        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LIGHT_COLOR).background(), this.color.marginBottom(6));
        this.options.add(UI.label(UIKeys.FORMS_EDITORS_LIGHT_DISTANCE).background(), this.distance);
    }

    @Override
    public void startEdit(LightForm form)
    {
        super.startEdit(form);

        this.color.setColor(form.color.get().getARGBColor());
        this.distance.setValue(form.distance.get());
    }
}