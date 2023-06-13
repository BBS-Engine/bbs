package mchorse.bbs.game.states;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.DoubleType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.data.types.StringType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * States allow to store values of the world that can be
 * used in dialogues, crafting tables, events and etc.
 * to control logic and store arbitrary numerical values
 */
public class States implements IMapSerializable
{
    public static final String QUEST_PREFIX = "quests.";
    public static final String DIALOGUE_PREFIX = "dialogue.";
    public static final String FACTIONS_PREFIX = "factions.";

    public Map<String, Object> values = new HashMap<String, Object>();

    private File file;

    public States()
    {}

    public States(File file)
    {
        this.file = file;
    }

    protected void post(String id, Object previous, Object current)
    {
        /* TODO: Mappet.EVENT_BUS.post(new StateChangedEvent(this, id, previous, current)); */
    }

    /* CRUD */

    public void add(String id, double value)
    {
        Object previous = this.values.get(id);

        if (previous == null || previous instanceof Number)
        {
            this.values.put(id, (previous == null ? 0 : ((Number) previous).doubleValue()) + value);
            this.post(id, previous, value);
        }
    }

    public void setNumber(String id, double value)
    {
        Object previous = this.values.get(id);

        this.values.put(id, value);
        this.post(id, previous, value);
    }

    public void setString(String id, String value)
    {
        Object previous = this.values.get(id);

        this.values.put(id, value);
        this.post(id, previous, value);
    }

    public double getNumber(String id)
    {
        Object object = this.values.get(id);

        return object instanceof Number ? ((Number) object).doubleValue() : 0;
    }

    public String getString(String id)
    {
        Object object = this.values.get(id);

        return object instanceof String ? (String) object : "";
    }

    public boolean reset(String id)
    {
        Object previous = this.values.remove(id);

        this.post(id, previous, null);

        return previous != null;
    }

    public boolean resetMasked(String id)
    {
        if (id.trim().equals("*"))
        {
            boolean wasEmpty = this.values.isEmpty();

            if (!wasEmpty)
            {
                this.clear();
            }

            return !wasEmpty;
        }

        if (id.contains("*"))
        {
            id = id.replaceAll("\\*", ".*");

            Pattern pattern = Pattern.compile("^" + id + "$");
            int size = this.values.size();

            this.values.keySet().removeIf(key -> pattern.matcher(key).matches());

            if (this.values.size() != size)
            {
                this.post(null, null, null);

                return true;
            }

            return false;
        }

        return this.reset(id);
    }

    public void clear()
    {
        this.values.clear();
        this.post(null, null, null);
    }

    public void copy(States states)
    {
        this.values.clear();
        this.values.putAll(states.values);

        this.post(null, null, null);
    }

    /* Quest convenience methods */

    public void completeQuest(String id)
    {
        this.add(QUEST_PREFIX + id, 1);
    }

    public int getQuestCompletedTimes(String id)
    {
        return (int) this.getNumber(QUEST_PREFIX + id);
    }

    public boolean wasQuestCompleted(String id)
    {
        return this.getQuestCompletedTimes(id) > 0;
    }

    /* Faction convenience methods */

    public void addFactionScore(String id, int score, int defaultScore)
    {
        if (this.hasFaction(id))
        {
            this.add(FACTIONS_PREFIX + id, score);
        }
        else
        {
            this.setNumber(FACTIONS_PREFIX + id, defaultScore + score);
        }
    }

    public void setFactionScore(String id, int score)
    {
        this.setNumber(FACTIONS_PREFIX + id, score);
    }

    public int getFactionScore(String id)
    {
        return (int) this.getNumber(FACTIONS_PREFIX + id);
    }

    public boolean clearFactionScore(String id)
    {
        return this.reset(FACTIONS_PREFIX + id);
    }

    public void clearAllFactionScores()
    {
        this.values.keySet().removeIf((key) -> key.startsWith(FACTIONS_PREFIX));
    }

    public boolean hasFaction(String id)
    {
        return this.values.containsKey(FACTIONS_PREFIX + id);
    }

    /* Dialogues convenience methods */

    public void readDialogue(String id, String marker)
    {
        this.add(this.getDialogueId(id, marker), 1);
    }

    public boolean hasReadDialogue(String id, String marker)
    {
        return this.getReadDialogueTimes(id, marker) > 0;
    }

    public int getReadDialogueTimes(String id, String marker)
    {
        return (int) this.getNumber(this.getDialogueId(id, marker));
    }

    private String getDialogueId(String id, String marker)
    {
        id = DIALOGUE_PREFIX + id;

        if (marker != null && !marker.isEmpty())
        {
            id += ":" + marker;
        }

        return id;
    }

    @Override
    public void toData(MapType data)
    {
        for (Map.Entry<String, Object> entry : this.values.entrySet())
        {
            if (entry.getValue() instanceof Number)
            {
                data.putDouble(entry.getKey(), ((Number) entry.getValue()).doubleValue());
            }
            else if (entry.getValue() instanceof String)
            {
                data.putString(entry.getKey(), (String) entry.getValue());
            }
        }
    }

    @Override
    public void fromData(MapType data)
    {
        for (String key : data.keys())
        {
            BaseType base = data.get(key);

            if (BaseType.isString(base))
            {
                this.values.put(key, ((StringType) base).value);
            }
            else if (base instanceof DoubleType)
            {
                this.values.put(key, ((DoubleType) base).value);
            }
        }
    }

    /* Deserialization and serialization */

    public void load()
    {
        if (!this.file.exists())
        {
            return;
        }

        try
        {
            this.fromData((MapType) DataToString.read(this.file));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean save()
    {
        return DataToString.writeSilently(this.file, this.toData());
    }
}