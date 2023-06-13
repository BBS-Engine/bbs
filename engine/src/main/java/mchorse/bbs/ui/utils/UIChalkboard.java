package mchorse.bbs.ui.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.core.IDisposable;
import mchorse.bbs.core.IRenderable;
import mchorse.bbs.core.input.IKeyHandler;
import mchorse.bbs.core.input.IMouseHandler;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.resources.Pixels;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class UIChalkboard extends UIBaseMenu implements IDisposable, IMouseHandler, IKeyHandler, IRenderable
{
    private UIColor picker;

    private Pixels pixels;
    private Texture texture;

    private UIRenderingContext render;
    private int color = Colors.setA(Colors.RED, 1F);
    private boolean drawing;

    private boolean enabled;
    private int lastX;
    private int lastY;

    public UIChalkboard(IBridge bridge, UIRenderingContext render)
    {
        super(bridge);

        this.render = render;

        this.texture = new Texture();
        this.texture.setFilter(GL11.GL_NEAREST);

        this.picker = new UIColor((c) -> this.color = c).withAlpha();
        this.picker.direction(Direction.TOP).setColor(this.color);
        this.picker.relative(this.main).x(10).y(1F, -30).wh(60, 20);

        this.main.add(this.picker);
        this.main.keys().register(Keys.CHALKBOARD_CLEAR, () -> this.resize(Window.width, Window.height));
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("chalkboard");
    }

    public boolean isEnabled()
    {
        return this.bridge.get(IBridgePlayer.class).isDevelopment() && BBSSettings.enableChalkboard.get() && this.enabled;
    }

    @Override
    protected void closeMenu()
    {}

    @Override
    public void delete()
    {
        this.texture.delete();

        if (this.pixels != null)
        {
            this.pixels.delete();
        }
    }

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        if (!this.isEnabled())
        {
            return;
        }

        MouseInput input = BBS.getEngine().mouse;

        if (action == GLFW.GLFW_PRESS)
        {
            if (this.mouseClicked(BBSSettings.transform(input.x), BBSSettings.transform(input.y), button))
            {
                return;
            }
        }
        else if (action == GLFW.GLFW_RELEASE)
        {
            if (this.mouseReleased(BBSSettings.transform(input.x), BBSSettings.transform(input.y), button))
            {
                return;
            }
        }

        if (button == 0 && action == GLFW.GLFW_PRESS)
        {
            this.drawing = true;

            this.lastX = input.x;
            this.lastY = input.y;
        }
        else if (button == 0 && action == GLFW.GLFW_RELEASE)
        {
            this.drawing = false;
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {}

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        if (this.bridge.get(IBridgePlayer.class).isDevelopment() && key == GLFW.GLFW_KEY_F8 && action == GLFW.GLFW_RELEASE)
        {
            this.enabled = !this.enabled;

            return true;
        }

        super.handleKey(key, scancode, action, mods);

        return this.isEnabled();
    }

    @Override
    public void handleTextInput(int key)
    {
        if (this.isEnabled())
        {
            super.handleTextInput(key);
        }
    }

    @Override
    public void resize(int width, int height)
    {
        int scale = BBSSettings.getScale();

        super.resize(width / scale, height / scale);

        if (this.pixels != null)
        {
            this.pixels.delete();
        }

        this.pixels = Pixels.fromSize(width, height);
        this.pixels.rewindBuffer();
        this.texture.bind();
        this.texture.updateTexture(this.pixels);
    }

    @Override
    public void render(float transition)
    {
        if (!this.isEnabled())
        {
            return;
        }

        MouseInput input = BBS.getEngine().mouse;
        int scale = BBSSettings.getScale();
        int x = input.x;
        int y = input.y;
        int sx = x / scale;
        int sy = y / scale;

        this.context.setup(this.render);

        if (this.pixels != null)
        {
            this.texture.bind();

            if (this.drawing)
            {
                double distance = new Vector2d(x, y).distance(this.lastX, this.lastY);

                for (int i = 0; i < distance; i++)
                {
                    int xx = (int) (Interpolations.lerp(x, this.lastX, i / distance));
                    int yy = (int) (Interpolations.lerp(y, this.lastY, i / distance));

                    this.pixels.drawRect(xx - 1, yy - 2, 2, 4, this.color);
                    this.pixels.drawRect(xx - 2, yy - 1, 1, 2, this.color);
                    this.pixels.drawRect(xx + 1, yy - 1, 1, 2, this.color);
                }

                this.pixels.rewindBuffer();
                this.texture.updateTexture(this.pixels);

                this.lastX = x;
                this.lastY = y;
            }

            this.context.draw.fullTexturedBox(0, 0, Window.width / scale, Window.height / scale);
        }

        this.context.draw.box(sx - 1, sy - 1, sx + 1, sy + 1, this.color);

        this.renderMenu(this.render, sx, sy);
    }
}