package mchorse.sandbox;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.utils.CrashReport;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.Profiler;
import mchorse.bbs.utils.TimePrintStream;
import mchorse.bbs.utils.cli.ArgumentParser;
import mchorse.bbs.utils.cli.ArgumentType;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.world.WorldMetadata;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;

public class Sandbox
{
    public static final String VERSION = "0.7.2";
    public static final String GIT_HASH = "@GIT_HASH@";
    public static final String FULL_VERSION = VERSION + (GIT_HASH.startsWith("@") ? " (dev)" : " (" + GIT_HASH + ")");

    public static final Profiler PROFILER = new Profiler();

    /* Command line arguments */

    public File gameDirectory;
    public String defaultWorld = "flat";
    public int windowWidth = 1280;
    public int windowHeight = 720;
    public boolean openGLDebug;
    public boolean development;
    public boolean fabric;

    public static Link link(String path)
    {
        return new Link("sandbox", path);
    }

    public static void main(String[] args)
    {
        PROFILER.begin("bootstrap");

        System.out.println(IOUtils.readText(BBS.class.getResourceAsStream("/assets/strings/title.txt")));
        System.out.println("\nBBS: " + FULL_VERSION + ", LWJGL: " + Version.getVersion() + ", GLFW: " + GLFW.glfwGetVersionString());

        System.setOut(new TimePrintStream(System.out));
        System.setErr(new TimePrintStream(System.err));

        ArgumentParser parser = new ArgumentParser();

        parser.register("gameDirectory", ArgumentType.PATH)
            .register("defaultWorld", "dw", ArgumentType.STRING)
            .register("glDebug", "gld", ArgumentType.NUMBER)
            .register("width", "ww", ArgumentType.NUMBER)
            .register("height", "wh", ArgumentType.NUMBER)
            .register("development", "dev", ArgumentType.NUMBER)
            .register("fabric", ArgumentType.NUMBER);

        Sandbox game = new Sandbox();

        game.setup(parser.parse(args));
        game.launch();
    }

    private void setup(MapType data)
    {
        if (data.has("gameDirectory"))
        {
            this.gameDirectory = new File(data.getString("gameDirectory"));
        }

        this.defaultWorld = data.getString("defaultWorld", "flat");
        this.windowWidth = data.getInt("width", this.windowWidth);
        this.windowHeight = data.getInt("height", this.windowHeight);
        this.openGLDebug = data.getBool("glDebug", this.openGLDebug);
        this.development = data.getBool("development", this.development);
        this.fabric = data.getBool("fabric", this.fabric);
    }

    public void launch()
    {
        PROFILER.endBegin("launch");

        if (this.gameDirectory == null || !this.gameDirectory.isDirectory())
        {
            throw new IllegalStateException("Given game directory '" + this.gameDirectory + "' doesn't exist or not a directory...");
        }

        SandboxEngine engine = new SandboxEngine(this);
        long id = -1;

        try
        {
            PROFILER.endBegin("setup_window");

            /* Start the game */
            Window.initialize("BBS " + FULL_VERSION, this.windowWidth, this.windowHeight, this.openGLDebug);
            Window.setupStates();

            id = Window.getWindow();

            PROFILER.endBegin("clean_world");
            this.createCleanWorld();
            PROFILER.endBegin("init_engine");
            engine.init();
            PROFILER.endBegin("load_world");
            engine.get(IBridgeWorld.class).loadWorld(this.defaultWorld);
            PROFILER.end();
            PROFILER.print();
            engine.start(id);
        }
        catch (Exception e)
        {
            File crashes = new File(this.gameDirectory, "crashes");
            Pair<File, String> crash = CrashReport.writeCrashReport(crashes, e, "BBS " + FULL_VERSION + " has crashed! Here is a crash stacktrace:");

            /* Here we should actually save a crash log with exception
             * and other relevant information */
            e.printStackTrace();

            CrashReport.showDialogue(crash, "BBS " + FULL_VERSION + " has crashed! The crash log " + crash.a.getName() + " was generated in \"crashes\" folder, which you should send to BBS' developer(s).\n\nIMPORTANT: don't screenshot this window!");
        }

        /* Terminate the game */
        engine.delete();

        Callbacks.glfwFreeCallbacks(id);
        GLFW.glfwDestroyWindow(id);

        GLFW.glfwSetErrorCallback(null).free();
        GLFW.glfwTerminate();
    }

    private void createCleanWorld()
    {
        this.gameDirectory.mkdirs();

        File defaultWorld = new File(this.gameDirectory, "worlds/" + this.defaultWorld);

        if (!defaultWorld.exists())
        {
            try
            {
                this.createWorld(this.gameDirectory, defaultWorld);
            }
            catch (IOException e)
            {
                System.err.println("Failed to create default world!");
                e.printStackTrace();
            }
        }
    }

    private void createWorld(File gameDirectory, File defaultWorld) throws IOException
    {
        /* Generate default tile set */
        BlockSet blockSet = new BlockSet(Link.assets("tilesets/default.png"));
        File blockSetFile = new File(gameDirectory, "assets/tilesets/default.json");

        blockSet.fromData(DataToString.mapFromString(IOUtils.readText(this.getClass().getResourceAsStream("/assets/tilesets/default.json"))));
        blockSetFile.getParentFile().mkdirs();
        DataToString.write(blockSetFile, blockSet.toData(), true);

        /* Generate default metadata */
        WorldMetadata metadata = new WorldMetadata(defaultWorld);

        metadata.column = true;
        metadata.generator = Link.bbs("flat");
        metadata.metadata.putString("generator.primary", blockSet.get(1).getLink().toString());
        metadata.metadata.putString("generator.secondary", blockSet.get(2).getLink().toString());
        metadata.metadata.putString("generator.foliage1", blockSet.get(3).getLink().toString());
        metadata.metadata.putString("generator.foliage2", blockSet.get(4).getLink().toString());

        defaultWorld.mkdirs();
        DataToString.write(new File(defaultWorld, "metadata.json"), metadata.toData(), true);
    }
}