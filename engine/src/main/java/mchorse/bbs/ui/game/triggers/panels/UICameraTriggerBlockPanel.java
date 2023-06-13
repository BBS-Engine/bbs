package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.CameraTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.utils.UI;

public class UICameraTriggerBlockPanel extends UIStringTriggerBlockPanel<CameraTriggerBlock>
{
    public UICirculate mode;

    public UICameraTriggerBlockPanel(UITriggerOverlayPanel overlay, CameraTriggerBlock block)
    {
        super(overlay, block);

        this.mode = new UICirculate(this::toggleItemCheck);

        for (CameraTriggerBlock.CameraMode mode : CameraTriggerBlock.CameraMode.values())
        {
            this.mode.addLabel(UIKeys.C_CAMERA_MODE.get(mode));
        }

        this.mode.setValue(block.mode.ordinal());

        this.add(UI.label(UIKeys.TRIGGERS_CAMERA_MODE).marginTop(12), this.mode);
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.CAMERAS;
    }

    private void toggleItemCheck(UICirculate b)
    {
        this.block.mode = CameraTriggerBlock.CameraMode.values()[b.getValue()];
    }
}