package mchorse.bbs.ui.game.triggers.panels;

import mchorse.bbs.game.triggers.blocks.ScriptTriggerBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.triggers.UITriggerOverlayPanel;
import mchorse.bbs.ui.utils.UI;

import java.util.List;

public class UIScriptTriggerBlockPanel extends UIDataTriggerBlockPanel<ScriptTriggerBlock>
{
    public UITextbox function;
    public UIToggle inline;
    public UITextEditor code;

    private List<UIElement> allElements;
    private UIElement elements;

    public UIScriptTriggerBlockPanel(UITriggerOverlayPanel overlay, ScriptTriggerBlock block)
    {
        super(overlay, block);

        this.function = new UITextbox(100, (text) -> this.block.function = text);
        this.function.setText(block.function);
        this.function.tooltip(UIKeys.TRIGGERS_SCRIPT_FUNCTION_TOOLTIP);

        this.inline = new UIToggle(UIKeys.TRIGGERS_INLINE, (b) ->
        {
            this.block.inline = b.getValue();

            this.updateFields();
        });
        this.inline.setValue(this.block.inline);

        this.code = new UITextEditor((t) -> this.block.code = t);
        this.code.setText(this.block.code);
        this.code.background().h(80);

        this.elements = UI.column();

        this.add(UI.label(UIKeys.TRIGGERS_FUNCTION).marginTop(12), this.function);

        this.allElements = this.getChildren(UIElement.class);
        this.allElements.remove(0);

        for (UIElement element : this.allElements)
        {
            element.removeFromParent();

            this.elements.add(element);
        }

        this.add(this.elements);
        this.updateFields();
        this.addDelay();
    }

    private void updateFields()
    {
        this.elements.removeAll();
        this.elements.add(this.inline);

        if (this.block.inline)
        {
            this.elements.add(this.code);
        }
        else
        {
            for (UIElement element : this.allElements)
            {
                this.elements.add(element);
            }
        }

        if (this.hasParent())
        {
            this.getParentContainer().resize();
        }
    }

    @Override
    protected ContentType getType()
    {
        return ContentType.SCRIPTS;
    }
}