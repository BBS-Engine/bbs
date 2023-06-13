package mchorse.bbs.game.utils.config;

import mchorse.bbs.settings.ui.UIValueFactory;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.settings.values.base.IValueUIProvider;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.scripts.highlighting.SyntaxStyle;
import mchorse.bbs.ui.game.scripts.themes.Themes;
import mchorse.bbs.ui.game.scripts.themes.UIThemeEditorOverlayPanel;

import java.util.Arrays;
import java.util.List;

public class ValueSyntaxStyle extends BaseValue implements IValueUIProvider
{
    private SyntaxStyle style = new SyntaxStyle();
    private String file = "monokai.json";

    public ValueSyntaxStyle(String id)
    {
        super(id);
    }

    public SyntaxStyle get()
    {
        return this.style;
    }

    public String getFile()
    {
        return this.file;
    }

    public void set(String file, SyntaxStyle style)
    {
        this.file = file;
        this.style = new SyntaxStyle(style.toData());

        this.notifyParent();
    }

    @Override
    public List<UIElement> getFields(UIElement ui)
    {
        UIButton button = new UIButton(UIKeys.SYNTAX_THEME_EDIT, (t) ->
        {
            UIOverlay.addOverlay(ui.getContext(), new UIThemeEditorOverlayPanel(), 0.6F, 0.95F);
        });

        return Arrays.asList(UIValueFactory.commetTooltip(button, this));
    }

    @Override
    public BaseType toData()
    {
        return new StringType(this.file);
    }

    @Override
    public void fromData(BaseType element)
    {
        if (!BaseType.isString(element))
        {
            return;
        }

        String file = ((StringType) element).value;
        SyntaxStyle style = Themes.readTheme(Themes.themeFile(file));

        if (style != null)
        {
            this.style = style;
            this.file = file;
        }
    }
}