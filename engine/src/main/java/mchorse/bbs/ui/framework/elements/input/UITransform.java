package mchorse.bbs.ui.framework.elements.input;

import mchorse.bbs.data.types.ListType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;

/**
 * Transformation editor GUI
 * 
 * Must be exactly 190 by 70 (with extra 12 on top for labels)
 */
public abstract class UITransform extends UIElement
{
    public UITrackpad tx;
    public UITrackpad ty;
    public UITrackpad tz;
    public UITrackpad sx;
    public UITrackpad sy;
    public UITrackpad sz;
    public UITrackpad rx;
    public UITrackpad ry;
    public UITrackpad rz;

    protected boolean vertical;

    private boolean renderLabels = true;

    public UITransform()
    {
        super();

        this.tx = new UITrackpad((value) -> this.internalSetT(value, this.ty.value, this.tz.value)).block();
        this.tx.tooltip(UIKeys.TRANSFORMS_X);
        this.tx.textbox.setColor(Colors.RED);
        this.ty = new UITrackpad((value) -> this.internalSetT(this.tx.value, value, this.tz.value)).block();
        this.ty.tooltip(UIKeys.TRANSFORMS_Y);
        this.ty.textbox.setColor(Colors.GREEN);
        this.tz = new UITrackpad((value) -> this.internalSetT(this.tx.value, this.ty.value, value)).block();
        this.tz.tooltip(UIKeys.TRANSFORMS_Z);
        this.tz.textbox.setColor(Colors.BLUE);

        this.sx = new UITrackpad((value) ->
        {
            this.internalSetS(value, this.sy.value, this.sz.value);
            this.syncScale(value);
        });
        this.sx.tooltip(UIKeys.TRANSFORMS_X);
        this.sx.textbox.setColor(Colors.RED);
        this.sy = new UITrackpad((value) ->
        {
            this.internalSetS(this.sx.value, value, this.sz.value);
            this.syncScale(value);
        });
        this.sy.tooltip(UIKeys.TRANSFORMS_Y);
        this.sy.textbox.setColor(Colors.GREEN);
        this.sz = new UITrackpad((value) ->
        {
            this.internalSetS(this.sx.value, this.sy.value, value);
            this.syncScale(value);
        });
        this.sz.tooltip(UIKeys.TRANSFORMS_Z);
        this.sz.textbox.setColor(Colors.BLUE);

        this.rx = new UITrackpad((value) -> this.internalSetR(value, this.ry.value, this.rz.value)).degrees();
        this.rx.tooltip(UIKeys.TRANSFORMS_X);
        this.rx.textbox.setColor(Colors.RED);
        this.ry = new UITrackpad((value) -> this.internalSetR(this.rx.value, value, this.rz.value)).degrees();
        this.ry.tooltip(UIKeys.TRANSFORMS_Y);
        this.ry.textbox.setColor(Colors.GREEN);
        this.rz = new UITrackpad((value) -> this.internalSetR(this.rx.value, this.ry.value, value)).degrees();
        this.rz.tooltip(UIKeys.TRANSFORMS_Z);
        this.rz.textbox.setColor(Colors.BLUE);

        UIElement first = new UIElement();
        UIElement second = new UIElement();
        UIElement third = new UIElement();

        first.relative(this).w(1F).h(20).row().height(20);
        first.add(this.tx, sx, rx);

        second.relative(this).y(0.5F, -10).w(1F).h(20).row().height(20);
        second.add(this.ty, sy, ry);

        third.relative(this).y(1F, -20).w(1F).h(20).row().height(20);
        third.add(this.tz, sz, rz);

        this.add(first, second, third);

        this.context((menu) ->
        {
            ListType transforms = Window.getClipboardList();

            if (transforms != null && transforms.size() < 9)
            {
                transforms = null;
            }

            menu.action(Icons.COPY, UIKeys.TRANSFORMS_CONTEXT_COPY, this::copyTransformations);

            if (transforms != null)
            {
                final ListType innerList = transforms;

                menu.action(Icons.PASTE, UIKeys.TRANSFORMS_CONTEXT_PASTE, () -> this.pasteAll(innerList));
                menu.action(Icons.ALL_DIRECTIONS, UIKeys.TRANSFORMS_CONTEXT_PASTE_TRANSLATION, () -> this.pasteTranslation(innerList));
                menu.action(Icons.MAXIMIZE, UIKeys.TRANSFORMS_CONTEXT_PASTE_SCALE, () -> this.pasteScale(innerList));
                menu.action(Icons.REFRESH, UIKeys.TRANSFORMS_CONTEXT_PASTE_ROTATION, () -> this.pasteRotation(innerList));
            }

            menu.action(Icons.CLOSE, UIKeys.TRANSFORMS_CONTEXT_RESET, this::reset);
        });

        this.wh(190, 70);
    }

    public UITransform noLabels()
    {
        this.renderLabels = false;

        return this;
    }

    public UITransform vertical()
    {
        this.vertical = true;

        for (UITrackpad trackpad : this.getChildren(UITrackpad.class))
        {
            trackpad.removeFromParent();
        }

        this.removeAll();
        this.resetFlex().w(1F).h(120).column().stretch().vertical();

        this.add(UI.label(UIKeys.TRANSFORMS_TRANSLATE), this.tx, this.ty, this.tz);
        this.add(UI.label(UIKeys.TRANSFORMS_SCALE).marginTop(4), this.sx, this.sy, this.sz);
        this.add(UI.label(UIKeys.TRANSFORMS_ROTATE).marginTop(4), this.rx, this.ry, this.rz);

        return this;
    }

    public UITransform verticalCompact()
    {
        this.vertical = true;

        for (UITrackpad trackpad : this.getChildren(UITrackpad.class))
        {
            trackpad.removeFromParent();
        }

        this.removeAll();
        this.resetFlex().w(1F).h(120).column().stretch().vertical();

        this.add(UI.label(UIKeys.TRANSFORMS_TRANSLATE), UI.row(this.tx, this.ty, this.tz));
        this.add(UI.label(UIKeys.TRANSFORMS_SCALE).marginTop(4), UI.row(this.sx, this.sy, this.sz));
        this.add(UI.label(UIKeys.TRANSFORMS_ROTATE).marginTop(4), UI.row(this.rx, this.ry, this.rz));

        return this;
    }

    private void syncScale(double value)
    {
        if (Window.isKeyPressed(GLFW.GLFW_KEY_SPACE))
        {
            this.fillS(value, value, value);
            this.internalSetS(value, value, value);
        }
    }

    public void fillSetT(double x, double y, double z)
    {
        this.fillT(x, y, z);
        this.setT(x, y, z);
    }

    public void fillSetS(double x, double y, double z)
    {
        this.fillS(x, y, z);
        this.setS(x, y, z);
    }

    public void fillSetR(double x, double y, double z)
    {
        this.fillR(x, y, z);
        this.setR(x, y, z);
    }

    public void fillT(double x, double y, double z)
    {
        this.tx.setValue(x);
        this.ty.setValue(y);
        this.tz.setValue(z);
    }

    public void fillS(double x, double y, double z)
    {
        this.sx.setValue(x);
        this.sy.setValue(y);
        this.sz.setValue(z);
    }

    public void fillR(double x, double y, double z)
    {
        this.rx.setValue(x);
        this.ry.setValue(y);
        this.rz.setValue(z);
    }
    
    private void internalSetT(double x, double y, double z)
    {
        try
        {
            this.setT(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void internalSetS(double x, double y, double z)
    {
        try
        {
            this.setS(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void internalSetR(double x, double y, double z)
    {
        try
        {
            this.setR(x, y, z);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public abstract void setT(double x, double y, double z);

    public abstract void setS(double x, double y, double z);

    public abstract void setR(double x, double y, double z);

    private void copyTransformations()
    {
        ListType list = new ListType();

        list.addDouble(this.tx.value);
        list.addDouble(this.ty.value);
        list.addDouble(this.tz.value);
        list.addDouble(this.sx.value);
        list.addDouble(this.sy.value);
        list.addDouble(this.sz.value);
        list.addDouble(this.rx.value);
        list.addDouble(this.ry.value);
        list.addDouble(this.rz.value);

        Window.setClipboard(list);
    }

    public void pasteAll(ListType list)
    {
        this.pasteTranslation(list);
        this.pasteScale(list);
        this.pasteRotation(list);
    }

    public void pasteTranslation(ListType list)
    {
        Vector3d translation = this.getVector(list, 0);

        this.tx.setValue(translation.x);
        this.ty.setValue(translation.y);
        this.tz.setValueAndNotify(translation.z);
    }

    public void pasteScale(ListType list)
    {
        Vector3d scale = this.getVector(list, 3);

        this.sz.setValue(scale.z);
        this.sy.setValue(scale.y);
        this.sx.setValueAndNotify(scale.x);
    }

    public void pasteRotation(ListType list)
    {
        Vector3d rotation = this.getVector(list, 6);

        this.rx.setValue(rotation.x);
        this.ry.setValue(rotation.y);
        this.rz.setValueAndNotify(rotation.z);
    }

    private Vector3d getVector(ListType list, int offset)
    {
        Vector3d result = new Vector3d();

        if (list.get(offset).isNumeric() && list.get(offset + 1).isNumeric() && list.get(offset + 2).isNumeric())
        {
            result.x = list.get(offset).asNumeric().doubleValue();
            result.y = list.get(offset + 1).asNumeric().doubleValue();
            result.z = list.get(offset + 2).asNumeric().doubleValue();
        }

        return result;
    }

    protected void reset()
    {
        this.fillSetT(0, 0, 0);
        this.fillSetS(1, 1, 1);
        this.fillSetR(0, 0, 0);
    }

    @Override
    public void render(UIContext context)
    {
        if (!this.vertical && this.renderLabels)
        {
            context.batcher.textShadow(UIKeys.TRANSFORMS_TRANSLATE.get(), this.tx.area.x, this.tx.area.y - 12);
            context.batcher.textShadow(UIKeys.TRANSFORMS_SCALE.get(), this.sx.area.x, this.sx.area.y - 12);
            context.batcher.textShadow(UIKeys.TRANSFORMS_ROTATE.get(), this.rx.area.x, this.rx.area.y - 12);
        }

        super.render(context);
    }
}