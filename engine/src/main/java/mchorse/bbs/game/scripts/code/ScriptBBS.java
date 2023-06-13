package mchorse.bbs.game.scripts.code;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.scripts.code.global.ScriptAnimations;
import mchorse.bbs.game.scripts.code.global.ScriptCamera;
import mchorse.bbs.game.scripts.code.global.ScriptClient;
import mchorse.bbs.game.scripts.code.global.ScriptData;
import mchorse.bbs.game.scripts.code.global.ScriptForms;
import mchorse.bbs.game.scripts.code.global.ScriptHUD;
import mchorse.bbs.game.scripts.code.global.ScriptItems;
import mchorse.bbs.game.scripts.code.global.ScriptUI;
import mchorse.bbs.game.scripts.code.global.ScriptWorlds;
import mchorse.bbs.game.scripts.user.IScriptBBS;
import mchorse.bbs.game.scripts.user.IScriptBlockVariant;
import mchorse.bbs.game.scripts.user.global.IScriptAnimations;
import mchorse.bbs.game.scripts.user.global.IScriptCamera;
import mchorse.bbs.game.scripts.user.global.IScriptClient;
import mchorse.bbs.game.scripts.user.global.IScriptData;
import mchorse.bbs.game.scripts.user.global.IScriptForms;
import mchorse.bbs.game.scripts.user.global.IScriptHUD;
import mchorse.bbs.game.scripts.user.global.IScriptItems;
import mchorse.bbs.game.scripts.user.global.IScriptUI;
import mchorse.bbs.game.scripts.user.global.IScriptWorlds;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.voxel.blocks.BlockLink;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.world.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ScriptBBS implements IScriptBBS
{
    private IScriptAnimations animations = new ScriptAnimations(this);
    private IScriptClient client = new ScriptClient();
    private IScriptItems items = new ScriptItems(this);
    private IScriptData data = new ScriptData();
    private IScriptForms forms = new ScriptForms(this);
    private IScriptUI ui = new ScriptUI(this);
    private IScriptCamera camera = new ScriptCamera(this);
    private IScriptHUD hud = new ScriptHUD(this);
    private IScriptWorlds worlds = new ScriptWorlds(this);

    private IBridge bridge;

    public ScriptBBS(IBridge bridge)
    {
        this.bridge = bridge;
    }

    public IBridge getBridge()
    {
        return this.bridge;
    }

    @Override
    public IScriptAnimations getAnimations()
    {
        return this.animations;
    }

    @Override
    public IScriptClient getClient()
    {
        return this.client;
    }

    @Override
    public IScriptItems getItems()
    {
        return this.items;
    }

    @Override
    public IScriptData getData()
    {
        return this.data;
    }

    @Override
    public IScriptForms getForms()
    {
        return this.forms;
    }

    @Override
    public IScriptUI getUi()
    {
        return this.ui;
    }

    @Override
    public IScriptCamera getCamera()
    {
        return this.camera;
    }

    @Override
    public IScriptHUD getHud()
    {
        return this.hud;
    }

    @Override
    public IScriptWorlds getWorlds()
    {
        return this.worlds;
    }

    @Override
    public IScriptBlockVariant getBlockVariant(String blockId, int variant)
    {
        World world = this.worlds.getCurrent().getRawWorld();
        IBlockVariant v = world.chunks.builder.models.getVariant(new BlockLink(Link.create(blockId), variant));

        return new ScriptBlockVariant(v);
    }

    @Override
    public boolean isDevelopment()
    {
        return this.bridge.get(IBridgePlayer.class).isDevelopment();
    }

    @Override
    public Object get(String key)
    {
        return BBSData.getScripts().objects.get(key);
    }

    @Override
    public void set(String key, Object object)
    {
        BBSData.getScripts().objects.put(key, object);
    }

    @Override
    public void send(String message)
    {
        this.bridge.get(IBridgeWorld.class).sendMessage(IKey.str(message));
    }

    @Override
    public String dump(Object object, boolean simple)
    {
        if (object instanceof ScriptObjectMirror)
        {
            return object.toString();
        }

        Class<?> clazz = object.getClass();
        StringBuilder output = new StringBuilder(simple ? clazz.getSimpleName() : clazz.getTypeName());

        output.append(" {\n");

        for (Field field : clazz.getDeclaredFields())
        {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            output.append("    ");

            if (!simple)
            {
                output.append(this.getModifier(field.getModifiers()));
            }

            output.append(field.getName());

            if (!simple)
            {
                output.append(" (");
                output.append(simple ? field.getType().getSimpleName() : field.getType().getTypeName());
                output.append(")");
            }

            String value = "";

            try
            {
                field.setAccessible(true);
                Object o = field.get(object);

                value = o == null ? "null" : o.toString();
            }
            catch (Exception e)
            {}

            output.append(": ").append(value).append("\n");
        }

        output.append("\n");

        for (Method method : clazz.getDeclaredMethods())
        {
            if (Modifier.isStatic(method.getModifiers()))
            {
                continue;
            }

            output.append("    ");

            if (!simple)
            {
                output.append(this.getModifier(method.getModifiers()));
            }

            output.append(simple ? method.getReturnType().getSimpleName() : method.getReturnType().getTypeName());
            output.append(" ");
            output.append(method.getName()).append("(");

            int size = method.getParameterCount();

            for (int i = 0; i < size; i++)
            {
                Class<?> arg = method.getParameterTypes()[i];

                output.append(simple ? arg.getSimpleName() : arg.getTypeName());

                if (i < size - 1)
                {
                    output.append(", ");
                }
            }

            output.append(")").append("\n");
        }

        output.append("}");

        return output.toString();
    }

    private String getModifier(int m)
    {
        String modifier = Modifier.isFinal(m) ? "final " : "";

        if (Modifier.isPublic(m))
        {
            modifier += "public ";
        }
        else if (Modifier.isProtected(m))
        {
            modifier += "protected ";
        }
        else if (Modifier.isPrivate(m))
        {
            modifier += "private ";
        }

        return modifier;
    }
}