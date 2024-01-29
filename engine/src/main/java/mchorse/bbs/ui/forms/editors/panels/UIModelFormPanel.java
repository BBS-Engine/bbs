package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.utils.pose.UIPoseEditor;
import mchorse.bbs.utils.colors.Color;

public class UIModelFormPanel extends UIFormPanel<ModelForm>
{
    public UIColor color;
    public UIPoseEditor poseEditor;

    public UIButton pick;

    public UIModelFormPanel(UIForm editor)
    {
        super(editor);

        this.color = new UIColor((c) -> this.form.color.set(new Color().set(c))).withAlpha();
        this.poseEditor = new UIPoseEditor();
        this.pick = new UIButton(UIKeys.FORMS_EDITOR_MODEL_PICK_TEXTURE, (b) ->
        {
            Link link = this.form.texture.get();
            CubicModel model = this.form.getModel();

            if (model != null && link == null)
            {
                link = model.texture;
            }

            UITexturePicker.open(this, link, (l) -> this.form.texture.set(l));
        });

        this.options.add(this.pick, this.color, this.poseEditor.marginTop(8));
    }

    private void pickGroup(String group)
    {
        this.poseEditor.selectBone(group);
    }

    @Override
    public void startEdit(ModelForm form)
    {
        super.startEdit(form);

        this.poseEditor.setPose(form.pose.get(), this.form.getModel().poseGroup);
        this.poseEditor.fillGroups(this.form.getRenderer().getBones());
        this.color.setColor(form.color.get().getARGBColor());
    }

    @Override
    public void pickBone(String bone)
    {
        super.pickBone(bone);

        this.pickGroup(bone);
    }
}