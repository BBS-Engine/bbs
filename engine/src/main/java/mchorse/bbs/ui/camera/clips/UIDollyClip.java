package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.ClipContext;
import mchorse.bbs.camera.clips.overwrite.DollyClip;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.data.types.FloatType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.modules.UIAngleModule;
import mchorse.bbs.ui.camera.clips.modules.UIPointModule;
import mchorse.bbs.ui.camera.utils.UICameraUtils;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.undo.CompoundUndo;

public class UIDollyClip extends UIClip<DollyClip>
{
    public UIPointModule point;
    public UIAngleModule angle;

    public UITrackpad distance;
    public UIIcon reverse;
    public UIButton interp;

    public UITrackpad yaw;
    public UITrackpad pitch;

    public UIDollyClip(DollyClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.angle = new UIAngleModule(editor);
        this.distance = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.distance, new FloatType(value.floatValue()))));
        this.distance.tooltip(UIKeys.CAMERA_PANELS_DOLLY_DISTANCE);
        this.reverse = new UIIcon(Icons.REVERSE, (b) -> this.reverse());
        this.reverse.tooltip(UIKeys.CAMERA_PANELS_DOLLY_REVERSE);
        this.yaw = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.yaw, new FloatType(value.floatValue()))));
        this.yaw.tooltip(UIKeys.CAMERA_PANELS_DOLLY_YAW);
        this.pitch = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.pitch, new FloatType(value.floatValue()))));
        this.pitch.tooltip(UIKeys.CAMERA_PANELS_DOLLY_PITCH);

        this.interp = new UIButton(UIKeys.CAMERA_PANELS_INTERPOLATION, (b) ->
        {
            UICameraUtils.interps(this.getContext(), this.clip.interp.get(), (i) ->
            {
                this.editor.postUndo(this.undo(this.clip.interp, new StringType(i.toString())));
            });
        });
        this.interp.tooltip(new InterpolationTooltip(1F, 0.5F, () -> this.clip.interp.get()));
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_DOLLY_TITLE).marginTop(12));
        this.panels.add(UI.row(0, 0, 20, this.distance, this.reverse), this.yaw, this.pitch, this.interp);

        this.panels.add(this.point.marginTop(12), this.angle.marginTop(6));
        this.panels.context((menu) -> UICameraUtils.positionContextMenu(menu, this.editor, this.clip.position));
    }

    private void reverse()
    {
        Position position = new Position();

        this.clip.applyLast(new ClipContext(), position);

        this.editor.postUndo(new CompoundUndo<CameraWork>(
            this.undo(this.clip.position, position.toData()),
            this.undo(this.clip.distance, new FloatType(-this.clip.distance.get()))
        ));

        this.fillData();
    }

    @Override
    public void editClip(Position position)
    {
        this.editor.postUndo(new CompoundUndo<CameraWork>(
            this.undo(this.clip.position, position.toData()),
            this.undo(this.clip.yaw, new FloatType(position.angle.yaw)),
            this.undo(this.clip.pitch, new FloatType(position.angle.pitch))
        ));

        super.editClip(position);
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.point.fill(this.clip.position.getPoint());
        this.angle.fill(this.clip.position.getAngle());

        this.yaw.setValue(this.clip.yaw.get());
        this.pitch.setValue(this.clip.pitch.get());
        this.distance.setValue(this.clip.distance.get());
    }

    @Override
    public void render(UIContext context)
    {
        double speed = this.clip.distance.get() / (this.clip.duration.get() / 20D);
        String label = UIKeys.CAMERA_PANELS_DOLLY_SPEED.formatString(UITrackpad.format(speed));

        context.batcher.textCard(context.font, label, this.area.mx(context.font.getWidth(label)), this.area.ey() - context.font.getHeight() - 20);

        super.render(context);
    }
}