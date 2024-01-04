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
    public ClipsData cameraClips;
    public ClipsData voiceLinesClips;
    public int panel;

    /* Replays */
    private KeyframeSelection keyframesBefore = new KeyframeSelection();
    private KeyframeSelection keyframesAfter = new KeyframeSelection();
    private KeyframeSelection propertiesBefore = new KeyframeSelection();
    private KeyframeSelection propertiesAfter = new KeyframeSelection();

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
        UIClips cameraClips = editor.cameraClips.clips;
        UIClips voiceLineClips = editor.screenplay.editor.clips;

        if (editor.screenplay.isVisible())
        {
            this.panel = 2;
        }
        else if (editor.replays.isVisible())
        {
            this.panel = 1;
        }
        else
        {
            this.panel = 0;
        }

        this.tick = editor.getCursor();
        this.cameraClips = new ClipsData(cameraClips);
        this.voiceLinesClips = new ClipsData(voiceLineClips);

        this.keyframesBefore = this.keyframesAfter = editor.replays.keyframeEditor == null
            ? new KeyframeSelection()
            : editor.replays.keyframeEditor.keyframes.createSelection();

        this.propertiesBefore = this.propertiesAfter = editor.replays.propertyEditor == null
            ? new KeyframeSelection()
            : editor.replays.propertyEditor.properties.createSelection();
    }

    public void selectedBefore(List<Integer> cameraClipsSelection, List<Integer> voiceLineSelection, KeyframeSelection keyframe, KeyframeSelection properties)
    {
        this.cameraClips.selectedBefore.clear();
        this.cameraClips.selectedBefore.addAll(cameraClipsSelection);

        this.voiceLinesClips.selectedBefore.clear();
        this.voiceLinesClips.selectedBefore.addAll(voiceLineSelection);

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

    public static class ClipsData
    {
        public double viewMin;
        public double viewMax;
        public int scroll;

        public List<Integer> selectedBefore = new ArrayList<>();
        public List<Integer> selectedAfter = new ArrayList<>();

        public ClipsData(UIClips clips)
        {
            this.viewMin = clips.scale.getMinValue();
            this.viewMax = clips.scale.getMaxValue();
            this.scroll = clips.vertical.scroll;

            this.selectedAfter.addAll(clips.getSelection());
            this.selectedBefore.addAll(this.selectedAfter);
        }

        public List<Integer> getSelection(boolean redo)
        {
            return redo ? this.selectedAfter : this.selectedBefore;
        }

        public void apply(UIClips clips)
        {
            clips.scale.view(this.viewMin, this.viewMax);
            clips.vertical.scrollTo(this.scroll);
        }
    }
}