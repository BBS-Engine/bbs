package mchorse.bbs.ui.tileset.panels;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.voxel.tilesets.factory.BlockModelVertical;

public class UIModelBlockVertical <T extends BlockModelVertical> extends UIModelBlockFactory<T>
{
    public UIButton top;
    public UIButton bottom;

    public UIModelBlockVertical(UITileSetEditorPanel editor)
    {
        super(editor);

        this.top = new UIButton(UIKeys.TILE_SET_VERTICAL_TOP, (b) -> this.editUV(this.model.topUV));
        this.bottom = new UIButton(UIKeys.TILE_SET_VERTICAL_BOTTOM, (b) -> this.editUV(this.model.bottomUV));

        this.view.add(UI.row(this.top, this.bottom));

        this.addCollisionBoxFields();
    }
}