package mchorse.studio.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.core.IEngine;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.events.RenderHUDEvent;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.ShaderRepository;
import mchorse.bbs.graphics.window.IFileDropListener;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.world.World;
import mchorse.studio.StudioEngine;
import mchorse.studio.settings.StudioSettings;
import mchorse.studio.ui.welcome.UIWelcomeMenu;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class UIScreen implements IEngine, IFileDropListener
{
    public StudioEngine engine;

    public UIRenderingContext context;
    public UIShaders shaders;

    public UIBaseMenu menu;
    private UIDashboard dashboard;
    private UIHUD hud;

    private boolean refresh;

    public UIScreen(StudioEngine engine)
    {
        this.engine = engine;

        this.hud = new UIHUD(this);
    }

    public void reload(World world)
    {
        if (this.dashboard != null)
        {
            this.dashboard.reloadWorld(world);
        }

        if (StudioSettings.welcome.get())
        {
            this.showMenu(this.getDashboard());
        }
        else
        {
            String id = this.getLanguageCode();

            if (!BBSSettings.language.get().equals(id))
            {
                BBSSettings.language.set(id);
            }

            this.showMenu(new UIWelcomeMenu(this.engine));
        }
    }

    private String getLanguageCode()
    {
        Locale locale = Locale.getDefault();
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String code = lang.toLowerCase() + "_" + country.toUpperCase();

        return BBS.getL10n().getSupportedLanguageCodes().contains(code) ? code : L10n.DEFAULT_LANGUAGE;
    }

    public UIHUD getHUD()
    {
        return this.hud;
    }

    /* UIBaseMenu related code */

    public boolean hasMenu()
    {
        return this.menu != null;
    }

    public boolean isPaused()
    {
        return this.hasMenu() && this.menu.canPause();
    }

    public boolean canRefresh()
    {
        if (this.refresh)
        {
            this.refresh = false;

            return true;
        }

        return !this.hasMenu() || this.menu.canRefresh();
    }

    public UIDashboard getDashboard()
    {
        if (this.dashboard == null)
        {
            this.dashboard = new UIDashboard(this.engine);
            this.dashboard.main.keys().register(KeysApp.WELCOME, () -> this.showMenu(new UIWelcomeMenu(this.engine)));
        }

        return this.dashboard;
    }

    public void pause()
    {
        this.showMenu(this.getDashboard());
    }

    public void showMenu(UIBaseMenu menu)
    {
        UIBaseMenu old = this.menu;

        if (this.menu != null)
        {
            this.menu.onClose(menu);
        }

        this.menu = menu;

        if (this.menu != null)
        {
            int scale = BBSSettings.getScale();

            this.menu.context.setup(this.context);
            this.menu.onOpen(old);
            this.menu.resize(Window.width / scale, Window.height / scale);
        }

        Window.toggleMousePointer(this.menu == null);
        this.engine.keys.keybinds.resetKeybinds();
        this.engine.controller.reset();
    }

    @Override
    public void resize(int width, int height)
    {
        this.refresh = true;

        this.shaders.resize(width, height);

        if (this.menu != null)
        {
            int scale = BBSSettings.getScale();

            this.menu.resize(width / scale, height / scale);
        }
    }

    @Override
    public void init() throws Exception
    {
        this.shaders = new UIShaders();
        this.context = new UIRenderingContext(this.engine.renderer.context, this.shaders.ortho);

        this.context.setUBO(this.shaders.ubo);

        ShaderRepository mainShaders = this.context.getMainShaders();
        ShaderRepository pickingShaders = this.context.getPickingShaders();

        mainShaders.register(this.shaders.vertexRGBA2D);
        mainShaders.register(this.shaders.vertexRGBA);
        mainShaders.register(this.shaders.vertexUVRGBA2D);
        mainShaders.register(this.shaders.vertexUVRGBA);
        mainShaders.register(this.shaders.vertexNormalUVRGBA);
        mainShaders.register(this.shaders.vertexNormalUVLightRGBA);
        mainShaders.register(this.shaders.vertexNormalUVRGBABones);

        pickingShaders.register(this.shaders.pickingVertexRGBA);
        pickingShaders.register(this.shaders.pickingVertexUVRGBA);
        pickingShaders.register(this.shaders.pickingVertexNormalUVRGBA);
        pickingShaders.register(this.shaders.pickingVertexNormalUVLightRGBA);
        pickingShaders.register(this.shaders.pickingVertexNormalUVRGBABones);
        pickingShaders.register(this.shaders.pickingPreview);
    }

    @Override
    public void delete()
    {
        this.shaders.ubo.delete();
    }

    @Override
    public void update()
    {
        this.hud.update();

        if (this.menu != null)
        {
            this.menu.update();
        }
    }

    /* User input handling */

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        if (this.menu == null)
        {
            return;
        }

        MouseInput mouse = this.engine.mouse;

        if (action == GLFW.GLFW_PRESS)
        {
            this.menu.mouseClicked(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), button);
        }
        else if (action == GLFW.GLFW_RELEASE)
        {
            this.menu.mouseReleased(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), button);
        }
    }

    @Override
    public void handleScroll(double x, double y)
    {
        if (this.menu == null)
        {
            return;
        }

        MouseInput mouse = this.engine.mouse;
        int mouseWheel = (int) Math.round(y);

        if (mouseWheel != 0)
        {
            this.menu.mouseScrolled(BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y), mouseWheel);
        }
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        if (this.menu != null)
        {
            return this.menu.handleKey(key, scancode, action, mods);
        }

        return false;
    }

    @Override
    public void handleTextInput(int key)
    {
        if (this.menu != null)
        {
            this.menu.handleTextInput(key);
        }
    }

    @Override
    public void render(float transition)
    {
        this.context.setTransition(transition);
        this.context.getUBO().update(this.shaders.ortho, Matrices.EMPTY_4F);

        GLStates.setupDepthFunction2D();

        int scale = BBSSettings.getScale();
        int w = Window.width / scale;
        int h = Window.height / scale;

        this.renderHUD(w, h);

        /* Flush the last operation */
        this.context.batcher.flush();

        GLStates.setupDepthFunction3D();
    }

    private void renderHUD(int w, int h)
    {
        this.engine.controller.renderHUD(this.context, w, h);

        BBS.events.post(new RenderHUDEvent(this.context, w, h));

        if (this.menu != null)
        {
            MouseInput mouse = this.engine.mouse;

            this.menu.renderMenu(this.context, BBSSettings.transform(mouse.x), BBSSettings.transform(mouse.y));
        }

        this.hud.postRenderHud(this.context, w, h);
        this.context.runRunnables();
    }

    public void renderWorld(RenderingContext context)
    {
        if (this.menu != null)
        {
            this.menu.renderInWorld(context);
        }
    }

    @Override
    public void acceptFilePaths(String[] paths)
    {
        if (this.menu != null)
        {
            for (IFileDropListener listener : this.menu.getRoot().getChildren(IFileDropListener.class))
            {
                listener.acceptFilePaths(paths);
            }
        }
    }
}