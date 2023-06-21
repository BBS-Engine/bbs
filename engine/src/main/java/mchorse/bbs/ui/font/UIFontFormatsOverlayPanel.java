package mchorse.bbs.ui.font;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.graphics.text.format.BaseFontFormat;
import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.font.format.UIBaseFontFormat;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIFontFormatsOverlayPanel extends UIOverlayPanel
{
    public UIScrollView list;
    public UIIcon add;

    private Font font;

    public UIFontFormatsOverlayPanel(Font font)
    {
        super(UIKeys.FONT_EDITOR_CODES_TITLE);

        this.font = font;

        this.list = UI.scrollView(5, 10);
        this.list.relative(this.content).full();

        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            this.getContext().replaceContextMenu((menu) ->
            {
                for (String key : BBS.getFactoryFontFormats().getStringKeys())
                {
                    menu.shadow().action(Icons.ADD, UIKeys.FONT_EDITOR_CODES_CONTEXT_ADD.format(UIKeys.C_FONT_FORMAT.get(key)), () ->
                    {
                        char available = this.getAvailableControlCharacter();
                        IFontFormat format = BBS.getFactoryFontFormats().create(Link.create(key));

                        format.setControlCharacter(String.valueOf(available));
                        this.font.formats.put((int) format.getControlCharacter(), format);
                        this.addFormatPanel(format);
                        this.list.resize();
                    });
                }
            });
        });
        this.add.tooltip(UIKeys.FONT_EDITOR_CODES_ADD);

        this.icons.add(this.add);
        this.content.add(this.list);

        for (IFontFormat format : font.formats.values())
        {
            this.addFormatPanel(format);
        }
    }

    private char getAvailableControlCharacter()
    {
        char start = 'A';

        while (this.font.formats.containsKey(start))
        {
            start += 1;
        }

        return start;
    }

    private void addFormatPanel(IFontFormat format)
    {
        try
        {
            Class<? extends UIBaseFontFormat> clazz = BBS.getFactoryFontFormats().getData(format);
            UIBaseFontFormat formatUI = clazz.getConstructor().newInstance();

            formatUI.fill((BaseFontFormat) format);
            formatUI.context((menu) ->
            {
                menu.shadow().action(Icons.REMOVE, UIKeys.FONT_EDITOR_CODES_CONTEXT_REMOVE, Colors.NEGATIVE, () ->
                {
                    this.font.formats.remove(format.getControlCharacter());
                    this.list.remove(formatUI);
                    this.list.resize();
                });
            });

            this.list.add(formatUI);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}