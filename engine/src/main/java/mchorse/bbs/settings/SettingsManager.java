package mchorse.bbs.settings;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.settings.values.ValueBoolean;
import mchorse.bbs.settings.values.ValueColors;
import mchorse.bbs.settings.values.ValueDouble;
import mchorse.bbs.settings.values.ValueFloat;
import mchorse.bbs.settings.values.ValueInt;
import mchorse.bbs.settings.values.ValueLink;
import mchorse.bbs.settings.values.ValueString;
import mchorse.bbs.settings.values.base.BaseValue;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SettingsManager
{
    public static final HashMap<String, Class<? extends BaseValue>> TYPES = new HashMap<>();

    public final Map<String, Settings> modules = new HashMap<>();

    static
    {
        TYPES.put("boolean", ValueBoolean.class);
        TYPES.put("double", ValueDouble.class);
        TYPES.put("float", ValueFloat.class);
        TYPES.put("int", ValueInt.class);
        TYPES.put("link", ValueLink.class);
        TYPES.put("string", ValueString.class);
        TYPES.put("colors", ValueColors.class);
    }

    public void reload()
    {
        for (Settings settings : this.modules.values())
        {
            this.load(settings, settings.file);
        }
    }

    public boolean load(Settings settings, File file)
    {
        if (!file.exists())
        {
            settings.save(file);

            return false;
        }

        try
        {
            settings.fromData(DataToString.read(file));

            return true;
        }
        catch (Exception e)
        {}

        return false;
    }
}