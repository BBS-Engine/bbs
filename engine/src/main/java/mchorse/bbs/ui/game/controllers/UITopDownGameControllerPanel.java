package mchorse.bbs.ui.game.controllers;

import mchorse.bbs.game.controllers.TopDownGameController;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;

public class UITopDownGameControllerPanel extends UIBaseGameControllerPanel<TopDownGameController>
{
    public UITrackpad pitch;

    public UITopDownGameControllerPanel()
    {
        super();

        this.pitch = new UITrackpad((v) -> this.gameController.pitch = v.floatValue());

        this.addAfter(this.cameraOffset, this.pitch);
        this.addAfter(this.cameraOffset, UI.label(UIKeys.PLAYER_DATA_CAMERA_PITCH));
    }

    @Override
    public void fill(TopDownGameController gameController)
    {
        super.fill(gameController);

        this.pitch.setValue(gameController.pitch);
    }
}