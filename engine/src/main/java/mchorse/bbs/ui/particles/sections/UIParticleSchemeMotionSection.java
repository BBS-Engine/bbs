package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.components.motion.ParticleComponentInitialSpeed;
import mchorse.bbs.particles.components.motion.ParticleComponentInitialSpin;
import mchorse.bbs.particles.components.motion.ParticleComponentMotion;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionDynamic;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionParametric;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.utils.UI;

public class UIParticleSchemeMotionSection extends UIParticleSchemeModeSection<ParticleComponentMotion>
{
    public UIElement position;
    public UIButton positionSpeed;
    public UIButton positionX;
    public UIButton positionY;
    public UIButton positionZ;
    public UIButton positionDrag;

    public UIElement rotation;
    public UIButton rotationAngle;
    public UIButton rotationRate;
    public UIButton rotationAcceleration;
    public UIButton rotationDrag;

    private ParticleComponentInitialSpeed speed;
    private ParticleComponentInitialSpin spin;

    public UIParticleSchemeMotionSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.positionSpeed = new UIButton(UIKeys.SNOWSTORM_MOTION_POSITION_SPEED, (b) ->
        {
            this.editMoLang("motion.speed", (str) -> this.speed.speed = this.parse(str, this.speed.speed), this.speed.speed);
        });
        this.positionX = new UIButton(UIKeys.X, (str) -> this.updatePosition(0));
        this.positionY = new UIButton(UIKeys.Y, (str) -> this.updatePosition(1));
        this.positionZ = new UIButton(UIKeys.Z, (str) -> this.updatePosition(2));
        this.positionDrag = new UIButton(UIKeys.SNOWSTORM_MOTION_POSITION_DRAG, (b) ->
        {
            ParticleComponentMotionDynamic component = (ParticleComponentMotionDynamic) this.component;

            this.editMoLang("motion.drag", (str) -> component.motionDrag = this.parse(str, component.motionDrag), component.motionDrag);
        });

        this.rotationAngle = new UIButton(UIKeys.SNOWSTORM_MOTION_ROTATION_ANGLE, (b) ->
        {
            this.editMoLang("motion.angle", (str) -> this.spin.rotation = this.parse(str, this.spin.rotation), this.spin.rotation);
        });
        this.rotationRate = new UIButton(UIKeys.SNOWSTORM_MOTION_ROTATION_SPEED, (b) ->
        {
            this.editMoLang("motion.angle_speed", (str) -> this.spin.rate = this.parse(str, this.spin.rate), this.spin.rate);
        });
        this.rotationAcceleration = new UIButton(UIKeys.SNOWSTORM_MOTION_ROTATION_ACCELERATION, (b) ->
        {
            if (this.component instanceof ParticleComponentMotionDynamic)
            {
                ParticleComponentMotionDynamic component = (ParticleComponentMotionDynamic) this.component;

                this.editMoLang("motion.angle_acceleration", (str) -> component.rotationAcceleration = this.parse(str, component.rotationAcceleration), component.rotationAcceleration);
            }
            else
            {
                ParticleComponentMotionParametric component = (ParticleComponentMotionParametric) this.component;

                this.editMoLang("motion.angle_expression", (str) -> component.rotation = this.parse(str, component.rotation), component.rotation);
            }
        });
        this.rotationDrag = new UIButton(UIKeys.SNOWSTORM_MOTION_ROTATION_DRAG, (b) ->
        {
            ParticleComponentMotionDynamic component = (ParticleComponentMotionDynamic) this.component;

            this.editMoLang("motion.angle_drag", (str) -> component.rotationDrag = this.parse(str, component.rotationDrag), component.rotationDrag);
        });

        this.position = new UIElement();
        this.position.column(5).vertical().stretch();
        this.position.add(UI.label(UIKeys.SNOWSTORM_MOTION_POSITION, 20).anchor(0, 1F), this.positionSpeed);
        this.position.add(UI.row(this.positionX, this.positionY, this.positionZ));

        this.rotation = new UIElement();
        this.rotation.column(5).vertical().stretch();
        this.rotation.add(UI.label(UIKeys.SNOWSTORM_MOTION_ROTATION, 20).anchor(0, 1F), this.rotationAngle, this.rotationRate);
        this.rotation.add(this.rotationAcceleration);

        this.fields.add(this.position, this.rotation);
    }

    private void updatePosition(int index)
    {
        if (this.component instanceof ParticleComponentMotionDynamic)
        {
            ParticleComponentMotionDynamic component = (ParticleComponentMotionDynamic) this.component;

            this.editMoLang("motion.acceleration_" + index, (str) -> component.motionAcceleration[index] = this.parse(str, component.motionAcceleration[index]), component.motionAcceleration[index]);
        }
        else
        {
            ParticleComponentMotionParametric component = (ParticleComponentMotionParametric) this.component;

            this.editMoLang("motion.position_" + index, (str) -> component.position[index] = this.parse(str, component.position[index]), component.position[index]);
        }
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_MOTION_TITLE;
    }

    @Override
    protected void fillModes(UICirculate button)
    {
        button.addLabel(UIKeys.SNOWSTORM_MOTION_DYNAMIC);
        button.addLabel(UIKeys.SNOWSTORM_MOTION_PARAMETRIC);
    }

    @Override
    protected Class<ParticleComponentMotion> getBaseClass()
    {
        return ParticleComponentMotion.class;
    }

    @Override
    protected Class getDefaultClass()
    {
        return ParticleComponentMotionDynamic.class;
    }

    @Override
    protected Class getModeClass(int value)
    {
        if (value == 1)
        {
            return ParticleComponentMotionParametric.class;
        }

        return ParticleComponentMotionDynamic.class;
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        this.speed = this.scheme.getOrCreate(ParticleComponentInitialSpeed.class);
        this.spin = this.scheme.getOrCreate(ParticleComponentInitialSpin.class);

        this.positionDrag.removeFromParent();
        this.rotationDrag.removeFromParent();

        if (this.component instanceof ParticleComponentMotionDynamic)
        {
            this.position.add(this.positionDrag);
            this.rotation.add(this.rotationDrag);
        }

        this.resizeParent();
    }
}