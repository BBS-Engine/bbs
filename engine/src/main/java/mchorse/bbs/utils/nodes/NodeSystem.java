package mchorse.bbs.utils.nodes;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.utils.factory.IFactory;
import mchorse.bbs.utils.manager.data.AbstractData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class NodeSystem <T extends Node, D> extends AbstractData
{
    private IFactory<T, D> factory;

    public Map<UUID, T> nodes = new HashMap<UUID, T>();
    public Map<UUID, List<NodeRelation<T>>> relations = new HashMap<UUID, List<NodeRelation<T>>>();
    public T main;

    public NodeSystem(IFactory<T, D> factory)
    {
        this.factory = factory;
    }

    public IFactory<T, D> getFactory()
    {
        return this.factory;
    }

    /**
     * Add a node to node system
     *
     * If the node doesn't have an UUID, then it will be given a random
     * one to avoid UUID collision
     *
     * @throws IllegalStateException when node's UUID is already present
     */
    public void add(T node)
    {
        if (node.getId() == null)
        {
            UUID id;

            do
            {
                id = UUID.randomUUID();
            }
            while (this.nodes.containsKey(id));

            node.setId(id);
        }
        else if (this.nodes.containsKey(node.getId()))
        {
            throw new IllegalStateException("Node by UUID " + node.getId() + " is already present in this node system!");
        }

        this.nodes.put(node.getId(), node);
    }

    /**
     * Tie an input node to an output node
     */
    public boolean tie(T output, T input)
    {
        if (output == input)
        {
            return false;
        }

        if (this.nodes.containsKey(output.getId()) && this.nodes.containsKey(input.getId()) && !this.hasRelation(output, input))
        {
            List<NodeRelation<T>> relations = this.relations.get(output.getId());

            if (relations == null)
            {
                relations = new ArrayList<NodeRelation<T>>();

                this.relations.put(output.getId(), relations);
            }

            relations.add(new NodeRelation<T>(output, input));

            return true;
        }

        return false;
    }

    public void untie(T output, T input)
    {
        List<NodeRelation<T>> relations = this.relations.get(output.getId());

        if (relations != null)
        {
            relations.removeIf((relation) -> relation.input == input);

            if (relations.isEmpty())
            {
                this.relations.remove(output.getId());
            }
        }
    }

    public void addTie(T output, T toAdd)
    {
        this.add(toAdd);
        this.tie(output, toAdd);
    }

    public void addMain(T node)
    {
        this.add(node);
        this.main = node;
    }

    public boolean remove(T node)
    {
        UUID key = node.getId();

        if (!this.nodes.containsKey(key))
        {
            return false;
        }

        this.nodes.remove(key);

        this.relations.remove(node.getId());

        Iterator<Map.Entry<UUID, List<NodeRelation<T>>>> it = this.relations.entrySet().iterator();

        while (it.hasNext())
        {
            Map.Entry<UUID, List<NodeRelation<T>>> entry = it.next();

            entry.getValue().removeIf(relation -> relation.input == node);

            if (entry.getValue().isEmpty())
            {
                it.remove();
            }
        }

        return true;
    }

    /**
     * Checks whether output and input has a relationship in this
     * node system
     */
    public boolean hasRelation(T output, T input)
    {
        return this.getRelation(output, input) != null;
    }

    public NodeRelation<T> getRelation(T output, T input)
    {
        if (!this.relations.containsKey(output.getId()))
        {
            return null;
        }

        for (NodeRelation<T> relation : this.relations.get(output.getId()))
        {
            if (Objects.equals(relation.input.getId(), input.getId()))
            {
                return relation;
            }
        }

        return null;
    }

    public List<T> getChildren(T node)
    {
        List<T> children = new ArrayList<T>();

        if (this.relations.containsKey(node.getId()))
        {
            for (NodeRelation<T> relation : this.relations.get(node.getId()))
            {
                if (relation.output == node)
                {
                    children.add(relation.input);
                }
            }
        }

        return children;
    }

    public List<T> getRoots()
    {
        List<T> roots = new ArrayList<T>();

        main:
        for (T node : this.nodes.values())
        {
            for (List<NodeRelation<T>> relations : this.relations.values())
            {
                for (NodeRelation<T> relation : relations)
                {
                    if (relation.input == node)
                    {
                        continue main;
                    }
                }
            }

            roots.add(node);
        }

        return roots;
    }

    /* Serialization / deserialization */

    @Override
    public void toData(MapType data)
    {
        ListType nodes = new ListType();

        for (T node : this.nodes.values())
        {
            MapType nodeMap = this.factory.toData(node);

            if (this.relations.containsKey(node.getId()))
            {
                ListType relations = new ListType();

                for (NodeRelation<T> relation : this.relations.get(node.getId()))
                {
                    relations.addString(relation.input.getId().toString());
                }

                nodeMap.put("relations", relations);
            }

            nodes.add(nodeMap);
        }

        if (nodes.size() > 0)
        {
            if (this.main != null && this.nodes.containsKey(this.main.getId()))
            {
                data.putString("main", this.main.getId().toString());
            }

            data.put("nodes", nodes);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        Map<UUID, List<UUID>> map = new HashMap<UUID, List<UUID>>();

        if (data.has("nodes"))
        {
            ListType nodes = data.getList("nodes");

            for (int i = 0; i < nodes.size(); i++)
            {
                MapType nodeMap = nodes.getMap(i);
                T node = this.factory.fromData(nodeMap);

                /* Relations are not serialized by nodes themselves */
                if (nodeMap.has("relations"))
                {
                    List<UUID> uuids = new ArrayList<UUID>();
                    ListType relations = nodeMap.getList("relations");

                    map.put(node.getId(), uuids);

                    for (int j = 0; j < relations.size(); j++)
                    {
                        uuids.add(UUID.fromString(relations.getString(j)));
                    }
                }

                this.add(node);
            }
        }

        /* Tie collected nodes */
        for (Map.Entry<UUID, List<UUID>> entry : map.entrySet())
        {
            for (UUID input : entry.getValue())
            {
                T nodeOutput = this.nodes.get(entry.getKey());
                T nodeInput = this.nodes.get(input);

                if (nodeOutput == nodeInput || nodeInput == null || nodeOutput == null)
                {
                    continue;
                }

                this.tie(nodeOutput, nodeInput);
            }
        }

        if (data.has("main"))
        {
            this.main = this.nodes.get(UUID.fromString(data.getString("main")));
        }
    }
}