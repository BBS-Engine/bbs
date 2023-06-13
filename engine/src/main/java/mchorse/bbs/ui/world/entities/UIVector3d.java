package mchorse.bbs.ui.world.entities;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIVector3d extends UIElement
{
    public UITrackpad x;
    public UITrackpad y;
    public UITrackpad z;

    private Consumer<Vector3d> callback;
    private Vector3d vector = new Vector3d();

    public UIVector3d(Consumer<Vector3d> callback)
    {
        this.callback = callback;

        this.x = new UITrackpad((v) ->
        {
            this.vector.x = v;

            this.syncCallback(v);
        });
        this.x.textbox.setColor(Colors.RED);
        this.y = new UITrackpad((v) ->
        {
            this.vector.y = v;

            this.syncCallback(v);
        });
        this.y.textbox.setColor(Colors.GREEN);
        this.z = new UITrackpad((v) ->
        {
            this.vector.z = v;

            this.syncCallback(v);
        });
        this.z.textbox.setColor(Colors.BLUE);

        this.h(20).row();
        this.add(this.x, this.y, this.z);
    }

    private void syncCallback(double value)
    {
        if (Window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
        {
            this.vector.set(value, value, value);
            this.fill(this.vector);
        }

        if (this.callback != null)
        {
            this.callback.accept(this.vector);
        }
    }

    public void fill(Vector3d vector)
    {
        this.vector.set(vector);

        this.x.setValue(this.vector.x);
        this.y.setValue(this.vector.y);
        this.z.setValue(this.vector.z);
    }

    public void fill(Vector3f vector)
    {
        this.vector.set(vector);

        this.x.setValue(this.vector.x);
        this.y.setValue(this.vector.y);
        this.z.setValue(this.vector.z);
    }
}