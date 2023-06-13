package mchorse.bbs.ui.tileset.panels;

import mchorse.bbs.cubic.data.model.ModelCube;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UITextareaOverlayPanel;
import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.world.entities.UIVector3d;
import mchorse.bbs.voxel.tilesets.factory.BlockModelCombined;

public class UIModelBlockCombined extends UIModelBlockFactory<BlockModelCombined>
{
    public UIVector3d offset;
    public UIToggle rotations;
    public UIButton copy;
    public UIButton paste;

    public UIModelBlockCombined(UITileSetEditorPanel editor)
    {
        super(editor);

        this.copy = new UIButton(UIKeys.TILE_SET_COMBINED_CONTEXT_COPY, this::copyData);
        this.paste = new UIButton(UIKeys.TILE_SET_COMBINED_CONTEXT_PASTE, this::pasteData);
        this.rotations = new UIToggle(UIKeys.TILE_SET_COMBINED_ROTATIONS, (b) ->
        {
            this.model.rotations = b.getValue();
            this.recompile();
        });
        this.offset = new UIVector3d((v) ->
        {
            this.model.offset.set(v);
            this.recompile();
        });

        this.view.add(UI.label(UIKeys.TILE_SET_COMBINED_OFFSET).marginTop(8), this.offset);
        this.view.add(this.rotations, UI.row(this.copy, this.paste));

        this.addCollisionBoxFields();
    }

    private void copyData(UIButton b)
    {
        ListType list = new ListType();

        for (ModelCube cube : this.model.cubes)
        {
            list.add(cube.toData());
        }

        Window.setClipboard(DataToString.toString(list, true));
    }

    private void pasteData(UIButton b)
    {
        UITextareaOverlayPanel panel = new UITextareaOverlayPanel(
            UIKeys.TILE_SET_COMBINED_PASTE_MODAL_TITLE,
            UIKeys.TILE_SET_COMBINED_PASTE_MODAL_DESCRIPTION,
            this::pasteData
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void pasteData(String s)
    {
        ListType list = DataToString.listFromString(s);

        if (list == null)
        {
            return;
        }

        this.model.cubes.clear();

        for (BaseType base : list)
        {
            ModelCube cube = new ModelCube();

            cube.fromData(base.asMap());
            this.model.cubes.add(cube);
        }

        this.model.compile();
        this.editor.dirty();
    }

    @Override
    public void fill(BlockModelCombined model)
    {
        super.fill(model);

        this.offset.fill(model.offset);
        this.rotations.setValue(model.rotations);
    }
}