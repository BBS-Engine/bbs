package mchorse.bbs.game.quests.chains;

import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.manager.data.AbstractData;

import java.util.ArrayList;
import java.util.List;

public class QuestChain extends AbstractData
{
    public List<QuestEntry> entries = new ArrayList<QuestEntry>();

    @Override
    public void fromData(MapType data)
    {
        ListType entries = data.getList("entries");

        if (!entries.isEmpty())
        {
            this.entries.clear();

            for (BaseType type : entries)
            {
                if (!type.isMap())
                {
                    continue;
                }

                QuestEntry entry = new QuestEntry();

                entry.fromData(type.asMap());
                this.entries.add(entry);
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        ListType entries = new ListType();

        for (QuestEntry entry : this.entries)
        {
            entries.add(entry.toData());
        }

        data.put("entries", entries);
    }
}