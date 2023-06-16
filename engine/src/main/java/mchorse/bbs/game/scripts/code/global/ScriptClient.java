package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.BBS;
import mchorse.bbs.game.scripts.user.global.IScriptClient;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;

public class ScriptClient implements IScriptClient
{
    @Override
    public void reload(String component)
    {
        boolean all = component == null || component.isEmpty();

        if (all || component.equals("textures"))
        {
            BBS.getTextures().reload();
        }

        if (all || component.equals("language"))
        {
            BBS.getL10n().reload();
        }

        if (all || component.equals("models"))
        {
            BBS.getModels().reload();
        }
    }

    @Override
    public void setWindowSize(int w, int h)
    {
        Window.setSize(w, h);
    }

    @Override
    public boolean playVideo(String path)
    {
        return BBS.getTextures().playVideo(Link.create(path));
    }

    @Override
    public boolean stopVideo(String path)
    {
        return BBS.getTextures().stopVideo(Link.create(path));
    }
}