package mchorse.bbs.game.crafting;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.game.utils.manager.data.AbstractData;

import java.util.ArrayList;
import java.util.List;

public class CraftingTable extends AbstractData
{
    public String title = "";
    public String action = "";
    public List<CraftingRecipe> recipes = new ArrayList<CraftingRecipe>();

    @Override
    public void toData(MapType data)
    {
        ListType recipes = new ListType();

        for (CraftingRecipe recipe : this.recipes)
        {
            MapType recipeMap = recipe.toData();

            if (!recipeMap.isEmpty())
            {
                recipes.add(recipeMap);
            }
        }

        if (!this.title.isEmpty())
        {
            data.putString("title", this.title);
        }

        if (!this.action.isEmpty())
        {
            data.putString("action", this.action);
        }

        if (!recipes.isEmpty())
        {
            data.put("recipes", recipes);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        if (data.has("title"))
        {
            this.title = data.getString("title");
        }

        if (data.has("action"))
        {
            this.action = data.getString("action");
        }

        if (data.has("recipes"))
        {
            ListType recipes = data.getList("recipes");

            for (int i = 0; i < recipes.size(); i++)
            {
                CraftingRecipe recipe = new CraftingRecipe();

                recipe.fromData(recipes.getMap(i));
                this.recipes.add(recipe);
            }
        }
    }

    public void filter(Entity player)
    {
        this.recipes.removeIf(recipe -> !recipe.isAvailable(player));
    }
}