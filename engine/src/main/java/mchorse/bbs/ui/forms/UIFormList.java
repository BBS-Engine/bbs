package mchorse.bbs.ui.forms;

import mchorse.bbs.BBS;
import mchorse.bbs.forms.FormArchitect;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.categories.UIFormCategory;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UIFormList extends UIElement
{
    public IUIFormList palette;

    public UIScrollView forms;

    public UIElement bar;
    public UITextbox search;
    public UIButton edit;
    public UIIcon close;

    private UIFormCategory recent;
    private List<UIFormCategory> categories = new ArrayList<UIFormCategory>();

    public UIFormList(IUIFormList palette)
    {
        this.palette = palette;

        this.forms = UI.scrollView(0, 0);
        this.bar = new UIElement();
        this.search = new UITextbox(100, this::search).placeholder(UIKeys.FORMS_LIST_SEARCH);
        this.edit = new UIButton(UIKeys.FORMS_LIST_EDIT, this::edit);
        this.close = new UIIcon(Icons.CLOSE, this::close);

        this.forms.relative(this).full();
        this.bar.relative(this).x(10).y(1F, -30).w(1F, -20).h(20).row().height(20);
        this.edit.w(80);
        this.close.w(20);

        this.bar.add(this.search, this.edit, this.close);
        this.add(this.forms, this.bar);

        this.search.keys().register(Keys.FORMS_FOCUS, this::focusSearch);

        this.markContainer();
        this.setupForms(BBS.getForms());
    }

    private void focusSearch()
    {
        this.search.clickItself();
    }

    public void setupForms(FormArchitect forms)
    {
        this.categories.clear();
        this.forms.removeAll();

        for (FormCategory category : forms.categories)
        {
            UIFormCategory uiCategory = category.createUI(this);

            category.update();
            this.forms.add(uiCategory);
            this.categories.add(uiCategory);
        }

        this.recent = (UIFormCategory) this.forms.getChildren().get(0);
        this.categories.get(this.categories.size() - 1).marginBottom(40);
        this.resize();
    }

    private void search(String search)
    {
        search = search.trim();

        for (UIFormCategory category : this.categories)
        {
            category.search(search);
        }
    }

    private void edit(UIButton b)
    {
        this.palette.toggleEditor();
    }

    private void close(UIIcon b)
    {
        this.palette.exit();
    }

    public void selectCategory(UIFormCategory category, Form form, boolean notify)
    {
        this.deselect();

        category.selected = form;

        if (notify)
        {
            this.palette.accept(form);
        }
    }

    public void deselect()
    {
        for (UIFormCategory category : this.categories)
        {
            category.selected = null;
        }
    }

    public UIFormCategory getSelectedCategory()
    {
        for (UIFormCategory category : this.categories)
        {
            if (category.selected != null)
            {
                return category;
            }
        }

        return null;
    }

    public Form getSelected()
    {
        UIFormCategory category = this.getSelectedCategory();

        return category == null ? null : category.selected;
    }

    public void setSelected(Form form)
    {
        boolean found = false;

        this.deselect();

        for (UIFormCategory category : this.categories)
        {
            int index = category.category.forms.indexOf(form);

            if (index == -1)
            {
                category.selected = null;
            }
            else
            {
                found = true;

                category.select(category.category.forms.get(index), false);
            }
        }

        if (!found && form != null)
        {
            Form copy = form.copy();

            this.recent.category.forms.add(copy);
            this.recent.select(copy, false);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        /* Render form's display name and ID */
        Form selected = this.getSelected();

        if (selected != null)
        {
            String displayName = selected.getDisplayName();
            String id = selected.getId();

            int w = Math.max(context.font.getWidth(displayName), context.font.getWidth(id));
            int x = this.search.area.x;
            int y = this.search.area.y - 24;

            context.batcher.box(x, y, x + w + 8, this.search.area.y, Colors.A50);
            context.batcher.textShadow(displayName, x + 4, y + 4);
            context.batcher.textShadow(id, x + 4, y + 14, Colors.LIGHTEST_GRAY);
        }
    }
}