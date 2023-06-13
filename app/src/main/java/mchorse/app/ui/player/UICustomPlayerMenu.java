package mchorse.app.ui.player;

import mchorse.app.ui.inventory.UIInventoryHandler;
import mchorse.app.ui.inventory.UIPlayerInventory;
import mchorse.app.ui.inventory.UIPlayerSlot;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;
import mchorse.bbs.ui.ui.IUIChangesHandler;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

public class UICustomPlayerMenu extends UIBasePlayerMenu implements IUIChangesHandler
{
    public UIElement questArea;
    public UIElement slotsArea;
    public UIElement inventoryArea;

    private UserInterfaceContext uiContext;

    public UICustomPlayerMenu(IBridge bridge, UserInterfaceContext context)
    {
        super(bridge);

        Entity player = bridge.get(IBridgePlayer.class).getController();
        PlayerComponent character = player.get(PlayerComponent.class);

        this.uiContext = context;
        this.uiContext.setup(this);

        /* Fetch UI created elements */
        this.form = this.uiContext.getElement("form", UIFormRenderer.class);
        this.form.form = player.get(FormComponent.class).form;
        this.form.setEntity(player);
        this.questArea = this.uiContext.getElement("quest");
        this.slotsArea = this.uiContext.getElement("slots");
        this.inventoryArea = this.uiContext.getElement("inventory");

        /* Hardcoded elements */
        this.handler = new UIInventoryHandler();
        this.inventory = new UIPlayerInventory(character.inventory, this.handler);
        this.inventory.relative(this.inventoryArea).wh(1F, 1F);
        this.mainHand = new UIPlayerSlot(0, character.equipment, this.handler);
        this.offHand = new UIPlayerSlot(1, character.equipment, this.handler);

        this.quests = new UIQuestInfoList((l) -> this.pickQuest(l.get(0)));
        this.quests.background().add(this.collectQuestInfos(character));
        this.quests.relative(this.questArea).w(1F).h(80);
        this.quest = UI.scrollView(5, 10);
        this.quest.relative(this.questArea).y(80).w(1F).hTo(this.questArea.area, 1F);

        this.main.add(this.handler.noCulling());
        this.slotsArea.add(this.mainHand, this.offHand);
        this.questArea.add(this.quests, this.quest);
        this.inventoryArea.add(this.inventory);

        boolean notEmpty = !this.quests.getList().isEmpty();

        this.questArea.setVisible(notEmpty);

        if (notEmpty)
        {
            QuestInfo info = this.quests.getList().get(0);

            this.pickQuest(info);
            this.quests.setCurrentScroll(info);
        }
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