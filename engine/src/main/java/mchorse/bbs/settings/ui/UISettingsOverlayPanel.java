package mchorse.bbs.settings.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.Settings;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UISettingsOverlayPanel extends UIOverlayPanel
{
    public UIScrollView options;

    private Settings settings;
    private IKey title = UIKeys.CONFIG_TITLE;
    private UIIcon currentButton;

    public UISettingsOverlayPanel()
    {
        super(UIKeys.CONFIG_TITLE);

        this.options = new UIScrollView(ScrollDirection.VERTICAL);
        this.options.scroll.scrollSpeed = 51;

        this.options.relative(this.content).full();
        this.options.column().scroll().vertical().stretch().padding(10).height(20);

        for (Settings settings : BBS.getConfigs().modules.values())
        {
            UIIcon icon = new UIIcon(settings.icon, (b) ->
            {
                this.selectConfig(settings.getId(), b);
            });

            icon.tooltip(IKey.lang(UIValueFactory.getTitleKey(settings)), Direction.LEFT);
            this.icons.add(icon);
        }

        this.add(this.options);
        this.selectConfig("bbs", this.icons.getChildren(UIIcon.class).get(2));
        this.markContainer();
    }

    public void selectConfig(String mod, UIIcon currentButton)
    {
        this.settings = BBS.getConfigs().modules.get(mod);
        this.currentButton = currentButton;

        this.refresh();
    }

    public void refresh()
    {
        if (this.settings == null)
        {
            return;
        }

        this.options.removeAll();

        boolean first = true;

        for (ValueGroup category : this.settings.categories.values())
        {
            if (!category.isVisible())
            {
                continue;
            }

            String catTitleKey = UIValueFactory.getCategoryTitleKey(category);
            String catTooltipKey = UIValueFactory.getCategoryTooltipKey(category);

            UILabel label = UI.label(IKey.lang(catTitleKey)).labelAnchor(0, 1).background(() -> BBSSettings.primaryColor(Colors.A50));
            List<UIElement> options = new ArrayList<UIElement>();

            label.tooltip(IKey.lang(catTooltipKey), Direction.BOTTOM);
            this.options.add(label);

            for (BaseValue value : category.getAll())
            {
                if (!value.isVisible() || !(value instanceof IValueUIProvider))
                {
                    continue;
                }

                for (UIElement element : ((IValueUIProvider) value).getFields(this))
                {
                    options.add(element);
                }
            }

            UIElement firstContainer = UI.column(5, 0, 20, label, options.remove(0)).marginTop(first ? 0 : 24);

            this.options.add(firstContainer);

            for (UIElement element : options)
            {
                this.options.add(element);
            }

            first = false;
        }

        this.resize();
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.currentButton != null)
        {
            this.currentButton.area.render(context.batcher, BBSSettings.primaryColor(Colors.A100));
        }
    }
}