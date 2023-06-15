package mchorse.bbs.ui.world.tools.schematic;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.UIBlockVariant;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.storage.data.ChunkDisplay;
import mchorse.bbs.voxel.tilesets.BlockSet;
import net.querz.nbt.tag.CompoundTag;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UISchematicOverlayPanel extends UIOverlayPanel
{
    public UISchematicRenderer renderer;
    public UIScrollView blocks;

    private BlockSet blockSet;
    private Consumer<Chunk> callback;
    private Schematic schematic;

    private ChunkDisplay display;
    private boolean first = true;
    private Map<Integer, UIBlockVariant> blockVariants = new HashMap<Integer, UIBlockVariant>();

    public UISchematicOverlayPanel(BlockSet blockSet, CompoundTag schematic, Consumer<Chunk> callback)
    {
        super(UIKeys.WORLD_EDITOR_SCHEMATIC_TITLE);

        this.blockSet = blockSet;
        this.callback = callback;
        this.schematic = Schematic.fromSchematic(schematic, blockSet);

        this.display = new ChunkDisplay(null, this.schematic.getChunk(), 0, 0, 0);
        this.renderer = new UISchematicRenderer(this.display, this::handleRenderCallback);
        this.renderer.relative(this.content).x(-10).w(1F, 20).h(1F);
        this.blocks = UI.scrollView(5, 10);
        this.blocks.relative(this.renderer).x(1F).w(120).h(1F).anchorX(1F);

        int i = 0;

        for (Integer integer : this.schematic.getUniqueBlocks())
        {
            UILabel label = UI.label(UIKeys.WORLD_EDITOR_SCHEMATIC_BLOCK.format(i)).anchor(0, 0.5F);
            UIBlockVariant variant = new UIBlockVariant((b) -> this.replace(integer, b)).allowEmpty();
            UIElement row = UI.row(label, variant);

            label.h(24);
            variant.setVariant(blockSet.variants.get(0), false);
            this.blocks.add(row);
            this.blockVariants.put(integer, variant);

            i += 1;
        }

        this.content.add(this.renderer, new UIRenderable(this::renderScrollBackground), this.blocks);
    }

    private void handleRenderCallback(Vector3i vector3i)
    {
        int block = this.schematic.getDataBlockAt(vector3i.x, vector3i.y, vector3i.z);

        if (block != 0)
        {
            UIBlockVariant variant = this.blockVariants.get(block);

            if (variant != null)
            {
                variant.clickItself();
            }
        }
    }

    private void replace(Integer blockId, IBlockVariant blockVariant)
    {
        if (blockVariant == null)
        {
            blockVariant = this.blockSet.air;
        }

        this.schematic.replace(blockId, blockVariant);

        this.updateDisplay();
    }

    private void updateDisplay()
    {
        UIContext context = this.getContext();

        context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder().build(context.render, this.display, null);
    }

    @Override
    public void onClose()
    {
        super.onClose();

        if (this.callback != null)
        {
            this.callback.accept(this.display.chunk);
            this.display.delete();
        }
    }

    private void renderScrollBackground(UIContext context)
    {
        this.blocks.area.render(context.batcher, Colors.A50);
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        if (this.first)
        {
            this.first = false;

            this.updateDisplay();
        }

        super.renderBackground(context);

        this.renderer.area.render(context.batcher, 0xff2f2f2f);
    }
}