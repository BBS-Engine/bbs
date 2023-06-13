package mchorse.bbs.animation;

import mchorse.bbs.BBSData;

import java.util.HashMap;
import java.util.Map;

public class Animations
{
    public Map<String, AnimationPlayer> animations = new HashMap<String, AnimationPlayer>();

    public AnimationPlayer get(String key)
    {
        if (this.animations.containsKey(key))
        {
            if (BBSData.getAnimations().exists(key))
            {
                this.animations.remove(key);
            }
            else
            {
                return this.animations.get(key);
            }
        }

        Animation animation = BBSData.getAnimations().load(key);

        if (animation == null)
        {
            this.animations.put(key, null);

            return null;
        }

        AnimationPlayer player = new AnimationPlayer();

        player.animation = animation;

        this.animations.put(key, player);

        return player;
    }

    public void remove(String key)
    {
        this.animations.remove(key);
    }
}