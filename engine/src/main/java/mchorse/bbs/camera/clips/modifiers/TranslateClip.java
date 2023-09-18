package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePoint;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;

/**
 * Translate camera modifier
 * 
 * This camera modifier is basically translates the position of 
 * calculated camera fixture by stored X, Y and Z.
 */
public class TranslateClip extends ComponentClip
{
    public final ValuePoint translate = new ValuePoint("translate", new Point(0, 0, 0));

    public TranslateClip()
    {
        super();

        this.add(this.translate);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        Point point = this.translate.get();

        position.point.x = this.applyProperty(context.count, 0, position.point.x, point.x);
        position.point.y = this.applyProperty(context.count, 1, position.point.y, point.y);
        position.point.z = this.applyProperty(context.count, 2, position.point.z, point.z);
    }

    private double applyProperty(int count, int i, double absolute, double relative)
    {
        if (this.isActive(i))
        {
            return relative;
        }

        return count == 0 ? absolute : absolute + relative;
    }

    @Override
    public Clip create()
    {
        return new TranslateClip();
    }
}