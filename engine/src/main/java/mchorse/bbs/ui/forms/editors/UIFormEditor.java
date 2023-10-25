package mchorse.bbs.ui.forms.editors;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.BodyPart;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.IUIFormList;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.forms.editors.utils.UIPickableFormRenderer;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.objects.objects.UIPropTransform;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;
import java.util.function.Consumer;

public class UIFormEditor extends UIElement implements IUIFormList
{
    private static final int TREE_WIDTH = 140;
    private static boolean TOGGLED = true;

    public UIFormPalette palette;

    public UIElement formsArea;
    public UIForms forms;
    public UIScrollView bodyPartData;

    public UIButton pick;
    public UIToggle enabled;
    public UIToggle useTarget;
    public UIStringList bone;
    public UIPropTransform transform;

    public UIElement editArea;
    public UIPickableFormRenderer renderer;
    public UIForm editor;

    public UIIcon finish;
    public UIIcon toggleSidebar;

    public Form form;

    private Consumer<Form> callback;

    public UIFormEditor(UIFormPalette palette)
    {
        this.palette = palette;

        this.formsArea = new UIElement();
        this.formsArea.relative(this).w(TREE_WIDTH).h(1F);

        this.forms = new UIForms((l) -> this.pickForm(l.get(0)));
        this.forms.relative(this.formsArea).w(1F).h(0.5F);
        this.forms.context(this::createFormContextMenu);

        this.bodyPartData = UI.scrollView(5, 10);
        this.bodyPartData.relative(this.formsArea).w(1F).y(0.5F).h(0.5F);

        this.pick = new UIButton(UIKeys.FORMS_EDITOR_PICK_FORM, (b) ->
        {
            UIForms.FormEntry current = this.forms.getCurrentFirst();

            this.openFormList(current.part.getForm(), (f) ->
            {
                current.part.setForm(FormUtils.copy(f));

                this.refreshFormList();
                this.switchEditor(current.part.getForm());
            });
        });

        this.enabled = new UIToggle(UIKeys.FORMS_EDITOR_ENABLED, (b) ->
        {
            this.forms.getCurrentFirst().part.enabled = b.getValue();
        });

        this.useTarget = new UIToggle(UIKeys.FORMS_EDITOR_USE_TARGET, (b) ->
        {
            this.forms.getCurrentFirst().part.useTarget = b.getValue();
        });

        this.bone = new UIStringList((l) ->
        {
            this.forms.getCurrentFirst().part.bone = l.get(0);
        });
        this.bone.background().h(16 * 6);

        this.transform = new UIPropTransform();
        this.transform.verticalCompact();

        this.editArea = new UIElement();
        this.editArea.relative(this).x(TREE_WIDTH).w(1F, -TREE_WIDTH).h(1F);

        this.renderer = new UIPickableFormRenderer(this);
        this.renderer.relative(this.editArea).full();

        this.finish = new UIIcon(Icons.IN, (b) -> this.palette.exit());
        this.finish.tooltip(UIKeys.FORMS_EDITOR_FINISH, Direction.RIGHT).relative(this.editArea).xy(0, 1F).anchorY(1F);

        this.toggleSidebar = new UIIcon(Icons.LEFTLOAD, (b) ->
        {
            this.toggleSidebar();

            TOGGLED = !TOGGLED;
        });
        this.toggleSidebar.tooltip(UIKeys.FORMS_EDITOR_TOGGLE_TREE, Direction.RIGHT).relative(this.finish).y(-1F);

        this.bodyPartData.add(this.pick, this.enabled, this.useTarget, UI.label(UIKeys.FORMS_EDITOR_BONE).marginTop(8), this.bone, this.transform.marginTop(8));
        this.formsArea.add(this.forms, this.bodyPartData);
        this.editArea.add(this.finish, this.toggleSidebar);
        this.add(this.formsArea, this.editArea);
    }

    public void pickFormFromRenderer(Pair<Form, String> pair)
    {
        this.forms.setCurrentForm(pair.a);
        this.pickForm(this.forms.getCurrentFirst());

        if (!pair.b.isEmpty())
        {
            this.editor.pickBone(pair.b);
        }
    }

    private void toggleSidebar()
    {
        this.formsArea.toggleVisible();
        this.toggleSidebar.both(this.formsArea.isVisible() ? Icons.LEFTLOAD : Icons.RIGHTLOAD);

        if (this.formsArea.isVisible())
        {
            this.editArea.x(TREE_WIDTH).w(1F, -TREE_WIDTH);
        }
        else
        {
            this.editArea.x(0).w(1F);
        }

        this.editArea.resize();
    }

    private void createFormContextMenu(ContextMenuManager menu)
    {
        UIForms.FormEntry current = this.forms.getCurrentFirst();

        if (current != null)
        {
            menu.action(Icons.ADD, UIKeys.FORMS_EDITOR_CONTEXT_ADD, () -> this.addBodyPart(new BodyPart()));

            if (current.part != null)
            {
                menu.action(Icons.COPY, UIKeys.FORMS_EDITOR_CONTEXT_COPY, this::copyBodyPart);
            }

            MapType data = Window.getClipboardMap("_FormEditorBodyPart");

            if (data != null)
            {
                menu.action(Icons.PASTE, UIKeys.FORMS_EDITOR_CONTEXT_PASTE, () -> this.pasteBodyPart(data));
            }

            if (current.part != null)
            {
                menu.action(Icons.REMOVE, UIKeys.FORMS_EDITOR_CONTEXT_REMOVE, this::removeBodyPart);
            }
        }
    }

    private void addBodyPart(BodyPart part)
    {
        UIForms.FormEntry current = this.forms.getCurrentFirst();

        current.getForm().parts.addBodyPart(part);
        this.refreshFormList();
    }

    private void copyBodyPart()
    {
        Window.setClipboard(this.forms.getCurrentFirst().part.toData(), "_FormEditorBodyPart");
    }

    private void pasteBodyPart(MapType data)
    {
        BodyPart part = new BodyPart();

        part.fromData(data);
        this.addBodyPart(part);
    }

    private void removeBodyPart()
    {
        int index = this.forms.getIndex();
        UIForms.FormEntry current = this.forms.getCurrentFirst();

        current.form.parts.removeBodyPart(current.part);

        this.refreshFormList();
        this.forms.setIndex(index - 1);
        this.pickForm(this.forms.getCurrentFirst());
    }

    private void pickForm(UIForms.FormEntry entry)
    {
        this.bodyPartData.setVisible(entry.part != null);

        if (entry.part != null)
        {
            this.enabled.setValue(entry.part.enabled);
            this.useTarget.setValue(entry.part.useTarget);
            this.bone.clear();
            this.bone.add(entry.form.getRenderer().getBones());
            this.bone.sort();
            this.bone.setCurrentScroll(entry.part.bone);
            this.transform.setTransform(entry.part.getTransform());

            this.bodyPartData.scroll.scrollTo(0);
            this.bodyPartData.resize();
        }

        this.switchEditor(entry.getForm());
    }

    public void openFormList(Form current, Consumer<Form> callback)
    {
        UIFormEditorList list = new UIFormEditorList(this);

        list.setSelected(current);
        this.callback = callback;

        list.relative(this).full();
        list.resize();
        this.add(list);
    }

    public boolean isEditing()
    {
        return this.form != null;
    }

    public boolean edit(Form form)
    {
        this.form = null;

        if (form == null)
        {
            return false;
        }

        form = form.copy();

        this.bodyPartData.setVisible(false);

        if (this.switchEditor(form))
        {
            this.form = form;

            if (TOGGLED != this.formsArea.isVisible())
            {
                this.toggleSidebar();
            }

            this.palette.accept(form);
            this.renderer.reset();
            this.renderer.form = form;
            this.refreshFormList();
            this.forms.setIndex(0);

            return true;
        }

        return false;
    }

    public void refreshFormList()
    {
        UIForms.FormEntry current = this.forms.getCurrentFirst();

        this.forms.setForm(this.form);
        this.forms.setCurrentScroll(current);
    }

    public boolean switchEditor(Form form)
    {
        UIForm editor = BBS.getForms().getEditor(form);

        if (editor == null)
        {
            return false;
        }

        if (this.editor != null)
        {
            this.editor.removeFromParent();
        }

        this.editor = editor;

        this.editArea.prepend(this.editor);

        this.editor.setEditor(this);
        this.editor.startEdit(form);
        this.editor.relative(this.editArea).full();
        this.editor.resize();

        this.renderer.removeFromParent();
        this.renderer.resize();
        this.editArea.prepend(this.renderer);

        return true;
    }

    public Form finish()
    {
        Form form = this.form;

        this.exit();

        this.editor.finishEdit();
        this.editor.removeFromParent();
        this.editor = null;
        this.form = null;

        return form;
    }

    @Override
    public void exit()
    {
        this.callback = null;

        List<UIFormList> children = this.getChildren(UIFormList.class);

        if (!children.isEmpty())
        {
            children.get(0).removeFromParent();
        }
    }

    @Override
    public void toggleEditor()
    {}

    @Override
    public void accept(Form form)
    {
        if (this.callback != null)
        {
            this.callback.accept(form);
        }
    }

    @Override
    public void render(UIContext context)
    {
        if (this.formsArea.isVisible())
        {
            this.formsArea.area.render(context.batcher, Colors.A50);
        }

        super.render(context);
    }
}