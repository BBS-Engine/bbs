package mchorse.bbs.ui.utils.renderers;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIDraw;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyCodes;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mouse renderer
 * 
 * This class is responsible for rendering a mouse pointer on the screen 
 */
public class InputRenderer
{
    private static boolean disabledForFrame = false;

    private List<PressedKey> pressedKeys = new ArrayList<PressedKey>();
    private float lastQX = 1;
    private float lastQY = 0;
    private float currentQX = 0;
    private float currentQY = 1;
    private long lastDWheelTime;
    private int lastDWheelScroll;

    public static void disable()
    {
        disabledForFrame = true;
    }

    /* Shift -6 and -8 to get it into the center */
    public static void renderMouseButtons(UIDraw draw, int x, int y, int scroll, boolean left, boolean right, boolean middle, boolean isScrolling)
    {
        Icons.MOUSE_BODY.render(draw, x - 1, y);

        if (left)
        {
            Icons.MOUSE_LMB.render(draw, x, y + 1);
        }

        if (right)
        {
            Icons.MOUSE_RMB.render(draw, x + 6, y + 1);
        }

        if (middle || isScrolling)
        {
            int offset = 0;

            y += 1;

            if (isScrolling)
            {
                offset = scroll < 0 ? 1 : -1;
            }

            draw.box(x + 4, y, x + 8, y + 6, 0x20000000);
            draw.box(x + 5, y + 1 + offset, x + 7, y + 5 + offset, 0xff444444);
            draw.box(x + 5, y + 4 + offset, x + 7, y + 5 + offset, 0xff333333);
        }
    }

    public static void renderMouseWheel(UIDraw draw, int x, int y, int scroll, long current)
    {
        int color = BBSSettings.primaryColor.get();

        draw.dropShadow(x, y, x + 4, y + 16, 2, Colors.A50 | color, color);
        draw.box(x, y, x + 4, y + 16, 0xff111111);
        draw.box(x + 1, y, x + 3, y + 15, 0xff2a2a2a);

        int offset = (int) ((current % 1000 / 50) % 4);

        if (scroll >= 0)
        {
            offset = 3 - offset;
        }

        for (int i = 0; i < 4; i++)
        {
            draw.box(x, y + offset, x + 4, y + offset + 1, 0x88555555);

            y += 4;
        }
    }

    public void render(UIBaseMenu menu, int mouseX, int mouseY)
    {
        if (disabledForFrame)
        {
            disabledForFrame = false;

            return;
        }

        this.renderMouse(menu.context.draw, mouseX, mouseY);

        if (BBSSettings.enableKeystrokeRendering.get())
        {
            this.renderKeys(menu, mouseX, mouseY);
        }
    }

    /**
     * Draw mouse cursor
     */
    private void renderMouse(UIDraw draw, int x, int y)
    {
        if (BBSSettings.enableCursorRendering.get())
        {
            Icons.CURSOR.render(draw, x, y);
        }

        if (BBSSettings.enableMouseButtonRendering.get())
        {
            boolean left = Window.isMouseButtonPressed(0);
            boolean right = Window.isMouseButtonPressed(1);
            boolean middle = Window.isMouseButtonPressed(2);

            int scroll = BBS.getEngine().mouse.lastScrollY;
            long current = System.currentTimeMillis();
            boolean isScrolling = scroll != 0 || current - this.lastDWheelTime < 500;

            if (scroll != 0)
            {
                this.lastDWheelTime = current;
                this.lastDWheelScroll = scroll;
            }

            if (scroll == 0 && isScrolling)
            {
                scroll = this.lastDWheelScroll;
            }

            x += 16;
            y += 2;

            if (left || right || middle || isScrolling)
            {
                renderMouseButtons(draw, x, y, scroll, left, right, middle, isScrolling);
            }

            if (isScrolling)
            {
                x += 16;

                renderMouseWheel(draw, x, y, scroll, current);
            }
        }
    }

    /**
     * Render pressed key strokes
     */
    private void renderKeys(UIBaseMenu menu, int mouseX, int mouseY)
    {
        float lqx = Math.round(mouseX / (float) menu.width);
        float lqy = Math.round(mouseY / (float) menu.height);
        int mode = BBSSettings.keystrokeMode.get();

        if (lqx == this.currentQX && lqy == this.currentQY)
        {
            this.currentQX = this.lastQX;
            this.currentQY = this.lastQY;
        }

        if (mode == 1)
        {
            this.currentQX = 0;
            this.currentQY = 1;
        }
        else if (mode == 2)
        {
            this.currentQX = 1;
            this.currentQY = 1;
        }
        else if (mode == 3)
        {
            this.currentQX = 1;
            this.currentQY = 0;
        }
        else if (mode == 4)
        {
            this.currentQX = 0;
            this.currentQY = 0;
        }

        float qx = this.currentQX;
        float qy = this.currentQY;

        int fy = qy > 0.5F ? 1 : -1;
        int offset = BBSSettings.keystrokeOffset.get();
        int mx = offset + (int) (qx * (menu.width - offset * 2));
        int my = offset + (int) (qy * (menu.height - 20 - offset * 2));

        FontRenderer font = menu.context.font;
        Iterator<PressedKey> it = this.pressedKeys.iterator();

        while (it.hasNext())
        {
            PressedKey key = it.next();

            if (key.hasExpired())
            {
                it.remove();
            }
            else
            {
                int x = mx + (qx < 0.5F ? key.x : -(key.x + key.width + 16));
                int y = my + (int) (Interpolation.EXP_INOUT.interpolate(0, 1, key.getFactor()) * 50 * fy);
                int fw = 16 + key.width;

                UIDraw draw = menu.context.draw;

                Icons.KEY_CAP_LEFT.render(draw, x, y);
                Icons.KEY_CAP_REPEATABLE.renderArea(draw, x + 4, y, fw - 8, 20);
                Icons.KEY_CAP_RIGHT.render(draw, x + fw, y, 1F, 0F);
                font.render(draw.context, key.getLabel(), x + 8, y + 5, Colors.A100);
            }
        }

        this.lastQX = lqx;
        this.lastQY = lqy;
    }

    public void keyPressed(UIContext context, int key)
    {
        if (key < 0 || context == null || context.font == null)
        {
            return;
        }

        boolean inputUnfocused = context.activeElement == null;

        if (inputUnfocused)
        {
            PressedKey last = null;
            int offset = -1000;

            for (PressedKey pressed : this.pressedKeys)
            {
                if (pressed.key == key)
                {
                    offset = pressed.increment(context.font);
                }
                else if (offset != -1000)
                {
                    pressed.x += offset;
                }

                last = pressed;
            }

            if (offset != -1000)
            {
                return;
            }

            offset = BBSSettings.keystrokeOffset.get();
            int x = last == null ? 0 : last.x + last.width + 18;
            PressedKey newKey = new PressedKey(key, x);

            newKey.setupName(context.font);

            if (newKey.x + newKey.width + offset > context.menu.width - offset * 2)
            {
                newKey.x = 0;
            }

            this.pressedKeys.add(newKey);
        }
    }

    /**
     * Information about pressed key strokes
     */
    public static class PressedKey
    {
        public static int INDEX = 0;

        public int key;
        public long time;
        public int x;

        public String name;
        public int width;
        public int i;
        public int times = 1;

        public PressedKey(int key, int x)
        {
            this.key = key;
            this.time = System.currentTimeMillis();
            this.x = x;

            this.i = INDEX ++;
        }

        public void setupName(FontRenderer font)
        {
            this.name = KeyCodes.getName(this.key);
            this.width = font.getWidth(this.name) - 1;
        }

        public float getFactor()
        {
            return (System.currentTimeMillis() - this.time - 500) / 1000F;
        }

        public boolean hasExpired()
        {
            if (Window.isKeyPressed(this.key))
            {
                this.time = System.currentTimeMillis();
            }

            return System.currentTimeMillis() - this.time > 1500;
        }

        public String getLabel()
        {
            if (this.times > 1)
            {
                return this.name + " (" + this.times + ")";
            }

            return this.name;
        }

        public int increment(FontRenderer font)
        {
            int lastWidth = this.width;

            this.times ++;
            this.width = font.getWidth(this.getLabel());

            return this.width - lastWidth;
        }
    }
}