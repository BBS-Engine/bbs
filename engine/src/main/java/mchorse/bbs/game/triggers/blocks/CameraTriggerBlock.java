package mchorse.bbs.game.triggers.blocks;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.controller.PlayCameraController;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.game.utils.EnumUtils;

public class CameraTriggerBlock extends StringTriggerBlock
{
    public CameraMode mode = CameraMode.PLAY;

    public CameraTriggerBlock()
    {
        super();
    }

    @Override
    public void trigger(DataContext context)
    {
        IBridge bridge = context.world.bridge;

        if (this.mode == CameraMode.PLAY && !this.id.isEmpty())
        {
            CameraWork work = BBSData.getCameras().load(this.id);

            if (work != null)
            {
                bridge.get(IBridgeCamera.class).getCameraController().add(new PlayCameraController(bridge, work));
            }
        }
        else if (this.mode == CameraMode.STOP)
        {
            bridge.get(IBridgeCamera.class).getCameraController().remove(PlayCameraController.class);
        }
    }

    @Override
    protected String getKey()
    {
        return "camera";
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

        this.mode = EnumUtils.getValue(data.getInt("mode"), CameraMode.values(), CameraMode.PLAY);
    }

    public enum CameraMode
    {
        PLAY, STOP;
    }
}