package mchorse.bbs.ui.game.triggers;

import mchorse.bbs.game.triggers.Trigger;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;

public class UITrigger extends UIElement
{
    public UIButton open;

    private Trigger trigger;
    private Runnable onClose;

    public UITrigger()
    {
        this(null);
    }

    public UITrigger(Trigger trigger)
    {
        super();

        this.open = new UIButton(UIKeys.TRIGGER_EDIT, (b) -> this.openTriggerEditor());
        this.open.relative(this).full();

        this.h(20);

        this.add(this.open);

        this.set(trigger);
    }

    public UITrigger onClose(Runnable onClose)
    {
        this.onClose = onClose;

        return this;
    }

    private void openTriggerEditor()
    {
        UITriggerOverlayPanel panel = new UITriggerOverlayPanel(this.trigger);

        if (this.onClose != null)
        {
            panel.onClose((e) -> this.onClose.run());
        }

        UIOverlay.addOverlay(this.getContext(), panel, 0.55F, 0.75F);
    }

    private void updateTooltip()
    {
        if (this.trigger == null)
        {
            this.tooltip = null;
        }
        else
        {
            this.tooltip(UIKeys.TRIGGER_QUANTITY.format(this.trigger.blocks.size()));
        }
    }

    public Trigger get()
    {
        return this.trigger;
    }

    public void set(Trigger trigger)
    {
        this.trigger = trigger;

        this.updateTooltip();
    }
}