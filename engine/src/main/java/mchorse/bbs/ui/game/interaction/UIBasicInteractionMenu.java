package mchorse.bbs.ui.game.interaction;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.game.dialogues.DialogueInteraction;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.game.crafting.UICrafting;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

public class UIBasicInteractionMenu extends UIBaseInteractionMenu
{
    public UIElement area;

    public UIBasicInteractionMenu(IBridge bridge, DialogueInteraction interaction)
    {
        super(bridge);

        this.area = new UIElement();
        this.area.relative(this.viewport).x(0.2F).y(20).w(0.4F).h(1F, -20);

        /* Dialogue */
        this.reactionText = new UIText();
        this.replies = UI.scrollView(10, 10);

        this.reactionText.relative(this.area).w(1F).hTo(this.replies.area);
        this.replies.relative(this.area).y(0.75F).w(1F).hTo(this.area.area, 1F);

        /* Crafting */
        this.crafting = new UICrafting(this);
        this.crafting.relative(this.area).y(0.45F).w(1F).hTo(this.area.area, 1F);

        /* Quests */
        this.quest = new UIElement();
        this.quests = new UIQuestInfoList((l) -> this.pickQuest(l.get(0)));
        this.questArea = UI.scrollView(5, 10);
        this.accept = new UIButton(UIKeys.INTERACTION_ACCEPT, (b) -> this.actionQuest());

        this.quest.relative(this.area).y(0.25F).w(1F).hTo(this.area.area, 1F);
        this.quests.background().relative(this.quest).y(10).w(1F).h(56);
        this.questArea.relative(this.quest).y(66).w(1F).hTo(this.accept.area, -5);
        this.accept.relative(this.quest).x(1F, -10).y(1F, -10).wh(80, 20).anchor(1F, 1F);

        this.quest.add(this.accept, this.questArea, this.quests);

        /* General */
        this.form = new UIFormRenderer();
        this.form.grid = false;
        this.form.relative(this.viewport).x(0.4F).w(0.6F).h(1F);

        this.form.camera.setFov(40);
        this.form.setDistance(2.1F);
        this.form.setRotation(-27, 5);
        this.form.setPosition(-0.1313307F, 1.3154614F, 0.0359409F);
        this.form.setEnabled(false);

        this.back = new UIButton(UIKeys.INTERACTION_BACK, (b) -> BBSData.getDialogues().pickReply(this.bridge, -1));
        this.back.resetFlex().relative(this.quest).x(10).y(1F, -10).wh(80, 20).anchorY(1F);

        UIRenderable background = new UIRenderable((context) ->
        {
            context.draw.box(0, 0, this.area.area.x(0.65F), this.area.area.ey(), Colors.A75);
            context.draw.gradientHBox(this.area.area.x(0.65F), 0, this.area.area.x(1.125F), this.area.area.ey(), Colors.A75, 0);
        });

        this.area.add(this.quest, this.crafting, this.replies, this.reactionText, this.back);
        this.main.add(this.form, background, this.area);

        this.pickReply(interaction);
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.renderDefaultBackground();
    }
}