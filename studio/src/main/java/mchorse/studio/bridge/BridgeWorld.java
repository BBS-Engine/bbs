package mchorse.studio.bridge;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.voxel.storage.ChunkFactory;
import mchorse.bbs.world.World;
import mchorse.bbs.world.WorldMetadata;
import mchorse.studio.StudioEngine;

import java.io.File;

public class BridgeWorld extends BaseBridge implements IBridgeWorld
{
    public BridgeWorld(StudioEngine engine)
    {
        super(engine);
    }

    @Override
    public World getWorld()
    {
        return this.engine.world;
    }

    @Override
    public boolean loadWorld(String world)
    {
        File worldFolder = BBS.getGamePath("worlds/" + world);

        if (!worldFolder.isDirectory())
        {
            return false;
        }

        WorldMetadata metadata = WorldMetadata.fromFile(worldFolder);

        if (this.engine.world != null)
        {
            this.engine.world.delete();
            this.engine.world = null;
        }

        ChunkFactory factory = metadata.createFactory();

        this.engine.world = new World(this.engine, factory, metadata.createGenerator());

        this.engine.world.initialize(factory);
        this.engine.world.readExtraData(metadata.save);
        this.engine.screen.reload(this.engine.world);

        this.engine.cameraController.camera.setFarNear(0.01F, (this.engine.world.chunks.getW() * this.engine.world.chunks.s) / 2F);
        this.engine.cameraController.camera.updatePerspectiveProjection(Window.width, Window.height);

        return true;
    }
}