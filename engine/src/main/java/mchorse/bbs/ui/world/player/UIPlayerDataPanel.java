package mchorse.bbs.ui.world.player;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.controllers.BaseGameController;
import mchorse.bbs.game.controllers.IGameController;
import mchorse.bbs.game.player.PlayerData;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.game.controllers.UIBaseGameControllerPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.UIWorldPanel;
import mchorse.bbs.ui.world.entities.UIEntitiesPanel;
import mchorse.bbs.world.entities.Entity;

import java.util.Collection;

public class UIPlayerDataPanel extends UIWorldPanel
{
    public UIScrollView editor;
    public UIButton switchGameController;

    private PlayerData data;
    private Entity player;

    public UIPlayerDataPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.data = dashboard.bridge.get(IBridgePlayer.class).getPlayerData();

        this.editor = UI.scrollView(5, 10);
        this.editor.scroll.cancelScrolling().opposite();
        this.editor.relative(this).y(10).w(180).h(1F, -10);

        this.switchGameController = new UIButton(UIKeys.PLAYER_DATA_SWITCH_GAME_CONTROLLER, (b) ->
        {
            Collection<Link> keys = BBS.getFactoryGameControllers().getKeys();
            UIStringOverlayPanel panel = UIStringOverlayPanel.links(UIKeys.PLAYER_DATA_SWITCH_GAME_CONTROLLER_TITLE, false, keys, (link) ->
            {
                IGameController gameController = BBS.getFactoryGameControllers().create(link);

                gameController.fromData(this.data.getGameController().toData());
                this.data.setGameController(gameController);

                this.appear();
            });

            panel.set(BBS.getFactoryGameControllers().getType(this.data.getGameController()));

            UIOverlay.addOverlay(this.getContext(), panel);
        });

        this.add(this.editor);
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public void appear()
    {
        this.player = this.data.createPlayer(this.dashboard.bridge.get(IBridgeWorld.class).getWorld().architect);

        this.editor.removeAll();

        UIEntitiesPanel.setupEntityEditor(this.editor, this.player);

        this.editor.add(UI.label(UIKeys.PLAYER_DATA_TITLE).background().marginTop(8), this.switchGameController);

        try
        {
            UIBaseGameControllerPanel panel = BBS.getFactoryGameControllers().getData(this.data.getGameController()).getConstructor().newInstance();

            if (panel != null)
            {
                panel.fill((BaseGameController) this.data.getGameController());
                this.editor.add(panel);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        this.editor.resize();
    }

    @Override
    public void close()
    {
        this.save();
    }

    public void save()
    {
        if (this.player != null)
        {
            this.data.updatePlayerData(this.player.toData());
        }
    }
}