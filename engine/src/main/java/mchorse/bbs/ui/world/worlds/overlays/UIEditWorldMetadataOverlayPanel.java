package mchorse.bbs.ui.world.worlds.overlays;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.util.HashSet;
import java.util.function.Consumer;

public class UIEditWorldMetadataOverlayPanel extends UIWorldMetadataOverlayPanel
{
    public UIEditWorldMetadataOverlayPanel(BlockSet blocks, Consumer<UIWorldMetadataOverlayPanel> callback)
    {
        super(blocks, callback, new HashSet<>());

        this.submit.label = UIKeys.EDIT_WORLD_SUBMIT;
    }

    @Override
    protected void rebuild()
    {
        int color = Colors.A50 | BBSSettings.primaryColor.get();

        this.view.removeAll();
        this.view.add(UI.label(UIKeys.EDIT_WORLD_TITLE).background(color), this.name.marginBottom(12));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_CHUNKS).marginTop(6), this.chunks);
        this.view.add(UI.label(UIKeys.EDIT_WORLD_CHUNK_LIMIT).background(color).marginBottom(6).marginTop(6), this.x.marginBottom(6));

        if (!this.column.getValue())
        {
            this.view.add(this.y.marginBottom(6));
        }

        this.view.add(this.z.marginBottom(6));
        this.view.add(this.submit);

        this.view.resize();
    }

    @Override
    protected boolean cannotSubmitWorldId()
    {
        return false;
    }

    @Override
    protected boolean cannotSubmitWithEmptyGeneratorValues()
    {
        return false;
    }
}