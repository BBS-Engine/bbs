package mchorse.bbs.settings.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.settings.Settings;
import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UILabelList;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.ScrollDirection;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.List;

public class UISettingsPanel extends UIDashboardPanel
{
    public UIIcon reload;
    public UILabelList<String> mods;
    public UIScrollView options;

    private Settings settings;
    private IKey title = UIKeys.CONFIG_TITLE;

    public UISettingsPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.reload = new UIIcon(Icons.REFRESH, (button) -> this.reload());
        this.reload.tooltip(UIKeys.CONFIG_RELOAD_TOOLTIP, Direction.BOTTOM);
        this.mods = new UILabelList<String>((mod) -> this.selectConfig(mod.get(0).value));
        this.options = new UIScrollView(ScrollDirection.HORIZONTAL);
        this.options.scroll.scrollSpeed = 51;

        this.reload.relative(this).set(120 - 14, 12, 16, 16);
        this.mods.relative(this).set(10, 35, 110, 0).h(1, -45);
        this.options.relative(this).set(130, 0, 0, 0).w(1, -130).h(1F);
        this.options.column().scroll().width(240).height(20).padding(15);

        this.fillClientMods();

        this.add(this.reload, this.mods, this.options);
        this.selectConfig("bbs");
        this.markContainer();
    }

    private void reload()
    {
        BBS.getConfigs().reload();
        this.refresh();
    }

    private void fillClientMods()
    {
        for (Settings settings : BBS.getConfigs().modules.values())
        {
            this.mods.add(IKey.lang(UIValueFactory.getTitleKey(settings)), settings.getId());
        }

        this.mods.sort();
    }

    public void selectConfig(String mod)
    {
        this.mods.setCurrentValue(mod);
        this.settings = BBS.getConfigs().modules.get(mod);
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

            UILabel label = UI.label(IKey.lang(catTitleKey)).labelAnchor(0, 1).background();
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
    public void appear()
    {
        super.appear();

        this.mods.sort();
    }

    @Override
    public void render(UIContext context)
    {
        this.mods.area.render(context.batcher, Colors.A75, -10, -35, -10, -10);
        context.batcher.textShadow(this.title.get(), this.area.x + 10, this.area.y + 20 - context.font.getHeight() / 2);

        super.render(context);
    }
}