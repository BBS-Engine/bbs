package mchorse.bbs.ui.film.utils.undo;

import mchorse.bbs.settings.values.ValueGroup;
import mchorse.bbs.ui.film.UIClips;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.utils.undo.IUndo;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public abstract class FilmEditorUndo implements IUndo<ValueGroup>
{
    /* Timeline */
    public int tick;
    public double viewMin;
    public double viewMax;
    public int scroll;
    public int panel;

    /* Replays */
    private List<List<Integer>> keyframesSelectedBefore = new ArrayList<>();
    private List<List<Integer>> keyframesSelectedAfter = new ArrayList<>();
    private Vector2i keyframeSelectedBefore = new Vector2i();
    private Vector2i keyframeSelectedAfter = new Vector2i();

    private List<Integer> selectedBefore = new ArrayList<>();
    private List<Integer> selectedAfter = new ArrayList<>();

    public List<Integer> getSelection(boolean redo)
    {
        return redo ? this.selectedAfter : this.selectedBefore;
    }

    public List<List<Integer>> getKeyframeSelection(boolean redo)
    {
        return redo ? this.keyframesSelectedAfter : this.keyframesSelectedBefore;
    }

    public Vector2i getKeyframeSelected(boolean redo)
    {
        return redo ? this.keyframeSelectedAfter : this.keyframeSelectedBefore;
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

        this.keyframesSelectedAfter.addAll(editor.replays.collectSelection());
        this.keyframesSelectedBefore.addAll(this.keyframesSelectedAfter);
        this.keyframeSelectedAfter.set(editor.replays.findSelected());
        this.keyframeSelectedBefore.set(this.keyframeSelectedAfter);
    }

    public void selectedBefore(List<Integer> selection, List<List<Integer>> keyframeSelection, Vector2i keyframeSelected)
    {
        this.selectedBefore.clear();
        this.selectedBefore.addAll(selection);

        this.keyframesSelectedBefore.clear();
        this.keyframesSelectedBefore.addAll(keyframeSelection);
        this.keyframeSelectedBefore.set(keyframeSelected);
    }
}