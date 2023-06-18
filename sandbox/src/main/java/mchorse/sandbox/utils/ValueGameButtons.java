package mchorse.sandbox.utils;

import mchorse.sandbox.ui.UIKeysApp;
import mchorse.bbs.BBS;
import mchorse.bbs.settings.values.ValueUI;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UIUtils;

import java.util.Arrays;
import java.util.List;

public class ValueGameButtons extends ValueUI
{
    public ValueGameButtons(String id)
    {
        super(id);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        return Arrays.asList(new UIButton(UIKeysApp.BUTTONS_OPEN_FOLDER, (b) -> UIUtils.openFolder(BBS.getGameFolder())));
    }
}