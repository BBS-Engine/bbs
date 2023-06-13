package mchorse.bbs.cubic.data.animation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Animations
{
    public Map<String, Animation> animations = new HashMap<String, Animation>();

    public Collection<Animation> getAll()
    {
        return this.animations.values();
    }

    public void add(Animation animation)
    {
        this.animations.put(animation.id, animation);
    }

    public Animation get(String id)
    {
        return this.animations.get(id);
    }
}