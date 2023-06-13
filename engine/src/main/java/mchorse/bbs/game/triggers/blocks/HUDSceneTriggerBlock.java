package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgeHUD;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.huds.HUDScene;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;

public class HUDSceneTriggerBlock extends StringTriggerBlock
{
    public HUDMode mode = HUDMode.SETUP;

    @Override
    public void trigger(DataContext context)
    {
        if (this.mode == HUDMode.SETUP)
        {
            HUDScene scene = BBSData.getHUDs().load(id);

            if (scene != null)
            {
                context.world.bridge.get(IBridgeHUD.class).getHUDStage().scenes.put(this.id, scene);
            }
        }
        else if (this.mode == HUDMode.STOP)
        {
            context.world.bridge.get(IBridgeHUD.class).getHUDStage().scenes.remove(this.id);
        }
    }

    @Override
    protected String getKey()
    {
        return "scene";
    }

    @Override
    public void toData(MapType data)
    {
        super.toData(data);

        data.putInt("mode", this.mode.ordinal());
    }

    @Override
    public void fromData(MapType data)
    {
        super.fromData(data);

        this.mode = EnumUtils.getValue(data.getInt("mode"), HUDMode.values(), HUDMode.SETUP);
    }

    public static enum HUDMode
    {
        SETUP, STOP;
    }
}