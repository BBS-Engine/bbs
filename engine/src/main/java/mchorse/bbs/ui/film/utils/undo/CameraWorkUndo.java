package mchorse.bbs.ui.film.utils.undo;

import mchorse.bbs.camera.data.StructureBase;
import mchorse.bbs.ui.film.UIClips;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.utils.undo.IUndo;

import java.util.ArrayList;
import java.util.List;

public abstract class CameraWorkUndo implements IUndo<StructureBase>
{
    public int tick;
    public double viewMin;
    public double viewMax;
    public int scroll;

    private List<Integer> selectedBefore = new ArrayList<>();
    private List<Integer> selectedAfter = new ArrayList<>();

    public List<Integer> getSelection(boolean redo)
    {
        return redo ? this.selectedAfter : this.selectedBefore;
    }

    public void editor(UIFilmPanel editor)
    {
        UIClips timeline = editor.timeline;

        this.tick = editor.getCursor();
        this.viewMin = timeline.scale.getMinValue();
        this.viewMax = timeline.scale.getMaxValue();
        this.scroll = timeline.vertical.scroll;

        this.selectedAfter.addAll(timeline.getSelection());
        this.selectedBefore.addAll(this.selectedAfter);
    }

    public void selectedBefore(List<Integer> selection)
    {
        this.selectedBefore.clear();
        this.selectedBefore.addAll(selection);
    }
}