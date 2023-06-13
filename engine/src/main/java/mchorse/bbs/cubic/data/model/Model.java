package mchorse.bbs.cubic.data.model;

import mchorse.bbs.math.molang.MolangParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Model
{
    public int textureWidth;
    public int textureHeight;

    public final MolangParser parser;

    /**
     * This list contains only the root groups of the model (and not all of the groups)
     */
    public List<ModelGroup> topGroups = new ArrayList<ModelGroup>();

    private Map<String, ModelGroup> namedGroups = new HashMap<String, ModelGroup>();
    private List<ModelGroup> orderedGroups = new ArrayList<ModelGroup>();
    private int nextIndex;

    public Model(MolangParser parser)
    {
        this.parser = parser;
    }

    public void initialize()
    {
        this.fillGroups(this.topGroups);

        this.orderedGroups = Collections.unmodifiableList(this.orderedGroups);
    }

    private void fillGroups(List<ModelGroup> groups)
    {
        for (ModelGroup group : groups)
        {
            this.namedGroups.put(group.id, group);
            this.orderedGroups.add(group);

            group.index = this.nextIndex;
            this.nextIndex += 1;

            this.fillGroups(group.children);
        }
    }

    public List<ModelGroup> getOrderedGroups()
    {
        return this.orderedGroups;
    }

    public Set<String> getAllGroupKeys()
    {
        return this.namedGroups.keySet();
    }

    public Collection<ModelGroup> getAllGroups()
    {
        return this.namedGroups.values();
    }

    public ModelGroup getGroup(String id)
    {
        return this.namedGroups.get(id);
    }
}