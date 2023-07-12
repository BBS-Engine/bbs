package mchorse.studio.ui;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.studio.Studio;

public class UIPauseMenu extends UIBaseMenu
{
    public UIButton resume;

    public UIPauseMenu(IBridge bridge)
    {
        super(bridge);

        this.resume = new UIButton(UIKeysApp.PAUSE_RESUME, (b) -> this.closeThisMenu());
        this.resume.relative(this.main).xy(0.5F, 0.5F).w(100).anchor(0.5F);

        this.main.add(this.resume);
    }

    @Override
    public Link getMenuId()
    {
        return Studio.link("pause");
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();
    }
}