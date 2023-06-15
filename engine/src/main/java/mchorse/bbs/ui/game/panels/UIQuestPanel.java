package mchorse.bbs.ui.game.panels;

import mchorse.bbs.game.quests.Quest;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.game.quests.UIObjectives;
import mchorse.bbs.ui.game.quests.UIQuestCard;
import mchorse.bbs.ui.game.quests.UIRewards;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;

public class UIQuestPanel extends UIDataDashboardPanel<Quest>
{
    public UITextbox title;
    public UITextarea<TextLine> story;
    public UIToggle cancelable;
    public UIToggle instant;

    public UITrigger accept;
    public UITrigger decline;
    public UITrigger complete;

    public UIObjectives objectives;
    public UIRewards rewards;

    public UIElement preview;

    public UIQuestPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.title = new UITextbox(1000, (text) ->
        {
            this.data.title = text;

            this.updatePreview();
        });
        this.story = new UITextarea<TextLine>((text) ->
        {
            this.data.story = text;

            this.updatePreview();
        });
        this.story.wrap().background().padding(6).h(120);
        this.cancelable = new UIToggle(UIKeys.QUESTS_CANCELABLE, (b) -> this.data.cancelable = b.getValue());
        this.instant = new UIToggle(UIKeys.QUESTS_INSTANT, (b) -> this.data.instant = b.getValue());
        this.instant.tooltip(UIKeys.QUESTS_INSTANT_TOOLTIP);

        this.accept = new UITrigger();
        this.decline = new UITrigger();
        this.complete = new UITrigger();

        this.objectives = new UIObjectives();
        this.objectives.marginBottom(20);
        this.rewards = new UIRewards();

        UILabel objectiveLabel = UI.label(UIKeys.QUESTS_OBJECTIVES_TITLE).background();
        UILabel rewardLabel = UI.label(UIKeys.QUESTS_REWARDS_TITLE).background();
        UIIcon addObjective = new UIIcon(Icons.ADD, (b) -> this.getContext().replaceContextMenu((menu) -> this.objectives.getAdds(menu)));
        UIIcon addReward = new UIIcon(Icons.ADD, (b) -> this.getContext().replaceContextMenu((menu) -> this.rewards.getAdds(menu)));

        addObjective.relative(objectiveLabel).xy(1F, 0.5F).w(10).anchor(1F, 0.5F);
        addReward.relative(rewardLabel).xy(1F, 0.5F).w(10).anchor(1F, 0.5F);
        objectiveLabel.marginTop(12).marginBottom(5).add(addObjective);
        rewardLabel.marginBottom(5).add(addReward);

        UIScrollView scrollEditor = this.createScrollEditor();

        scrollEditor.scroll.opposite();
        scrollEditor.add(UI.label(UIKeys.QUESTS_TITLE), this.title);
        scrollEditor.add(UI.label(UIKeys.QUESTS_DESCRIPTION).marginTop(12), this.story);
        scrollEditor.add(this.cancelable.marginTop(6), this.instant);
        scrollEditor.add(objectiveLabel, this.objectives);
        scrollEditor.add(rewardLabel, this.rewards);
        scrollEditor.add(UI.label(UIKeys.QUESTS_ACCEPT).background().marginTop(20).marginBottom(4), this.accept);
        scrollEditor.add(UI.label(UIKeys.QUESTS_DECLINE).background().marginTop(12).marginBottom(4), this.decline);
        scrollEditor.add(UI.label(UIKeys.QUESTS_COMPLETE).background().marginTop(12).marginBottom(4), this.complete);

        this.preview = new UIElement();
        this.preview.relative(this.editor).x(1F).wTo(this.iconBar.getFlex(), 1F).h(1F);

        this.add(this.preview);
        this.editor.add(scrollEditor);
        this.overlay.namesList.setFileIcon(Icons.EXCLAMATION);

        this.editor.w(200);

        this.fill(null);
    }

    private void updatePreview()
    {
        this.preview.removeAll();

        if (this.data != null)
        {
            Entity entity = EntityArchitect.createDummy();
            UIScrollView card = UI.scrollView(5, 10);

            card.relative(this.preview).xy(0.5F, 0.5F).w(200).h(0.6F).anchor(0.5F);
            UIQuestCard.fillQuest(entity, card, this.data, true);

            this.preview.add(card);
            this.preview.resize();
        }
    }

    @Override
    public ContentType getType()
    {
        return ContentType.QUESTS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_QUESTS;
    }

    @Override
    public void fill(Quest data)
    {
        super.fill(data);

        this.editor.setVisible(data != null);
        this.preview.setVisible(data != null);

        this.updatePreview();

        if (data != null)
        {
            this.title.setText(data.title);
            this.story.setText(data.story);
            this.cancelable.setValue(data.cancelable);
            this.instant.setValue(data.instant);

            this.accept.set(data.accept);
            this.decline.set(data.decline);
            this.complete.set(data.complete);

            this.objectives.set(data.objectives);
            this.rewards.set(data.rewards);
        }

        /* Hack: due to grid resizer with custom width can't access
         * width for reasons it not being assigned yet, I have to double
         * resize it so the second time it could calculate correct height
         * if the editor */
        this.resize();
        this.resize();
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.preview.isVisible())
        {
            ((UIElement) this.preview.getChildren().get(0)).area.render(context.batcher, Colors.A25);
        }
    }
}