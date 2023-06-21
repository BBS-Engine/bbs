package mchorse.bbs.ui.game.states;

import mchorse.bbs.game.states.States;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.l10n.keys.IKey;

public class UIStatesOverlay extends UIOverlayPanel
{
    public UIIcon add;
    public UIStatesEditor states;

    public UIStatesOverlay(IKey title, States states)
    {
        super(title);

        this.add = new UIIcon(Icons.ADD, (b) -> this.states.addNew());

        this.states = new UIStatesEditor();
        this.states.set(states);
        this.states.relative(this.content).x(-10).y(-5).w(1F, 20).h(1F, 5);

        this.icons.add(this.add.marginRight(4));
        this.content.add(this.states);
    }
}