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

    private List<Integer> selectedBefore = new ArrayList<>();
    private List<Integer> selectedAfter = new ArrayList<>();

    /* Replays */
    private KeyframeSelection keyframesBefore = new KeyframeSelection();
    private KeyframeSelection keyframesAfter = new KeyframeSelection();
    private KeyframeSelection propertiesBefore = new KeyframeSelection();
    private KeyframeSelection propertiesAfter = new KeyframeSelection();

    public List<Integer> getSelection(boolean redo)
    {
        return redo ? this.selectedAfter : this.selectedBefore;
    }

    public KeyframeSelection getKeyframeSelection(boolean redo)
    {
        return redo ? this.keyframesAfter : this.keyframesBefore;
    }

    public KeyframeSelection getPropertiesSelection(boolean redo)
    {
        return redo ? this.propertiesAfter : this.propertiesBefore;
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

        this.keyframesBefore = this.keyframesAfter = editor.replays.keyframeEditor == null
            ? new KeyframeSelection()
            : editor.replays.keyframeEditor.keyframes.createSelection();

        this.propertiesBefore = this.propertiesAfter = editor.replays.propertyEditor == null
            ? new KeyframeSelection()
            : editor.replays.propertyEditor.properties.createSelection();
    }

    public void selectedBefore(List<Integer> selection, KeyframeSelection keyframe, KeyframeSelection properties)
    {
        this.selectedBefore.clear();
        this.selectedBefore.addAll(selection);

        this.keyframesBefore = keyframe;
        this.propertiesBefore = properties;
    }

    public static class KeyframeSelection
    {
        public List<List<Integer>> selected = new ArrayList<>();
        public Vector2i current = new Vector2i(-1, -1);
        public double min;
        public double max;
    }
}