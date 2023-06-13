package mchorse.bbs.ui.world.entities;

import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.objects.WorldObject;
import org.joml.Vector3d;

import java.util.List;
import java.util.function.Consumer;

public class UIEntityList extends UIList<Entity>
{
    public UIEntityList(Consumer<List<Entity>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected String elementToString(int i, Entity element)
    {
        Vector3d position = element.basic.position;

        return "(" + (int) position.x + ", " + (int) position.y + ", " + (int) position.z + ") " + element.id;
    }
}