package mchorse.bbs.ui.game.states;

import mchorse.bbs.game.states.States;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIState extends UIElement
{
    public UITextbox id;
    public UIIcon convert;
    public UIElement value;
    public UIIcon remove;

    private String key;
    private States states;

    public UIState(String key, States states)
    {
        super();

        this.key = key;
        this.states = states;

        this.id = new UITextbox(1000, this::rename);
        this.id.w(120);
        this.id.setText(key);
        this.convert = new UIIcon(Icons.REFRESH, this::convert);
        this.remove = new UIIcon(Icons.REMOVE, this::removeState);

        this.row(0).preferred(2);
        this.updateValue();
    }

    public String getKey()
    {
        return this.key;
    }

    private void rename(String key)
    {
        if (this.states.values.containsKey(key) || key.isEmpty())
        {
            this.id.setColor(Colors.NEGATIVE);

            return;
        }

        this.id.setColor(Colors.WHITE);

        Object value = this.states.values.remove(this.key);

        this.states.values.put(key, value);
        this.key = key;
    }

    private void convert(UIIcon element)
    {
        Object object = this.states.values.get(this.key);

        if (object instanceof String)
        {
            this.states.values.put(this.key, 0);
        }
        else
        {
            this.states.values.put(this.key, "");
        }

        this.updateValue();
    }

    private void removeState(UIIcon element)
    {
        this.states.values.remove(this.key);

        UIElement parent = this.getParentContainer();

        this.removeFromParent();
        parent.resize();
    }

    private void updateValue()
    {
        Object object = this.states.values.get(this.key);

        if (object instanceof String)
        {
            UITextbox element = new UITextbox(10000, this::updateString);

            element.setText((String) object);
            this.value = element;
        }
        else
        {
            UITrackpad element = new UITrackpad(this::updateNumber);

            element.setValue(((Number) object).doubleValue());
            this.value = element;
        }

        this.removeAll();
        this.add(this.id, this.convert, this.value, this.remove);

        if (this.hasParent())
        {
            this.getParentContainer().resize();
        }
    }

    private void updateString(String s)
    {
        this.states.values.put(this.key, s);
    }

    private void updateNumber(double v)
    {
        this.states.values.put(this.key, v);
    }
}