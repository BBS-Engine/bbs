package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.UITransform;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Timer;
import mchorse.bbs.utils.Transform;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class UIPropTransform extends UITransform
{
    private static final double[] CURSOR_X = new double[1];
    private static final double[] CURSOR_Y = new double[1];

    private Transform transform;
    private Consumer<Transform> callback;

    private boolean editing;
    private int mode;
    private Axis axis = Axis.X;
    private int lastX;
    private Vector3f cache = new Vector3f();
    private Timer checker = new Timer(30);

    public UIPropTransform()
    {}

    public UIPropTransform(Consumer<Transform> callback)
    {
        this.callback = callback;
    }

    public UIPropTransform enableHotkeys()
    {
        IKey category = UIKeys.TRANSFORMS_KEYS_CATEGORY;
        Supplier<Boolean> active = () -> this.editing;

        this.keys().register(Keys.TRANSFORMATIONS_TRANSLATE, () -> this.enableMode(0)).category(category);
        this.keys().register(Keys.TRANSFORMATIONS_SCALE, () -> this.enableMode(1)).category(category);
        this.keys().register(Keys.TRANSFORMATIONS_ROTATE, () -> this.enableMode(2)).category(category);
        this.keys().register(Keys.TRANSFORMATIONS_X, () -> this.axis = Axis.X).active(active).category(category);
        this.keys().register(Keys.TRANSFORMATIONS_Y, () -> this.axis = Axis.Y).active(active).category(category);
        this.keys().register(Keys.TRANSFORMATIONS_Z, () -> this.axis = Axis.Z).active(active).category(category);

        return this;
    }

    public Transform getTransform()
    {
        return this.transform;
    }

    public void setTransform(Transform transform)
    {
        this.transform = transform;

        this.fillT(transform.translate.x, transform.translate.y, transform.translate.z);
        this.fillS(transform.scale.x, transform.scale.y, transform.scale.z);
        this.fillR(MathUtils.toDeg(transform.rotate.x), MathUtils.toDeg(transform.rotate.y), MathUtils.toDeg(transform.rotate.z));
        this.fillR2(MathUtils.toDeg(transform.rotate2.x), MathUtils.toDeg(transform.rotate2.y), MathUtils.toDeg(transform.rotate2.z));
    }

    private void enableMode(int mode)
    {
        this.editing = true;
        this.mode = mode;

        UIContext context = this.getContext();

        this.lastX = context.mouseX;
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

    @Override
    public void setR2(double x, double y, double z)
    {
        this.transform.rotate2.set(MathUtils.toRad((float) x), MathUtils.toRad((float) y), MathUtils.toRad((float) z));
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
        }

        return super.subKeyPressed(context);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editing && this.checker.isTime())
        {
            /* UIContext.mouseX can't be used because when cursor is outside of window
             * its position stops being updated. That's why it has to be queried manually
             * through GLFW...
             *
             * It gets updated outside the window only when one of mouse buttons is
             * being held! */
            GLFW.glfwGetCursorPos(Window.getWindow(), CURSOR_X, CURSOR_Y);

            double rawX = CURSOR_X[0];
            double fx = Math.ceil(Window.width / (double) context.menu.width);
            int border = 5;
            int borderPadding = border + 1;

            if (rawX <= border)
            {
                Window.moveCursor(Window.width - borderPadding, BBS.getEngine().mouse.y);

                this.lastX = context.menu.width - (int) (borderPadding / fx);
                this.checker.mark();
            }
            else if (rawX >= Window.width - border)
            {
                Window.moveCursor(borderPadding, BBS.getEngine().mouse.y);

                this.lastX = (int) (borderPadding / fx);
                this.checker.mark();
            }
            else
            {
                int dx = context.mouseX - this.lastX;
                Vector3f vector = this.getValue();
                boolean all = Window.isAltPressed();

                float factor = this.mode == 0 ? 0.05F : (this.mode == 1 ? 0.01F : MathUtils.toRad(0.5F));

                if (this.axis == Axis.X || all) vector.x += factor * dx;
                if (this.axis == Axis.Y || all) vector.y += factor * dx;
                if (this.axis == Axis.Z || all) vector.z += factor * dx;

                this.setTransform(this.transform);

                this.lastX = context.mouseX;
            }
        }

        super.render(context);

        if (this.editing)
        {
            String label = UIKeys.TRANSFORMS_EDITING.get();
            int x = this.area.mx(context.font.getWidth(label));
            int y = this.area.my(context.font.getHeight());

            context.batcher.textCard(context.font, label, x, y, Colors.WHITE, BBSSettings.primaryColor(Colors.A50));
        }
    }
}