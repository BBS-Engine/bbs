package mchorse.sandbox;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeHUD;
import mchorse.bbs.bridge.IBridgeMenu;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeRender;
import mchorse.bbs.bridge.IBridgeVideoRecorder;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.controller.CameraController;
import mchorse.bbs.core.Engine;
import mchorse.bbs.core.ITickable;
import mchorse.bbs.core.keybinds.Keybind;
import mchorse.bbs.core.keybinds.KeybindCategory;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.events.L10nReloadEvent;
import mchorse.bbs.events.UpdateEvent;
import mchorse.bbs.events.register.RegisterItemsEvent;
import mchorse.bbs.events.register.RegisterKeybindsClassesEvent;
import mchorse.bbs.events.register.RegisterL10nEvent;
import mchorse.bbs.events.register.RegisterSettingsEvent;
import mchorse.bbs.game.player.PlayerData;
import mchorse.bbs.game.scripts.ScriptUtils;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.window.IFileDropListener;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.l10n.L10nUtils;
import mchorse.bbs.resources.Link;
import mchorse.bbs.resources.packs.InternalAssetsSourcePack;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueLanguage;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.keys.KeybindSettings;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.OS;
import mchorse.bbs.utils.recording.ScreenshotRecorder;
import mchorse.bbs.utils.recording.VideoRecorder;
import mchorse.bbs.utils.resources.Pixels;
import mchorse.bbs.utils.watchdog.WatchDog;
import mchorse.bbs.world.World;
import mchorse.sandbox.bridge.BridgeAnimations;
import mchorse.sandbox.bridge.BridgeCamera;
import mchorse.sandbox.bridge.BridgeHUD;
import mchorse.sandbox.bridge.BridgeMenu;
import mchorse.sandbox.bridge.BridgePlayer;
import mchorse.sandbox.bridge.BridgeRender;
import mchorse.sandbox.bridge.BridgeVideoRecorder;
import mchorse.sandbox.bridge.BridgeWorld;
import mchorse.sandbox.settings.SandboxSettings;
import mchorse.sandbox.ui.KeysApp;
import mchorse.sandbox.ui.UIKeysApp;
import mchorse.sandbox.ui.UIScreen;
import mchorse.sandbox.ui.l10n.UILanguageEditorOverlayPanel;
import mchorse.sandbox.ui.utility.UIUtilityMenu;
import mchorse.sandbox.ui.utility.UIUtilityOverlayPanel;
import net.fabricmc.loader.impl.game.minecraft.Hooks;
import org.greenrobot.eventbus.Subscribe;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SandboxEngine extends Engine implements IBridge, IFileDropListener
{
    /* Game */
    public SandboxRenderer renderer;
    public SandboxController controller;
    public UIScreen screen;
    public World world;
    public PlayerData playerData;

    public CameraController cameraController = new CameraController();

    /* Utility */
    public VideoRecorder video;
    public ScreenshotRecorder screenshot;

    private WatchDog watchDog;

    public final boolean development;

    private Map<Class, Object> apis = new HashMap<Class, Object>();

    public SandboxEngine(Sandbox game)
    {
        super();

        this.development = game.development;

        this.apis.put(IBridgeAnimations.class, new BridgeAnimations(this));
        this.apis.put(IBridgeCamera.class, new BridgeCamera(this));
        this.apis.put(IBridgeHUD.class, new BridgeHUD(this));
        this.apis.put(IBridgeMenu.class, new BridgeMenu(this));
        this.apis.put(IBridgePlayer.class, new BridgePlayer(this));
        this.apis.put(IBridgeRender.class, new BridgeRender(this));
        this.apis.put(IBridgeVideoRecorder.class, new BridgeVideoRecorder(this));
        this.apis.put(IBridgeWorld.class, new BridgeWorld(this));

        BBS.events.register(this);

        if (game.fabric)
        {
            Hooks.startClient(game.gameDirectory, game);
        }

        BBS.registerCore(this, game.gameDirectory);
        BBS.registerFactories();
        BBS.registerFoundation();

        this.registerMiscellaneous();
        this.registerKeybinds();

        this.screen = new UIScreen(this);
        this.renderer = new SandboxRenderer(this);
        this.controller = new SandboxController(this);
        this.playerData = new PlayerData(this, BBS.getDataPath("player.json"));
        this.playerData.load();
        this.cameraController.camera.position.set(0, 0.5, 0);

        if (this.development)
        {
            this.watchDog = new WatchDog(BBS.getAssetsFolder());
            this.watchDog.register(BBS.getTextures());
            this.watchDog.register(BBS.getModels());
            this.watchDog.register(BBS.getSounds());
            this.watchDog.register(BBS.getFonts());
            this.watchDog.start();
        }
    }

    @Subscribe
    public void registerItems(RegisterItemsEvent event)
    {
        File itemsFile = BBS.getAssetsPath("items.json");

        if (itemsFile.isFile())
        {
            try
            {
                event.items.fromData(DataToString.read(itemsFile).asMap());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Subscribe
    public void registerSettings(RegisterSettingsEvent event)
    {
        event.register("sandbox", SandboxSettings::register);
        event.register("keybinds", KeybindSettings::register);
    }

    @Subscribe
    public void registerL10n(RegisterL10nEvent event)
    {
        this.reloadSupportedLanguages();

        event.l10n.registerOne((lang) -> Link.create("sandbox:strings/" + lang + ".json"));
    }

    @Subscribe
    public void reloadL10n(L10nReloadEvent event)
    {
        if (!this.development)
        {
            return;
        }

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
        BBS.getProvider().register(new InternalAssetsSourcePack("sandbox", SandboxEngine.class));

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

        global.add(screenshot.keys(GLFW.GLFW_KEY_F2));
        global.add(debug.keys(GLFW.GLFW_KEY_F3));
        global.add(fullscreen.keys(GLFW.GLFW_KEY_F11));

        if (this.development)
        {
            Keybind video = new Keybind("video", () -> this.video.toggleRecording(this.renderer.finalFramebuffer));
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

                    UIOverlay.addOverlay(currentMenu.context, new UIUtilityOverlayPanel(UIKeysApp.UTILITY_TITLE, null));
                }
            });

            global.add(video.keys(GLFW.GLFW_KEY_F4));
            global.add(utilities.keys(GLFW.GLFW_KEY_F6));
        }

        this.keys.keybinds.add(global);
    }

    /* Engine implementation */

    @Override
    public void init() throws Exception
    {
        super.init();

        Sandbox.PROFILER.endBegin("window_icon");
        this.updateWindowIcon();

        Sandbox.PROFILER.endBegin("init_bbs");
        BBS.initialize();
        Sandbox.PROFILER.endBegin("init_bbs_data");
        BBSData.load(BBS.getDataFolder(), this);

        Sandbox.PROFILER.endBegin("init_renderer");
        this.renderer.init();
        this.screen.init();
        this.resize(Window.width, Window.height);

        Window.focus();
        Window.toggleMousePointer(true);

        this.controller.init();

        Sandbox.PROFILER.endBegin("init_js");
        this.forceLoadJS();
        Sandbox.PROFILER.endBegin("init_callbacks");
        this.registerSettingsCallbacks();

        /* FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(new File("C:\\Users\\Admin\\Documents\\Studio\\Footage\\bbs_27.mp4"));
        Texture texture = BBS.getTextures().createTexture(Link.create("test:test"));

        grabber.start();

        new VideoPlaybackThread(grabber, texture); */
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
        SandboxSettings.renderFrameRate.callback((v) -> this.frameRate = ((ValueInt) v).get());
        SandboxSettings.renderVsync.callback((v) -> Window.setVSync(((ValueBoolean) v).get()));
        SandboxSettings.renderQuality.callback((v) -> this.resize(Window.width, Window.height));

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

    private void forceLoadJS()
    {
        try
        {
            if (ScriptUtils.tryCreatingEngine().eval("true") instanceof Boolean)
            {
                System.out.println("JS scripting engine was successfully launched!");
            }
        }
        catch (Exception e)
        {
            System.err.println("JS scripting engine failed to launch!");
            e.printStackTrace();
        }
    }

    @Override
    public void delete()
    {
        super.delete();

        this.screen.delete();
        this.world.delete();
        this.playerData.save();

        if (this.development)
        {
            this.watchDog.stop();
        }

        BBSData.delete();
        BBS.terminate();
    }

    @Override
    public boolean handleKey(int key, int scancode, int action, int mods)
    {
        return this.screen.getChalkboard().handleKey(key, scancode, action, mods)
            || this.keys.keybinds.handleKey(key, scancode, action, mods)
            || this.controller.handleKey(key, scancode, action, mods)
            || this.screen.handleKey(key, scancode, action, mods);
    }

    @Override
    public void handleTextInput(int key)
    {
        this.screen.getChalkboard().handleTextInput(key);

        if (!this.screen.getChalkboard().isEnabled())
        {
            this.screen.handleTextInput(key);
        }
    }

    @Override
    public void handleMouse(int button, int action, int mode)
    {
        if (this.screen.getChalkboard().isEnabled())
        {
            this.screen.getChalkboard().handleMouse(button, action, mode);

            return;
        }

        if (!this.screen.hasMenu())
        {
            this.controller.handleMouse(button, action, mode);
        }

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
        return this.playerData.getGameController().handleGamepad(button, action);
    }

    @Override
    public void update()
    {
        super.update();

        if (!this.screen.isPaused())
        {
            this.world.update();

            BBSData.getRecords().tick();
            BBSData.getScenes().tick();

            if (this.cameraController.getCurrent() instanceof ITickable)
            {
                ((ITickable) this.cameraController.getCurrent()).update();
            }
        }

        this.renderer.update();
        this.controller.update();
        this.screen.update();
        this.cameraController.update();

        BBS.events.post(new UpdateEvent());
    }

    @Override
    public void render(float transition)
    {
        super.render(transition);

        this.playerData.getGameController().render(transition);

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