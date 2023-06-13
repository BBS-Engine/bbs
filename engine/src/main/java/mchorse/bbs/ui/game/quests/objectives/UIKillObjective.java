package mchorse.bbs.ui.game.quests.objectives;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.game.quests.objectives.KillObjective;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UISoundOverlayPanel;

public class UIKillObjective extends UIObjective<KillObjective>
{
    public UIButton entity;
    public UITrackpad count;
    public UITextbox data;

    public UIKillObjective(KillObjective objective)
    {
        super(objective);

        this.entity = new UIButton(UIKeys.OVERLAYS_ENTITIES_MAIN, (b) -> this.openPickEntityOverlay());
        this.entity.relative(this).y(12).w(0.5F, -3);

        this.count = new UITrackpad((value) -> this.objective.count = value.intValue());
        this.count.integer().limit(0).setValue(objective.count);
        this.count.relative(this).x(1F).y(12).w(0.5F, -2).anchorX(1F);

        this.data = new UITextbox(10000, this::parseTag);
        this.data.relative(this).y(49).w(1F);
        this.data.setText(objective.data == null ? "" : objective.data.toString());

        this.message.relative(this).y(1F).w(1F).anchorY(1F);

        this.h(106);

        this.add(this.message, this.entity, this.count, this.data);
    }

    private void parseTag(String string)
    {
        this.objective.data = DataToString.mapFromString(string);
    }

    private void openPickEntityOverlay()
    {
        /* TODO: ... */
        UISoundOverlayPanel overlay = new UISoundOverlayPanel(this::setEntity);

        UIOverlay.addOverlay(this.getContext(), overlay.set(this.objective.entity), 0.5F, 0.6F);
    }

    private void setEntity(Link link)
    {
        this.objective.entity = link == null ? "" : link.toString();
    }

    @Override
    public IKey getMessageTooltip()
    {
        return UIKeys.QUESTS_OBJECTIVE_KILL_MESSAGE_TOOLTIP;
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        context.font.renderWithShadow(context.render, UIKeys.QUESTS_OBJECTIVE_KILL_ENTITY.get(), this.entity.area.x, this.entity.area.y - 12);
        context.font.renderWithShadow(context.render, UIKeys.QUESTS_OBJECTIVE_KILL_COUNT.get(), this.count.area.x, this.count.area.y - 12);
        context.font.renderWithShadow(context.render, UIKeys.QUESTS_OBJECTIVE_KILL_DATA.get(), this.data.area.x, this.data.area.y - 12);
    }
}