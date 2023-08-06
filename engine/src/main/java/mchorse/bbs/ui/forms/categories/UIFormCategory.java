package mchorse.bbs.ui.forms.categories;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.categories.UserFormCategory;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.UIFormList;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UIFormCategory extends UIElement
{
    public static final int HEADER_HEIGHT = 20;
    public static final int CELL_WIDTH = 60;
    public static final int CELL_HEIGHT = 80;

    public UIFormList list;
    public FormCategory category;
    public Form selected;

    private int last;
    private String search = "";
    private List<Form> searched = new ArrayList<>();

    public UIFormCategory(FormCategory category, UIFormList list)
    {
        this.category = category;
        this.list = list;

        this.context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.FORMS_CATEGORIES_CONTEXT_ADD_CATEGORY, () ->
            {
                UIOverlay.addOverlay(this.getContext(), new UIPromptOverlayPanel(
                    UIKeys.FORMS_CATEGORIES_ADD_CATEGORY_TITLE,
                    UIKeys.FORMS_CATEGORIES_ADD_CATEGORY_DESCRIPTION,
                    (str) ->
                    {
                        BBS.getForms().addUserCategory(new UserFormCategory(IKey.raw(str)));
                        list.setupForms(BBS.getForms());
                    }
                ));
            });

            if (this.selected != null)
            {
                List<UserFormCategory> categories = BBS.getForms().getUserCategories();

                categories.remove(this.category);
                menu.action(Icons.COPY, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_FORM, () -> Window.setClipboard(FormUtils.toData(this.selected)));

                if (!categories.isEmpty())
                {
                    menu.action(Icons.COPY, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_TO_CATEGORY, () ->
                    {
                        this.getContext().replaceContextMenu((m) ->
                        {
                            for (UserFormCategory formCategory : categories)
                            {
                                m.action(Icons.ADD, UIKeys.FORMS_CATEGORIES_CONTEXT_COPY_TO.format(formCategory.title), () ->
                                {
                                    formCategory.forms.add(FormUtils.copy(this.selected));
                                });
                            }
                        });
                    });
                }
            }
        });

        this.h(20);
    }

    public void search(String search)
    {
        this.search = search;

        this.searched.clear();

        if (search.isEmpty())
        {
            return;
        }

        for (Form form : this.category.forms)
        {
            if (form.getId().contains(search) || form.getDisplayName().contains(search))
            {
                this.searched.add(form);
            }
        }
    }

    public List<Form> getForms()
    {
        if (this.search.isEmpty())
        {
            return this.category.forms;
        }

        return this.searched;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context))
        {
            int x = context.mouseX - this.area.x;
            int y = context.mouseY - this.area.y - HEADER_HEIGHT;
            int perRow = this.area.w / CELL_WIDTH;

            if (y < 0)
            {
                if (x < this.area.x + 30 + context.font.getWidth(this.category.title.get()))
                {
                    this.category.hidden = !this.category.hidden;

                    return true;
                }
                else
                {
                    return super.subMouseClicked(context);
                }
            }

            x /= CELL_WIDTH;
            y /= CELL_HEIGHT;

            List<Form> forms = this.getForms();
            int i = x + y * perRow;

            if (i >= 0 && i < forms.size())
            {
                this.select(forms.get(i), true);
            }
            else
            {
                this.select(null, true);
            }
        }

        return super.subMouseClicked(context);
    }

    public void select(Form form, boolean notify)
    {
        if (this.list != null)
        {
            this.list.selectCategory(this, form, notify);
        }

        this.selected = form;
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        context.batcher.textCard(context.font, this.category.title.get(), this.area.x + 26, this.area.y + 6);

        if (this.category.hidden)
        {
            context.batcher.icon(Icons.MOVE_UP, this.area.x + 16, this.area.y + 4, 0.5F, 0F);
        }
        else
        {
            context.batcher.icon(Icons.MOVE_DOWN, this.area.x + 16, this.area.y + 5, 0.5F, 0F);
        }

        List<Form> forms = this.getForms();
        int h = HEADER_HEIGHT;
        int x = 0;
        int i = 0;
        int perRow = this.area.w / CELL_WIDTH;

        if (!forms.isEmpty() && !this.category.hidden)
        {
            for (Form form : forms)
            {
                if (i == perRow)
                {
                    h += CELL_HEIGHT;
                    x = 0;
                    i = 0;
                }

                int cx = this.area.x + x;
                int cy = this.area.y + h;
                boolean isSelected = this.selected == form;

                context.batcher.clip(cx, cy, CELL_WIDTH, CELL_HEIGHT, context);

                if (isSelected)
                {
                    context.batcher.box(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get());
                }

                form.getRenderer().renderUI(context, cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT);

                if (isSelected)
                {
                    context.batcher.outline(cx, cy, cx + CELL_WIDTH, cy + CELL_HEIGHT, Colors.A50 | BBSSettings.primaryColor.get(), 2);
                }

                context.batcher.unclip(context);

                x += CELL_WIDTH;
                i += 1;
            }

            h += CELL_HEIGHT;
        }

        if (this.last != h)
        {
            this.last = h;

            UIElement container = this.getParentContainer();

            if (container != null)
            {
                this.h(h);
                container.resize();
            }
        }
    }
}