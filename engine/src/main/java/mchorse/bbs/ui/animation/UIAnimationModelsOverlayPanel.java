package mchorse.bbs.ui.animation;

import mchorse.bbs.animation.Animation;
import mchorse.bbs.animation.AnimationModel;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.overlay.UIMapOverlayPanel;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import org.joml.Vector3d;

import java.util.function.Consumer;

public class UIAnimationModelsOverlayPanel extends UIMapOverlayPanel<AnimationModel>
{
    public UIAnimationModelsOverlayPanel(Animation animation, Consumer<String> callback)
    {
        super(UIKeys.ANIMATION_MODEL_TITLE, animation.models, callback);

        this.addContext = UIKeys.ANIMATION_MODEL_CONTEXT_ADD;
        this.copyContext = UIKeys.ANIMATION_MODEL_CONTEXT_COPY;
        this.pasteContext = UIKeys.ANIMATION_MODEL_CONTEXT_PASTE;
        this.renameContext = UIKeys.ANIMATION_MODEL_CONTEXT_RENAME;
        this.removeContext = UIKeys.ANIMATION_MODEL_CONTEXT_REMOVE;
        this.addOverlay = UIKeys.ANIMATION_MODEL_ADD;
        this.pasteOverlay = UIKeys.ANIMATION_MODEL_PASTE;
        this.renameOverlay = UIKeys.ANIMATION_MODEL_RENAME;
        this.removeOverlay = UIKeys.ANIMATION_MODEL_REMOVE;
    }

    @Override
    protected String getCopyKey()
    {
        return "_AnimationModelCopy";
    }

    @Override
    protected AnimationModel create()
    {
        AnimationModel model = new AnimationModel();

        IBridge bridge = this.getContext().menu.bridge;
        Camera camera = bridge.get(IBridgeCamera.class).getCamera();
        RayTraceResult result = new RayTraceResult();
        Vector3d position;

        RayTracer.trace(result, bridge.get(IBridgeWorld.class).getWorld().chunks, camera.position, camera.getLookDirection(), 64);

        if (result.type != RayTraceType.MISS)
        {
            position = result.hit;
        }
        else
        {
            position = new Vector3d(camera.getLookDirection()).mul(5).add(camera.position);
        }

        model.insert(position);

        return model;
    }
}