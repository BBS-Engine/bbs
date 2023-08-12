package mchorse.bbs.ui.font;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.graphics.text.Glyph;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UISidebarDashboardPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.BoxPacker;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.PNGEncoder;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.resources.Pixels;
import org.joml.Vector2i;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class UIFontPanel extends UISidebarDashboardPanel
{
    public UIFontOverlayPanel overlay;
    public UIGlyphPixelsEditor pixelsEditor;

    public UIIcon load;
    public UIIcon open;
    public UIIcon save;
    public UIIcon codes;

    public Font font;
    public Link fontLink;
    public Map<Integer, GlyphData> glyphs = new HashMap<>();

    public UIFontPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.overlay = new UIFontOverlayPanel(UIKeys.FONT_EDITOR_TITLE, this);
        this.pixelsEditor = new UIGlyphPixelsEditor();

        this.load = new UIIcon(Icons.MORE, (b) -> this.loadFont());
        this.load.tooltip(UIKeys.FONT_EDITOR_LOAD, Direction.LEFT);
        this.open = new UIIcon(Icons.GEAR, (b) ->
        {
            UIOverlay.addOverlayRight(this.getContext(), this.overlay, 200, 20);
        });
        this.save = new UIIcon(Icons.SAVED, (b) -> this.saveFont());
        this.save.tooltip(UIKeys.FONT_EDITOR_SAVE, Direction.LEFT);
        this.codes = new UIIcon(Icons.PROPERTIES, (b) -> this.openFormattingCodesEditor());
        this.codes.tooltip(UIKeys.FONT_EDITOR_CODES, Direction.LEFT);

        this.pixelsEditor.relative(this.editor).full();

        this.editor.add(this.pixelsEditor);
        this.iconBar.add(this.load, this.open, this.save, this.codes);

        this.loadFont(Link.create("assets:fonts/bbs_round.json"));

        this.keys().register(Keys.OPEN_DATA_MANAGER, this.open::clickItself);
    }

    private void openFormattingCodesEditor()
    {
        UIOverlay.addOverlay(this.getContext(), new UIFontFormatsOverlayPanel(this.font), 0.6F, 0.8F);
    }

    public void pickGlyph(Integer glyph)
    {
        GlyphData data = this.glyphs.get(glyph);

        this.pixelsEditor.fillGlyph(data);
    }

    /* Save and load the font */

    private void loadFont()
    {
        Set<Link> linkSet = BBS.getFonts().getFontSet();

        if (!linkSet.isEmpty())
        {
            UIStringOverlayPanel panel = UIStringOverlayPanel.links(UIKeys.FONT_EDITOR_LOAD_TITLE, false, linkSet, this::loadFont);

            UIOverlay.addOverlay(this.getContext(), panel);
        }
    }

    private void loadFont(Link link)
    {
        if (link == null)
        {
            return;
        }

        try
        {
            for (GlyphData data : this.glyphs.values())
            {
                data.pixels.delete();
            }

            this.glyphs.clear();

            MapType data = DataToString.mapFromString(IOUtils.readText(BBS.getProvider().getAsset(link)));

            this.font = Font.fromMap(data);
            this.fontLink = link;
            Link textureLink = new Link(link.source, StringUtils.replaceExtension(link.path, "png"));

            Texture texture = BBS.getTextures().getTexture(textureLink);
            Pixels pixels = Pixels.fromTexture(texture);

            for (int i = 0; i < font.glyphs.length; i++)
            {
                Glyph glyph = font.glyphs[i];

                if (glyph != null)
                {
                    Pixels copy = pixels.createCopy(glyph.tile.x, glyph.tile.y, glyph.tile.w, glyph.tile.h);

                    this.glyphs.put((int) glyph.character, new GlyphData(glyph, copy));
                }
            }

            pixels.delete();

            this.overlay.glyphsList.clear();
            this.overlay.glyphsList.add(this.glyphs.keySet());
            this.overlay.glyphsList.sort();

            this.pickGlyph(this.overlay.glyphsList.getList().get(0));

            this.overlay.name.setText(this.font.name);
            this.overlay.height.setValue(this.font.height);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void saveFont()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.FONT_EDITOR_SAVE_TITLE,
            UIKeys.FONT_EDITOR_SAVE_DESCRIPTION,
            (path) -> this.saveFont(Link.create(path))
        );

        panel.text.setText(this.fontLink.toString());

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void saveFont(Link link)
    {
        List<Glyph> glyphs = new ArrayList<>();

        for (Map.Entry<Integer, GlyphData> entry : this.glyphs.entrySet())
        {
            Glyph glyph = entry.getValue().glyph;

            glyph.character = (char) entry.getKey().intValue();
            glyphs.add(glyph);
        }

        glyphs.sort(Comparator.comparingInt(a -> a.character));

        Vector2i result = BoxPacker.pack(glyphs.stream().map((glyph) -> glyph.tile).collect(Collectors.toList()), 1);
        Pixels pixels = Pixels.fromSize(result.x, result.y);

        for (GlyphData data : this.glyphs.values())
        {
            pixels.draw(data.pixels, data.glyph.tile.x, data.glyph.tile.y);
        }

        this.fontLink = link;

        try
        {
            File textureFile = BBS.getProvider().getFile(new Link(link.source, StringUtils.replaceExtension(link.path, "png")));

            textureFile.getParentFile().mkdirs();
            PNGEncoder.writeToFile(pixels, textureFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {
            MapType fontData = new MapType(false);

            this.font.setupGlyphs(glyphs);
            this.font.toData(fontData);
            DataToString.write(BBS.getProvider().getFile(link), fontData, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        pixels.delete();
    }

    @Override
    public void requestNames()
    {}
}