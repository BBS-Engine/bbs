package mchorse.bbs.ui.game.scripts.utils.documentation;

import java.util.ArrayList;
import java.util.List;

public class DocList extends DocEntry
{
    public List<DocEntry> entries = new ArrayList<DocEntry>();

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public List<DocEntry> getEntries()
    {
        return this.entries;
    }
}