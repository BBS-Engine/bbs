package mchorse.bbs.ui.tileset.panels;

import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.voxel.tilesets.factory.BlockModelVertical;

public class UIModelBlockWithCollision <T extends BlockModelVertical> extends UIModelBlockFactory<T>
{
    public UIModelBlockWithCollision(UITileSetEditorPanel editor)
    {
        super(editor);

        this.addCollisionBoxFields();
    }
}