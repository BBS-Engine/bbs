package mchorse.bbs.game.scripts.user.entities;

import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.quests.Quests;

/**
 * Player entity interface.
 *
 * <p>This interface represents a player entity.</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        if (c.getSubject().isPlayer())
 *        {
 *            // Do something with the player...
 *        }
 *    }
 * }</pre>
 */
public interface IScriptPlayer extends IScriptEntity
{
    /**
     * Get player's inventory.
     *
     * <pre>{@code
    // Assuming that subject is a player entity
     *    var item = bbs.items.create("bbs@hamster", 69);
     *    var inventory = c.getSubject().getInventory();
     *
     *    inventory.addStack(item);
     * }</pre>
     */
    public ItemInventory getInventory();

    /**
     * Get player's equipment. It's just like inventory, but specific slots are
     * assigned to specific equipment slots like:
     *
     * <ul>
     *     <li><code>0</code> is primary item.</li>
     * </ul>
     *
     * <pre>{@code
     *    // Assuming that subject is a player entity
     *    var item = bbs.items.create("bbs@hamster", 69);
     *    var equipment = c.getSubject().getEquipment();
     *
     *    equipment.addStack(item);
     * }</pre>
     */
    public ItemInventory getEquipment();

    /**
     * Get entity's quests (if it has some, only players have quests).
     *
     * <pre>{@code
     *    // Assuming that c.getSubject() is a player
     *    var quests = c.getSubject().getQuests();
     *
     *    if (!quests.has("important_quest"))
     *    {
     *        bbs.send("I think you should complete the main quest chain before attempting side quests...");
     *    }
     * }</pre>
     */
    public Quests getQuests();

    /**
     * Check whether this player can be controlled.
     */
    public boolean canControl();

    /**
     * Toggle player's controls.
     *
     * @param canControl True is can control, false is lock the controls.
     */
    public void setControl(boolean canControl);
}