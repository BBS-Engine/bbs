package mchorse.bbs.ui.dashboard.panels;

import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.overlay.UICRUDOverlayPanel;
import mchorse.bbs.ui.dashboard.panels.overlay.UIDataOverlayPanel;
import mchorse.bbs.ui.dashboard.panels.overlay.UIOptionsOverlayPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.UIDataUtils;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.manager.data.AbstractData;
import mchorse.bbs.utils.math.Interpolation;

import java.util.List;

public abstract class UIDataDashboardPanel <T extends AbstractData> extends UICRUDDashboardPanel
{
    public static final IKey KEYS_CATEGORY = UIKeys.PANELS_KEYS_CATEGORY;

    public UIIcon saveIcon;
    public UIIcon optionsIcon;
    public UIOptionsOverlayPanel options;

    protected T data;
    protected boolean save;

    public UIDataDashboardPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.options = new UIOptionsOverlayPanel();
        this.saveIcon = new UIIcon(Icons.SAVED, (b) -> this.save());
        this.optionsIcon = new UIIcon(Icons.GEAR, (b) ->
        {
            UIOverlay.addOverlayRight(this.getContext(), this.options, 200, 20).noBackground();
        });

        this.iconBar.add(this.saveIcon);

        this.keys().register(Keys.SAVE, this.saveIcon::clickItself).active(() -> this.data != null);
    }

    protected void addOptions()
    {
        this.iconBar.addAfter(this.saveIcon, this.optionsIcon);
    }

    public T getData()
    {
        return this.data;
    }

    /**
     * Get the content type of this panel
     */
    public abstract ContentType getType();

    @Override
    protected UICRUDOverlayPanel createOverlayPanel()
    {
        return new UIDataOverlayPanel<>(this.getTitle(), this, this::pickData);
    }

    @Override
    public void pickData(String id)
    {
        this.save();
        this.requestData(id);
    }

    public void requestData(String id)
    {
        this.fill((T) this.getType().getManager().load(id));
    }

    /* Data population */

    public void fill(T data)
    {
        this.data = data;

        this.saveIcon.setEnabled(data != null);
        this.editor.setVisible(data != null);
        this.overlay.dupe.setEnabled(data != null);
        this.overlay.rename.setEnabled(data != null);
        this.overlay.remove.setEnabled(data != null);
        this.optionsIcon.setEnabled(data != null);
    }

    public void fillDefaultData(T data)
    {}

    public void fillNames(List<String> names)
    {
        String value = this.data == null ? null : this.data.getId();

        this.overlay.namesList.fill(names);
        this.overlay.namesList.setCurrentFile(value);
    }

    protected UIScrollView createScrollEditor()
    {
        UIScrollView scrollEditor = UI.scrollView(5, 10);

        scrollEditor.relative(this.editor).full();

        return scrollEditor;
    }

    @Override
    public void open()
    {
        super.open();

        this.save = true;
    }

    @Override
    public void appear()
    {
        super.appear();

        if (this.data != null)
        {
            this.requestData(this.data.getId());
        }
    }

    @Override
    public void requestNames()
    {
        UIDataUtils.requestNames(this.getType(), this::fillNames);
    }

    @Override
    public void disappear()
    {
        super.disappear();

        if (this.save)
        {
            this.save();
        }
    }

    @Override
    public void close()
    {
        super.close();

        if (this.save)
        {
            this.save();
        }
    }

    public void save()
    {
        if (!this.update && this.data != null && this.editor.isEnabled())
        {
            this.forceSave();
        }
    }

    public void forceSave()
    {
        this.preSave();
        this.getType().getManager().save(this.data.getId(), this.data.toData());
    }

    protected void preSave()
    {}

    @Override
    public void render(UIContext context)
    {
        if (this.data == null)
        {
            double ticks = context.getTickTransition() % 15D;
            double factor = Math.abs(ticks / 15D * 2 - 1F);

            int x = this.openOverlay.area.x - 10 + (int) Interpolation.SINE_INOUT.interpolate(-10, 0, factor);
            int y = this.openOverlay.area.my();

            context.batcher.icon(Icons.ARROW_RIGHT, x, y, 0.5F, 0.5F);
        }

        super.render(context);

        if (!this.editor.isEnabled() && this.data != null)
        {
            this.renderLockedArea(context);
        }
    }
}