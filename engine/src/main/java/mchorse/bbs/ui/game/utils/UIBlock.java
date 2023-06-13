package mchorse.bbs.ui.game.utils;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.icons.Icons;
import org.joml.Vector3i;

import java.util.function.Consumer;

public class UIBlock extends UIElement
{
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;

    public Consumer<Vector3i> callback;

    public UIBlock(Consumer<Vector3i> callback)
    {
        super();

        this.callback = callback;
        this.x = new UITrackpad((v) -> this.callback());
        this.x.integer();
        this.y = new UITrackpad((v) -> this.callback());
        this.y.integer();
        this.z = new UITrackpad((v) -> this.callback());
        this.z.integer();

        this.row();
        this.add(this.x, this.y, this.z);

        this.context((menu) -> menu.action(Icons.MOVE_TO, UIKeys.BLOCK_POS_CONTEXT_PASTE, this::pastePosition));
    }

    private void pastePosition()
    {
        this.set(new Vector3i().set(this.getContext().menu.bridge.get(IBridgeCamera.class).getCamera().position));
        this.callback();
    }

    protected void callback()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.get());
        }
    }

    private Vector3i get()
    {
        return new Vector3i((int) this.x.getValue(), (int) this.y.getValue(), (int) this.z.getValue());
    }

    public void set(Vector3i pos)
    {
        if (pos == null)
        {
            pos = new Vector3i();
        }

        this.x.setValue(pos.x);
        this.y.setValue(pos.y);
        this.z.setValue(pos.z);
    }
}