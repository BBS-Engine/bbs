package mchorse.bbs.ui.font;

import mchorse.bbs.graphics.text.Glyph;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.resources.Pixels;

import java.util.List;

public class UIFontOverlayPanel extends UIOverlayPanel
{
    public UIElement column;
    public UITextbox name;
    public UITrackpad height;
    public UIGlyphList glyphsList;
    public UISearchList<Integer> searchList;

    private UIFontPanel panel;

    public UIFontOverlayPanel(IKey title, UIFontPanel panel)
    {
        super(title);

        this.panel = panel;

        this.name = new UITextbox((t) -> this.panel.font.name = t);
        this.height = new UITrackpad((v) -> this.panel.font.height = v.intValue()).integer().limit(1);
        this.height.tooltip(UIKeys.FONT_EDITOR_HEIGHT_TOOLTIP);
        this.column = UI.column(
            UI.label(UIKeys.FONT_EDITOR_NAME), this.name,
            UI.label(UIKeys.FONT_EDITOR_HEIGHT).marginTop(8), this.height,
            UI.label(UIKeys.FONT_EDITOR_GLYPHS).marginTop(8)
        );

        this.glyphsList = new UIGlyphList((l) -> this.panel.pickGlyph(l.get(0)));
        this.glyphsList.context((menu) ->
        {
            menu.shadow().action(Icons.ADD, UIKeys.FONT_EDITOR_CONTEXT_ADD, () -> this.addGlyph(null));

            if (this.glyphsList.isSelected())
            {
                menu.action(Icons.DUPE, UIKeys.FONT_EDITOR_CONTEXT_DUPLICATE, () ->
                {
                    this.addGlyph(this.panel.glyphs.get(this.glyphsList.getCurrentFirst()));
                });

                menu.action(Icons.FONT, UIKeys.FONT_EDITOR_CONTEXT_COPY_SYMBOL, () -> Window.setClipboard(String.valueOf((char) this.glyphsList.getCurrentFirst().intValue())));
                menu.action(Icons.COPY, UIKeys.FONT_EDITOR_CONTEXT_COPY_CODE, () -> Window.setClipboard(String.valueOf(this.glyphsList.getCurrentFirst())));
                menu.action(Icons.REMOVE, UIKeys.FONT_EDITOR_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeGlyph);
            }
        });
        this.searchList = new UISearchList<Integer>(this.glyphsList);
        this.searchList.label(UIKeys.SEARCH);

        this.column.relative(this.content).xy(10, 10).w(1F, -20);
        this.searchList.relative(this.column).y(1F, 5).w(1F).hTo(this.content.getFlex(), 1F);

        this.content.add(this.column, this.searchList);
    }

    private void addGlyph(GlyphData data)
    {
        UIOverlay.addOverlay(this.getContext(), new UIPromptOverlayPanel(
            UIKeys.FONT_EDITOR_CONTEXT_ADD_TITLE,
            UIKeys.FONT_EDITOR_CONTEXT_ADD_DESCRIPTION,
            (str) -> this.addGlyph(str, data)
        ));
    }

    private int parseGlyph(String string)
    {
        string = string.trim();

        int glyph = -1;

        try
        {
            glyph = Integer.parseInt(string);
        }
        catch (Exception e)
        {}

        if (glyph < 0 && string.startsWith("#"))
        {
            try
            {
                glyph = StringUtils.parseHex(string.substring(1));
            }
            catch (Exception e)
            {}
        }

        return glyph;
    }

    private void addGlyph(String string, GlyphData data)
    {
        int glyph = this.parseGlyph(string);

        if (glyph < 0)
        {
            UIOverlay.addOverlay(this.getContext(), new UIMessageOverlayPanel(
                UIKeys.ERROR,
                UIKeys.FONT_EDITOR_ERROR_INVALID_GLYPH.format(string)
            ));

            return;
        }

        if (this.panel.glyphs.containsKey(glyph))
        {
            UIOverlay.addOverlay(this.getContext(), new UIMessageOverlayPanel(
                UIKeys.ERROR,
                UIKeys.FONT_EDITOR_ERROR_ALREADY_EXISTS.format(string, Character.getName(glyph).toLowerCase())
            ));

            return;
        }

        int height = this.panel.font.height;
        int width = (int) (height * 0.7F);
        Glyph aGlyph = new Glyph(0, 0, width, height);

        if (data == null)
        {
            aGlyph.character = (char) glyph;
            aGlyph.advance = width;
            aGlyph.offsetX = 0;
            aGlyph.offsetY = -height;
        }
        else
        {
            aGlyph.fromData(data.glyph.toData());
            aGlyph.character = (char) glyph;
        }

        Pixels pixels = data == null ? Pixels.fromSize(width, height) : data.pixels.createCopy(0, 0, data.pixels.width, data.pixels.height);
        GlyphData newData = new GlyphData(aGlyph, pixels);

        this.glyphsList.add(glyph);
        this.glyphsList.sort();
        this.glyphsList.setCurrentScroll(glyph);
        this.panel.glyphs.put(glyph, newData);
        this.panel.pickGlyph(glyph);
    }

    private void removeGlyph()
    {
        int glyph = this.glyphsList.getCurrentFirst();
        int index = this.glyphsList.getIndex();

        this.panel.glyphs.remove(glyph);
        this.glyphsList.remove(glyph);

        List<Integer> list = this.glyphsList.getList();

        index = MathUtils.clamp(index, 0, list.size() - 1);

        this.panel.pickGlyph(list.get(index));
        this.glyphsList.setIndex(index);
    }
}