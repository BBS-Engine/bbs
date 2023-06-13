package mchorse.bbs.ui.game.states;

import mchorse.bbs.game.states.States;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.utils.colors.Colors;

import java.util.Comparator;

public class UIStatesEditor extends UIScrollView
{
    private States states;

    public UIStatesEditor()
    {
        super();

        this.column().vertical().stretch().scroll().padding(10);
    }

    public States get()
    {
        return this.states;
    }

    public UIStatesEditor set(States states)
    {
        this.states = states;

        this.removeAll();

        if (states != null)
        {
            for (String key : states.values.keySet())
            {
                this.add(new UIState(key, states));
            }
        }

        this.sortElements();
        this.resize();

        return this;
    }

    private void sortElements()
    {
        this.getChildren().sort(Comparator.comparing(a -> ((UIState) a).getKey()));
    }

    public void addNew()
    {
        if (this.states == null)
        {
            return;
        }

        int index = this.states.values.size() + 1;
        String key = "state_" + index;

        while (this.states.values.containsKey(key))
        {
            index += 1;
            key = "state_" + index;
        }

        this.states.values.put(key, 0);
        this.add(new UIState(key, this.states));

        this.sortElements();

        this.getParentContainer().resize();
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.states != null && this.states.values.isEmpty())
        {
            int w = this.area.w / 2;
            int x = this.area.mx(w);

            context.draw.wallText(context.font, UIKeys.STATES_EMPTY.get(), x, this.area.my(), Colors.WHITE, w, 12, 0.5F, 0.5F);
        }
    }
}