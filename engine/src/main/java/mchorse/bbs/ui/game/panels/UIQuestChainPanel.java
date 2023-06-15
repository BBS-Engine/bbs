package mchorse.bbs.ui.game.panels;

import mchorse.bbs.game.quests.chains.QuestChain;
import mchorse.bbs.game.quests.chains.QuestEntry;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.game.quests.UIQuestEntry;
import mchorse.bbs.ui.game.quests.UIQuestEntryList;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIQuestChainPanel extends UIDataDashboardPanel<QuestChain>
{

    public UIScrollView view;
    public UIQuestEntry entry;
    public UIQuestEntryList entries;

    public UIQuestChainPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.view = this.createScrollEditor();
        this.view.x(120).w(1F, -120).h(1F).column(0).padding(0);
        this.entry = new UIQuestEntry();
        this.entries = new UIQuestEntryList((list) -> this.pickQuestEntry(list.get(0), false));
        this.entries.sorting().context((menu) ->
        {
            menu.action(Icons.ADD, UIKeys.QUEST_CHAINS_CONTEXT_ADD, this::addRecipe);

            if (this.entries.isSelected())
            {
                menu.action(Icons.REMOVE, UIKeys.QUEST_CHAINS_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeRecipe);
            }
        });
        this.entries.relative(this.editor).w(120).h(1F);

        this.editor.add(this.entries, this.view);
        this.view.add(this.entry);
        this.overlay.namesList.setFileIcon(Icons.LIST);

        this.fill(null);
    }

    private void pickQuestEntry(QuestEntry recipe, boolean select)
    {
        this.view.setVisible(recipe != null);

        if (recipe != null)
        {
            this.entry.set(recipe);
            this.editor.resize();

            if (select)
            {
                this.entries.setCurrentScroll(recipe);
            }
        }
    }

    private void addRecipe()
    {
        QuestEntry entry = new QuestEntry();

        this.data.entries.add(entry);
        this.pickQuestEntry(entry, true);
        this.editor.resize();
        this.entries.update();
    }

    private void removeRecipe()
    {
        int index = this.entries.getIndex();

        this.data.entries.remove(this.entries.getCurrentFirst());

        if (index > 0)
        {
            index -= 1;
        }

        this.pickQuestEntry(this.data.entries.isEmpty() ? null : this.data.entries.get(index), true);
        this.entries.update();
    }

    @Override
    public ContentType getType()
    {
        return ContentType.CHAINS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_CHAINS;
    }

    @Override
    public void fill(QuestChain data)
    {
        super.fill(data);

        this.editor.setVisible(data != null);

        if (data != null)
        {
            this.entries.setList(this.data.entries);
            this.pickQuestEntry(this.data.entries.isEmpty() ? null : this.data.entries.get(0), true);

            this.resize();
        }
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editor.isVisible())
        {
            this.entries.area.render(context.batcher, Colors.A100);

            if (this.data.entries.isEmpty())
            {
                UIDataUtils.renderRightClickHere(context, this.entries.area);
            }
        }

        super.render(context);
    }
}