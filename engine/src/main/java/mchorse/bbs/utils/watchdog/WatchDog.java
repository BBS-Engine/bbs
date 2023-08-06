package mchorse.bbs.utils.watchdog;

import mchorse.bbs.BBS;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class WatchDog implements Runnable
{
    private Path folder;
    private List<IWatchDogListener> listeners = new ArrayList<>();

    private WatchService service;
    private Map<WatchKey, Path> keys = new HashMap<>();
    private Thread thread;
    private boolean stopThread;

    public WatchDog(File folder)
    {
        this.folder = folder.toPath();
    }

    public void register(IWatchDogListener listener)
    {
        this.listeners.add(listener);
    }

    public void registerFolder(Path path)
    {
        try
        {
            WatchKey key = path.register(this.service,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
            );

            this.keys.put(key, path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void registerFolderRecursive(final Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException
            {
                WatchDog.this.registerFolder(dir);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void start()
    {
        this.thread = new Thread(this);

        this.thread.start();
    }

    public void stop()
    {
        this.stopThread = true;
    }

    @Override
    public void run()
    {
        try
        {
            this.service = FileSystems.getDefault().newWatchService();

            this.registerFolderRecursive(this.folder);
        }
        catch (IOException e)
        {
            System.err.println("Failed to start a watch dog thread!");

            e.printStackTrace();
        }

        while (!this.stopThread)
        {
            if (!this.pollEvents())
            {
                return;
            }
        }
    }

    /**
     * @return {@code true} if everything is fine, {@code false} if watch dog has to be
     * stopped.
     */
    private boolean pollEvents()
    {
        WatchKey key;

        try
        {
            key = this.service.poll(1, TimeUnit.SECONDS);
        }
        catch (InterruptedException x)
        {
            return false;
        }

        if (key == null)
        {
            return true;
        }

        Path folder = this.keys.get(key);

        for (WatchEvent<?> event : key.pollEvents())
        {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == StandardWatchEventKinds.OVERFLOW)
            {
                return true;
            }

            WatchEvent<Path> e = (WatchEvent<Path>) event;
            Path filename = e.context();
            Path file = folder.resolve(filename);

            if (Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS) && file.toFile().length() == 0)
            {
                continue;
            }

            if (kind == StandardWatchEventKinds.ENTRY_CREATE && Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS))
            {
                try
                {
                    this.registerFolderRecursive(file);
                }
                catch (Exception x)
                {
                    x.printStackTrace();
                }
            }

            WatchDogEvent type = WatchDogEvent.CREATED;

            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) type = WatchDogEvent.MODIFIED;
            else if (kind == StandardWatchEventKinds.ENTRY_DELETE) type = WatchDogEvent.DELETED;

            final WatchDogEvent finalType = type;

            BBS.getEngine().scheduledRunnables.add(() ->
            {
                for (IWatchDogListener listener : this.listeners)
                {
                    listener.accept(file, finalType);
                }
            });
        }

        if (!key.reset())
        {
            this.keys.remove(key);

            if (this.keys.isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}