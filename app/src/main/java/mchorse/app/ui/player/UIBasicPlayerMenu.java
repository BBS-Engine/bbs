package mchorse.app.ui.player;

import mchorse.app.ui.UIKeysApp;
import mchorse.app.ui.inventory.UIInventoryHandler;
import mchorse.app.ui.inventory.UIPlayerInventory;
import mchorse.app.ui.inventory.UIPlayerSlot;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.quests.chains.QuestInfo;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.forms.editors.utils.UIFormRenderer;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.game.quests.UIQuestInfoList;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.components.FormComponent;

public class UIBasicPlayerMenu extends UIBasePlayerMenu
{
    public UIBasicPlayerMenu(IBridge bridge)
    {
        super(bridge);

        Entity player = bridge.get(IBridgePlayer.class).getController();
        PlayerComponent character = player.get(PlayerComponent.class);

        this.form = new UIFormRenderer();
        this.form.grid = false;
        this.form.form = player.get(FormComponent.class).form;
        this.form.setEntity(player);
        this.form.relative(this.main).w(1F).h(1F);
        this.handler = new UIInventoryHandler();
        this.inventory = new UIPlayerInventory(character.inventory, this.handler);
        this.inventory.relative(this.main).x(1F, -20).y(0.5F).anchor(1F, 0.5F);
        this.mainHand = new UIPlayerSlot(0, character.equipment, this.handler);
        this.mainHand.relative(this.inventory).y(-40);
        this.offHand = new UIPlayerSlot(1, character.equipment, this.handler);
        this.offHand.relative(this.inventory).x(20).y(-40);

        this.quests = new UIQuestInfoList((l) -> this.pickQuest(l.get(0)));
        this.quests.background().add(this.collectQuestInfos(character));
        this.quests.relative(this.main).x(20).y(30).w(160).h(80);
        this.quest = UI.scrollView(5, 10);
        this.quest.relative(this.quests).y(1F).w(1F).hTo(this.main.area, 1F, -20);

        this.main.add(this.form, this.inventory, this.mainHand, this.offHand, this.handler.noCulling());
        this.main.add(this.quests, this.quest);
        this.main.add(new UIRenderable(this::renderOverlayLabels));

        boolean notEmpty = !this.quests.getList().isEmpty();

        this.quests.setVisible(notEmpty);
        this.quest.setVisible(notEmpty);

        if (notEmpty)
        {
            QuestInfo info = this.quests.getList().get(0);

            this.pickQuest(info);
            this.quests.setCurrentScroll(info);
        }
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        super.preRenderMenu(context);

        this.viewport.render(this.context.batcher, Colors.A75);
    }

    private void renderOverlayLabels(UIContext context)
    {
        int h = this.context.font.getHeight() + 3;

        context.batcher.textShadow(UIKeysApp.PLAYER_EQUIPMENT.get(), this.mainHand.area.x, this.mainHand.area.y - h);
        context.batcher.textShadow(UIKeysApp.PLAYER_INVENTORY.get(), this.inventory.area.x, this.inventory.area.y - h);

        if (this.quest.isVisible())
        {
            context.batcher.textShadow(UIKeys.PANELS_QUESTS.get(), this.quests.area.x, this.quests.area.y - h);
        }
    }
}