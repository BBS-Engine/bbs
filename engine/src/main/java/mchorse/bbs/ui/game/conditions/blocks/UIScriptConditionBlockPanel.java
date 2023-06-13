package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.ScriptConditionBlock;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;

public class UIScriptConditionBlockPanel extends UIConditionBlockPanel<ScriptConditionBlock>
{
    public UIButton script;
    public UITextbox function;
    public UITextbox customData;
    public UIToggle inline;
    public UITextEditor code;

    private UIElement elements;

    public UIScriptConditionBlockPanel(UIConditionOverlayPanel overlay, ScriptConditionBlock block)
    {
        super(overlay, block);

        this.script = new UIButton(UIKeys.OVERLAYS_SCRIPT, (b) -> this.openScripts());
        this.function = new UITextbox(100, (t) -> this.block.function = t);
        this.function.setText(this.block.function);
        this.customData = new UITextbox(10000, (t) -> this.block.customData = t);
        this.customData.setText(this.block.customData);
        this.inline = new UIToggle(UIKeys.CONDITIONS_SCRIPT_INLINE, (b) ->
        {
            this.block.inline = b.getValue();

            this.updateFields();
        });
        this.inline.setValue(this.block.inline);

        this.code = new UITextEditor((t) -> this.block.code = t);
        this.code.setText(this.block.code);
        this.code.background().h(80);

        this.elements = UI.column().marginTop(12);

        this.add(this.elements);
        this.updateFields();
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
            this.elements.add(this.script);
            this.elements.add(UI.label(UIKeys.TRIGGERS_FUNCTION).marginTop(12), this.function);
            this.elements.add(UI.label(UIKeys.NODES_EVENT_DATA).marginTop(12), this.customData);
        }

        if (this.hasParent())
        {
            this.getParentContainer().resize();
        }
    }

    private void openScripts()
    {
        UIDataUtils.openPicker(this.getContext(), ContentType.SCRIPTS, this.block.script, (name) -> this.block.script = name);
    }
}