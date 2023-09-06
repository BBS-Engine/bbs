package mchorse.bbs.ui.film.utils.undo;

import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.ui.film.UIClips;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.utils.undo.IUndo;

import java.util.ArrayList;
import java.util.List;

public abstract class FilmEditorUndo implements IUndo<ValueGroup>
{
    public int tick;
    public double viewMin;
    public double viewMax;
    public int scroll;
    public int panel;

    private List<Integer> selectedBefore = new ArrayList<>();
    private List<Integer> selectedAfter = new ArrayList<>();

    public List<Integer> getSelection(boolean redo)
    {
        return redo ? this.selectedAfter : this.selectedBefore;
    }

    public void editor(UIFilmPanel editor)
    {
        UIClips timeline = editor.timeline;

        if (editor.screenplay.isVisible())
        {
            this.panel = 2;
        }
        else if (editor.replays.isVisible())
        {
            this.panel = 1;
        }

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