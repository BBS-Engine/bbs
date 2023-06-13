package mchorse.bbs.forms.properties;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;

public interface IFormProperty <T> extends IMapSerializable
{
    /**
     * Get this property's key (which is used for serialization).
     */
    public String getKey();

    /**
     * Get this property's form owner.
     */
    public Form getForm();

    /**
     * Overwite the value of this property (this also stops tweening).
     */
    public void set(T value);

    /**
     * Get current value of the property (which isn't affected by tweening).
     */
    public T get();

    /**
     * Get tweened value (if this property's tween is in progress) or the current value.
     */
    public T get(float transition);

    /**
     * Update current property (needed for tweening).
     */
    public void update();

    public default void tween(T newValue, int duration)
    {
        this.tween(newValue, duration, Interpolation.LINEAR);
    }

    /**
     * Tween this property to new value. Some properties may not fully support tweening!
     */
    public default void tween(T newValue, int duration, String interpolation)
    {
        for (Interpolation i : Interpolation.values())
        {
            if (i.key.equals(interpolation))
            {
                this.tween(newValue, duration, i);

                return;
            }
        }
    }

    /**
     * Tween this property to new value. Some properties may not fully support tweening!
     */
    public void tween(T newValue, int duration, IInterpolation interpolation);

    /**
     * Pause tween animation at given offset.
     */
    public void pause(int offset);

    /**
     * Check whether this property is in progress of tweening.
     */
    public boolean isTweening();

    /**
     * Get tween factor (0 - started tweening, 1 - finished tweening).
     */
    public float getTweenFactor(float transition);
}