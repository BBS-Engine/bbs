package mchorse.bbs.ui.game.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.values.ValueUI;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;

import java.util.Arrays;
import java.util.List;

public class ValueAudioButtons extends ValueUI
{
    public ValueAudioButtons(String id)
    {
        super(id);
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton openAudio = new UIButton(UIKeys.MAIN_OPEN_AUDIO, (button) -> UIUtils.openFolder(BBS.getAssetsPath("audio")));

        return Arrays.asList(UI.row(5, 0, 20, openAudio));
    }
}
