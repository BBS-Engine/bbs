package mchorse.bbs.ui.forms;

import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.forms.categories.UIFormCategory;
import mchorse.bbs.ui.forms.editors.UIFormEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIFormPalette extends UIElement implements IUIFormList
{
    public UIFormList list;
    public UIFormEditor editor;

    public Consumer<Form> callback;

    private UIFormCategory lastSelected;

    public static UIFormPalette open(UIElement parent, boolean editing, Form form, Consumer<Form> callback)
    {
        UIContext context = parent.getContext();

        if (!parent.getRoot().getChildren(UIFormPalette.class).isEmpty() || context == null)
        {
            return null;
        }

        context.unfocus();

        UIFormPalette palette = new UIFormPalette(callback);

        palette.resetFlex().relative(parent).full();
        palette.resize();

        parent.add(palette);

        palette.setSelected(form);
        palette.edit(editing);

        return palette;
    }

    public UIFormPalette(Consumer<Form> callback)
    {
        this.callback = callback;

        this.list = new UIFormList(this);
        this.list.relative(this).full();

        this.editor = new UIFormEditor(this);
        this.editor.relative(this).full();
        this.editor.setVisible(false);

        this.add(this.list, this.editor);

        this.blockInsideEvents().markContainer();
    }

    public UIFormPalette updatable()
    {
        this.editor.renderer.updatable();

        return this;
    }

    public void edit(boolean editing)
    {
        if (editing != this.editor.isEditing())
        {
            this.toggleEditor();
        }
    }

    @Override
    public void exit()
    {
        if (!this.editor.isEditing())
        {
            this.removeFromParent();
        }
        else
        {
            this.toggleEditor();
        }
    }

    @Override
    public void toggleEditor()
    {
        if (!this.editor.isEditing())
        {
            Form form = this.list.getSelected();

            if (this.editor.edit(form))
            {
                this.lastSelected = this.list.getSelectedCategory();
            }
        }
        else
        {
            Form form = this.editor.finish();

            if (this.lastSelected.category.canModify(form))
            {
                int index = this.lastSelected.category.forms.indexOf(this.lastSelected.selected);

                if (index >= 0)
                {
                    this.lastSelected.category.forms.set(index, form);
                }
            }

            this.list.setSelected(form);
            this.accept(form);

            this.lastSelected = null;
        }

        this.list.setVisible(!this.editor.isEditing());
        this.editor.setVisible(this.editor.isEditing());
    }

    @Override
    public void accept(Form form)
    {
        if (this.callback != null)
        {
            this.callback.accept(form);
        }
    }

    public void setSelected(Form form)
    {
        this.list.setSelected(form);
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.exit();
        }

        return true;
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.draw, Colors.A75);

        super.render(context);
    }
}