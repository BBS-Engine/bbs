package mchorse.bbs.ui.ui.components;

import mchorse.bbs.game.scripts.ui.components.UIStringListComponent;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

import java.util.Arrays;

public class UIStringListComponentPanel extends UIComponentPanel<UIStringListComponent>
{
    public UITextarea<TextLine> values;
    public UITrackpad selected;
    public UIColor background;

    public UIStringListComponentPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.values = new UITextarea<TextLine>((t) ->
        {
            this.component.values.clear();
            this.component.values.addAll(Arrays.asList(t.split("\n")));
            this.panel.needsUpdate();
        });
        this.values.background().h(80);

        this.selected = new UITrackpad((v) ->
        {
            this.component.selected = v.intValue();
            this.panel.needsUpdate();
        }).limit(-1).integer();

        this.background = new UIColor((c) ->
        {
            this.component.background = c;
            this.panel.needsUpdate();
        }).withAlpha();

        this.prepend(this.background.marginBottom(8));
        this.prepend(this.selected);
        this.prepend(UI.label(UIKeys.UI_COMPONENTS_STRINGS_SELECTED));
        this.prepend(this.values);
        this.prepend(createSectionLabel(UIKeys.UI_COMPONENTS_STRINGS_TITLE));
    }

    @Override
    public void fill(UIStringListComponent component)
    {
        super.fill(component);

        this.values.setText(String.join("\n", component.values));
        this.selected.setValue(component.selected);
        this.background.setColor(component.background);
    }
}