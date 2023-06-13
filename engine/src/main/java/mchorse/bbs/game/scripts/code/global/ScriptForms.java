package mchorse.bbs.game.scripts.code.global;

import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.scripts.code.ScriptBBS;
import mchorse.bbs.game.scripts.user.global.IScriptForms;

public class ScriptForms implements IScriptForms
{
    private ScriptBBS factory;

    public ScriptForms(ScriptBBS factory)
    {
        this.factory = factory;
    }

    @Override
    public Form create(String string)
    {
        return this.create(this.factory.getData().map(string));
    }

    @Override
    public Form create(MapType data)
    {
        if (data == null)
        {
            return null;
        }

        return FormUtils.fromData(data);
    }
}
