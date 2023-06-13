package mchorse.bbs.animation;

import mchorse.bbs.graphics.RenderingContext;

public class AnimationPlayer
{
    public Animation animation;

    public float currentTime;
    public boolean visible = true;
    public boolean playing;

    public void play()
    {
        this.playing = true;
    }

    public void update()
    {
        this.currentTime += 1;
    }

    public boolean canRemove()
    {
        return this.playing && this.animation != null && this.currentTime > this.animation.duration;
    }

    public void render(RenderingContext context)
    {
        if (this.animation == null || !this.visible)
        {
            return;
        }

        float currentTime = this.currentTime;

        if (this.playing)
        {
            currentTime += context.getTransition();
        }

        this.animation.render(context, currentTime);
    }
}