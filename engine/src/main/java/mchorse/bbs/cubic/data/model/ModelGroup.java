package mchorse.bbs.cubic.data.model;

import mchorse.bbs.utils.Transform;

import java.util.ArrayList;
import java.util.List;

public class ModelGroup
{
    public final String id;
    public List<ModelGroup> children = new ArrayList<>();
    public List<ModelCube> cubes = new ArrayList<>();
    public List<ModelMesh> meshes = new ArrayList<>();
    public boolean visible = true;
    public int index = -1;

    public Transform initial = new Transform();
    public Transform current = new Transform();

    public ModelGroup(String id)
    {
        this.id = id;
    }
}