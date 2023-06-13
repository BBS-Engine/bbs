package mchorse.bbs.ui.game.panels;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.game.dialogues.Dialogue;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataRunDashboardPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.game.nodes.UIDialogueNodeGraph;
import mchorse.bbs.ui.game.nodes.UIEventBaseNodePanel;
import mchorse.bbs.ui.game.triggers.UITrigger;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.world.entities.Entity;

public class UIDialoguePanel extends UIDataRunDashboardPanel<Dialogue>
{
    public static final IKey EMPTY = UIKeys.NODES_INFO_EMPTY_DIALOGUE;

    public UIDialogueNodeGraph graph;
    public UIEventBaseNodePanel panel;

    public UIToggle closable;
    public UITrigger onClose;

    public UIDialoguePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.graph = new UIDialogueNodeGraph(BBS.getFactoryDialogues(), this::pickNode);
        this.graph.notifyAboutMain().relative(this.editor).full();

        this.closable = new UIToggle(UIKeys.NODES_DIALOGUE_CLOSABLE, (b) -> this.data.closable = b.getValue());
        this.onClose = new UITrigger();

        this.editor.add(this.graph);

        this.addOptions();
        this.options.fields.add(UI.label(UIKeys.NODES_DIALOGUE_ON_CLOSE_TRIGGER), this.onClose, this.closable);

        this.overlay.namesList.setFileIcon(Icons.BUBBLE);

        this.fill(null);
    }

    private void pickNode(EventBaseNode node)
    {
        if (this.panel != null)
        {
            this.panel.removeFromParent();
            this.panel = null;
        }

        if (node != null)
        {
            UIEventBaseNodePanel panel = null;

            try
            {
                panel = BBS.getFactoryDialogues().getData(node).panelUI.getConstructor().newInstance();

                panel.set(node);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            if (panel != null)
            {
                panel.relative(this).y(1F).w(220).anchorY(1F);

                this.panel = panel;
                this.panel.resize();

                this.editor.add(panel);
            }
        }
    }

    @Override
    protected void run()
    {
        this.save();
        this.save = false;

        Entity player = this.getContext().menu.bridge.get(IBridgePlayer.class).getController();

        if (player != null)
        {
            BBSData.getDialogues().open(new DataContext(player), this.data.getId());
        }
    }

    @Override
    public ContentType getType()
    {
        return ContentType.DIALOGUES;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_DIALOGUES;
    }

    @Override
    public void fill(Dialogue data)
    {
        super.fill(data);

        this.graph.setVisible(data != null);

        if (data != null)
        {
            this.graph.set(data);
            this.closable.setValue(data.closable);
            this.onClose.set(data.onClose);
        }
    }
}