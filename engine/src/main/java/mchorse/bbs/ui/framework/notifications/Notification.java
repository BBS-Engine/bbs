package mchorse.bbs.ui.framework.notifications;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.Interpolations;

public class Notification
{
    public final IKey message;
    public final int background;
    public final int color;

    private int tick;

    public Notification(IKey message, int background, int color)
    {
        this.message = message;
        this.background = background;
        this.color = color;

        this.tick = 80;
    }

    public boolean isExpired()
    {
        return this.tick <= 0;
    }

    public float getFactor(float transition)
    {
        float envelope = Interpolations.envelope(this.tick - transition, 0F, 20F, 70F, 80F);

        return Interpolation.QUAD_INOUT.interpolate(0F, 1F, envelope);
    }

    public void update()
    {
        this.tick -= 1;
    }
}