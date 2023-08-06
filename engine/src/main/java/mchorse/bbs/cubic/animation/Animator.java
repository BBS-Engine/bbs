package mchorse.bbs.cubic.animation;

import mchorse.bbs.cubic.CubicModel;
import mchorse.bbs.cubic.data.animation.Animation;
import mchorse.bbs.cubic.data.animation.Animations;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Animator class
 * 
 * This class is responsible for applying currently running actions onto 
 * form (more specifically onto an armature).
 */
public class Animator
{
    /* Actions */
    public ActionPlayback idle;
    public ActionPlayback running;
    public ActionPlayback crouching;
    public ActionPlayback crouchingIdle;
    public ActionPlayback dying;
    public ActionPlayback falling;

    public ActionPlayback jump;
    public ActionPlayback swipe;
    public ActionPlayback hurt;
    public ActionPlayback land;
    public ActionPlayback shoot;
    public ActionPlayback consume;

    public ActionPlayback joystickUp;
    public ActionPlayback joystickDown;
    public ActionPlayback joystickLeft;
    public ActionPlayback joystickRight;

    /* Action pipeline properties */
    public ActionPlayback active;
    public ActionPlayback lastActive;
    public List<ActionPlayback> actions = new ArrayList<>();

    public double prevX = Float.MAX_VALUE;
    public double prevZ = Float.MAX_VALUE;
    public double prevMY;

    /* States */
    public boolean wasOnGround = true;

    private CubicModel model;

    private boolean wasUpPressed;
    private boolean wasDownPressed;
    private boolean wasLeftPressed;
    private boolean wasRightPressed;

    public List<String> getActions()
    {
        return Arrays.asList(
            "idle", "running", "crouching", "crouching_idle", "dying", "falling",
            "swipe", "jump", "hurt", "land", "shoot", "consume",
            "joystick_up", "joystick_down", "joystick_left", "joystick_right"
        );
    }

    public void setup(CubicModel model, ActionsConfig actions)
    {
        this.model = model;

        this.idle = this.createAction(this.idle, actions.getConfig("idle"), true);
        this.running = this.createAction(this.running, actions.getConfig("running"), true);
        this.crouching = this.createAction(this.crouching, actions.getConfig("crouching"), true);
        this.crouchingIdle = this.createAction(this.crouchingIdle, actions.getConfig("crouching_idle"), true);
        this.dying = this.createAction(this.dying, actions.getConfig("dying"), false);
        this.falling = this.createAction(this.falling, actions.getConfig("falling"), true);

        this.swipe = this.createAction(this.swipe, actions.getConfig("swipe"), false);
        this.jump = this.createAction(this.jump, actions.getConfig("jump"), false, 2);
        this.hurt = this.createAction(this.hurt, actions.getConfig("hurt"), false, 3);
        this.land = this.createAction(this.land, actions.getConfig("land"), false);
        this.shoot = this.createAction(this.shoot, actions.getConfig("shoot"), true);
        this.consume = this.createAction(this.consume, actions.getConfig("consume"), true);

        this.joystickUp = this.createAction(this.joystickUp, actions.getConfig("joystick_up"), false);
        this.joystickDown = this.createAction(this.joystickDown, actions.getConfig("joystick_down"), false);
        this.joystickLeft = this.createAction(this.joystickLeft, actions.getConfig("joystick_left"), true);
        this.joystickRight = this.createAction(this.joystickRight, actions.getConfig("joystick_right"), true);
    }

    /**
     * Create an action with default priority
     */
    public ActionPlayback createAction(ActionPlayback old, ActionConfig config, boolean looping)
    {
        return this.createAction(old, config, looping, 1);
    }

    /**
     * Create an action playback based on given arguments. This method
     * is used for creating actions so it was easier to tell which
     * actions are missing. Beside that, you can pass an old action so
     * in form merging situation it wouldn't interrupt animation.
     */
    public ActionPlayback createAction(ActionPlayback old, ActionConfig config, boolean looping, int priority)
    {
        CubicModel model = this.model;
        Animations animations = model == null ? null : model.animations;

        if (animations == null)
        {
            return null;
        }

        Animation action = animations.get(config.name);

        /* If given action is missing, then omit creation of ActionPlayback */
        if (action == null)
        {
            return null;
        }

        /* If old is the same, then there is no point creating a new one */
        if (old != null && old.action == action)
        {
            old.config = config;
            old.setSpeed(1);

            return old;
        }

        return new ActionPlayback(action, config, looping, priority);
    }

    /**
     * Update animator. This method is responsible for updating action 
     * pipeline and also change current actions based on entity's state.
     */
    public void update(Entity target)
    {
        BasicComponent basic = target.basic;

        /* Fix issue with forms sudden running action */
        if (this.prevX == Float.MAX_VALUE)
        {
            this.prevX = basic.position.x;
            this.prevZ = basic.position.y;
        }

        this.controlActions(target);

        /* Update primary actions */
        if (this.active != null)
        {
            this.active.update();
        }

        if (this.lastActive != null)
        {
            this.lastActive.update();
        }

        /* Update secondary actions */
        Iterator<ActionPlayback> it = this.actions.iterator();

        while (it.hasNext())
        {
            ActionPlayback action = it.next();

            action.update();

            if (action.finishedFading() && action.isFadingModeOut())
            {
                action.stopFade();
                it.remove();
            }
        }
    }

    /**
     * This method is designed specifically to isolate any controlling 
     * code (i.e. the ones that is responsible for switching between 
     * actions).
     */
    protected void controlActions(Entity target)
    {
        BasicComponent basic = target.basic;
        double dx = basic.position.x - this.prevX;
        double dz = basic.position.z - this.prevZ;
        final float threshold = 0.05F;
        boolean moves = Math.abs(dx) > threshold || Math.abs(dz) > threshold;

        /* if (target.getHealth() <= 0)
        {
            this.setActiveAction(this.dying);
        }
        else if (target.isPlayerSleeping())
        {
            this.setActiveAction(this.sleeping);
        }
        else if (wet)
        {
            this.setActiveAction(!moves ? this.swimmingIdle : this.swimming);
        }
        else if (target.isRiding())
        {
            Entity riding = target.getRidingEntity();
            moves = Math.abs(riding.posX - this.prevX) > threshold || Math.abs(riding.posZ - this.prevZ) > threshold;

            this.prevX = riding.posX;
            this.prevZ = riding.posZ;
            this.setActiveAction(!moves ? this.ridingIdle : this.riding);
        }
        else if (creativeFlying || target.isElytraFlying())
        {
            this.setActiveAction(!moves ? this.flyingIdle : this.flying);
        } */
        if (false)
        {
            // TODO: implement more actions?
        }
        else
        {
            if (basic.sneak)
            {
                this.setActiveAction(!moves ? this.crouchingIdle : this.crouching);
            }
            else if (!basic.grounded && basic.velocity.y < 0 && basic.fall > 1.25)
            {
                this.setActiveAction(this.falling);
            }
            /* else if (target.isSprinting() && this.sprinting != null)
            {
                this.setActiveAction(this.sprinting);
            } */
            else
            {
                this.setActiveAction(!moves ? this.idle : this.running);
            }

            if (basic.grounded && !this.wasOnGround && /* !target.isSprinting() && */ this.prevMY < -0.5)
            {
                this.addAction(this.land);
            }
        }

        if (!basic.grounded && this.wasOnGround && Math.abs(basic.velocity.y) > 0.2F)
        {
            this.addAction(this.jump);
            this.wasOnGround = false;
        }

        if (basic.hitTimer >= 5)
        {
            this.addAction(this.swipe);
        }

        PlayerComponent component = target.get(PlayerComponent.class);

        if (component != null)
        {
            int gamepad = component.gamepad;

            boolean wasUpPressed = this.isGamepadPressed(gamepad, GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
            boolean wasDownPressed = this.isGamepadPressed(gamepad, GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN);
            boolean wasLeftPressed = this.isGamepadPressed(gamepad, GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
            boolean wasRightPressed = this.isGamepadPressed(gamepad, GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);

            if (wasUpPressed && !this.wasUpPressed) this.addAction(this.joystickUp);
            if (wasDownPressed && !this.wasDownPressed) this.addAction(this.joystickDown);
            if (wasLeftPressed && !this.wasLeftPressed) this.addAction(this.joystickLeft);
            if (wasRightPressed && !this.wasRightPressed) this.addAction(this.joystickRight);

            this.wasUpPressed = wasUpPressed;
            this.wasDownPressed = wasDownPressed;
            this.wasLeftPressed = wasLeftPressed;
            this.wasRightPressed = wasRightPressed;
        }

        this.prevX = basic.position.x;
        this.prevZ = basic.position.z;
        this.prevMY = basic.velocity.y;

        this.wasOnGround = basic.grounded;
    }

    private boolean isGamepadPressed(int gamepad, int button)
    {
        return ((gamepad >> button) & 0b1) == 1;
    }

    /**
     * Set current active (primary) action 
     */
    public void setActiveAction(ActionPlayback action)
    {
        if (this.active == action || action == null)
        {
            return;
        }

        if (this.active != null && action.priority < this.active.priority)
        {
            return;
        }

        if (this.active != null)
        {
            this.lastActive = this.active;
        }

        this.active = action;
        this.active.rewind();
        this.active.fadeIn();
    }

    public void addAction(ActionPlayback action)
    {
        this.addAction(action, true);
    }

    /**
     * Add an additional secondary action to the playback 
     */
    public void addAction(ActionPlayback action, boolean rewind)
    {
        if (action == null)
        {
            return;
        }

        if (this.actions.contains(action))
        {
            if (rewind)
            {
                action.rewind();
            }

            return;
        }

        action.rewind();
        action.fadeIn();
        this.actions.add(action);
    }

    /**
     * Apply currently running action pipeline onto given armature
     */
    public void applyActions(Entity target, Model armature, float transition)
    {
        if (this.lastActive != null && this.active.isFading())
        {
            this.lastActive.apply(target, armature, transition, 1F, false);
        }

        if (this.active != null)
        {
            float fade = this.active.isFading() ? this.active.getFadeFactor(transition) : 1F;

            this.active.apply(target, armature, transition, fade, false);
        }

        for (ActionPlayback action : this.actions)
        {
            if (action.isFading())
            {
                action.apply(target, armature, transition, action.getFadeFactor(transition), true);
            }
            else
            {
                action.apply(target, armature, transition, 1F, true);
            }
        }
    }
}