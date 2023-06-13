package mchorse.bbs.ui.framework.tooltips;

import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.utils.renderers.InterpolationRenderer;
import mchorse.bbs.utils.math.IInterpolation;

import java.util.function.Supplier;

public class InterpolationTooltip implements ITooltip
{
    public float ax;
    public float ay;
    public Supplier<IInterpolation> interpolation;
    public Supplier<Integer> duration;
    public int margin = 10;

    public InterpolationTooltip(float ax, float ay, Supplier<IInterpolation> interpolation)
    {
        this(ax, ay, interpolation, null);
    }

    public InterpolationTooltip(float ax, float ay, Supplier<IInterpolation> interpolation, Supplier<Integer> duration)
    {
        this.ax = ax;
        this.ay = ay;
        this.interpolation = interpolation;
        this.duration = duration;
    }

    public InterpolationTooltip margin(int margin)
    {
        this.margin = margin;

        return this;
    }

    @Override
    public IKey getLabel()
    {
        return IKey.EMPTY;
    }

    @Override
    public void renderTooltip(UIContext context)
    {
        Area area = context.tooltip.area;
        IInterpolation interpolation = this.interpolation == null ? null : this.interpolation.get();
        int duration = this.duration == null ? 40 : this.duration.get();

        float fx = (this.ax - 0.5F) * 2;

        int x = area.x(this.ax) + (int) (this.margin * fx);
        int y = area.y(this.ay);

        InterpolationRenderer.renderInterpolationPreview(interpolation, context, x, y, 1 - this.ax, this.ay, duration);
    }
}