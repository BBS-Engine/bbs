package mchorse.bbs.film.screenplay;

import mchorse.bbs.settings.values.ValueList;

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
        this.list.add(action);
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

    @Override
    protected ScreenplayAction create(String id)
    {
        return new ScreenplayAction(id);
    }
}