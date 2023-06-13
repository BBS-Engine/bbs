package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeHUD;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.huds.HUDForm;
import mchorse.bbs.game.huds.HUDScene;
import mchorse.bbs.game.huds.HUDStage;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.user.global.IScriptHUD;

public class ScriptHUD implements IScriptHUD
{
    private ScriptBBS factory;

    public ScriptHUD(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public boolean setup(String id)
    {
        HUDScene scene = BBSData.getHUDs().load(id);

        if (scene != null)
        {
            this.factory.getBridge().get(IBridgeHUD.class).getHUDStage().scenes.put(id, scene);
        }

        return scene != null;
    }

    @Override
    public void changeForm(String id, int index, Form form)
    {
        if (form == null)
        {
            return;
        }

        HUDScene scene = this.factory.getBridge().get(IBridgeHUD.class).getHUDStage().scenes.get(id);

        if (scene != null && index >= 0 && index < scene.forms.size())
        {
            HUDForm hudForm = scene.forms.get(index);

            hudForm.form = form;
        }
    }

    @Override
    public void changeForm(String id, int index, MapType form)
    {
        if (form == null)
        {
            return;
        }

        this.changeForm(id, index, FormUtils.fromData(form));
    }

    @Override
    public void close(String id)
    {
        HUDStage stage = this.factory.getBridge().get(IBridgeHUD.class).getHUDStage();

        if (id == null)
        {
            stage.scenes.clear();
        }
        else
        {
            stage.scenes.remove(id);
        }
    }
}