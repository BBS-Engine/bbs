package mchorse.bbs.ui.framework;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.core.Engine;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.elements.IViewport;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.IViewportStack;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.renderers.InputRenderer;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.StringJoiner;

/**
 * Base class for GUI screens using this framework
 */
public abstract class UIBaseMenu implements ITickable, IKeyHandler
{
    private static InputRenderer inputRenderer = new InputRenderer();

    private UIRootElement root;
    public UIElement main;
    public UIElement overlay;
    public UIContext context;
    public Area viewport = new Area();

    public int width;
    public int height;

    public final IBridge bridge;

    public UIBaseMenu(IBridge bridge)
    {
        this.bridge = bridge;

        this.context = new UIContext(this);

        this.root = new UIRootElement(this.context);
        this.root.markContainer().relative(this.viewport).full();
        this.root.keys().register(Keys.KEYBINDS, () -> this.context.toggleKeybinds());

        this.main = new UIElement();
        this.main.relative(this.viewport).full();
        this.overlay = new UIElement();
        this.overlay.relative(this.viewport).full();
        this.root.add(this.main, this.overlay);

        this.context.keybinds.relative(this.viewport).wh(0.5F, 1F);
    }

    public abstract Link getMenuId();

    public UIRootElement getRoot()
    {
        return this.root;
    }

    public boolean canPause()
    {
        return true;
    }

    public boolean canRefresh()
    {
        return true;
    }

    public void onOpen(UIBaseMenu oldMenu)
    {}

    public void onClose(UIBaseMenu nextMenu)
    {}

    @Override
    public void update()
    {
        this.context.update();
    }

    public void resize(int width, int height)
    {
        this.width = width;
        this.height = height;

        this.viewport.set(0, 0, this.width, this.height);
        this.viewportSet();

        this.context.pushViewport(this.viewport);
        this.root.resize();
        this.context.popViewport();
    }

    protected void viewportSet()
    {}

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        boolean result = false;

        this.context.setMouse(mouseX, mouseY, mouseButton);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            result = this.root.mouseClicked(this.context);
            this.context.popViewport();
        }

        return result;
    }

    public boolean mouseScrolled(int x, int y, int scroll)
    {
        boolean result = false;

        this.context.setMouseWheel(x, y, scroll);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            result = this.root.mouseScrolled(this.context);
            this.context.popViewport();
        }

        return result;
    }

    public boolean mouseReleased(int mouseX, int mouseY, int mouseButton)
    {
        boolean result = false;

        this.context.setMouse(mouseX, mouseY, mouseButton);

        if (this.root.isEnabled())
        {
            this.context.pushViewport(this.viewport);
            result = this.root.mouseReleased(this.context);
            this.context.popViewport();
        }

        return result;
    }

    @Override
    public boolean handleKey(int key, int scanCode, int action, int mods)
    {
        if (action == GLFW.GLFW_PRESS)
        {
            inputRenderer.keyPressed(this.context, key);
        }

        this.context.setKeyEvent(key, scanCode, action);

        if (this.root.isEnabled() && this.root.keyPressed(this.context))
        {
            return true;
        }

        if (this.context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.closeMenu();

            return true;
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {
        this.context.setKeyTyped((char) key);

        if (this.root.isEnabled())
        {
            this.root.textInput(this.context);
        }
    }

    /**
     * This method is called when this screen is about to get closed
     */
    protected void closeMenu()
    {
        this.bridge.get(IBridgeMenu.class).closeMenu();
    }

    public void closeThisMenu()
    {
        this.closeMenu();
    }

    public void renderDefaultBackground()
    {
        this.context.batcher.box(0, 0, this.width, this.height, Colors.A50);
    }

    public void renderMenu(UIRenderingContext context, int mouseX, int mouseY)
    {
        this.context.resetMatrix();
        this.context.setMouse(mouseX, mouseY);

        this.preRenderMenu(context);

        if (this.root.isVisible())
        {
            this.context.reset();
            this.context.pushViewport(this.viewport);

            this.root.render(this.context);

            this.context.popViewport();
            this.context.renderTooltip();
        }

        if (this.main.isVisible())
        {
            inputRenderer.render(this, mouseX, mouseY);
        }

        if (context.isDebug())
        {
            this.renderDebugInfo(context);
        }
    }

    protected void renderDebugInfo(UIRenderingContext context)
    {
        Engine engine = this.bridge.getEngine();
        String text = "FPS: " + engine.lastFPS + "\n";
        Vector3d position = this.bridge.get(IBridgeCamera.class).getCamera().position;

        text += Math.floor(position.x) + ", " + Math.floor(position.y) + ", " + Math.floor(position.z) + "\n";
        text += "Entities: " + this.bridge.get(IBridgeWorld.class).getWorld().entities.size();

        if (engine.joystick.isPresent())
        {
            text += "\nJoystick: ";

            StringJoiner axesJoiner = new StringJoiner(", ");
            StringJoiner buttonJoiner = new StringJoiner(", ");

            GLFWGamepadState state = engine.joystick.getUpdatedState();
            FloatBuffer axes = state.axes();

            while (axes.position() < axes.limit())
            {
                axesJoiner.add(String.valueOf(Math.round(axes.get() * 100F) / 100F));
            }

            ByteBuffer buttons = state.buttons();

            while (buttons.position() < buttons.limit())
            {
                buttonJoiner.add(String.valueOf(buttons.get()));
            }

            text += axesJoiner.toString();
            text += "\n" + buttonJoiner;
        }

        FontRenderer font = context.getFont();
        int y = 7;

        for (String line : text.split("\n"))
        {
            context.batcher.textCard(font, line, 7, y, Colors.WHITE, BBSSettings.primaryColor(Colors.A100));

            y += font.getHeight() + 7;
        }
    }

    protected void preRenderMenu(UIRenderingContext context)
    {}

    public void renderInWorld(RenderingContext context)
    {}

    public static class UIRootElement extends UIElement implements IViewport
    {
        private UIContext context;

        public UIRootElement(UIContext context)
        {
            super();

            this.context = context;

            this.markContainer();
        }

        public UIContext getContext()
        {
            return this.context;
        }

        @Override
        public void apply(IViewportStack stack)
        {
            stack.pushViewport(this.area);
        }

        @Override
        public void unapply(IViewportStack stack)
        {
            stack.popViewport();
        }
    }
}