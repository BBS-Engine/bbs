package mchorse.bbs.camera.clips.modifiers;

import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.ClipContext;
import mchorse.bbs.camera.data.Point;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePoint;

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

        this.register(this.translate);
    }

    @Override
    public void applyClip(ClipContext context, Position position)
    {
        Point point = this.translate.get();

        position.point.x = this.isActive(0) ? point.x : position.point.x + point.x;
        position.point.y = this.isActive(1) ? point.y : position.point.y + point.y;
        position.point.z = this.isActive(2) ? point.z : position.point.z + point.z;
    }

    @Override
    public Clip create()
    {
        return new TranslateClip();
    }
}