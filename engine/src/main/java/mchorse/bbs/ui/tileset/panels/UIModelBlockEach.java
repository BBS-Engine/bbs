package mchorse.bbs.ui.tileset.panels;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.voxel.tilesets.factory.BlockModelEach;

public class UIModelBlockEach extends UIModelBlockVertical<BlockModelEach>
{
    public UIButton back;
    public UIButton right;
    public UIButton left;

    public UIModelBlockEach(UITileSetEditorPanel editor)
    {
        super(editor);

        this.back = new UIButton(UIKeys.TILE_SET_EACH_BACK, (b) -> this.editUV(this.model.backUV));
        this.right = new UIButton(UIKeys.TILE_SET_EACH_RIGHT, (b) -> this.editUV(this.model.rightUV));
        this.left = new UIButton(UIKeys.TILE_SET_EACH_LEFT, (b) -> this.editUV(this.model.leftUV));

        this.all.removeFromParent();
        this.all.label = UIKeys.TILE_SET_EACH_FRONT;
        this.top.removeFromParent();
        this.bottom.removeFromParent();

        this.view.add(UI.row(this.all, this.back));
        this.view.add(UI.row(this.top, this.bottom));
        this.view.add(UI.row(this.right, this.left));

        this.addCollisionBoxFields();
    }
}