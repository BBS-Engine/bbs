package mchorse.bbs.utils;

import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.cubic.data.model.Model;
import mchorse.bbs.cubic.data.model.ModelGroup;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.forms.ModelForm;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Pose implements IMapSerializable
{
    public boolean staticPose;
    public final Map<String, Transform> transforms = new HashMap<String, Transform>();

    public static void getAvailableKeys(String prefix, Set<String> keys)
    {
        keys.add(IPuppet.combinePaths(prefix, "x"));
        keys.add(IPuppet.combinePaths(prefix, "y"));
        keys.add(IPuppet.combinePaths(prefix, "z"));
        keys.add(IPuppet.combinePaths(prefix, "sx"));
        keys.add(IPuppet.combinePaths(prefix, "sy"));
        keys.add(IPuppet.combinePaths(prefix, "sz"));
        keys.add(IPuppet.combinePaths(prefix, "rx"));
        keys.add(IPuppet.combinePaths(prefix, "ry"));
        keys.add(IPuppet.combinePaths(prefix, "rz"));
    }

    public void apply(Model model)
    {
        if (this.transforms.isEmpty())
        {
            return;
        }

        for (Map.Entry<String, Transform> entry : this.transforms.entrySet())
        {
            Transform transform = entry.getValue();
            ModelGroup group = model.getGroup(entry.getKey());

            if (this.staticPose)
            {
                group.current.translate.set(group.initial.translate);
                group.current.scale.set(group.initial.scale);
                group.current.rotate.set(group.initial.rotate);
            }

            if (group != null)
            {
                group.current.translate.add(transform.translate);
                group.current.scale.add(transform.scale).sub(1, 1, 1);
                group.current.rotate.add(
                    (float) Math.toDegrees(transform.rotate.x),
                    (float) Math.toDegrees(transform.rotate.y),
                    (float) Math.toDegrees(transform.rotate.z)
                );
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

        if (obj instanceof Pose)
        {
            Pose pose = (Pose) obj;

            return this.staticPose == pose.staticPose
                    && Objects.equals(this.transforms, pose.transforms);
        }

        return false;
    }

    public void copy(Pose pose)
    {
        this.staticPose = pose.staticPose;

        this.transforms.clear();

        if (!pose.transforms.isEmpty())
        {
            for (Map.Entry<String, Transform> entry : pose.transforms.entrySet())
            {
                this.transforms.put(entry.getKey(), entry.getValue().copy());
            }
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.putBool("static", this.staticPose);

        if (!this.transforms.isEmpty())
        {
            MapType pose = new MapType();

            for (Map.Entry<String, Transform> entry : this.transforms.entrySet())
            {
                pose.put(entry.getKey(), entry.getValue().toData());
            }

            data.put("pose", pose);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        this.staticPose = data.getBool("static");
        this.transforms.clear();

        if (data.has("pose"))
        {
            MapType pose = data.getMap("pose");

            if (!pose.isEmpty())
            {
                for (String key : pose.keys())
                {
                    Transform transform = new Transform();

                    transform.fromData(pose.getMap(key));
                    this.transforms.put(key, transform);
                }
            }
        }
    }

    public boolean isEmpty()
    {
        return this.transforms.isEmpty();
    }
}