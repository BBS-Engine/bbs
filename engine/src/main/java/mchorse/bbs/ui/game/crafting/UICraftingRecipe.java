package mchorse.bbs.ui.game.crafting;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;

public class UICraftingRecipe extends UIElement
{
    private CraftingRecipe recipe;
    private UICraftingRecipes recipes;

    public UICraftingRecipe(UICraftingRecipes recipes, CraftingRecipe recipe)
    {
        super();

        this.recipe = recipe;
        this.recipes = recipes;

        UIIcon in = new UIIcon(Icons.IN, null);
        UIIcon out = new UIIcon(Icons.OUT, null);

        in.setEnabled(false);
        in.disabledColor = Colors.WHITE;
        out.setEnabled(false);
        out.disabledColor = Colors.WHITE;

        UIElement output = this.createItems(recipe.output);
        UIElement column = UI.column(UI.label(IKey.raw(TextUtils.processColoredText(recipe.title))));

        if (!recipe.description.trim().isEmpty())
        {
            column.add(new UIText(TextUtils.processColoredText(recipe.description)).color(Colors.LIGHTER_GRAY, true).marginTop(4));
        }

        column.add(UI.label(UIKeys.CRAFTING_INPUT).marginTop(12), this.createItems(recipe.input));
        output.w(recipe.output.size() > 1 ? 44 : 20);

        this.add(UI.row(column, output));

        this.column().vertical().stretch().padding(10);
    }

    private UIElement createItems(List<ItemStack> input)
    {
        UIElement element = new UIElement();

        for (ItemStack stack : input)
        {
            UISlot slot = new UISlot(0, null);

            slot.renderDisabled = false;
            slot.setStack(stack);
            slot.setEnabled(false);
            slot.wh(20, 20);

            element.add(slot);
        }

        element.grid(4).width(20);

        return element;
    }

    public CraftingRecipe getRecipe()
    {
        return this.recipe;
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 0)
        {
            this.recipes.recipeClicked(this);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.recipes.getCurrent() == this)
        {
            this.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
        }

        int y = this.area.ey();

        context.batcher.box(this.area.x, y - 1, this.area.ex(), y, Colors.A50);

        super.render(context);
    }
}