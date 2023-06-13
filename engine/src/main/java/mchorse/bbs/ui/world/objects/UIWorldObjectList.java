package mchorse.bbs.ui.world.objects;

import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.world.objects.WorldObject;

import java.util.List;
import java.util.function.Consumer;

public class UIWorldObjectList extends UIList<WorldObject>
{
    public UIWorldObjectList(Consumer<List<WorldObject>> callback)
    {
        super(callback);

        this.scroll.scrollItemSize = 16;
    }
}