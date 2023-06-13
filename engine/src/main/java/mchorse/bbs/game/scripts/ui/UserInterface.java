package mchorse.bbs.game.scripts.ui;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.utils.UIRootComponent;
import mchorse.bbs.game.utils.manager.data.AbstractData;

public class UserInterface extends AbstractData
{
    public UIRootComponent root = new UIRootComponent();
    public boolean background;
    public boolean closable = true;

    /* Handler settings */
    public String script = "";
    public String function = "";

    @Override
    public void toData(MapType data)
    {
        data.put("root", this.root.toData());
        data.putBool("background", this.background);
        data.putBool("closeable", this.closable);
        data.putString("script", this.script);
        data.putString("function", this.function);
    }

    @Override
    public void fromData(MapType data)
    {
        this.root.fromData(data.getMap("root"));

        if (data.has("background"))
        {
            this.background = data.getBool("background");
        }

        if (data.has("closeable"))
        {
            this.closable = data.getBool("closeable");
        }

        this.script = data.getString("script");
        this.function = data.getString("function");
    }

    public UIComponent get(String id)
    {
        return this.findComponent(this.root, id);
    }

    private UIComponent findComponent(UIComponent component, String id)
    {
        for (UIComponent child : component.getChildComponents())
        {
            if (child.id.equals(id))
            {
                return child;
            }

            UIComponent result = this.findComponent(child, id);

            if (result != null)
            {
                return result;
            }
        }

        return null;
    }
}