package mchorse.bbs.ui.world;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIClickable;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;

import java.util.function.Consumer;

public class UIBlockVariant extends UIClickable<IBlockVariant>
{
    private IBlockVariant variant;
    private UIBlockPicker picker;

    public boolean renderDisabled = true;

    public UIBlockVariant(Consumer<IBlockVariant> callback)
    {
        super(callback);

        this.wh(24, 24);
    }

    public UIBlockVariant allowEmpty()
    {
        this.context((menu) -> menu.action(Icons.TRASH, UIKeys.BLOCK_VARIANT_CONTEXT_INSERT_EMPTY, () -> this.setVariant(null)));

        return this;
    }

    public IBlockVariant getVariant()
    {
        return this.variant;
    }

    public void setVariant(IBlockVariant variant)
    {
        this.setVariant(variant, true);
    }

    public void setVariant(IBlockVariant variant, boolean notify)
    {
        this.variant = variant;

        if (this.callback != null && notify)
        {
            this.callback.accept(variant);
        }

        if (this.picker != null)
        {
            this.picker.removeFromParent();
        }
    }

    @Override
    protected void click(int mouseButton)
    {
        UIContext context = this.getContext();

        if (this.picker == null)
        {
            this.picker = new UIBlockPicker(context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder().models, this::setVariant);
        }

        int size = UIBlockPicker.BLOCK_SLOT_SIZE;

        this.picker.relative(context.menu.overlay).xy(0.5F, 0.5F).wh(20 + size * 8, 20 + size * 4).anchor(0.5F, 0.5F);
        this.picker.resize();

        context.menu.overlay.add(this.picker);
    }

    @Override
    protected IBlockVariant get()
    {
        return this.variant;
    }

    @Override
    protected void renderSkin(UIContext context)
    {
        int border = this.picker != null && this.picker.hasParent() ? Colors.A100 | BBSSettings.primaryColor.get() : Colors.WHITE;

        context.draw.box(this.area.x, this.area.y, this.area.ex(), this.area.ey(), border);
        context.draw.box(this.area.x + 1, this.area.y + 1, this.area.ex() - 1, this.area.ey() - 1, Colors.LIGHTEST_GRAY);

        if (this.variant != null)
        {
            ChunkBuilder blockBuilder = context.menu.bridge.get(IBridgeWorld.class).getChunkBuilder();

            int x = this.area.x;
            int y = this.area.y;
            int scale = Math.min(this.area.w, this.area.h) / 2;

            context.draw.clip(x + 1, y + 1, this.area.w - 2, this.area.h - 2, context);

            blockBuilder.renderInUI(context, this.variant, this.area.mx(), this.area.my(), scale);

            context.draw.unclip(context);
        }

        if (this.renderDisabled)
        {
            context.draw.lockedArea(this);
        }
    }
}