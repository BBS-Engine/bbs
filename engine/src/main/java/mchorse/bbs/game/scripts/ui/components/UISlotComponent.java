package mchorse.bbs.game.scripts.ui.components;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.utils.DiscardMethod;
import mchorse.bbs.game.scripts.user.ui.IScriptUIBuilder;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.items.UISlot;

/**
 * Item stack (slot) UI component.
 *
 * <p>This component allows users to input an item stack. The value that gets written
 * to UI context's data (if ID is present) is an data map.</p>
 *
 * <p>This component can be created using {@link IScriptUIBuilder#item()} method.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var stack = ui.item().id("stack").tooltip("To dupe an item, please pick that item.");
 *
 *        stack.rxy(0.5, 0.5).wh(24, 24).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *        var data = uiContext.getData();
 *
 *        if (uiContext.getLast() === "stack")
 *        {
 *            var item = bbs.items.create(data.getMap("stack"));
 *            var pos = c.getSubject().getPosition();
 *
 *            bbs.worlds.current.dropItem(item, pos.x, pos.y + 2, pos.z);
 *
 *            // Item stack UI component also includes the slot from which
 *            // item was picked from player's inventory. -1 means it was
 *            // picked from elsewhere (from search or pasted into the
 *            // field)
 *            var slot = data.getInt("stack.slot");
 *
 *            if (slot >= 0)
 *            {
 *                // When slot isn't -1, you can access it from player's inventory
 *                var corresponding = c.getSubject().getInventory().getStack(slot);
 *
 *                // ...
 *            }
 *        }
 *    }
 * }</pre>
 */
public class UISlotComponent extends UIComponent
{
    public ItemStack stack = ItemStack.EMPTY;

    /**
     * Set item stack component's item from scripts.
     *
     * <pre>{@code
     *    function main(c)
     *    {
     *        var ui = bbs.ui.create().background();
     *        var stack = ui.item().id("stack").tooltip("An exhibit D.", 1);
     *
     *        stack.rxy(0.5, 0.5).wh(24, 24).anchor(0.5);
     *        stack.stack(bbs.items.create("bbs@hamster", 12));
     *        bbs.ui.open(ui);
     *    }
     * }</pre>
     */
    public UISlotComponent stack(ItemStack stack)
    {
        this.change("stack");

        this.stack = stack == null ? ItemStack.EMPTY : stack.copy();

        return this;
    }

    @Override
    @DiscardMethod
    protected int getDefaultUpdateDelay()
    {
        return UIComponent.DELAY;
    }

    @Override
    @DiscardMethod
    protected void applyProperty(UserInterfaceContext context, String key, UIElement element)
    {
        super.applyProperty(context, key, element);

        if (key.equals("stack"))
        {
            ((UISlot) element).setStack(this.stack);
        }
    }

    @Override
    @DiscardMethod
    protected UIElement subCreate(UserInterfaceContext context)
    {
        final UISlot element = new UISlot(0, null);

        element.callback = this.id.isEmpty() ? null : (stack) ->
        {
            context.data.put(this.id, stack.toData());
            context.data.putInt(this.id + ".slot", element.lastSlot);
            context.dirty(this.id, this.updateDelay);
        };
        element.setStack(this.stack);
        element.renderDisabled = false;

        return this.apply(element, context);
    }

    @Override
    @DiscardMethod
    public void populateData(MapType data)
    {
        super.populateData(data);

        if (!this.id.isEmpty())
        {
            data.put(this.id, this.stack.toData());
        }
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("stack", this.stack.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("stack"))
        {
            this.stack = ItemStack.create(data.getMap("stack"));
        }
    }
}