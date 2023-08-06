package mchorse.bbs.forms.forms;

import mchorse.bbs.animation.IPuppet;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.utils.keyframes.KeyframeChannel;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.world.entities.Entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BodyPartManager implements IMapSerializable, IPuppet
{
    /**
     * Form owner of this body part manager.
     */
    Form owner;

    private final List<BodyPart> parts = new ArrayList<>();

    public BodyPartManager(Form owner)
    {
        this.owner = owner;
    }

    public Form getOwner()
    {
        return this.owner;
    }

    public List<BodyPart> getAll()
    {
        return Collections.unmodifiableList(this.parts);
    }

    public void addBodyPart(BodyPart part)
    {
        part.setManager(this);

        this.parts.add(part);
    }

    public void removeBodyPart(BodyPart part)
    {
        if (this.parts.remove(part))
        {
            part.setManager(null);
        }
    }

    public void update(Entity target)
    {
        for (BodyPart part : this.parts)
        {
            part.update(target);
        }
    }

    @Override
    public void freeze()
    {
        for (BodyPart part : this.parts)
        {
            part.freeze();
        }
    }

    @Override
    public void getAvailableKeys(String prefix, Set<String> keys)
    {
        for (int i = 0; i < this.parts.size(); i++)
        {
            this.parts.get(i).getAvailableKeys(IPuppet.combinePaths(prefix, String.valueOf(i)), keys);
        }
    }

    @Override
    public void applyKeyframes(String prefix, Map<String, KeyframeChannel> keyframes, float ticks)
    {
        for (int i = 0; i < this.parts.size(); i++)
        {
            this.parts.get(i).applyKeyframes(IPuppet.combinePaths(prefix, String.valueOf(i)), keyframes, ticks);
        }
    }

    @Override
    public boolean fillDefaultValue(String prefix, ValueDouble value)
    {
        for (int i = 0; i < this.parts.size(); i++)
        {
            if (this.parts.get(i).fillDefaultValue(IPuppet.combinePaths(prefix, String.valueOf(i)), value))
            {
                return true;
            }
        }

        return false;
    }

    public void tween(BodyPartManager parts, int duration, IInterpolation interpolation)
    {
        if (this.parts.size() > parts.parts.size())
        {
            while (this.parts.size() != parts.parts.size())
            {
                this.parts.remove(this.parts.size() - 1);
            }
        }
        else if (this.parts.size() < parts.parts.size())
        {
            for (int i = this.parts.size(); i < parts.parts.size(); i++)
            {
                this.parts.add(parts.parts.get(i).copy());
            }
        }

        for (int i = 0, c = this.parts.size(); i < c; i++)
        {
            BodyPart thisPart = this.parts.get(i);
            BodyPart otherPart = parts.parts.get(i);

            thisPart.tween(otherPart, duration, interpolation);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof BodyPartManager)
        {
            return Objects.equals(this.parts, ((BodyPartManager) obj).parts);
        }

        return false;
    }

    @Override
    public void toData(MapType data)
    {
        ListType parts = new ListType();

        for (BodyPart bodypart : this.parts)
        {
            parts.add(bodypart.toData());
        }

        if (!parts.isEmpty())
        {
            data.put("parts", parts);
        }
    }

    @Override
    public void fromData(MapType data)
    {
        ListType parts = data.getList("parts");

        for (BodyPart part : this.parts)
        {
            part.setManager(null);
        }

        this.parts.clear();

        for (BaseType partData : parts)
        {
            if (!partData.isMap())
            {
                continue;
            }

            BodyPart bodypart = new BodyPart();

            bodypart.fromData(partData.asMap());
            this.addBodyPart(bodypart);
        }
    }
}