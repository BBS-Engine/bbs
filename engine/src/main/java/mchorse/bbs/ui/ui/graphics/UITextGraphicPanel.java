package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.game.scripts.ui.graphics.TextGraphic;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UITextGraphicPanel extends UIGraphicPanel<TextGraphic>
{
    public UITextbox text;

    public UITextGraphicPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.text = new UITextbox(1000, (t) -> this.graphic.text = t);

        this.add(UI.label(UIKeys.UI_GRAPHICS_TEXT), this.text);
    }

    @Override
    public void fill(TextGraphic graphic)
    {
        super.fill(graphic);

        this.text.setText(graphic.text);
    }
}
