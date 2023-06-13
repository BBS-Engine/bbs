package mchorse.bbs.ui.world.utils;

import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.undo.UndoManager;
import mchorse.bbs.voxel.storage.ChunkManager;
import mchorse.bbs.voxel.undo.BlockDiff;
import mchorse.bbs.voxel.undo.BlocksUndo;
import mchorse.bbs.voxel.undo.ChunkProxy;
import mchorse.bbs.voxel.utils.BlockSelection;
import mchorse.bbs.world.World;

import java.util.List;

public class WorldEditorChunkProxy extends ChunkProxy
{
    public UIWorldEditorPanel editor;
    public BlockSelection selectionBefore = new BlockSelection();
    public BlockSelection selectionAfter = new BlockSelection();

    public WorldEditorChunkProxy(UIWorldEditorPanel editor, ChunkManager chunks, UndoManager<World> undoManager)
    {
        super(chunks, undoManager);

        this.editor = editor;
    }

    @Override
    public void begin()
    {
        super.begin();

        this.selectionBefore.copy(this.editor.getSelection());
    }

    @Override
    protected BlocksUndo createUndo(List<BlockDiff> list)
    {
        this.selectionAfter.copy(this.editor.getSelection());

        return new WorldEditorBlocksUndo(this.editor, list, this.selectionBefore, this.selectionAfter);
    }
}