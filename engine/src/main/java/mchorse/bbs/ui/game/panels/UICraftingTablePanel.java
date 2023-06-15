package mchorse.bbs.ui.game.panels;

import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.crafting.UICraftingRecipeEditor;
import mchorse.bbs.ui.game.crafting.UICraftingRecipeList;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UICraftingTablePanel extends UIDataDashboardPanel<CraftingTable>
{
    public UITextbox title;
    public UITextbox action;

    public UIScrollView view;
    public UICraftingRecipeEditor recipe;
    public UICraftingRecipeList recipes;

    public UICraftingTablePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.title = new UITextbox(1000, (text) -> this.data.title = text);
        this.action = new UITextbox(1000, (text) -> this.data.action = text);

        this.view = this.createScrollEditor();
        this.view.x(120).w(1F, -120).h(1F).column(0).padding(0);
        this.recipe = new UICraftingRecipeEditor();
        this.recipes = new UICraftingRecipeList((list) -> this.pickRecipe(list.get(0), false));
        this.recipes.sorting().context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.CRAFTING_CONTEXT_ADD, this::addRecipe);

            if (this.recipes.isSelected())
            {
                menu.action(Icons.REMOVE, UIKeys.CRAFTING_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeRecipe);
            }
        });
        this.recipes.relative(this.editor).w(120).h(1F);

        this.editor.add(this.recipes, this.view);
        this.view.add(this.recipe);
        this.overlay.namesList.setFileIcon(Icons.WRENCH);

        this.addOptions();
        this.options.fields.add(UI.label(UIKeys.CRAFTING_TITLE), this.title);
        this.options.fields.add(UI.label(UIKeys.CRAFTING_ACTION).marginTop(8), this.action);

        this.fill(null);
    }

    private void pickRecipe(CraftingRecipe recipe, boolean select)
    {
        this.view.setVisible(recipe != null);

        if (recipe != null)
        {
            this.recipe.set(recipe);
            this.editor.resize();

            if (select)
            {
                this.recipes.setCurrentScroll(recipe);
            }
        }
    }

    private void addRecipe()
    {
        CraftingRecipe recipe = new CraftingRecipe();

        this.data.recipes.add(recipe);
        this.pickRecipe(recipe, true);
        this.editor.resize();
        this.recipes.update();
    }

    private void removeRecipe()
    {
        int index = this.recipes.getIndex();

        this.data.recipes.remove(this.recipes.getCurrentFirst());

        if (index > 0)
        {
            index -= 1;
        }

        this.pickRecipe(this.data.recipes.isEmpty() ? null : this.data.recipes.get(index), true);
        this.recipes.update();
    }

    @Override
    public ContentType getType()
    {
        return ContentType.CRAFTING_TABLES;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_CRAFTING;
    }

    @Override
    public void fill(CraftingTable data)
    {
        super.fill(data);

        this.title.setVisible(data != null);
        this.action.setVisible(data != null);
        this.editor.setVisible(data != null);

        if (data != null)
        {
            this.title.setText(this.data.title);
            this.action.setText(this.data.action);

            this.recipes.setList(this.data.recipes);
            this.pickRecipe(this.data.recipes.isEmpty() ? null : this.data.recipes.get(0), true);

            this.resize();
        }
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editor.isVisible())
        {
            this.recipes.area.render(context.batcher, Colors.A100);

            if (this.data.recipes.isEmpty())
            {
                UIDataUtils.renderRightClickHere(context, this.recipes.area);
            }
        }

        super.render(context);
    }
}