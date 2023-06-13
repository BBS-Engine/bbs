package mchorse.bbs.ui.game.interaction;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.dialogues.DialogueInteraction;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.game.crafting.UICrafting;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;
import mchorse.bbs.ui.ui.IUIChangesHandler;
import mchorse.bbs.ui.utils.UI;

public class UICustomInteractionMenu extends UIBaseInteractionMenu implements IUIChangesHandler
{
    public UIElement craftingArea;
    public UIElement reactionArea;

    private UserInterfaceContext uiContext;

    public UICustomInteractionMenu(IBridge bridge, DialogueInteraction interaction, UserInterfaceContext uiContext)
    {
        super(bridge);

        this.uiContext = uiContext;
        this.uiContext.setup(this);

        /* Pull up UI elements from components */
        this.form = this.uiContext.getElement("form", UIFormRenderer.class);
        this.reactionArea = this.uiContext.getElement("reaction");
        this.replies = this.uiContext.getElement("replies", UIScrollView.class);
        this.accept = this.uiContext.getElement("accept", UIButton.class);
        this.accept.callback = (b) -> this.actionQuest();
        this.back = this.uiContext.getElement("back", UIButton.class);
        this.back.callback = (b) -> BBSData.getDialogues().pickReply(this.bridge, -1);

        this.craftingArea = this.uiContext.getElement("crafting");
        this.quest = this.uiContext.getElement("quest");

        /* Hardcoded UI elements */
        this.reactionText = new UIText();
        this.reactionText.relative(this.reactionArea).w(1F).hTo(this.reactionArea.area, 1F);
        this.quests = new UIQuestInfoList((l) -> this.pickQuest(l.get(0)));
        this.quests.background().relative(this.quest).w(1F).h(56);
        this.questArea = UI.scrollView(5, 10);
        this.questArea.relative(this.quest).y(56).w(1F).hTo(this.quest.area, 1F);

        this.crafting = new UICrafting(this);
        this.crafting.relative(this.craftingArea).full();

        this.reactionArea.add(this.reactionText);
        this.quest.add(this.quests, this.questArea);
        this.craftingArea.add(this.crafting);

        this.pickReply(interaction);
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
    public void onClose(UIBaseMenu nextMenu)
    {
        super.onClose(nextMenu);

        this.uiContext.close();
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