package mchorse.bbs.voxel.tilesets.factory;

import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.tileset.panels.UIModelBlockFactory;
import mchorse.bbs.ui.utils.icons.Icon;

import java.util.function.Function;

public class BlockModelFactoryData
{
    public final Icon icon;
    public final Function<UITileSetEditorPanel, UIModelBlockFactory> panel;

    public BlockModelFactoryData(Icon icon, Function<UITileSetEditorPanel, UIModelBlockFactory> panel)
    {
        this.icon = icon;
        this.panel = panel;
    }
}