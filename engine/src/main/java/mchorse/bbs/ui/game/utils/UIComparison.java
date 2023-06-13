package mchorse.bbs.ui.game.utils;

import mchorse.bbs.game.utils.Comparison;
import mchorse.bbs.game.utils.ComparisonMode;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.utils.UI;

public class UIComparison extends UIElement
{
    public Comparison comparison;

    private UICirculate mode;
    private UITrackpad value;
    private UITextbox expression;

    public UIComparison(Comparison comparison)
    {
        super();

        this.comparison = comparison;

        this.mode = new UICirculate(this::toggleComparison);

        for (ComparisonMode mode : ComparisonMode.values())
        {
            this.mode.addLabel(mode.stringify());
        }

        this.mode.setValue(comparison.comparison.ordinal());
        this.value = new UITrackpad((v) -> this.comparison.value = v);
        this.value.setValue(comparison.value);

        this.expression = new UITextbox(1000, (t) -> this.comparison.expression = t);
        this.expression.setText(this.comparison.expression);
        this.expression.tooltip(UIKeys.CONDITIONS_EXPRESSION_TOOLTIP);

        this.row();
        this.toggleComparison(this.mode);
    }

    private void toggleComparison(UICirculate b)
    {
        this.comparison.comparison = ComparisonMode.values()[b.getValue()];

        UIElement insert = this.value;
        IKey label = UIKeys.CONDITIONS_VALUE;

        if (this.comparison.comparison == ComparisonMode.EXPRESSION)
        {
            insert = this.expression;
            label = UIKeys.CONDITIONS_EXPRESSION;
        }
        else if (this.comparison.comparison == ComparisonMode.IS_TRUE || this.comparison.comparison == ComparisonMode.IS_FALSE)
        {
            insert = null;
        }

        this.removeAll();
        this.add(UI.column(UI.label(UIKeys.CONDITIONS_COMPARISON), this.mode));

        if (insert != null)
        {
            this.add(UI.column(UI.label(label), insert));
        }

        UIElement container = this.getParentContainer();

        if (container != null)
        {
            container.resize();
        }
    }
}