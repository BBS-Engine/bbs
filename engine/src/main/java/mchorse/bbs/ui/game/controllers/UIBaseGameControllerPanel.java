package mchorse.bbs.ui.game.controllers;

import mchorse.bbs.game.controllers.BaseGameController;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;

public class UIBaseGameControllerPanel <T extends BaseGameController> extends UIElement
{
    public UITrackpad fov;
    public UIVector3d cameraOffset;
    public UIToggle jump;
    public UIToggle jumpGround;

    protected T gameController;

    public UIBaseGameControllerPanel()
    {
        super();

        this.fov = new UITrackpad((v) -> this.gameController.fov = v.floatValue());
        this.fov.limit(0, 180);
        this.cameraOffset = new UIVector3d((v) -> this.gameController.cameraOffset.set(v));
        this.jump = new UIToggle(UIKeys.PLAYER_DATA_JUMP, (b) -> this.gameController.jump = b.getValue());
        this.jump.tooltip(UIKeys.PLAYER_DATA_JUMP_TOOLTIP);
        this.jumpGround = new UIToggle(UIKeys.PLAYER_DATA_JUMP_GROUND, (b) -> this.gameController.jumpGround = b.getValue());
        this.jumpGround.tooltip(UIKeys.PLAYER_DATA_JUMP_GROUND_TOOLTIP);

        this.add(UI.label(UIKeys.PLAYER_DATA_FOV), this.fov);
        this.add(UI.label(UIKeys.PLAYER_DATA_CAMERA_OFFSET), this.cameraOffset);
        this.add(this.jump.marginTop(8));
        this.add(this.jumpGround);

        this.column().vertical().stretch();
    }

    public void fill(T gameController)
    {
        this.gameController = gameController;

        this.fov.setValue(gameController.fov);
        this.cameraOffset.fill(gameController.cameraOffset);
        this.jump.setValue(gameController.jump);
        this.jumpGround.setValue(gameController.jumpGround);
    }
}