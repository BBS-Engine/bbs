package mchorse.bbs.game.entities.components;

import mchorse.bbs.BBSData;
import mchorse.bbs.core.input.JoystickInput;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.Dialogue;
import mchorse.bbs.game.dialogues.DialogueContext;
import mchorse.bbs.game.items.ItemInventory;
import mchorse.bbs.game.items.ItemStack;
import mchorse.bbs.game.quests.Quests;
import mchorse.bbs.game.states.States;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.utils.AABB;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.Component;
import mchorse.bbs.world.entities.components.ItemComponent;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFWGamepadState;

import java.nio.FloatBuffer;

public class PlayerComponent extends Component
{
    public final States states = new States();
    public final Quests quests = new Quests();
    public final ItemInventory inventory = new ItemInventory(40);
    public final ItemInventory equipment = new ItemInventory();

    /* Joystick options */
    public final float[] prevSticks = new float[6];
    public final float[] sticks = new float[6];
    public int gamepad;

    /* Temporary context data */
    protected Dialogue dialogue;
    protected DialogueContext dialogueContext;

    public boolean canControl = true;

    public void dropItem(ItemStack stack)
    {
        Vector3d position = this.entity.basic.position;

        this.entity.world.dropItem(stack, position.x, position.y, position.z);
    }

    public void setDialogue(Dialogue dialogue, DialogueContext dialogueContext)
    {
        if (dialogue == null && this.dialogue != null)
        {
            this.dialogue.onClose.trigger(this.dialogueContext.data);
        }

        this.dialogue = dialogue;
        this.dialogueContext = dialogueContext;
    }

    public Dialogue getDialogue()
    {
        return this.dialogue;
    }

    public DialogueContext getDialogueContext()
    {
        return this.dialogueContext;
    }

    @Override
    public void preUpdate()
    {
        for (int i = 0; i < this.prevSticks.length; i++)
        {
            this.prevSticks[i] = this.sticks[i];
        }

        super.preUpdate();

        if (this.entity.basic.ticks % 5 == 0)
        {
            this.checkForItems();
        }
    }

    private void checkForItems()
    {
        Vector3d pos = this.entity.basic.position;
        AABB volume = new AABB(pos.x, pos.y + this.entity.basic.hitbox.h / 2, pos.z, 0, 0, 0).inflate(0.5F, 0.5F, 0.5F);

        for (Entity entity : this.entity.world.getEntitiesInAABB(volume))
        {
            ItemComponent item = entity.get(ItemComponent.class);

            if (item != null && item.isAlive() && this.inventory.addStack(item.stack))
            {
                item.kill(this.entity);

                this.triggerItemPickup(item.stack);
            }
        }
    }

    private void triggerItemPickup(ItemStack stack)
    {
        if (!BBSData.getSettings().playerItemPickup.isEmpty())
        {
            BBSData.getSettings().playerItemPickup.trigger(new DataContext(this.entity).set("item", stack.copy()));
        }
    }

    public void updateJoystick(GLFWGamepadState state)
    {
        FloatBuffer buffer = state.axes();

        this.sticks[0] = buffer.get();
        this.sticks[1] = buffer.get();
        this.sticks[2] = buffer.get();
        this.sticks[3] = buffer.get();
        this.sticks[4] = buffer.get();
        this.sticks[5] = buffer.get();

        this.gamepad = JoystickInput.getJoystickStateAsInt(state);
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.put("states", this.states.toData());
        data.put("quests", this.quests.toData());
        data.put("inventory", this.inventory.toData());
        data.put("equipment", this.equipment.toData());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        if (data.has("states"))
        {
            this.states.fromData(data.getMap("states"));
        }

        if (data.has("quests"))
        {
            this.quests.fromData(data.getMap("quests"));
        }

        if (data.has("inventory"))
        {
            this.inventory.fromData(data.getMap("inventory"));
        }

        if (data.has("equipment"))
        {
            this.equipment.fromData(data.getMap("equipment"));
        }
    }
}
