package mchorse.bbs.ui.game.crafting;

import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.IUIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public class UICraftingRecipes extends UIScrollView
{
    private UICraftingRecipe current;

    private Consumer<UICraftingRecipe> callback;

    public UICraftingRecipes(Consumer<UICraftingRecipe> callback)
    {
        super();

        this.callback = callback;

        this.column(0).vertical().stretch().scroll();
    }

    public UICraftingRecipe getCurrent()
    {
        return this.current;
    }

    public void setTable(CraftingTable table)
    {
        this.removeAll();

        for (CraftingRecipe recipe : table.recipes)
        {
            UICraftingRecipe recipeElement = new UICraftingRecipe(this, recipe);

            this.add(recipeElement);
        }

        if (this.getParentContainer() != null)
        {
            this.getParentContainer().resize();
        }
    }

    public void setRecipe(CraftingRecipe recipe)
    {
        for (IUIElement element : this.getChildren())
        {
            UICraftingRecipe recipeElement = (UICraftingRecipe) element;

            if (recipeElement.getRecipe() == recipe)
            {
                this.current = recipeElement;
                this.scroll.scrollTo(this.current.area.y - this.area.y);

                break;
            }
        }
    }

    public void recipeClicked(UICraftingRecipe recipe)
    {
        this.current = recipe;

        if (this.callback != null)
        {
            this.callback.accept(recipe);
        }
    }

    @Override
    public void render(UIContext context)
    {
        this.area.render(context.batcher, Colors.A50);

        super.render(context);
    }
}