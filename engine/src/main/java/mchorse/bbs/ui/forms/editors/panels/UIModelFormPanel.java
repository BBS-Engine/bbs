package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.colors.Color;

public class UIModelFormPanel extends UIFormPanel<ModelForm>
{
    public UIPropTransform pose;
    public UIStringList groups;
    public UIToggle staticPose;
    public UIColor color;

    public UIButton pick;

    public UIModelFormPanel(UIForm editor)
    {
        super(editor);

        this.pose = new UIPropTransform();
        this.groups = new UIStringList((l) -> this.pickGroup(l.get(0)));
        this.groups.background().context((menu) ->
        {
            menu.action(Icons.COPY, UIKeys.POSE_CONTEXT_COPY, () ->
            {
                Window.setClipboard(this.form.toData(), "_ModelCopyPose");
            });

            MapType data = Window.getClipboardMap("_ModelCopyPose");

            if (data != null)
            {
                menu.action(Icons.PASTE, UIKeys.POSE_CONTEXT_PASTE, () ->
                {
                    this.form.pose.fromData(data);

                    this.startEdit(this.form);
                });
            }

            menu.action(Icons.REFRESH, UIKeys.POSE_CONTEXT_RESET, () ->
            {
                String current = this.groups.getCurrentFirst();

                this.form.pose.get().transforms.clear();
                this.pickBone(current);
            });
        });
        this.staticPose = new UIToggle(UIKeys.FORMS_EDITOR_MODEL_STATIC_POSE, (b) -> this.form.pose.get().staticPose = b.getValue());

        int w = this.options.getFlex().getW() - 20;

        this.groups.relative(this).x(1F, -10).y(10).w(w).hTo(this.staticPose.area, -5).anchorX(1F);
        this.staticPose.relative(this).x(1F, -10).w(w).y(1F, -10).w(w).anchor(1F);

        this.add(this.staticPose, this.groups);

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

    private void pickGroup(String group)
    {
        this.pose.setTransform(this.form.pose.get().get(group));
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

        String group = this.groups.getList().get(0);

        this.groups.setCurrentScroll(group);
        this.pickGroup(group);
    }

    @Override
    public void pickBone(String bone)
    {
        super.pickBone(bone);

        this.pickGroup(bone);
        this.groups.setCurrentScroll(bone);
    }
}