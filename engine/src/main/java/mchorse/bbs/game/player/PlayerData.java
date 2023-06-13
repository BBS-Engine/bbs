package mchorse.bbs.game.player;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.IMapSerializable;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.controllers.IGameController;
import mchorse.bbs.game.controllers.ThirdPersonGameController;
import mchorse.bbs.resources.Link;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

import java.io.File;
import java.io.IOException;

public class PlayerData implements IMapSerializable
{
    private IBridge bridge;
    private File file;

    private MapType playerData = new MapType();
    private IGameController gameController;

    public PlayerData(IBridge bridge, File file)
    {
        this.bridge = bridge;
        this.file = file;
    }

    public void setGameController(IGameController gameController)
    {
        if (gameController != null)
        {
            if (this.gameController != null)
            {
                this.gameController.disable();
            }

            this.gameController = gameController;

            gameController.initilize(this.bridge);
            gameController.enable();
        }
    }

    public IGameController getGameController()
    {
        return this.gameController;
    }

    public void load()
    {
        try
        {
            this.fromData((MapType) DataToString.read(this.file));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (this.gameController == null)
        {
            this.setGameController(new ThirdPersonGameController());
        }
    }

    public void save()
    {
        DataToString.writeSilently(this.file, this.toData(), true);
    }

    public void updatePlayerData(MapType playerData)
    {
        this.playerData = playerData.copy().asMap();
    }

    @Override
    public void fromData(MapType data)
    {
        this.playerData = data.getMap("player");

        try
        {
            IGameController controller = BBS.getFactoryGameControllers().fromData(data.getMap("gameController"));

            if (controller != null)
            {
                this.setGameController(controller);
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to load game controller!");
            e.printStackTrace();
        }
    }

    @Override
    public void toData(MapType data)
    {
        data.put("player", this.playerData.copy());
        data.put("gameController", BBS.getFactoryGameControllers().toData(this.gameController));
    }

    public Entity createPlayer(EntityArchitect architect)
    {
        Entity entity = null;

        try
        {
            entity = architect.create(this.playerData);
            entity.canBeSaved = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            entity = architect.create(Link.bbs("player"));
            entity.canBeSaved = false;

            this.playerData = entity.toData();
        }

        return entity;
    }
}