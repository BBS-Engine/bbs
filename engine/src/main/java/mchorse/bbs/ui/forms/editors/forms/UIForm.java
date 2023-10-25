package mchorse.bbs.ui.forms.editors.forms;

import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.UIFormEditor;
import mchorse.bbs.ui.forms.editors.panels.UIFormPanel;
import mchorse.bbs.ui.forms.editors.panels.UIGeneralFormPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIPanelBase;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public abstract class UIForm <T extends Form> extends UIPanelBase<UIFormPanel<T>>
{
    public UIFormEditor editor;

    public T form;
    public UIFormPanel<T> defaultPanel;

    public UIForm()
    {
        super(Direction.LEFT);
    }

    public Matrix4f getOrigin(float transition)
    {
        return this.getOrigin(transition, FormUtils.getPath(this.form));
    }

    protected Matrix4f getOrigin(float transition, String path)
    {
        Form root = FormUtils.getRoot(this.form);
        MatrixStack stack = new MatrixStack();
        Map<String, Matrix4f> map = new HashMap<>();

        root.getRenderer().collectMatrices(this.editor.renderer.getEntity(), stack, map, "", transition);

        Matrix4f matrix = map.get(path);

        return matrix == null ? Matrices.EMPTY_4F : matrix;
    }

    protected void registerDefaultPanels()
    {
        this.registerPanel(new UIGeneralFormPanel(this), UIKeys.FORMS_EDITORS_GENERAL, Icons.GEAR);
    }

    public void setEditor(UIFormEditor editor)
    {
        this.editor = editor;
    }

    public void startEdit(T form)
    {
        this.form = form;

        for (UIFormPanel<T> panel : this.panels)
        {
            panel.startEdit(form);
        }

        this.setPanel(this.defaultPanel);
    }

    public void finishEdit()
    {
        for (UIFormPanel<T> panel : this.panels)
        {
            panel.finishEdit();
        }
    }

    public void pickBone(String bone)
    {
        if (this.view != null)
        {
            this.view.pickBone(bone);
        }
    }

    @Override
    protected void renderBackground(UIContext context, int x, int y, int w, int h)
    {
        context.batcher.box(x, y, x + w, y + h, Colors.A100);
    }
}