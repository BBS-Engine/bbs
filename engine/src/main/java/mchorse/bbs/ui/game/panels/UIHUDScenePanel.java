package mchorse.bbs.ui.game.panels;

import mchorse.bbs.bridge.IBridgeHUD;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.huds.HUDForm;
import mchorse.bbs.game.huds.HUDScene;
import mchorse.bbs.game.huds.HUDStage;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.IUIElement;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.huds.UIHUDFormsOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.objects.objects.UIPropTransforms;

public class UIHUDScenePanel extends UIDataDashboardPanel<HUDScene>
{
    public UIIcon forms;
    public UINestedEdit form;
    public UIToggle ortho;
    public UITrackpad orthoX;
    public UITrackpad orthoY;
    public UITrackpad expire;
    public UIPropTransforms transformations;

    public UITrackpad fov;

    private HUDStage stage = new HUDStage();
    private HUDForm current;

    public UIHUDScenePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.forms = new UIIcon(Icons.POSE, (b) -> this.openForms());
        this.form = new UINestedEdit(this::openFormMenu);
        this.ortho = new UIToggle(UIKeys.HUDS_ORTHO, (b) -> this.current.ortho = b.getValue());
        this.orthoX = new UITrackpad((v) -> this.current.orthoX = v.floatValue());
        this.orthoX.limit(0, 1).metric().strong = 0.25D;
        this.orthoY = new UITrackpad((v) -> this.current.orthoY = v.floatValue());
        this.orthoY.limit(0, 1).metric().strong = 0.25D;
        this.expire = new UITrackpad((v) -> this.current.expire = v.intValue());
        this.expire.limit(0).integer();
        this.transformations = new UIPropTransforms();

        this.fov = new UITrackpad((v) -> this.data.fov = v.floatValue());
        this.fov.limit(0, 180);

        this.addOptions();
        this.options.fields.add(this.form, this.ortho, this.orthoX, this.orthoY, UI.label(UIKeys.HUDS_EXPIRE).marginTop(12), this.expire);
        this.options.fields.add(UI.label(UIKeys.HUDS_FOV).marginTop(12), this.fov, this.transformations.marginTop(20));

        this.iconBar.add(this.forms);

        this.overlay.namesList.setFileIcon(Icons.FULLSCREEN);

        this.fill(null);
    }

    private void openForms()
    {
        UIHUDFormsOverlayPanel overlay = new UIHUDFormsOverlayPanel(this.data, this::pickForm);

        UIOverlay.addOverlay(this.getContext(), overlay.set(this.current), 0.4F, 0.6F);
    }

    private void openFormMenu(boolean editing)
    {
        UIFormPalette.open(this, editing, this.current.form, this::setForm);
    }

    private void setForm(Form form)
    {
        form = FormUtils.copy(form);

        this.current.form = form;
        this.form.setForm(form);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.HUDS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_HUDS;
    }

    @Override
    public void fill(HUDScene data)
    {
        super.fill(data);

        this.forms.setEnabled(data != null);
        this.editor.setVisible(data != null);

        if (data != null)
        {
            this.stage.reset();
            this.stage.scenes.put(data.getId(), data);

            this.fov.setValue(data.fov);

            this.pickForm(this.data.forms.isEmpty() ? null : this.data.forms.get(0));
        }
    }

    private void pickForm(HUDForm current)
    {
        this.current = current;

        this.transformations.setVisible(current != null);

        for (IUIElement element : this.options.getChildren())
        {
            if (element instanceof UIElement)
            {
                ((UIElement) element).setEnabled(current != null);
            }
        }

        if (current != null)
        {
            this.form.setForm(current.form);
            this.ortho.setValue(current.ortho);
            this.orthoX.setValue(current.orthoX);
            this.orthoY.setValue(current.orthoY);
            this.expire.setValue(current.expire);
            this.transformations.setTransform(current.transform);
        }
    }

    @Override
    public void appear()
    {
        super.appear();

        this.replaceStage(this.stage);
    }

    @Override
    public void disappear()
    {
        super.disappear();

        this.replaceStage(null);
    }

    @Override
    public void close()
    {
        super.close();

        this.replaceStage(null);
    }

    private void replaceStage(HUDStage stage)
    {
        this.dashboard.bridge.get(IBridgeHUD.class).replaceHUDStage(stage);
    }
}