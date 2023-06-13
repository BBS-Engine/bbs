package mchorse.bbs.animation;

import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.Map;
import java.util.Set;

/**
 * Puppet interface needed to animate forms with animation editor.
 */
public interface IPuppet
{
    public static String combinePaths(String a, String b)
    {
        return StringUtils.combinePaths(a, b, ".");
    }

    /* Helper methods to work with nulls and non-puppet forms */

    public static void freeze(Form form)
    {
        if (form instanceof IPuppet)
        {
            ((IPuppet) form).freeze();
        }
        else if (form != null)
        {
            form.parts.freeze();
        }
    }

    public static void getAvailableKeys(Form form, String prefix, Set<String> keys)
    {
        if (form instanceof IPuppet)
        {
            ((IPuppet) form).getAvailableKeys(prefix, keys);
        }
        else if (form != null)
        {
            form.parts.getAvailableKeys(prefix, keys);
        }
    }

    public static void applyKeyframes(Form form, String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        if (form instanceof IPuppet)
        {
            ((IPuppet) form).applyKeyframes(prefix, keyframes, ticks);
        }
        else if (form != null)
        {
            form.parts.applyKeyframes(prefix, keyframes, ticks);
        }
    }

    public static boolean fillDefaultValue(Form form, String prefix, ValueDouble value)
    {
        if (form instanceof IPuppet)
        {
            return ((IPuppet) form).fillDefaultValue(prefix, value);
        }
        else if (form != null)
        {
            return form.parts.fillDefaultValue(prefix, value);
        }

        return false;
    }

    /**
     * Freeze the form (disable any animations). It's assumed that the
     * implementation also propagates this call to its children (body parts)!
     */
    public void freeze();

    /**
     * Get available keys for animation. It's assumed that the
     * implementation also propagates this call to its children (body parts)!
     */
    public void getAvailableKeys(String prefix, Set<String> keys);

    /**
     * Apply given keyframe channels to this puppet. It's assumed that the
     * implementation also propagates this call to its children (body parts)!
     */
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks);

    /**
     * Fill default value for given key. It's assumed that the
     * implementation also propagates this call to its children (body parts)!
     */
    public boolean fillDefaultValue(String prefix, ValueDouble value);
}