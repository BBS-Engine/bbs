package mchorse.bbs.ui.world.worlds.overlays;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.voxel.tilesets.BlockSet;

import java.util.HashSet;
import java.util.function.Consumer;

public class UIConvertWorldMetadataOverlayPanel extends UIWorldMetadataOverlayPanel
{
    public UIConvertWorldMetadataOverlayPanel(BlockSet blocks, Consumer<UIWorldMetadataOverlayPanel> callback)
    {
        super(blocks, callback, new HashSet<String>());

        this.submit.label = UIKeys.WORLDS_METADATA_CONVERT;
    }

    @Override
    protected void rebuild()
    {
        int color = Colors.A50 | BBSSettings.primaryColor.get();

        this.view.removeAll();
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_GENERAL).background(color));
        this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_CHUNK_SIZE).marginTop(6), this.chunkSize);
        this.view.add(this.compress, this.column.marginBottom(12));

        if (this.column.getValue())
        {
            this.view.add(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_OPTIONS).background(color));
            this.view.add(UI.row(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_BASE, 20).anchor(0, 0.5F), this.columnBase));
            this.view.add(UI.row(UI.label(UIKeys.WORLDS_OPTIONS_COLUMN_HEIGHT, 20).anchor(0, 0.5F), this.columnHeight).marginBottom(12));
        }

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