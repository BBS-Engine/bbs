package mchorse.bbs.ui.forms.editors.utils;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UICanvasEditor;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;

public class UICropEditor extends UICanvasEditor
{
    public UITrackpad left;
    public UITrackpad right;
    public UITrackpad top;
    public UITrackpad bottom;

    private Link texture;
    private Vector4f crop;

    private int handle = -1;

    private float originalX;
    private float originalY;

    public UICropEditor()
    {
        super();

        this.left = new UITrackpad((value) -> this.crop.x = value.floatValue());
        this.left.tooltip(UIKeys.FORMS_CROP_LEFT);
        this.right = new UITrackpad((value) -> this.crop.z = value.floatValue());
        this.right.tooltip(UIKeys.FORMS_CROP_RIGHT);
        this.top = new UITrackpad((value) -> this.crop.y = value.floatValue());
        this.top.tooltip(UIKeys.FORMS_CROP_TOP);
        this.bottom = new UITrackpad((value) -> this.crop.w = value.floatValue());
        this.bottom.tooltip(UIKeys.FORMS_CROP_BOTTOM);

        this.editor.add(this.left, this.right, this.top, this.bottom);
    }

    public void fill(Link texture, Vector4f crop)
    {
        this.texture = texture;
        this.crop = crop;

        this.fillFields();
    }

    private void fillFields()
    {
        this.left.setValue(this.crop.x);
        this.right.setValue(this.crop.z);
        this.top.setValue(this.crop.y);
        this.bottom.setValue(this.crop.w);
    }

    @Override
    protected void startDragging(UIContext context)
    {
        super.startDragging(context);

        this.handle = -1;

        Map<Integer, Vector2f> areas = new HashMap<>();

        Area area = new Area(this.calculateCropArea());

        areas.put(0, new Vector2f(area.x, area.y));
        areas.put(1, new Vector2f(area.ex(), area.y));
        areas.put(2, new Vector2f(area.ex(), area.ey()));
        areas.put(3, new Vector2f(area.x, area.ey()));

        for (Map.Entry<Integer, Vector2f> entry : areas.entrySet())
        {
            float dx = entry.getValue().x - context.mouseX;
            float dy = entry.getValue().y - context.mouseY;
            float d = dx * dx + dy * dy;

            if (d < 25)
            {
                this.handle = entry.getKey();

                if (this.handle == 0)
                {
                    this.originalX = this.crop.x;
                    this.originalY = this.crop.y;
                }
                else if (this.handle == 1)
                {
                    this.originalX = this.crop.z;
                    this.originalY = this.crop.y;
                }
                else if (this.handle == 2)
                {
                    this.originalX = this.crop.z;
                    this.originalY = this.crop.w;
                }
                else if (this.handle == 3)
                {
                    this.originalX = this.crop.x;
                    this.originalY = this.crop.w;
                }
            }
        }
    }

    @Override
    protected void dragging(UIContext context)
    {
        super.dragging(context);

        if (this.dragging && this.mouse == 0 && this.handle >= 0)
        {
            float dx = (context.mouseX - this.lastX) / (float) this.scaleX.getZoom();
            float dy = (context.mouseY - this.lastY) / (float) this.scaleY.getZoom();

            if (Window.isShiftPressed()) dx = 0;
            if (Window.isCtrlPressed()) dy = 0;

            if (this.handle == 0)
            {
                this.crop.x = Math.round(this.originalX + dx);
                this.crop.y = Math.round(this.originalY + dy);
            }
            else if (this.handle == 1)
            {
                this.crop.z = Math.round(this.originalX - dx);
                this.crop.y = Math.round(this.originalY + dy);
            }
            else if (this.handle == 2)
            {
                this.crop.z = Math.round(this.originalX - dx);
                this.crop.w = Math.round(this.originalY - dy);
            }
            else if (this.handle == 3)
            {
                this.crop.x = Math.round(this.originalX + dx);
                this.crop.w = Math.round(this.originalY - dy);
            }

            this.fillFields();
        }
    }

    @Override
    protected void renderCanvasFrame(UIContext context)
    {
        Area area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

        context.batcher.fullTexturedBox(context.render.getTextures().getTexture(this.texture), area.x, area.y, area.w, area.h);
    }

    @Override
    protected void renderForeground(UIContext context)
    {
        Area area = this.calculateCropArea();

        context.batcher.normalizedBox(area.x, area.y, area.ex(), area.ey(), Colors.setA(Colors.ACTIVE, 0.25F));
        context.batcher.normalizedBox(area.x, area.y, area.ex(), area.ey(), Colors.setA(Colors.WHITE, 0.25F));

        this.drawHandle(context, 0, new Vector2f(area.x, area.y));
        this.drawHandle(context, 1, new Vector2f(area.ex(), area.y));
        this.drawHandle(context, 2, new Vector2f(area.ex(), area.ey()));
        this.drawHandle(context, 3, new Vector2f(area.x, area.ey()));
    }

    private void drawHandle(UIContext context, int handle, Vector2f position)
    {
        int x = (int) position.x;
        int y = (int) position.y;
        int color = Colors.WHITE;

        if (this.handle == handle)
        {
            color = Colors.setA(Colors.ACTIVE, 1F);
        }

        context.batcher.box(x - 3, y - 3, x + 3, y + 3, color);
        context.batcher.box(x - 2, y - 2, x + 2, y + 2, Colors.A100);
    }

    private Area calculateCropArea()
    {
        return this.calculate(
            (int) (-this.w / 2 + this.crop.x),
            (int) (-this.h / 2 + this.crop.y),
            (int) (this.w / 2 - this.crop.z),
            (int) (this.h / 2 - this.crop.w)
        );
    }
}