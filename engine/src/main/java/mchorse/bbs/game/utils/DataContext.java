package mchorse.bbs.game.utils;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataContext
{
    public static final ExpressionRewriter REWRITER = new ExpressionRewriter();

    /* TODO: instead of world, change to IBridge probably */
    public World world;
    public Entity subject;
    public Entity object;

    private boolean canceled;

    private Map<String, Object> values = new HashMap<String, Object>();

    public DataContext(Object object)
    {
        if (object instanceof World)
        {
            this.world = (World) object;
        }
        else if (object instanceof Entity)
        {
            this.subject = (Entity) object;
            this.world = this.subject.world;
        }

        this.setup();
    }

    public DataContext(World world)
    {
        this.world = world;

        this.setup();
    }

    public DataContext(Entity subject)
    {
        this.subject = subject;
        this.world = subject.world;

        this.setup();
    }

    public DataContext(Entity subject, Entity object)
    {
        this.subject = subject;
        this.object = object;
        this.world = subject.world;

        this.setup();
    }

    public void cancel()
    {
        this.cancel(true);
    }

    public void cancel(boolean canceled)
    {
        this.canceled = canceled;
    }

    public boolean isCanceled()
    {
        return this.canceled;
    }

    private void setup()
    {
        Entity player = this.getPlayer();
        Entity npc = this.getNpc();

        this.set("subject", this.subject == null ? "" : this.subject.getUUID().toString());
        this.set("subject_name", this.subject == null ? "" : this.subject.id);
        this.set("object", this.object == null ? "" : this.object.getUUID().toString());
        this.set("object_name", this.object == null ? "" : this.object.id);
        this.set("player", player == null ? "" : player.getUUID().toString());
        this.set("player_name", player == null ? "" : player.id);
        this.set("npc", npc == null ? "" : npc.getUUID().toString());
        this.set("npc_name", npc == null ? "" : npc.id);
    }

    public DataContext set(String key, double value)
    {
        this.values.put(key, value);

        return this;
    }

    public DataContext set(String key, Object value)
    {
        this.values.put(key, value);

        return this;
    }

    public DataContext parse(String data)
    {
        MapType map = DataToString.mapFromString(data);

        return map == null ? this : this.parse(map);
    }

    public DataContext parse(MapType data)
    {
        for (String key : data.keys())
        {
            BaseType value = data.get(key);

            if (value.isNumeric())
            {
                this.set(key, value.asNumeric().doubleValue());
            }
            else if (BaseType.isString(value))
            {
                this.set(key, ((StringType) value).value);
            }
        }

        return this;
    }

    public Map<String, Object> getValues()
    {
        return this.values;
    }

    public Object getValue(String key)
    {
        return this.values.get(key);
    }

    public String process(String text)
    {
        if (!text.contains("${"))
        {
            return text;
        }

        return REWRITER.set(this).replace(text);
    }

    public Entity getPlayer()
    {
        if (EntityUtils.isPlayer(this.subject))
        {
            return this.subject;
        }
        else if (EntityUtils.isPlayer(this.object))
        {
            return this.object;
        }

        return null;
    }

    public Entity getNpc()
    {
        if (EntityUtils.isNpc(this.subject))
        {
            return this.subject;
        }
        else if (EntityUtils.isNpc(this.object))
        {
            return this.object;
        }

        return null;
    }

    public Set<String> getKeys()
    {
        return this.values.keySet();
    }

    public DataContext copy()
    {
        DataContext context = new DataContext(this.world);

        context.subject = this.subject;
        context.object = this.object;
        context.world = this.world;
        context.values.putAll(this.values);
        context.setup();

        return context;
    }
}