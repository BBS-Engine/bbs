package mchorse.bbs.forms.properties;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.keyframes.generic.GenericKeyframeChannel;
import mchorse.bbs.utils.math.IInterpolation;

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
     * Get previous value of the property (which isn't affected by tweening).
     * It could be null.
     */
    public T getLast();

    /**
     * Update current property (needed for tweening).
     */
    public void update();

    /**
     * Tween this property to new value. Some properties may not fully support tweening!
     */
    public void tween(T newValue, T oldValue, int duration, IInterpolation interpolation, int offset, boolean playing);

    /**
     * Check whether this property is in progress of tweening.
     */
    public boolean isTweening();

    /**
     * Get tween factor (0 - started tweening, 1 - finished tweening).
     */
    public float getTweenFactor(float transition);

    /**
     * Get tween factor with interpolation applied (0 - started tweening, 1 - finished tweening).
     */
    public float getTweenFactorInterpolated(float transition);

    /**
     * Checks whether this property can create a generic keyframe channel
     */
    public boolean canCreateChannel();

    /**
     * Create a generic keyframe channel that can be used with this property
     */
    public GenericKeyframeChannel createChannel(String key);
}