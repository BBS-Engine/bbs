package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.UITransform;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class UIPropTransform extends UITransform
{
    public static final IKey EDITING_LABEL = IKey.lazy("Editing...");

    private Transform transform;
    private Consumer<Transform> callback;

    private boolean editing;
    private int mode;
    private Axis axis = Axis.X;
    private int lastX;
    private int lastY;
    private Vector3f cache = new Vector3f();

    public UIPropTransform()
    {}

    public UIPropTransform(Consumer<Transform> callback)
    {
        this.callback = callback;
    }

    public UIPropTransform enableHotkeys()
    {
        IKey category = IKey.lazy("Transformations");

        this.keys().register(new KeyCombo(IKey.lazy("Translate"), GLFW.GLFW_KEY_G), () -> this.enableMode(0)).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Scale"), GLFW.GLFW_KEY_S), () -> this.enableMode(1)).category(category);
        this.keys().register(new KeyCombo(IKey.lazy("Rotate"), GLFW.GLFW_KEY_R), () -> this.enableMode(2)).category(category);

        return this;
    }

    public void setTransform(Transform transform)
    {
        this.transform = transform;

        this.fillT(transform.translate.x, transform.translate.y, transform.translate.z);
        this.fillS(transform.scale.x, transform.scale.y, transform.scale.z);
        this.fillR(MathUtils.toDeg(transform.rotate.x), MathUtils.toDeg(transform.rotate.y), MathUtils.toDeg(transform.rotate.z));
    }

    private void enableMode(int mode)
    {
        this.editing = true;
        this.mode = mode;

        UIContext context = this.getContext();

        this.lastX = context.mouseX;
        this.lastY = context.mouseY;
        this.cache.set(this.getValue());
    }

    private Vector3f getValue()
    {
        if (this.mode == 1)
        {
            return this.transform.scale;
        }
        else if (this.mode == 2)
        {
            return this.transform.rotate;
        }

        return this.transform.translate;
    }

    private void disable()
    {
        this.editing = false;
    }

    @Override
    public void setT(double x, double y, double z)
    {
        this.transform.translate.set((float) x, (float) y, (float) z);
        this.submit();
    }

    @Override
    public void setS(double x, double y, double z)
    {
        this.transform.scale.set((float) x, (float) y, (float) z);
        this.submit();
    }

    @Override
    public void setR(double x, double y, double z)
    {
        this.transform.rotate.set(MathUtils.toRad((float) x), MathUtils.toRad((float) y), MathUtils.toRad((float) z));
        this.submit();
    }

    private void submit()
    {
        if (this.callback != null)
        {
            this.callback.accept(this.transform);
        }
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        if (this.editing)
        {
            if (context.mouseButton == 0)
            {
                this.disable();
                this.submit();
                this.setTransform(this.transform);

                return true;
            }
            else if (context.mouseButton == 1)
            {
                this.disable();
                this.getValue().set(this.cache);
                this.submit();
                this.setTransform(this.transform);

                return true;
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected boolean subKeyPressed(UIContext context)
    {
        if (this.editing)
        {
            if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
            {
                this.disable();
                this.getValue().set(this.cache);
                this.submit();
                this.setTransform(this.transform);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_ENTER))
            {
                this.disable();
                this.submit();
                this.setTransform(this.transform);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_X))
            {
                this.axis = Axis.X;
                this.getValue().set(this.cache);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_Y))
            {
                this.axis = Axis.Y;
                this.getValue().set(this.cache);

                return true;
            }
            else if (context.isPressed(GLFW.GLFW_KEY_Z))
            {
                this.axis = Axis.Z;
                this.getValue().set(this.cache);

                return true;
            }
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editing)
        {
            int dx = context.mouseX - this.lastX;
            int dy = context.mouseY - this.lastY;
            Vector3f vector = this.getValue();
            boolean all = Window.isAltPressed();

            float factor = this.mode == 0 ? 0.05F : (this.mode == 1 ? 0.01F : MathUtils.toRad(0.5F));

            if (this.axis == Axis.X || all) vector.x += factor * dx;
            if (this.axis == Axis.Y || all) vector.y += factor * dx;
            if (this.axis == Axis.Z || all) vector.z += factor * dx;

            this.setTransform(this.transform);

            this.lastX = context.mouseX;
            this.lastY = context.mouseY;
        }

        super.render(context);

        if (this.editing)
        {
            String label = EDITING_LABEL.get();
            int x = this.area.mx(context.font.getWidth(label));
            int y = this.area.my(context.font.getHeight());

            context.batcher.textCard(context.font, label, x, y, Colors.WHITE, BBSSettings.primaryColor(Colors.A50));
        }
    }
}