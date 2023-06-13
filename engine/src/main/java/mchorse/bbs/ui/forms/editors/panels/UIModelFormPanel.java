package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.world.objects.objects.UIPropTransforms;
import mchorse.bbs.utils.Pose;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.colors.Color;

public class UIModelFormPanel extends UIFormPanel<ModelForm>
{
    public UIPropTransforms pose;
    public UIButton createPose;
    public UIStringList groups;
    public UIToggle staticPose;
    public UIColor color;

    public UIButton pick;

    public UIModelFormPanel(UIForm editor)
    {
        super(editor);

        this.pose = new UIPropTransforms();
        this.createPose = new UIButton(IKey.EMPTY, this::togglePose);
        this.groups = new UIStringList((l) -> this.pickGroup(l.get(0)));
        this.groups.background();
        this.staticPose = new UIToggle(UIKeys.FORMS_EDITOR_MODEL_STATIC_POSE, (b) -> this.form.pose.get().staticPose = b.getValue());

        int w = this.options.getFlex().getW() - 20;

        this.createPose.relative(this).x(1F, -10).y(10).w(w).anchorX(1F);
        this.groups.relative(this).x(1F, -10).y(35).w(w).hTo(this.staticPose.area, -5).anchorX(1F);
        this.staticPose.relative(this).x(1F, -10).w(w).y(1F, -10).w(w).anchor(1F);

        this.add(this.createPose, this.staticPose, this.groups);

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

        this.color = new UIColor((c) -> this.form.color.set(new Color().set(c))).withAlpha();

        this.options.add(this.pick, this.color, this.pose.verticalCompact().marginTop(8));
    }

    private void togglePose(UIButton b)
    {
        Pose pose = this.form.pose.get();

        if (pose.isEmpty())
        {
            for (String group : this.groups.getList())
            {
                pose.transforms.put(group, new Transform());
            }
        }
        else
        {
            pose.transforms.clear();
        }

        this.updateElements();
    }

    private void pickGroup(String group)
    {
        this.pose.setTransform(this.form.pose.get().transforms.get(group));
    }

    @Override
    public void startEdit(ModelForm form)
    {
        super.startEdit(form);

        this.staticPose.setValue(form.pose.get().staticPose);

        this.groups.clear();
        this.groups.add(this.form.getRenderer().getBones());
        this.groups.sort();

        this.color.setColor(form.color.get().getARGBColor());

        this.updateElements();
    }

    @Override
    public void pickBone(String bone)
    {
        super.pickBone(bone);

        Pose pose = this.form.pose.get();

        if (!pose.isEmpty() && pose.transforms.containsKey(bone))
        {
            this.pickGroup(bone);
            this.groups.setCurrentScroll(bone);
        }
    }

    private void updateElements()
    {
        boolean poseIsPresent = !this.form.pose.get().isEmpty();

        this.pose.setVisible(poseIsPresent);
        this.groups.setVisible(poseIsPresent);
        this.staticPose.setVisible(poseIsPresent);

        this.createPose.label = poseIsPresent
            ? UIKeys.FORMS_EDITOR_MODEL_RESET_POSE
            : UIKeys.FORMS_EDITOR_MODEL_CREATE_POSE;

        if (poseIsPresent)
        {
            String group = this.groups.getList().get(0);

            this.groups.setCurrentScroll(group);
            this.pickGroup(group);
        }
    }
}