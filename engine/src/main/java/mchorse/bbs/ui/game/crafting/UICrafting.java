package mchorse.bbs.ui.game.crafting;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.game.crafting.CraftingTable;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.graphics.text.TextUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.world.entities.Entity;

public class UICrafting extends UIElement implements ICraftingScreen
{
    public static final IKey CRAFT_LABEL = UIKeys.CRAFTING_CRAFT;

    public UICraftingRecipes recipes;
    public UIButton craft;

    private ICraftingScreen crafting;
    private CraftingTable table;

    public UICrafting(ICraftingScreen crafting)
    {
        super();

        this.crafting = crafting;

        this.craft = new UIButton(CRAFT_LABEL, this::craft);
        this.craft.relative(this).x(1F, -10).y(1F, -10).wh(80, 20).anchor(1F, 1F);

        this.recipes = new UICraftingRecipes((element) -> this.pickRecipe(element.getRecipe()));
        this.recipes.relative(this).x(10).y(10).w(1F, -20).hTo(this.craft.area, -5);

        this.add(this.craft, this.recipes);
    }

    public CraftingTable get()
    {
        return this.table;
    }

    public void set(CraftingTable table)
    {
        this.table = table;
        this.craft.label = table.action.trim().isEmpty() ? CRAFT_LABEL : IKey.str(TextUtils.processColoredText(table.action));

        this.recipes.setTable(this.table);
        this.pickRecipe(this.table.recipes.get(0));
        this.recipes.setRecipe(this.table.recipes.get(0));

        this.keys().keybinds.clear();

        for (CraftingRecipe recipe : this.table.recipes)
        {
            if (recipe.hotkey > 0)
            {
                KeyCombo combo = new KeyCombo(UIKeys.CRAFTING_KEYS_CRAFT.format(recipe.title), recipe.hotkey);

                this.keys().register(combo, () ->
                {
                    this.pickRecipe(recipe);
                    this.recipes.setRecipe(recipe);
                    this.craft(this.craft);
                });
            }
        }
    }

    @Override
    public void refresh()
    {
        this.pickRecipe(this.recipes.getCurrent().getRecipe());
    }

    private void craft(UIButton button)
    {
        Entity player = this.getContext().menu.bridge.get(IBridgePlayer.class).getController();
        PlayerComponent character = player.get(PlayerComponent.class);
        int index = this.recipes.getChildren().indexOf(this.recipes.getCurrent());

        if (character != null)
        {
            DataContext context = null;

            if (character.getDialogueContext() != null)
            {
                context = character.getDialogueContext().data;
            }

            this.table.recipes.get(index).craft(player, context);
            this.crafting.refresh();
        }
    }

    private void pickRecipe(CraftingRecipe recipe)
    {
        Entity player = this.getContext().menu.bridge.get(IBridgePlayer.class).getController();

        this.craft.setEnabled(recipe.isPlayerHasAllItems(player));
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (BBSSettings.scriptUIDebug.get() && this.table != null)
        {
            int w = context.font.getWidth(this.table.getId());

            context.draw.textCard(context.font, this.table.getId(), this.area.mx(w), this.craft.area.my(context.font.getHeight() - 2));
        }
    }
}