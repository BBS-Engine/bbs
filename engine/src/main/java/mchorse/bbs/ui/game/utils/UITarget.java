package mchorse.bbs.ui.game.utils;

import mchorse.bbs.game.utils.Target;
import mchorse.bbs.game.utils.TargetMode;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;

public class UITarget extends UIElement
{
    public Target target;

    private UICirculate mode;
    private UITextbox selector;

    public UITarget(Target target)
    {
        super();

        this.mode = UIDataUtils.createTargetCirculate(TargetMode.GLOBAL, this::toggleTarget);
        this.selector = new UITextbox(1000, (t) -> this.target.selector = t);

        this.column(5).stretch().vertical();

        this.setTarget(target);
    }

    public void setTarget(Target target)
    {
        this.target = target;

        if (target != null)
        {
            this.mode.setValue(target.mode.ordinal());
            this.selector.setText(target.selector);
        }

        this.updateTarget();
    }

    public UITarget skipGlobal()
    {
        return this.skip(TargetMode.GLOBAL);
    }

    public UITarget skip(TargetMode target)
    {
        this.mode.disable(target.ordinal());

        return this;
    }

    private void toggleTarget(TargetMode target)
    {
        this.target.mode = target;

        this.updateTarget();
    }

    private void updateTarget()
    {
        if (this.target == null)
        {
            return;
        }

        this.removeAll();
        this.add(UI.label(UIKeys.CONDITIONS_TARGET), this.mode);

        if (this.target.mode == TargetMode.SELECTOR)
        {
            this.add(UI.label(UIKeys.CONDITIONS_SELECTOR), this.selector);
        }

        UIElement container = this.getParentContainer();

        if (container != null)
        {
            container.resize();
        }
    }
}