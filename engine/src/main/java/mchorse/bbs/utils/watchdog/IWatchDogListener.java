package mchorse.bbs.utils.watchdog;

import mchorse.bbs.BBS;
import mchorse.bbs.resources.Link;

import java.nio.file.Path;

public interface IWatchDogListener
{
    public static Link getAssetsLink(Path path)
    {
        String relativePath = BBS.getAssetsFolder().toPath().relativize(path).toString();

        return Link.assets(relativePath.replaceAll("\\\\", "/"));
    }

    public void accept(Path path, WatchDogEvent event);
}