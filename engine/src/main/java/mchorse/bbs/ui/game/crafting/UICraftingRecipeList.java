package mchorse.bbs.ui.game.crafting;

import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.ui.framework.elements.input.list.UIList;

import java.util.List;
import java.util.function.Consumer;

public class UICraftingRecipeList extends UIList<CraftingRecipe>
{
    public UICraftingRecipeList(Consumer<List<CraftingRecipe>> callback)
    {
        super(callback);
    }

    @Override
    protected String elementToString(int i, CraftingRecipe element)
    {
        return element.title.trim().isEmpty() ? "-" : TextUtils.processColoredText(element.title);
    }
}