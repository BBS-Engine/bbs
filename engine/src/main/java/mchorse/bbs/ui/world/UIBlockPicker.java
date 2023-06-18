package mchorse.bbs.ui.world;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.ScrollArea;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.util.function.Consumer;

public class UIBlockPicker extends UIElement
{
    public static final int BLOCK_SLOT_SIZE = 24;

    public BlockSet blocks;
    public Consumer<IBlockVariant> callback;

    public ScrollArea scroll = new ScrollArea().cancelScrolling();

    public UIBlockPicker(BlockSet blockSet, Consumer<IBlockVariant> callback)
    {
        this.blocks = blockSet;
        this.callback = callback;

        this.eventPropagataion(EventPropagation.BLOCK_INSIDE);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.copy(this.area);
        this.scroll.offset(-10);

        int blocks = this.scroll.w / BLOCK_SLOT_SIZE;

        this.scroll.scrollSize = (int) (Math.ceil((this.blocks.variants.size() + 1) / (float) blocks)) * BLOCK_SLOT_SIZE;
        this.scroll.clamp();
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.scroll.mouseClicked(context))
        {
            return true;
        }

        if (this.scroll.isInside(context) && context.mouseButton == 0 && this.callback != null)
        {
            int blocks = this.scroll.w / BLOCK_SLOT_SIZE;
            int x = context.mouseX - this.scroll.x;
            int y = context.mouseY - this.scroll.y + this.scroll.scroll;
            int index = MathUtils.clamp(x / BLOCK_SLOT_SIZE, 0, blocks - 1) + (int) Math.floor(y / BLOCK_SLOT_SIZE) * blocks;

            if (index >= 0 && index < this.blocks.variants.size() + 1)
            {
                this.callback.accept(index == 0 ? this.blocks.air : this.blocks.variants.get(index - 1));
            }
        }

        if (!this.area.isInside(context))
        {
            this.removeFromParent();

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        return this.scroll.mouseScroll(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        this.scroll.mouseReleased(context);

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        this.scroll.drag(context);

        /* Render background */
        context.batcher.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.WHITE);
        context.batcher.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        context.batcher.clip(this.scroll, context);

        int blocks = this.scroll.w / BLOCK_SLOT_SIZE;
        int hovered = -2;
        int hoveredX = 0;
        int hoveredY = 0;

        for (int i = 0; i < this.blocks.variants.size() + 1; i++)
        {
            int x = this.scroll.x + (i % blocks) * BLOCK_SLOT_SIZE;
            int y = this.scroll.y + (i / blocks) * BLOCK_SLOT_SIZE - this.scroll.scroll;

            Area.SHARED.set(x, y, BLOCK_SLOT_SIZE, BLOCK_SLOT_SIZE);

            boolean inside = Area.SHARED.isInside(context);

            context.batcher.box(x + 1, y + 1, x + BLOCK_SLOT_SIZE - 1, y + BLOCK_SLOT_SIZE - 1, inside ? Colors.setA(Colors.ACTIVE, 0.25F) : Colors.A25);

            if (inside)
            {
                hovered = i - 1;
                hoveredX = x;
                hoveredY = y;
            }
        }

        /* Render blocks */
        context.batcher.render();

        ChunkBuilder blockBuilder = context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder();

        GLStates.setupDepthFunction3D();

        context.render.getTextures().bind(blockBuilder.models.atlas);

        for (int i = 1; i < this.blocks.variants.size() + 1; i++)
        {
            IBlockVariant variant = this.blocks.variants.get(i - 1);
            int x = this.scroll.x + (i % blocks) * BLOCK_SLOT_SIZE;
            int y = this.scroll.y + (i / blocks) * BLOCK_SLOT_SIZE - this.scroll.scroll;
            int scale = 12;

            context.batcher.clip(x + 1, y + 1, BLOCK_SLOT_SIZE - 2, BLOCK_SLOT_SIZE - 2, context);
            blockBuilder.renderInUI(context, variant, x + BLOCK_SLOT_SIZE / 2, y + BLOCK_SLOT_SIZE / 2, scale);
            context.batcher.unclip(context);
        }

        GLStates.setupDepthFunction2D();

        this.scroll.renderScrollbar(context.batcher);

        context.batcher.unclip(context);

        /* Render tooltip of highlighted block picker */
        if (hovered >= -1)
        {
            IBlockVariant variant = hovered < 0 ? this.blocks.air : this.blocks.variants.get(hovered);
            String label = variant.getLink().toString();
            int w = context.font.getWidth(label);

            context.batcher.textCard(context.font, label, hoveredX + BLOCK_SLOT_SIZE / 2 - w / 2, hoveredY + BLOCK_SLOT_SIZE + 2);
        }

        super.render(context);
    }
}