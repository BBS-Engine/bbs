package mchorse.bbs.ui.forms.editors.panels;

import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.forms.forms.BlockForm;
import mchorse.bbs.ui.forms.editors.forms.UIForm;
import mchorse.bbs.ui.world.UIBlockVariant;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;

public class UIBlockFormPanel extends UIFormPanel<BlockForm>
{
    public UIBlockVariant block;

    public UIBlockFormPanel(UIForm<BlockForm> editor)
    {
        super(editor);

        this.block = new UIBlockVariant((b) -> this.form.block.set(b.getLink()));
        this.block.relative(this).x(0.5F).y(1F, -10).anchor(0.5F, 1F);

        this.add(this.block);
    }

    @Override
    public void startEdit(BlockForm form)
    {
        super.startEdit(form);

        ChunkBuilder builder = this.editor.getContext().menu.bridge.get(IBridgeWorld.class).getChunkBuilder();
        IBlockVariant variant = builder.models.getVariant(this.form.block.get());

        this.block.setVariant(variant);
    }
}