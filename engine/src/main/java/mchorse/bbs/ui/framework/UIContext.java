package mchorse.bbs.ui.framework;

import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.ui.framework.elements.IFocusedUIElement;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.context.UIContextMenu;
import mchorse.bbs.ui.framework.elements.input.UIKeybinds;
import mchorse.bbs.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs.ui.framework.elements.utils.IViewportStack;
import mchorse.bbs.ui.framework.elements.utils.UIViewportStack;
import mchorse.bbs.ui.framework.tooltips.UITooltip;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.keys.KeyAction;
import mchorse.bbs.utils.math.MathUtils;

import java.util.List;
import java.util.function.Consumer;

public class UIContext implements IViewportStack
{
    public FontRenderer font;
    public UIRenderingContext render;
    public Batcher2D batcher;

    /* GUI elements */
    public final UIBaseMenu menu;
    public final UITooltip tooltip;
    public final UIKeybinds keybinds;
    public IFocusedUIElement activeElement;
    public UIContextMenu contextMenu;

    /* Mouse states */
    public int mouseX;
    public int mouseY;
    public int mouseButton;
    public int mouseWheel;

    /* Keyboard states */
    private int keyCode;
    private int scanCode;
    private KeyAction keyAction = KeyAction.RELEASED;

    private char inputCharacter;

    /* Render states */
    private long tick;

    public UIViewportStack viewportStack = new UIViewportStack();

    public UIContext(UIBaseMenu menu)
    {
        this.menu = menu;
        this.tooltip = new UITooltip();
        this.keybinds = new UIKeybinds();
    }

    public long getTick()
    {
        return this.tick;
    }

    public float getTransition()
    {
        return this.render.getTransition();
    }

    public float getTickTransition()
    {
        return this.tick + this.render.getTransition();
    }

    public void setup(UIRenderingContext context)
    {
        this.font = context.getFont();
        this.render = context;
        this.batcher = context.batcher;
    }

    public void setMouse(int mouseX, int mouseY)
    {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.viewportStack.reset();
    }

    public void setMouse(int mouseX, int mouseY, int mouseButton)
    {
        this.setMouse(mouseX, mouseY);
        this.mouseButton = mouseButton;
    }

    public void setMouseWheel(int mouseX, int mouseY, int mouseWheel)
    {
        this.setMouse(mouseX, mouseY);
        this.mouseWheel = mouseWheel;
    }

    public void setKeyEvent(int keyCode, int scanCode, int action)
    {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.keyAction = KeyAction.get(action);
    }

    public void setKeyTyped(char character)
    {
        this.inputCharacter = character;
    }

    public void reset()
    {
        this.viewportStack.reset();

        this.resetTooltip();
    }

    public void resetTooltip()
    {
        this.tooltip.set(null, null);

        if (this.activeElement instanceof UIElement && !((UIElement) this.activeElement).canBeSeen())
        {
            this.unfocus();
        }
    }

    /* Keys */

    public int getKeyCode()
    {
        return this.keyCode;
    }

    public KeyAction getKeyAction()
    {
        return this.keyAction;
    }

    public char getInputCharacter()
    {
        return this.inputCharacter;
    }

    public boolean isPressed(int keyCode)
    {
        return this.keyCode == keyCode && this.keyAction == KeyAction.PRESSED;
    }

    public boolean isReleased(int keyCode)
    {
        return this.keyCode == keyCode && this.keyAction == KeyAction.RELEASED;
    }

    public boolean isRepeated(int keyCode)
    {
        return this.keyCode == keyCode && this.keyAction == KeyAction.REPEAT;
    }

    public boolean isHeld(int keyCode)
    {
        return this.keyCode == keyCode && this.keyAction != KeyAction.RELEASED;
    }

    public void toggleKeybinds()
    {
        if (this.keybinds.hasParent())
        {
            this.keybinds.removeFromParent();
        }
        else
        {
            this.menu.overlay.add(this.keybinds);
            this.keybinds.resize();
        }
    }

    /* Tooltip */

    public void renderTooltip()
    {
        this.tooltip.renderTooltip(this);
    }

    /* Element focusing */

    public boolean isFocused()
    {
        return this.activeElement != null;
    }

    public void focus(IFocusedUIElement element)
    {
        this.focus(element, false);
    }

    public void focus(IFocusedUIElement element, boolean select)
    {
        if (this.activeElement == element)
        {
            return;
        }

        if (this.activeElement != null)
        {
            this.activeElement.unfocus(this);

            if (select)
            {
                this.activeElement.unselect(this);
            }
        }

        this.activeElement = element;

        if (this.activeElement != null)
        {
            this.activeElement.focus(this);
            this.adjustScroll((UIElement) element);

            if (select)
            {
                this.activeElement.selectAll(this);
            }
        }
    }

    private void adjustScroll(UIElement element)
    {
        UIScrollView scroll = null;
        UIElement original = element;
        int i = 10;

        while (scroll == null && element != null && i >= 0)
        {
            element = element.getParent();

            if (element instanceof UIScrollView)
            {
                scroll = (UIScrollView) element;
            }

            i -= 1;
        }

        if (scroll == null)
        {
            return;
        }

        ScrollDirection direction = scroll.scroll.direction;
        int target = direction.getPosition(original.area, 0) - direction.getPosition(scroll.area, 0);

        scroll.scroll.scrollIntoView(target, direction.getSide(original.area) + 5, 5);
    }

    public void unfocus()
    {
        this.focus(null);
    }

    public boolean focus(UIElement parent, int factor)
    {
        UIElement p = parent.getParentContainer();
        List<IFocusedUIElement> focused = p.getChildren(IFocusedUIElement.class);
        int i = focused.indexOf(this.activeElement);

        if (i >= 0)
        {
            i = MathUtils.cycler(i + factor, 0, focused.size() - 1);

            this.focus(focused.get(i), true);
        }

        return i >= 0;
    }

    /* Context menu */

    public boolean hasContextMenu()
    {
        if (this.contextMenu == null)
        {
            return false;
        }

        if (!this.contextMenu.hasParent())
        {
            this.contextMenu = null;
        }

        return this.contextMenu != null;
    }

    public void setContextMenu(UIContextMenu menu)
    {
        if (this.hasContextMenu() || menu == null)
        {
            return;
        }

        menu.setMouse(this);
        menu.resize();

        this.contextMenu = menu;
        this.menu.overlay.add(menu);
    }

    public void replaceContextMenu(Consumer<ContextMenuManager> consumer)
    {
        ContextMenuManager manager = new ContextMenuManager();

        if (consumer != null)
        {
            consumer.accept(manager);
        }

        this.replaceContextMenu(manager.create());
    }

    public void replaceContextMenu(UIContextMenu menu)
    {
        if (menu == null)
        {
            return;
        }

        if (this.contextMenu != null)
        {
            this.contextMenu.removeFromParent();
        }

        menu.setMouse(this);
        menu.resize();

        this.contextMenu = menu;
        this.menu.overlay.add(menu);
    }

    /* Viewport */

    /**
     * Get absolute X coordinate of the mouse without the
     * scrolling areas applied
     */
    public int mouseX()
    {
        return this.globalX(this.mouseX);
    }

    /**
     * Get absolute Y coordinate of the mouse without the
     * scrolling areas applied
     */
    public int mouseY()
    {
        return this.globalY(this.mouseY);
    }

    @Override
    public int getShiftX()
    {
        return this.mouseX;
    }

    @Override
    public int getShiftY()
    {
        return this.mouseY;
    }

    @Override
    public int globalX(int x)
    {
        return this.viewportStack.globalX(x);
    }

    @Override
    public int globalY(int y)
    {
        return this.viewportStack.globalY(y);
    }

    @Override
    public int localX(int x)
    {
        return this.viewportStack.localX(x);
    }

    @Override
    public int localY(int y)
    {
        return this.viewportStack.localY(y);
    }

    @Override
    public void shiftX(int x)
    {
        this.mouseX += x;
        this.render.stack.translate(-x, 0, 0);
        this.viewportStack.shiftX(x);
    }

    @Override
    public void shiftY(int y)
    {
        this.mouseY += y;
        this.render.stack.translate(0, -y, 0);
        this.viewportStack.shiftY(y);
    }

    @Override
    public void pushViewport(Area viewport)
    {
        this.viewportStack.pushViewport(viewport);
    }

    @Override
    public void popViewport()
    {
        this.viewportStack.popViewport();
    }

    @Override
    public Area getViewport()
    {
        return this.viewportStack.getViewport();
    }

    public void resetMatrix()
    {
        this.render.stack.identity();
    }

    public void update()
    {
        this.tick += 1;
    }
}