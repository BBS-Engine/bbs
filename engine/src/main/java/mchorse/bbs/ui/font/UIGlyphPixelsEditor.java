package mchorse.bbs.ui.font;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.textures.UIPixelsEditor;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.resources.Pixels;

public class UIGlyphPixelsEditor extends UIPixelsEditor
{
    public UITrackpad advance;
    public UITrackpad offsetX;
    public UITrackpad offsetY;
    public UITrackpad width;
    public UITrackpad height;
    public UIToggle emoji;
    public UIKernings kernings;

    private GlyphData data;

    public UIGlyphPixelsEditor()
    {
        this.advance = new UITrackpad((v) -> this.data.glyph.advance = v.intValue()).integer();
        this.offsetX = new UITrackpad((v) -> this.data.glyph.offsetX = v.intValue()).integer();
        this.offsetY = new UITrackpad((v) -> this.data.glyph.offsetY = v.intValue()).integer();
        this.width = new UITrackpad((v) ->
        {
            this.data.glyph.tile.w = v.intValue();
            this.updatePixels();
        }).integer().limit(0);
        this.height = new UITrackpad((v) ->
        {
            this.data.glyph.tile.h = v.intValue();
            this.updatePixels();
        }).integer().limit(0);
        this.emoji = new UIToggle(UIKeys.FONT_EDITOR_GLYPH_EMOJI, (b) -> this.data.glyph.emoji = b.getValue());
        this.emoji.tooltip(UIKeys.FONT_EDITOR_GLYPH_EMOJI_TOOLTIP);
        this.kernings = new UIKernings();

        this.editor.add(UI.label(UIKeys.FONT_EDITOR_GLYPH_ADVANCE), this.advance);
        this.editor.add(UI.label(UIKeys.FONT_EDITOR_GLYPH_OFFSET), UI.row(this.offsetX, this.offsetY));
        this.editor.add(UI.label(UIKeys.FONT_EDITOR_GLYPH_SIZE), UI.row(this.width, this.height));
        this.editor.add(this.emoji, this.kernings);
    }

    private void updatePixels()
    {
        Pixels pixels = Pixels.fromSize((int) this.width.getValue(), (int) this.height.getValue());
        Pixels oldPixels = this.data.pixels;

        pixels.draw(oldPixels, 0, 0);
        oldPixels.delete();

        this.data.pixels = pixels;

        this.fillPixels(pixels);

        this.setEditing(true);
        this.primary.setColor(Colors.WHITE);
        this.secondary.setColor(Colors.A100);
    }

    public void fillGlyph(GlyphData data)
    {
        this.data = data;

        this.advance.setValue(data.glyph.advance);
        this.offsetX.setValue(data.glyph.offsetX);
        this.offsetY.setValue(data.glyph.offsetY);
        this.width.setValue(data.glyph.tile.w);
        this.height.setValue(data.glyph.tile.h);
        this.emoji.setValue(data.glyph.emoji);
        this.kernings.fill(data.glyph.kernings);

        this.fillPixels(data.pixels);

        this.setEditing(true);
        this.primary.setColor(Colors.WHITE);
        this.secondary.setColor(Colors.A100);
    }
}