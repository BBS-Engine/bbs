package mchorse.bbs.ui.world.objects.objects;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.objects.CameraObject;
import org.joml.Vector3d;

public class UICameraWorldObject extends UIWorldObject<CameraObject>
{
    public UIToggle enabled;
    public UITextbox texture;
    public UITrackpad textureWidth;
    public UITrackpad textureHeight;
    public UIVector3d rotation;

    public UIButton copyCamera;

    public UICameraWorldObject()
    {
        super();

        this.enabled = new UIToggle(UIKeys.WORLD_OBJECTS_CAMERA_ENABLED, (b) -> this.object.enabled = b.getValue());
        this.texture = new UITextbox(200, (t) -> this.object.texture = t.isEmpty() ? null : Link.create(t)).delayedInput();
        this.textureWidth = new UITrackpad((v) -> this.object.textureWidth = v.intValue()).limit(1, 4096).integer();
        this.textureHeight = new UITrackpad((v) -> this.object.textureHeight = v.intValue()).limit(1, 4096).integer();
        this.rotation = new UIVector3d((v) ->
        {
            this.object.rotation.set(MathUtils.toRad((float) v.x), MathUtils.toRad((float) v.y), MathUtils.toRad((float) v.z));
        });

        this.copyCamera = new UIButton(UIKeys.WORLD_OBJECTS_CAMERA_COPY_CAMERA, (b) ->
        {
            Camera camera = this.getContext().menu.bridge.get(IBridgeCamera.class).getCamera();

            this.object.position.set(camera.position).sub(0.5D, 0.5D, 0.5D);
            this.object.rotation.set(camera.rotation);

            this.fillData(this.object);
        });

        this.add(this.enabled);
        this.add(UI.label(UIKeys.WORLD_OBJECTS_CAMERA_TEXTURE).marginTop(8), this.texture, UI.row(this.textureWidth, this.textureHeight));
        this.add(UI.label(UIKeys.WORLD_OBJECTS_CAMERA_ROTATION).marginTop(8), this.rotation);
        this.add(this.copyCamera.marginTop(8));
    }

    @Override
    public void fillData(CameraObject object)
    {
        super.fillData(object);

        this.enabled.setValue(object.enabled);
        this.texture.setText(object.texture == null ? "" : object.texture.toString());
        this.textureWidth.setValue(object.textureWidth);
        this.textureHeight.setValue(object.textureHeight);
        this.rotation.fill(new Vector3d(MathUtils.toDeg(object.rotation.x), MathUtils.toDeg(object.rotation.y), MathUtils.toDeg(object.rotation.z)));
    }
}