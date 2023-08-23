package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.CameraWork;
import mchorse.bbs.camera.clips.overwrite.PathClip;
import mchorse.bbs.camera.data.InterpolationType;
import mchorse.bbs.camera.data.Position;
import mchorse.bbs.camera.values.ValuePosition;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.settings.values.base.BaseValue;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.modules.UIAngleModule;
import mchorse.bbs.ui.film.clips.modules.UIPointModule;
import mchorse.bbs.ui.film.clips.modules.UIPointsModule;
import mchorse.bbs.ui.film.utils.UICameraUtils;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.tooltips.InterpolationTooltip;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.keyframes.KeyframeInterpolations;
import mchorse.bbs.utils.math.IInterpolation;
import mchorse.bbs.utils.math.Interpolation;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.CompoundUndo;
import mchorse.bbs.utils.undo.IUndo;
import org.joml.Vector2d;

/**
 * Path clip panel
 *
 * This panel has the most modules used. It's responsible for editing path
 * clip. It uses point and angle modules to edit a position which is picked
 * from the points module. Interpolation module is used to modify path clip's
 * interpolation methods.
 */
public class UIPathClip extends UIClip<PathClip>
{
    public UIPointModule point;
    public UIAngleModule angle;
    public UIButton interpPoint;
    public UIButton interpAngle;

    public UIToggle autoCenter;
    public UITrackpad circularX;
    public UITrackpad circularZ;

    public UIPointsModule points;

    public ValuePosition position;

    public UIPathClip(PathClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.point = new UIPointModule(editor);
        this.angle = new UIAngleModule(editor);
        this.interpPoint = new UIButton(UIKeys.CAMERA_PANELS_POINT, (b) ->
        {
            UICameraUtils.interpTypes(this.getContext(), this.clip.interpolationPoint.get(), (i) ->
            {
                this.editor.postUndo(this.undo(this.clip.interpolationPoint, (interp) -> interp.set(i)));
            });
        });
        this.interpPoint.tooltip(new InterpolationTooltip(1F, 0.5F, () -> this.getInterp(this.clip.interpolationPoint.get())));
        this.interpAngle = new UIButton(UIKeys.CAMERA_PANELS_ANGLE, (b) ->
        {
            UICameraUtils.interpTypes(this.getContext(), this.clip.interpolationAngle.get(), (i) ->
            {
                this.editor.postUndo(this.undo(this.clip.interpolationAngle, (interp) -> interp.set(i)));
            });
        });
        this.interpAngle.tooltip(new InterpolationTooltip(1F, 0.5F, () -> this.getInterp(this.clip.interpolationAngle.get())));

        this.autoCenter = new UIToggle(UIKeys.CAMERA_PANELS_AUTO_CENTER, (b) ->
        {
            IUndo<CameraWork> undo = this.undo(this.clip.circularAutoCenter, (autoCenter) -> autoCenter.set(b.getValue()));

            if (!b.getValue())
            {
                Vector2d center = this.clip.calculateCenter(new Vector2d());

                this.circularX.setValue(center.x);
                this.circularZ.setValue(center.y);
                this.editor.postUndo(new CompoundUndo<>(
                    undo,
                    this.undo(this.clip.circularX, (circularX) -> circularX.set(center.x)),
                    this.undo(this.clip.circularZ, (circularZ) -> circularZ.set(center.y))
                ).noMerging());
            }
            else
            {
                this.editor.postUndo(undo);
            }
        });

        this.circularX = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.circularX, (circularX) -> circularX.set(value))));
        this.circularX.tooltip(UIKeys.CAMERA_PANELS_CIRCULAR_X);
        this.circularZ = new UITrackpad((value) -> this.editor.postUndo(this.undo(this.clip.circularZ, (circularZ) -> circularZ.set(value))));
        this.circularZ.tooltip(UIKeys.CAMERA_PANELS_CIRCULAR_Z);

        this.points = new UIPointsModule(this.editor, this::pickPoint);
        this.points.h(20);
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_PATH_POINTS).marginTop(12));
        this.panels.add(this.points, UI.row(this.interpPoint, this.interpAngle).marginBottom(6));
        this.panels.add(this.point.marginTop(12), this.angle.marginTop(6));
        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_CIRCULAR).marginTop(6), this.autoCenter, UI.row(this.circularX, this.circularZ));
        this.panels.context((menu) -> UICameraUtils.positionContextMenu(menu, editor, this.position));
    }

    private IInterpolation getInterp(InterpolationType type)
    {
        IInterpolation function = type.function;

        if (type == InterpolationType.HERMITE)
        {
            function = KeyframeInterpolations.HERMITE;
        }
        else if (type == InterpolationType.CUBIC)
        {
            function = Interpolation.CUBIC_INOUT;
        }

        return function;
    }

    private void updateSpeedPanel()
    {
        this.resize();
    }

    private ValuePosition getPosition(int index)
    {
        BaseValue value = this.clip.points.getAll().get(index);

        return value instanceof ValuePosition ? (ValuePosition) value : null;
    }

    public void pickPoint(int index)
    {
        this.points.setIndex(index);
        this.position = this.getPosition(index);

        this.point.fill(this.position.getPoint());
        this.angle.fill(this.position.getAngle());

        if (!Window.isCtrlPressed())
        {
            int offset = this.clip.getTickForPoint(index);

            if (offset == this.clip.duration.get())
            {
                offset -= 1;
            }

            this.editor.setCursor(this.clip.tick.get() + offset);
        }
    }

    @Override
    public void editClip(Position position)
    {
        if (this.position != null)
        {
            this.editor.postUndo(this.undo(this.position, position.toData()));

            super.editClip(position);
        }
    }

    @Override
    public void fillData()
    {
        super.fillData();

        int duration = this.clip.duration.get();
        int offset = MathUtils.clamp(this.editor.getCursor() - this.clip.tick.get(), 0, duration);
        int points = this.clip.size();
        int index = (int) ((offset / (float) duration) * points);

        index = MathUtils.clamp(index, 0, points - 1);

        this.position = this.getPosition(index);
        this.points.index = index;

        this.point.fill(this.position.getPoint());
        this.angle.fill(this.position.getAngle());
        this.points.fill(this.clip);
        this.updateSpeedPanel();

        this.autoCenter.setValue(this.clip.circularAutoCenter.get());
        this.circularX.setValue(this.clip.circularX.get());
        this.circularZ.setValue(this.clip.circularZ.get());

        this.points.index = index;
    }
}