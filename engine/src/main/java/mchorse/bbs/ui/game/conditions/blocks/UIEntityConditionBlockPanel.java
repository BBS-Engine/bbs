package mchorse.bbs.ui.game.conditions.blocks;

import mchorse.bbs.game.conditions.blocks.EntityConditionBlock;
import mchorse.bbs.game.utils.EntityUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UILabelOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.conditions.UIConditionOverlayPanel;
import mchorse.bbs.ui.game.utils.UIComparison;
import mchorse.bbs.ui.game.utils.UITarget;
import mchorse.bbs.ui.utils.Label;

import java.util.HashSet;
import java.util.Set;

public class UIEntityConditionBlockPanel extends UIConditionBlockPanel<EntityConditionBlock>
{
    public UIButton id;
    public UITarget target;
    public UIComparison comparison;

    public UIEntityConditionBlockPanel(UIConditionOverlayPanel overlay, EntityConditionBlock block)
    {
        super(overlay, block);

        this.id = new UIButton(UIKeys.CONDITIONS_ENTITY_ID, this::openProperties);
        this.target = new UITarget(block.target).skipGlobal();
        this.comparison = new UIComparison(block.comparison);

        this.add(this.id, this.target.marginTop(12), this.comparison.marginTop(12));
    }

    private void openProperties(UIButton b)
    {
        Set<Label<String>> labels = new HashSet<Label<String>>();

        for (String property : EntityUtils.ENTITY_PROPERTIES)
        {
            labels.add(new Label<String>(UIKeys.C_ENTITY_PROPERTY.get(property), property));
        }

        IKey title = UIKeys.CONDITIONS_ENTITY_OVERLAY_MAIN;
        UILabelOverlayPanel<String> overlay = new UILabelOverlayPanel<String>(title, labels, (l) -> this.block.id = l.value);

        UIOverlay.addOverlay(this.getContext(), overlay.set(this.block.id), 0.4F, 0.5F);
    }
}