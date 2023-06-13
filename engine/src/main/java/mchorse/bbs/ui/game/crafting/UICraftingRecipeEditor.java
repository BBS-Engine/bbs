package mchorse.bbs.ui.game.crafting;

import mchorse.bbs.game.crafting.CraftingRecipe;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UIKeybind;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.conditions.UICondition;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.game.utils.UIItems;
import mchorse.bbs.ui.utils.UI;

public class UICraftingRecipeEditor extends UIElement
{
    public UITextbox title;
    public UITextbox description;
    public UIItems input;
    public UIItems output;
    public UICondition checker;
    public UITrigger trigger;
    public UIKeybind hotkey;

    public CraftingRecipe recipe;

    public UICraftingRecipeEditor()
    {
        super();

        this.title = new UITextbox(1000, (text) -> this.recipe.title = text);
        this.description = new UITextbox(1000, (text) -> this.recipe.description = text);
        this.input = new UIItems(UIKeys.CRAFTING_INPUT, null);
        this.input.marginTop(12);
        this.output = new UIItems(UIKeys.CRAFTING_OUTPUT, null);
        this.checker = new UICondition();
        this.trigger = new UITrigger();
        this.hotkey = new UIKeybind((k) ->
        {
            this.recipe.hotkey = k.keys.isEmpty() ? -1 : k.keys.get(0);
        });

        this.column().vertical().stretch().padding(10);

        this.add(UI.label(UIKeys.CRAFTING_RECIPE_TITLE), this.title);
        this.add(UI.label(UIKeys.CRAFTING_RECIPE_DESCRIPTION).marginTop(12), this.description, this.input, this.output);
        this.add(UI.row(
            UI.column(UI.label(UIKeys.CRAFTING_RECIPE_VISIBLE), this.checker),
            UI.column(UI.label(UIKeys.CRAFTING_RECIPE_HOTKEY), this.hotkey)
        ).marginTop(12));
        this.add(UI.label(UIKeys.CRAFTING_RECIPE_TRIGGER).background().marginTop(12).marginBottom(5), this.trigger);
    }

    public void set(CraftingRecipe recipe)
    {
        this.recipe = recipe;

        this.title.setText(recipe.title);
        this.description.setText(recipe.description);
        this.input.set(recipe.input);
        this.output.set(recipe.output);
        this.checker.set(recipe.visible);
        this.trigger.set(recipe.trigger);
        this.hotkey.setKeyCodes(recipe.hotkey);
    }
}