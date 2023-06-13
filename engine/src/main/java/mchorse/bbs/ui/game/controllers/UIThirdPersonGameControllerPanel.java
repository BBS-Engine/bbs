package mchorse.bbs.ui.game.controllers;

import mchorse.bbs.game.controllers.ThirdPersonGameController;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;

public class UIThirdPersonGameControllerPanel extends UIBaseGameControllerPanel<ThirdPersonGameController>
{
    public UIToggle firstPerson;

    public UIThirdPersonGameControllerPanel()
    {
        super();

        this.firstPerson = new UIToggle(UIKeys.PLAYER_DATA_FIRST_PERSON, (b) -> this.gameController.firstPerson = b.getValue());

        this.prepend(this.firstPerson);
    }

    @Override
    public void fill(ThirdPersonGameController gameController)
    {
        super.fill(gameController);

        this.firstPerson.setValue(gameController.firstPerson);
    }
}