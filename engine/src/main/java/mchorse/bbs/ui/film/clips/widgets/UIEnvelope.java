package mchorse.bbs.ui.film.clips.widgets;

import mchorse.bbs.camera.utils.TimeUtils;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.clips.UIClip;
import mchorse.bbs.ui.film.utils.UICameraUtils;
import mchorse.bbs.ui.film.utils.keyframes.UICameraDopeSheetEditor;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.clips.Envelope;
import mchorse.bbs.utils.colors.Colors;

public class UIEnvelope extends UIElement
{
    public UIClip<? extends Clip> panel;

    public UIToggle enabled;
    public UIButton pickInterpolation;
    public UITrackpad fadeIn;
    public UITrackpad fadeOut;

    public UIToggle keyframes;
    public UIButton editKeyframes;
    public UICameraDopeSheetEditor channel;

    public UIEnvelope(UIClip<? extends Clip> panel)
    {
        super();

        this.panel = panel;

        InterpolationTooltip tooltip = new InterpolationTooltip(1F, 0.5F, () -> this.get().interpolation.get());

        this.enabled = new UIToggle(UIKeys.CAMERA_PANELS_ENABLED, (b) -> this.get().enabled.set(b.getValue()));
        this.pickInterpolation = new UIButton(UIKeys.CAMERA_PANELS_INTERPOLATION, (b) ->
        {
            UICameraUtils.interps(this.getContext(), this.get().interpolation.get(), this.get().interpolation::set);
        });
        this.pickInterpolation.tooltip(tooltip);

        this.fadeIn = new UITrackpad((value) -> this.get().fadeIn.set((float) TimeUtils.fromTime(value.floatValue())));
        this.fadeIn.tooltip(UIKeys.CAMERA_PANELS_ENVELOPES_START_D, Direction.TOP);
        this.fadeOut = new UITrackpad((value) -> this.get().fadeOut.set((float) TimeUtils.fromTime(value.floatValue())));
        this.fadeOut.tooltip(UIKeys.CAMERA_PANELS_ENVELOPES_END_D, Direction.TOP);

        this.keyframes = new UIToggle(UIKeys.CAMERA_PANELS_KEYFRAMES, (b) ->
        {
            this.get().keyframes.set(b.getValue());
            this.toggleKeyframes(b.getValue());
        });
        this.editKeyframes = new UIButton(UIKeys.CAMERA_PANELS_EDIT_KEYFRAMES, (b) ->
        {
            this.panel.editor.embedView(this.channel);
            this.channel.resetView();
        });
        this.channel = new UICameraDopeSheetEditor(panel.editor);

        this.column().vertical().stretch();
    }

    private void toggleKeyframes(boolean toggled)
    {
        this.removeAll();

        this.add(this.enabled);

        if (toggled)
        {
            this.add(this.editKeyframes);
        }
        else
        {
            this.add(this.pickInterpolation, UI.row(this.fadeIn, this.fadeOut));
        }

        this.add(this.keyframes);

        if (this.hasParent())
        {
            this.getParent().resize();

            if (toggled)
            {
                this.initiate();
            }
        }
    }

    public void initiate()
    {
        this.updateDuration();
        this.channel.resetView();
        this.channel.updateConverter();

        TimeUtils.configure(this.fadeIn, 0);
        TimeUtils.configure(this.fadeOut, 0);

        this.fillIntervals();
    }

    public void fillData()
    {
        Envelope envelope = this.get();

        this.enabled.setValue(envelope.enabled.get());
        this.fillIntervals();
        this.keyframes.setValue(envelope.keyframes.get());
        this.channel.setChannel(envelope.channel, Colors.ACTIVE);

        this.toggleKeyframes(envelope.keyframes.get());
    }

    private void fillIntervals()
    {
        Envelope envelope = this.get();

        this.fadeIn.setValue(TimeUtils.toTime(envelope.fadeIn.get().intValue()));
        this.fadeOut.setValue(TimeUtils.toTime(envelope.fadeOut.get().intValue()));
    }

    public void updateDuration()
    {
        this.channel.keyframes.duration = this.getDuration();
    }

    public int getDuration()
    {
        return this.panel.clip.duration.get();
    }

    public Envelope get()
    {
        return this.panel.clip.envelope;
    }
}