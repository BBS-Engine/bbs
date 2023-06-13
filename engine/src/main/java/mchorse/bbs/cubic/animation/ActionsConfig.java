package mchorse.bbs.cubic.animation;

import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Animated actions config. This little dude right there is 
 * responsible for storing configuration for the name of actions 
 * which should be used for particular <s>set of skills</s> actions. 
 */
public class ActionsConfig implements IMapSerializable
{
    private static Map<String, ActionConfig> a = new HashMap<String, ActionConfig>();
    private static Map<String, ActionConfig> b = new HashMap<String, ActionConfig>();

    public Map<String, ActionConfig> actions = new HashMap<String, ActionConfig>();

    public static void removeDefaultActions(Map<String, ActionConfig> map)
    {
        Iterator<Map.Entry<String, ActionConfig>> it = map.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<String, ActionConfig> entry = it.next();
            String key = entry.getKey();
            ActionConfig config = entry.getValue();

            if (config.isDefault(key))
            {
                it.remove();
            }
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof ActionsConfig)
        {
            ActionsConfig config = (ActionsConfig) obj;

            a.clear();
            a.putAll(this.actions);
            b.clear();
            b.putAll(config.actions);

            removeDefaultActions(a);
            removeDefaultActions(b);

            return a.equals(b);
        }

        return false;
    }

    public void copy(ActionsConfig config)
    {
        this.actions.clear();
        this.actions.putAll(config.actions);
    }

    /**
     * Get key for the action 
     */
    public ActionConfig getConfig(String key)
    {
        ActionConfig output = this.actions.get(key);

        return output == null ? new ActionConfig(key) : output;
    }

    @Override
    public void toData(MapType data)
    {
        for (Map.Entry<String, ActionConfig> entry : this.actions.entrySet())
        {
            if (entry.getValue().isDefault())
            {
                if (!entry.getValue().name.equals(entry.getKey()))
                {
                    data.putString(entry.getKey(), entry.getValue().name);
                }
            }
            else
            {
                data.put(entry.getKey(), entry.getValue().toData());
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.actions.clear();

        for (Map.Entry<String, BaseType> entry : data)
        {
            if (entry.getValue().isMap())
            {
                ActionConfig action = new ActionConfig();

                action.fromData(entry.getValue().asMap());
                this.actions.put(entry.getKey(), action);
            }
            else if (entry.getValue().isString())
            {
                ActionConfig action = new ActionConfig();

                action.name = entry.getValue().asString();

                this.actions.put(entry.getKey(), action);
            }
        }
    }
}