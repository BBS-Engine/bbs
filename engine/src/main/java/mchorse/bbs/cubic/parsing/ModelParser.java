package mchorse.bbs.cubic.parsing;

import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelCube;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.cubic.data.model.ModelMesh;
import mchorse.bbs.data.DataStorageUtils;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.math.molang.MolangParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelParser
{
    public static Model parse(MolangParser parser, MapType data)
    {
        ListType texture = data.getList("texture");
        Model model = new Model(parser);

        model.textureWidth = texture.getInt(0);
        model.textureHeight = texture.getInt(1);

        parseGroups(model, data.getMap("groups"));

        model.initialize();

        return model;
    }

    private static Model parseGroups(Model model, MapType groups)
    {
        Map<String, List<String>> hierarchy = new HashMap<>();
        Map<String, ModelGroup> flatGroups = new HashMap<>();

        for (String key : groups.keys())
        {
            MapType groupElement = groups.getMap(key);
            ModelGroup group = new ModelGroup(key);

            /* Fill hierarchy information */
            String parent = groupElement.has("parent") ? groupElement.getString("parent") : "";
            List<String> list = hierarchy.computeIfAbsent(parent, (k) -> new ArrayList<>());

            list.add(group.id);

            /* Setup initial transformations */
            if (groupElement.has("origin"))
            {
                group.initial.translate.set(DataStorageUtils.vector3fFromData(groupElement.getList("origin")));
            }

            if (groupElement.has("rotate"))
            {
                group.initial.rotate.set(DataStorageUtils.vector3fFromData(groupElement.getList("rotate")));
            }

            /* Setup cubes and meshes */
            if (groupElement.has("cubes"))
            {
                parseCubes(model, group, groupElement.getList("cubes"));
            }

            if (groupElement.has("meshes"))
            {
                parseMeshes(model, group, groupElement.getList("meshes"));
            }

            flatGroups.put(group.id, group);
        }

        /* Setup hierarchy */
        for (Map.Entry<String, List<String>> entry : hierarchy.entrySet())
        {
            if (entry.getKey().isEmpty())
            {
                continue;
            }

            ModelGroup group = flatGroups.get(entry.getKey());

            for (String child : entry.getValue())
            {
                group.children.add(flatGroups.get(child));
            }
        }

        List<String> topLevel = hierarchy.get("");

        if (topLevel != null)
        {
            for (String rootGroup : topLevel)
            {
                model.topGroups.add(flatGroups.get(rootGroup));
            }
        }

        return model;
    }

    private static void parseCubes(Model model, ModelGroup group, ListType cubes)
    {
        for (BaseType element : cubes)
        {
            ModelCube cube = new ModelCube();

            cube.fromData((MapType) element);
            cube.generateQuads(model.textureWidth, model.textureHeight);

            group.cubes.add(cube);
        }
    }

    private static void parseMeshes(Model model, ModelGroup group, ListType meshes)
    {
        for (BaseType element : meshes)
        {
            ModelMesh mesh = new ModelMesh();

            mesh.fromData((MapType) element);

            group.meshes.add(mesh);
        }
    }
}