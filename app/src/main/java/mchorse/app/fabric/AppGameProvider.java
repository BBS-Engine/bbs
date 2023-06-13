package mchorse.app.fabric;

import mchorse.app.Game;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.patch.GameTransformer;
import net.fabricmc.loader.impl.launch.FabricLauncher;
import net.fabricmc.loader.impl.metadata.BuiltinModMetadata;
import net.fabricmc.loader.impl.util.Arguments;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

public class AppGameProvider implements GameProvider
{
    private Arguments arguments;
    private Path gameDirectory;
    private GameTransformer transformer = new AppGameTransformer();
    private List<Path> jars = new ArrayList<Path>();
    private Set<String> jarFiles;

    public AppGameProvider()
    {
        this.jarFiles = new HashSet<String>();

        this.jarFiles.add("mchorse/bbs/BBS.class");
        this.jarFiles.add("mchorse/Launcher.class");
    }

    @Override
    public String getGameId()
    {
        return "bbs";
    }

    @Override
    public String getGameName()
    {
        return "BBS";
    }

    @Override
    public String getRawGameVersion()
    {
        return Game.FULL_VERSION;
    }

    @Override
    public String getNormalizedGameVersion()
    {
        return Game.VERSION;
    }

    @Override
    public Collection<BuiltinMod> getBuiltinMods()
    {
        BuiltinMod mod = new BuiltinMod(this.jars, new BuiltinModMetadata.Builder("bbs", Game.VERSION).build());

        return Arrays.asList(mod);
    }

    @Override
    public String getEntrypoint()
    {
        return "mchorse.app.Game";
    }

    @Override
    public Path getLaunchDirectory()
    {
        return this.gameDirectory;
    }

    @Override
    public boolean isObfuscated()
    {
        return false;
    }

    @Override
    public boolean requiresUrlClassLoader()
    {
        return false;
    }

    @Override
    public boolean isEnabled()
    {
        return true;
    }

    @Override
    public boolean locateGame(FabricLauncher launcher, String[] args)
    {
        this.arguments = new Arguments();
        this.arguments.parse(args);
        this.arguments.put("fabric", "1");

        this.gameDirectory = Paths.get(this.arguments.get("gameDirectory"));

        for (Path path : launcher.getClassPath())
        {
            if (this.isBBS(path))
            {
                this.jars.add(path);
            }
        }

        return Files.isDirectory(this.gameDirectory);
    }

    private boolean isBBS(Path path)
    {
        if (Files.isDirectory(path))
        {
            for (String string : this.jarFiles)
            {
                if (Files.exists(path.resolve(string)))
                {
                    return true;
                }
            }

            return false;
        }

        try (ZipFile zip = new ZipFile(path.toFile()))
        {
            for (String string : this.jarFiles)
            {
                if (zip.getEntry(string) != null)
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void initialize(FabricLauncher launcher)
    {}

    @Override
    public GameTransformer getEntrypointTransformer()
    {
        return this.transformer;
    }

    @Override
    public void unlockClassPath(FabricLauncher launcher)
    {
        for (Path path : this.jars)
        {
            launcher.addToClassPath(path);
        }
    }

    @Override
    public void launch(ClassLoader loader)
    {
        try
        {
            Class main = loader.loadClass(this.getEntrypoint());
            Method method = main.getMethod("main", String[].class);

            method.invoke(null, (Object) this.arguments.toArray());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public Arguments getArguments()
    {
        return this.arguments;
    }

    @Override
    public String[] getLaunchArguments(boolean sanitize)
    {
        return this.arguments.toArray();
    }
}