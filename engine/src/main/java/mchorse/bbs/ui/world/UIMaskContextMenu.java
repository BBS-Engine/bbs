package mchorse.bbs.ui.world;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.context.UIContextMenu;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.undo.ChunkProxy;

import java.util.Iterator;

public class UIMaskContextMenu extends UIContextMenu
{
    public UIIcon add;
    public UIIcon enabled;

    private ChunkProxy proxy;

    public UIMaskContextMenu(ChunkProxy proxy)
    {
        this.proxy = proxy;

        this.add = new UIIcon(Icons.ADD, (b) ->
        {
            this.proxy.getMask().add(this.proxy.getSet().air);
            this.rebuild();
        });
        this.add.wh(24, 24);

        this.enabled = new UIIcon(Icons.UNLOCKED, (b) ->
        {
            this.proxy.setMaskEnabled(!this.proxy.getMaskEnabled());
            this.updateEnabled();
        });
        this.enabled.wh(24, 24);

        this.rebuild();
        this.updateEnabled();
    }

    private void updateEnabled()
    {
        this.enabled.both(this.proxy.getMaskEnabled() ? Icons.LOCKED : Icons.UNLOCKED);
    }

    private void rebuild()
    {
        this.removeAll();
        this.add(this.enabled);

        for (int i = 0; i < this.proxy.getMask().size(); i++)
        {
            int cacheI = i;

            UIBlockVariant variant = new UIBlockVariant((v) -> this.proxy.getMask().set(cacheI, v));

            variant.setVariant(this.proxy.getMask().get(i));
            this.add(variant);
        }

        this.add(this.add);
        this.resize();
    }

    @Override
    protected void onRemove(UIElement parent)
    {
        super.onRemove(parent);

        Iterator<IBlockVariant> it = this.proxy.getMask().iterator();

        while (it.hasNext())
        {
            if (it.next().isAir())
            {
                it.remove();
            }
        }
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public void setMouse(UIContext context)
    {
        this.xy(context.mouseX(), context.mouseY()).w(150).grid(5).width(24).height(24).padding(5);
    }

    @Override
    public void render(UIContext context)
    {
        int color = BBSSettings.primaryColor.get();

        context.draw.dropShadow(this.area.x, this.area.y, this.area.ex(), this.area.ey(), 10, Colors.A25 | color, color);

        super.render(context);
    }
}