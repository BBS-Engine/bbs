package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.StateTriggerBlock;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIStateTriggerBlockPanel extends UITriggerBlockPanel<StateTriggerBlock>
{
    public UITextbox id;
    public UITarget target;
    public UICirculate mode;

    public UILabel valueLabel;
    public UIElement valueRow;
    public UIIcon convert;
    public UIElement value;

    public UIStateTriggerBlockPanel(UITriggerOverlayPanel overlay, StateTriggerBlock block)
    {
        super(overlay, block);

        this.id = new UITextbox(1000, (v) -> this.block.id = v);
        this.target = new UITarget(null);
        this.mode = new UICirculate(this::toggleItemCheck);

        for (StateTriggerBlock.StateMode mode : StateTriggerBlock.StateMode.values())
        {
            this.mode.addLabel(UIKeys.C_STATE_MODE.get(mode));
        }

        this.valueLabel = UI.label(UIKeys.CONDITIONS_VALUE);
        this.valueRow = UI.row(0);
        this.convert = new UIIcon(Icons.REFRESH, this::convert);

        this.id.setText(block.id);
        this.target.setTarget(block.target);
        this.mode.setValue(block.mode.ordinal());

        this.add(this.mode);
        this.add(UI.label(UIKeys.CONDITIONS_STATE_ID).marginTop(12), this.id);
        this.add(this.target.marginTop(12));
        this.add(this.valueLabel.marginTop(12), this.valueRow);

        this.toggleItemCheck(this.mode);
        this.updateValue();
    }

    private void toggleItemCheck(UICirculate b)
    {
        this.block.mode = StateTriggerBlock.StateMode.values()[b.getValue()];
        this.updateValue();
    }

    private void convert(UIIcon element)
    {
        Object object = this.block.value;

        if (object instanceof String)
        {
            this.block.value = 0D;
        }
        else
        {
            this.block.value = "";
        }

        this.updateValue();
    }

    private void updateValue()
    {
        Object object = this.block.value;

        if (object instanceof String)
        {
            if (this.block.mode == StateTriggerBlock.StateMode.ADD)
            {
                this.block.value = object = 0D;
            }
            else
            {
                UITextbox element = new UITextbox(10000, this::updateString);

                element.setText((String) object);
                this.value = element;
            }
        }

        if (object instanceof Number)
        {
            UITrackpad element = new UITrackpad(this::updateNumber);

            element.setValue(((Number) object).doubleValue());
            this.value = element;
        }

        this.valueLabel.setVisible(this.block.mode != StateTriggerBlock.StateMode.REMOVE);
        this.valueRow.removeAll();

        if (this.block.mode != StateTriggerBlock.StateMode.REMOVE)
        {
            this.valueRow.add(this.value);

            if (this.block.mode != StateTriggerBlock.StateMode.ADD)
            {
                this.valueRow.add(this.convert);
            }
        }

        if (this.hasParent())
        {
            this.getParentContainer().resize();
        }
    }

    private void updateString(String s)
    {
        this.block.value = s;
    }

    private void updateNumber(double v)
    {
        this.block.value = v;
    }
}