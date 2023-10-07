package mchorse.bbs.film.screenplay;

import mchorse.bbs.settings.values.ValueList;
import mchorse.bbs.utils.math.MathUtils;

public class Screenplay extends ValueList<ScreenplayAction>
{
    public Screenplay(String id)
    {
        super(id);
    }

    public ScreenplayAction addAction()
    {
        ScreenplayAction action = new ScreenplayAction("");

        this.preNotifyParent();
        this.add(action);
        this.sync();
        this.postNotifyParent();

        return action;
    }

    public void removeAction(ScreenplayAction action)
    {
        int index = this.list.indexOf(action);

        if (index < 0)
        {
            return;
        }

        this.preNotifyParent();
        this.list.remove(index);
        this.sync();
        this.postNotifyParent();
    }

    public boolean moveAction(ScreenplayAction action, int direction)
    {
        int index = this.list.indexOf(action);

        if (index == -1)
        {
            return false;
        }

        int newIndex = MathUtils.clamp(index + direction, 0, this.list.size() - 1);

        if (newIndex != index)
        {
            this.preNotifyParent();
            this.list.add(index, this.list.remove(newIndex));
            this.sync();
            this.postNotifyParent();
        }

        return newIndex != index;
    }

    @Override
    protected ScreenplayAction create(String id)
    {
        return new ScreenplayAction(id);
    }
}