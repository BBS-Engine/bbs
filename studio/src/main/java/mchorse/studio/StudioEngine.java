package mchorse.studio;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.controller.CameraController;
import mchorse.bbs.core.Engine;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.events.L10nReloadEvent;
import mchorse.bbs.events.UpdateEvent;
import mchorse.bbs.events.register.RegisterKeybindsClassesEvent;
import mchorse.bbs.events.register.RegisterL10nEvent;
import mchorse.bbs.events.register.RegisterSettingsEvent;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.window.IFileDropListener;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.l10n.L10nUtils;
import mchorse.bbs.resources.Link;
import mchorse.bbs.resources.packs.DataSourcePack;
import mchorse.bbs.resources.packs.ExternalAssetsSourcePack;
import mchorse.bbs.resources.packs.InternalAssetsSourcePack;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueLanguage;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.OS;
import mchorse.bbs.utils.recording.ScreenshotRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.watchdog.WatchDog;
import mchorse.bbs.world.World;
import mchorse.studio.bridge.BridgeCamera;
import mchorse.studio.bridge.BridgeMenu;
import mchorse.studio.bridge.BridgeRender;
import mchorse.studio.bridge.BridgeVideoRecorder;
import mchorse.studio.bridge.BridgeWorld;
import mchorse.studio.settings.StudioSettings;
import mchorse.studio.ui.KeysApp;
import mchorse.studio.ui.UIKeysApp;
import mchorse.studio.ui.UIScreen;
import mchorse.studio.ui.l10n.UILanguageEditorOverlayPanel;
import mchorse.studio.ui.utility.UIUtilityMenu;
import mchorse.studio.ui.utility.UIUtilityOverlayPanel;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class StudioEngine extends Engine implements IBridge, IFileDropListener
{
    /* Game */
    public StudioRenderer renderer;
    public UIScreen screen;
    public World world;

    public CameraController cameraController = new CameraController();

    /* Utility */
    public VideoRecorder video;
    public ScreenshotRecorder screenshot;

    private WatchDog watchDog;
    private WatchDog watchDog2;

    private Map<Class, Object> apis = new HashMap<>();

    public StudioEngine(Studio game)
    {
        super();

        this.apis.put(IBridgeCamera.class, new BridgeCamera(this));
        this.apis.put(IBridgeMenu.class, new BridgeMenu(this));
        this.apis.put(IBridgeRender.class, new BridgeRender(this));
        this.apis.put(IBridgeVideoRecorder.class, new BridgeVideoRecorder(this));
        this.apis.put(IBridgeWorld.class, new BridgeWorld(this));

        BBS.events.register(this);

        BBS.registerCore(this, game.gameDirectory);
        BBS.registerFactories();
        BBS.registerFoundation();

        this.screen = new UIScreen(this);
        this.renderer = new StudioRenderer(this);
        this.cameraController.camera.position.set(0, 0.5, 0);

        this.registerMiscellaneous();
        this.registerKeybinds();

        this.watchDog = new WatchDog(BBS.getAssetsFolder());
        this.watchDog.register(BBS.getTextures());
        this.watchDog.register(BBS.getModels());
        this.watchDog.register(BBS.getSounds());
        this.watchDog.register(BBS.getFonts());
        this.watchDog.start();

        this.watchDog2 = new WatchDog(BBS.getGamePath("studio"));
        this.watchDog2.register(BBS.getShaders());
        this.watchDog2.start();

        BBS.getShaders().setReloadCallback(this.renderer::reloadShaders);
    }

    @Subscribe
    public void registerSettings(RegisterSettingsEvent event)
    {
        event.register(Icons.BUCKET, "studio", StudioSettings::register);
    }

    @Subscribe
    public void registerL10n(RegisterL10nEvent event)
    {
        this.reloadSupportedLanguages();

        event.l10n.registerOne((lang) -> Studio.link("strings/" + lang + ".json"));
    }

    @Subscribe
    public void reloadL10n(L10nReloadEvent event)
    {
        File export = UILanguageEditorOverlayPanel.getLangEditorFolder();
        File[] files = export.listFiles();

        if (files == null)
        {
            return;
        }

        for (File file : files)
        {
            if (file.isFile() && file.getName().endsWith(".json"))
            {
                this.overwriteLanguage(event.l10n, file);
            }
        }
    }

    @Subscribe
    public void registerKeybindsClasses(RegisterKeybindsClassesEvent event)
    {
        event.register(KeysApp.class);
    }

    private void overwriteLanguage(L10n l10n, File file)
    {
        try
        {
            l10n.overwrite(DataToString.mapFromString(IOUtils.readText(file)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Register miscellaneous stuff
     */
    private void registerMiscellaneous()
    {
        File studio = BBS.getGamePath("studio");

        studio.mkdirs();

        BBS.getProvider().register(new ExternalAssetsSourcePack("studio", studio));
        BBS.getProvider().register(new InternalAssetsSourcePack("studio", StudioEngine.class));

        File file = BBS.getGamePath("assets.dat");

        if (file.isFile())
        {
            try
            {
                BBS.getProvider().register(new DataSourcePack(file.toURI().toURL()));
                System.out.println("Loaded packed assets from assets.dat!");
            }
            catch (MalformedURLException e)
            {
                System.err.println("Failed to load packed assets.dat!");
                e.printStackTrace();
            }
        }


        /* Recording */
        this.video = new VideoRecorder(BBS.getGamePath("movies"), this);
        this.screenshot = new ScreenshotRecorder(BBS.getGamePath("screenshots"), this);

        Window.registerFileDropListener(this);
    }

    /**
     * Register keybinds
     */
    private void registerKeybinds()
    {
        KeybindCategory global = new KeybindCategory("global");
        Keybind screenshot = new Keybind("screenshot", () -> this.screenshot.take(Window.isAltPressed()));
        Keybind fullscreen = new Keybind("fullscreen", this::toggleFullScreen);
        Keybind debug = new Keybind("debug", () -> this.renderer.context.setDebug(!this.renderer.context.isDebug()));
        Keybind freezeShadow = new Keybind("freeze_shadow", this.renderer::freezeShadow);

        global.add(screenshot.keys(GLFW.GLFW_KEY_F2));
        global.add(debug.keys(GLFW.GLFW_KEY_F3));
        global.add(fullscreen.keys(GLFW.GLFW_KEY_F11));
        global.add(freezeShadow.keys(GLFW.GLFW_KEY_F10));

        Keybind utilities = new Keybind("utilities", () ->
        {
            UIBaseMenu currentMenu = this.screen.menu;

            if (currentMenu == null)
            {
                this.screen.showMenu(new UIUtilityMenu(this));
            }
            else
            {
                if (UIOverlay.has(currentMenu.context))
                {
                    return;
                }

                UIOverlay.addOverlay(currentMenu.context, new UIUtilityOverlayPanel(UIKeysApp.UTILITY_TITLE, null), 240, 160);
            }
        });

        global.add(utilities.keys(GLFW.GLFW_KEY_F6));

        this.keys.keybinds.add(global);
    }

    /* Engine implementation */

    @Override
    public void init() throws Exception
    {
        super.init();

        Studio.PROFILER.endBegin("window_icon");
        this.updateWindowIcon();

        Studio.PROFILER.endBegin("init_bbs");
        BBS.initialize();
        Studio.PROFILER.endBegin("init_bbs_data");
        BBSData.load(BBS.getDataFolder(), this);

        Studio.PROFILER.endBegin("init_renderer");
        this.renderer.init();
        this.screen.init();
        this.resize(Window.width, Window.height);

        Window.focus();
        Window.toggleMousePointer(true);

        Studio.PROFILER.endBegin("init_callbacks");
        this.registerSettingsCallbacks();
    }

    private void updateWindowIcon()
    {
        if (OS.CURRENT == OS.MACOS)
        {
            return;
        }

        try
        {
            Pixels pixels48 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.assets("textures/icons/icon_48.png")));
            Pixels pixels32 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.assets("textures/icons/icon_32.png")));
            Pixels pixels16 = Pixels.fromPNGStream(BBS.getProvider().getAsset(Link.assets("textures/icons/icon_16.png")));

            Window.updateIcon(pixels48, pixels32, pixels16);

            pixels48.delete();
            pixels32.delete();
            pixels16.delete();
        }
        catch (Exception e)
        {
            System.err.println("Failed to register window icons!");
            e.printStackTrace();
        }
    }

    private void registerSettingsCallbacks()
    {
        StudioSettings.renderFrameRate.callback((v) -> this.frameRate = ((ValueInt) v).get());
        StudioSettings.renderVsync.callback((v) -> Window.setVSync(((ValueBoolean) v).get()));
        StudioSettings.renderQuality.callback((v) -> this.resize(Window.width, Window.height));

        BBSSettings.language.callback((v) ->
        {
            this.reloadSupportedLanguages();
            BBS.getL10n().reload(((ValueLanguage) v).get(), BBS.getProvider());
        });
        BBSSettings.userIntefaceScale.callback((v) -> BBS.getEngine().needsResize());
    }

    private void reloadSupportedLanguages()
    {
        BBS.getL10n().reloadSupportedLanguages(L10nUtils.readAdditionalLanguages(BBS.getAssetsPath("lang_editor/languages.json")));
    }

    @Override
    public void delete()
    {
        super.delete();

        this.screen.delete();
        this.world.delete();
        this.watchDog.stop();
        this.watchDog2.stop();

        BBSData.delete();
        BBS.terminate();
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        return this.keys.keybinds.handleKey(key, scancode, action, mods)
            || this.screen.handleKey(key, scancode, action, mods);
    }

    @Override
    public void handleTextInput(int key)
    {
        this.screen.handleTextInput(key);
    }

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        this.screen.handleMouse(button, action, mode);
    }

    @Override
    public void handleScroll(double x, double y)
    {
        this.screen.handleScroll(x, y);
    }

    @Override
    public boolean handleGamepad(int button, int action)
    {
        return false;
    }

    @Override
    public void update()
    {
        super.update();

        if (!this.screen.isPaused())
        {
            this.world.update();
            this.cameraController.tick();
        }

        this.world.view.updateChunks(this.cameraController.camera.position);
        this.renderer.update();
        this.screen.update();
        this.cameraController.updateSoundPosition();

        BBS.events.post(new UpdateEvent());
    }

    @Override
    public void render(float transition)
    {
        super.render(transition);

        float worldTransition = this.screen.isPaused() ? 0 : transition;

        this.cameraController.setup(this.cameraController.camera, worldTransition);

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        this.renderer.render(worldTransition);
        this.screen.render(transition);

        this.video.recordFrame();
        this.screenshot.recordFrame(Window.width, Window.height);
    }

    @Override
    public void resize(int width, int height)
    {
        GLStates.resetViewport();

        if (this.video.isRecording())
        {
            this.video.stopRecording();
        }

        this.cameraController.resize(width, height);
        this.renderer.resize(width, height);
        this.screen.resize(width, height);
    }

    /* IBridge implementation */

    @Override
    public Engine getEngine()
    {
        return this;
    }

    @Override
    public <T> T get(Class<T> apiInterface)
    {
        return apiInterface.cast(this.apis.get(apiInterface));
    }

    /* IFileDropListener implementation */

    @Override
    public void acceptFilePaths(String[] paths)
    {
        this.screen.acceptFilePaths(paths);
    }
}