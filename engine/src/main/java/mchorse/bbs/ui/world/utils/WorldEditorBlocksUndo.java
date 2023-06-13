package mchorse.bbs.ui.world.utils;

import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.voxel.undo.BlockDiff;
import mchorse.bbs.voxel.undo.BlocksUndo;
import mchorse.bbs.voxel.utils.BlockSelection;
import mchorse.bbs.world.World;

import java.util.List;

public class WorldEditorBlocksUndo extends BlocksUndo
{
    public UIWorldEditorPanel editor;
    public BlockSelection before = new BlockSelection();
    public BlockSelection after = new BlockSelection();

    public WorldEditorBlocksUndo(UIWorldEditorPanel editor, List<BlockDiff> blocks, BlockSelection before, BlockSelection after)
    {
        super(blocks);

        this.editor = editor;
        this.before.copy(before);
        this.after.copy(after);
    }

    @Override
    public void undo(World context)
    {
        super.undo(context);

        this.editor.getSelection().copy(this.before);
        this.editor.updateSelection();
    }

    @Override
    public void redo(World context)
    {
        super.redo(context);

        this.editor.getSelection().copy(this.after);
        this.editor.updateSelection();
    }
}