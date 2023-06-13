package mchorse.bbs.ui.ui;

import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIUserInterfaceMenu extends UIBaseMenu implements IUIChangesHandler
{
    public UserInterfaceContext uiContext;

    public static UIUserInterfaceMenu create(IBridge bridge, UserInterface ui)
    {
        return create(bridge, ui, null);
    }

    public static UIUserInterfaceMenu create(IBridge bridge, UserInterface ui, Object object)
    {
        return new UIUserInterfaceMenu(bridge, UserInterfaceContext.create(ui, object));
    }

    public UIUserInterfaceMenu(IBridge bridge, UserInterfaceContext uiContext)
    {
        super(bridge);

        this.uiContext = uiContext;
        this.uiContext.changesHandler = this;

        UIElement element = this.uiContext.ui.root.create(this.uiContext);

        element.relative(this.main).full();
        this.main.add(element);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("scripted_ui");
    }

    @Override
    public void handleUIChanges(MapType data)
    {
        for (String key : data.keys())
        {
            MapType map = data.getMap(key);
            UIElement element = this.uiContext.getElement(key);

            this.uiContext.getById(key).handleChanges(this.uiContext, map, element);
        }

        this.main.resize();
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public void onClose(UIBaseMenu nextMenu)
    {
        super.onClose(nextMenu);

        this.uiContext.close();
    }

    @Override
    protected void closeMenu()
    {
        if (this.uiContext.ui.closable)
        {
            super.closeMenu();
        }
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        if (this.uiContext.isDirty())
        {
            this.uiContext.sendToServer();
        }

        if (this.uiContext.ui.background)
        {
            this.renderDefaultBackground();
        }
    }
}