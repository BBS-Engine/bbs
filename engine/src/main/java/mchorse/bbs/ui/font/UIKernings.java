package mchorse.bbs.ui.font;

import mchorse.bbs.graphics.text.Kerning;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.List;

public class UIKernings extends UIElement
{
    public UIIcon add;
    public UIElement editor;

    private List<Kerning> kernings;

    public UIKernings()
    {
        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            Kerning kerning = new Kerning('\0', 0);

            this.kernings.add(kerning);
            this.addKerning(kerning);
            this.getParentContainer().resize();
        });
        this.add.tooltip(UIKeys.FONT_EDITOR_KERNING_ADD);
        this.editor = UI.column();

        this.column().vertical().stretch();
        this.add(UI.row(UI.label(UIKeys.FONT_EDITOR_KERNING_TITLE, 20).background().labelAnchor(0, 0.5F), this.add));
        this.add(this.editor);
    }

    private void addKerning(Kerning kerning)
    {
        UIKerning uiKerning = new UIKerning(kerning);

        uiKerning.context((menu) ->
        {
            menu.action(Icons.REMOVE, UIKeys.FONT_EDITOR_KERNING_REMOVE, () -> this.removeKerning(uiKerning));
        });

        this.editor.add(uiKerning);
    }

    private void removeKerning(UIKerning kerning)
    {
        this.kernings.remove(kerning.getKerning());
        kerning.removeFromParent();

        this.getParentContainer().resize();
    }

    public void fill(List<Kerning> kernings)
    {
        this.kernings = kernings;

        this.editor.removeAll();

        for (Kerning kerning : kernings)
        {
            this.addKerning(kerning);
        }

        this.getParentContainer().resize();
    }
}