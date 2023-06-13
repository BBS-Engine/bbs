package mchorse.bbs.cubic.animation;

import mchorse.bbs.cubic.CubicModelAnimator;
import mchorse.bbs.cubic.MolangHelper;
import mchorse.bbs.cubic.data.animation.Animation;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.world.entities.Entity;

public class ActionPlayback
{
    public Animation action;
    public ActionConfig config;

    private int fade;
    private float ticks;
    private int duration;
    private double speed = 1;

    private boolean looping;
    private Fade fading = Fade.FINISHED;
    public boolean playing = true;
    public int priority;

    public ActionPlayback(Animation action, ActionConfig config)
    {
        this(action, config, true);
    }

    public ActionPlayback(Animation action, ActionConfig config, boolean looping)
    {
        this.action = action;
        this.config = config;
        this.duration = action.getLengthInTicks();
        this.looping = looping;
        this.setSpeed(1);
    }

    public ActionPlayback(Animation action, ActionConfig config, boolean looping, int priority)
    {
        this(action, config, looping);
        this.priority = priority;
    }

    /* Action playback control methods */

    /**
     * Rewinds the animation (if config allows)
     */
    public void rewind()
    {
        if (this.config.loop)
        {
            this.ticks = Math.copySign(1, this.speed) < 0 ? this.duration : 0;
        }

        this.stopFade();
    }

    /**
     * Whether this action playback finished fading
     */
    public boolean finishedFading()
    {
        return this.fading != Fade.FINISHED && this.fade <= 0;
    }

    public boolean isFadingModeOut()
    {
        return this.fading == Fade.OUT;
    }

    public boolean isFadingModeIn()
    {
        return this.fading == Fade.IN;
    }

    /**
     * Whether this action playback is fading
     */
    public boolean isFading()
    {
        return this.fading != Fade.FINISHED && this.fade > 0;
    }

    /**
     * Start fading out
     */
    public void fadeOut()
    {
        this.fade = (int) this.config.fade;
        this.fading = Fade.OUT;
    }

    /**
     * Start fading in
     */
    public void fadeIn()
    {
        this.fade = (int) this.config.fade;
        this.fading = Fade.IN;
    }

    /**
     * Reset fading
     */
    public void stopFade()
    {
        this.fade = 0;
        this.fading = Fade.FINISHED;
    }

    public int getFade()
    {
        return this.fade;
    }

    /**
     * Calculate fade factor with given partial ticks
     *
     * Closer to 1 means started fading, meanwhile closer to 0 is almost
     * finished fading.
     */
    public float getFadeFactor(float transition)
    {
        float factor = (this.fade - transition) / this.config.fade;

        return this.fading == Fade.OUT ? factor : 1 - factor;
    }

    /**
     * Set speed of an action playback
     */
    public void setSpeed(double speed)
    {
        this.speed = speed * this.config.speed;
    }

    /* Update methods */

    public void update()
    {
        if (this.fading != Fade.FINISHED && this.fade > 0)
        {
            this.fade--;
        }

        if (!this.playing) return;

        this.ticks += this.speed;

        if (!this.looping && this.fading != Fade.OUT && this.ticks >= this.duration)
        {
            this.fadeOut();
        }

        if (this.looping)
        {
            if (this.ticks >= this.duration && this.speed > 0)
            {
                this.ticks -= this.duration;
                this.ticks += this.config.tick;
            }
            else if (this.ticks < 0 && this.speed < 0)
            {
                this.ticks = this.duration + this.ticks;
                this.ticks -= this.config.tick;
            }
        }
    }

    public float getTick(float transition)
    {
        float ticks = this.ticks + (float) (transition * this.speed);

        if (this.looping)
        {
            if (ticks >= this.duration && this.speed > 0)
            {
                ticks -= this.duration;
            }
            else if (this.ticks < 0 && this.speed < 0)
            {
                ticks = this.duration + ticks;
            }
        }

        return ticks;
    }

    public void apply(Entity target, Model armature, float transition, float blend, boolean skipInitial)
    {
        float tick = this.getTick(transition);

        MolangHelper.setMolangVariables(armature.parser, target, tick, transition);

        CubicModelAnimator.animate(target, armature, this.action, tick, blend, skipInitial);
    }

    public static enum Fade
    {
        OUT, FINISHED, IN
    }
}