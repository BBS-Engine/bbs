package mchorse.bbs.ui.tileset;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.factory.MapFactory;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.tileset.panels.UIModelBlockFactory;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactoryData;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UITileSetEditorPanel extends UIDashboardPanel
{
    public UIElement editor;
    public UIElement iconBar;
    public UITileSetRenderer renderer;
    public UIIcon edit;
    public UIIcon atlas;
    public UIIcon save;
    public UIModelBlockFactory panel;

    private ChunkBuilder blockBuilder;
    private BlockSet blockSet;

    private Map<Class, UIModelBlockFactory> panels = new HashMap<Class, UIModelBlockFactory>();

    private boolean dirty;

    public UITileSetEditorPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.editor = new UIElement();
        this.editor.relative(this).w(1F, -20).h(1F);
        this.iconBar = new UIElement();
        this.iconBar.relative(this).x(1F, -20).w(20).h(1F).column(0);

        this.renderer = new UITileSetRenderer(this, this::selectBlock);
        this.renderer.relative(this).w(1F).h(1F);
        this.edit = new UIIcon(Icons.MORE, (b) ->
        {
            UIBlockModelFactoriesOverlayPanel panel = new UIBlockModelFactoriesOverlayPanel(this, UIKeys.TILE_SET_BLOCK_MODELS_TITLE);

            UIOverlay.addOverlay(this.dashboard.context, panel);
        });
        this.atlas = new UIIcon(Icons.MATERIAL, (b) -> this.openAtlasPicker());
        this.save = new UIIcon(Icons.SAVED, (b) -> this.save());

        this.iconBar.add(this.edit, this.atlas, this.save);
        this.add(this.renderer, new UIRenderable(this::renderBackground), this.iconBar, this.editor);

        /* Register panels */
        MapFactory<BlockModelFactory, BlockModelFactoryData> factory = BBS.getFactoryBlockModels();

        for (Link key : factory.getKeys())
        {
            this.panels.put(factory.getTypeClass(key), factory.getData(key).panel.apply(this));
        }
    }

    public ChunkBuilder getBlockBuilder()
    {
        return this.blockBuilder;
    }

    public BlockSet getBlockSet()
    {
        return this.blockSet;
    }

    private void openAtlasPicker()
    {
        BlockSet blockSet = this.getBlockSet();
        UITexturePicker.open(this.editor, blockSet.atlas, (l) ->
        {
            Texture texture = BBS.getTextures().getTexture(l);

            if (texture != null)
            {
                blockSet.atlas = l;
                blockSet.atlasWidth = texture.width;
                blockSet.atlasHeight = texture.height;

                this.dirty();
            }
        });
    }

    private void save()
    {
        this.dirty = false;

        this.blockSet.rebuild();
        this.save.both(Icons.SAVED);
        this.dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks.rebuild();

        try
        {
            MapType map = this.blockSet.toData();

            IOUtils.writeText(BBS.getAssetsPath(this.blockSet.id.path), DataToString.toString(map, true));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void dirty()
    {
        this.dirty = true;

        this.save.both(Icons.SAVE);
    }

    public void selectBlock(BlockModelFactory current)
    {
        if (this.panel != null && this.panel.model == current)
        {
            return;
        }

        if (this.panel != null)
        {
            this.panel.removeFromParent();
        }

        this.renderer.setSelected(current);

        if (current != null)
        {
            this.panel = this.panels.get(current.getClass());

            if (this.panel != null)
            {
                this.panel.relative(this).x(1F, -220).y(0).w(200).h(1F);
                this.panel.fill(current);
                this.panel.resize();

                this.addBefore(this.iconBar, this.panel);
            }

            int i = this.blockSet.factories.indexOf(current);
            Vector3i p = this.renderer.getBlockPosition(i);

            this.renderer.setPosition(p.x + 0.5F, p.y + 0.5F, p.z + 0.5F);
        }
        else
        {
            this.panel = null;
        }
    }

    @Override
    public void open()
    {
        super.open();

        this.blockBuilder = dashboard.bridge.get(IBridgeWorld.class).getChunkBuilder();
        this.blockSet = this.blockBuilder.models;
    }

    @Override
    public void close()
    {
        super.close();

        if (this.dirty)
        {
            this.dirty = false;

            this.dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks.rebuild();
        }
    }

    private void renderBackground(UIContext context)
    {
        this.iconBar.area.render(context.draw, Colors.A50);
        context.draw.gradientHBox(this.iconBar.area.x - 6, this.iconBar.area.y, this.iconBar.area.x, this.iconBar.area.ey(), 0, Colors.A12);
    }
}