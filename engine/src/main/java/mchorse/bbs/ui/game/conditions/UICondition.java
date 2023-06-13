package mchorse.bbs.ui.game.conditions;

import mchorse.bbs.game.conditions.Condition;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;

public class UICondition extends UIElement
{
    public UIButton edit;

    private Condition condition;

    public UICondition()
    {
        this(null);
    }

    public UICondition(Condition condition)
    {
        super();

        this.edit = new UIButton(UIKeys.CONDITIONS_CONDITION_EDIT, this::openConditionEditor);

        this.h(20).row(0);

        this.set(condition);
        this.add(this.edit);
    }

    private void openConditionEditor(UIButton b)
    {
        UIConditionOverlayPanel panel = new UIConditionOverlayPanel(this.condition);

        UIOverlay.addOverlay(this.getContext(), panel, 0.6F, 0.8F);
    }

    public Condition get()
    {
        return this.condition;
    }

    public void set(Condition checker)
    {
        this.condition = checker;
    }
}