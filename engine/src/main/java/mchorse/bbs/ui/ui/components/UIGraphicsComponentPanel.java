package mchorse.bbs.ui.ui.components;

import mchorse.bbs.BBS;
import mchorse.bbs.game.scripts.ui.components.UIGraphicsComponent;
import mchorse.bbs.game.scripts.ui.graphics.Graphic;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.ui.graphics.UIGraphicPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIGraphicsComponentPanel extends UIComponentPanel<UIGraphicsComponent>
{
    public UIElement graphics;
    public UIIcon add;

    public UIGraphicsComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.graphics = UI.column(20);
        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            this.getContext().replaceContextMenu((menu) ->
            {
                for (Link link : BBS.getFactoryGraphics().getKeys())
                {
                    menu.shadow().action(Icons.ADD, UIKeys.UI_COMPONENTS_GRAPHICS_ADD.format(UIKeys.C_GRAPHICS.get(link)), () ->
                    {
                        Graphic graphic = BBS.getFactoryGraphics().create(link);

                        this.component.graphics.add(graphic);

                        this.addGraphicsPanel(graphic);
                        this.panel.needsUpdate();
                    });
                }
            });
        });

        UILabel label = createSectionLabel(UIKeys.UI_COMPONENTS_GRAPHICS_TITLE);

        this.add.relative(label).x(1F).y(0.5F).anchor(1F, 0.5F);
        label.add(this.add);

        this.prepend(this.graphics.marginBottom(8));
        this.prepend(label.marginBottom(8));
    }

    private void addGraphicsPanel(Graphic graphic)
    {
        try
        {
            UIGraphicPanel panel = BBS.getFactoryGraphics().getData(graphic).getConstructor(UIUserInterfacePanel.class).newInstance(this.panel);

            panel.fill(graphic);
            panel.context((menu) ->
            {
                menu.shadow().action(Icons.REMOVE, UIKeys.UI_COMPONENTS_GRAPHICS_REMOVE, () ->
                {
                    this.component.graphics.remove(graphic);
                    panel.removeFromParent();

                    this.panel.needsUpdate();
                    this.getParentContainer().resize();
                });
            });
            this.graphics.add(panel);

            UIElement element = this.getParentContainer();

            if (element != null)
            {
                element.resize();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void fill(UIGraphicsComponent component)
    {
        super.fill(component);

        this.graphics.removeAll();

        for (Graphic graphic : component.graphics)
        {
            this.addGraphicsPanel(graphic);
        }
    }
}