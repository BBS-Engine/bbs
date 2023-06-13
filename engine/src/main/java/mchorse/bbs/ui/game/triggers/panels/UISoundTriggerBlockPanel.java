package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.SoundTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.utils.UI;

public class UISoundTriggerBlockPanel extends UIStringTriggerBlockPanel<SoundTriggerBlock>
{
    public UICirculate playMode;

    public UISoundTriggerBlockPanel(UITriggerOverlayPanel overlay, SoundTriggerBlock block)
    {
        super(overlay, block);

        this.playMode = new UICirculate((target) -> this.block.playMode = SoundTriggerBlock.PlayMode.values()[target.getValue()]);

        for (SoundTriggerBlock.PlayMode mode : SoundTriggerBlock.PlayMode.values())
        {
            this.playMode.addLabel(UIKeys.C_PLAY_MODE.get(mode));
        }

        this.playMode.setValue(block.playMode.ordinal());

        this.add(UI.label(UIKeys.CONDITIONS_TARGET).marginTop(12), this.playMode);

        this.addDelay();
    }

    @Override
    protected IKey getLabel()
    {
        return UIKeys.OVERLAYS_SOUNDS_MAIN;
    }

    @Override
    protected ContentType getType()
    {
        return null;
    }

    @Override
    protected void openOverlay()
    {
        UISoundOverlayPanel overlay = new UISoundOverlayPanel(this::setSound);

        UIOverlay.addOverlay(this.getContext(), overlay.set(this.block.id), 0.5F, 0.9F);
    }

    private void setSound(Link location)
    {
        this.block.id = location == null ? "" : location.toString();
    }
}