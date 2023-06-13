package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.list.UIStringList;
import org.lwjgl.glfw.GLFW;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class UIListOverlayPanel extends UIMessageOverlayPanel
{
    public Consumer<List<String>> callback;

    public UIButton confirm;
    public UIStringList list;

    public UIListOverlayPanel(IKey title, IKey message, Consumer<String> callback)
    {
        super(title, message);

        this.callback = (list) ->
        {
            if (callback != null)
            {
                callback.accept(this.list.getIndex() == 0 ? "" : this.list.getCurrentFirst());
            }
        };

        this.confirm = new UIButton(UIKeys.OK, (b) -> this.send());
        this.confirm.relative(this.content).x(0.5F).y(1F, -10).w(100).anchor(0.5F, 1F);

        this.list = new UIStringList(null);
        this.list.relative(this.message).x(0.5F).y(1F, 10).w(100).hTo(this.confirm.area).anchorX(0.5F);
        this.list.add(UIKeys.NONE.get());
        this.list.setIndex(0);

        this.content.add(this.confirm, this.list);
    }

    public UIListOverlayPanel callback(Consumer<List<String>> callback)
    {
        this.callback = callback;

        return this;
    }

    public UIListOverlayPanel setValue(String value)
    {
        if (value.isEmpty())
        {
            this.list.setIndex(0);
        }
        else
        {
            this.list.setCurrent(value);
        }

        return this;
    }

    public UIListOverlayPanel addValues(Collection<String> values)
    {
        this.list.add(values);

        return this;
    }

    public void send()
    {
        if (this.list.isDeselected())
        {
            return;
        }

        this.close();

        if (this.callback != null)
        {
            this.callback.accept(this.list.getCurrent());
        }
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ENTER))
        {
            this.send();

            return true;
        }
        else if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.removeFromParent();

            return true;
        }

        return super.subKeyPressed(context);
    }
}