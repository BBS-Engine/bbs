package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.motion.ParticleComponentMotionCollision;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;

public class UIParticleSchemeCollisionSection extends UIParticleSchemeComponentSection<ParticleComponentMotionCollision>
{
    public UIToggle enabled;
    public UITrackpad drag;
    public UITrackpad bounciness;
    public UITrackpad radius;
    public UIToggle expire;

    private boolean wasPresent;

    public UIParticleSchemeCollisionSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.enabled = new UIToggle(UIKeys.SNOWSTORM_COLLISION_ENABLED, (b) -> this.editor.dirty());
        this.drag = new UITrackpad((value) ->
        {
            this.component.collisionDrag = value.floatValue();
            this.editor.dirty();
        });
        this.drag.tooltip(UIKeys.SNOWSTORM_COLLISION_DRAG);
        this.bounciness = new UITrackpad((value) ->
        {
            this.component.bounciness = value.floatValue();
            this.editor.dirty();
        });
        this.bounciness.tooltip(UIKeys.SNOWSTORM_COLLISION_BOUNCINESS);
        this.radius = new UITrackpad((value) ->
        {
            this.component.radius = value.floatValue();
            this.editor.dirty();
        });
        this.radius.tooltip(UIKeys.SNOWSTORM_COLLISION_RADIUS);
        this.expire = new UIToggle(UIKeys.SNOWSTORM_COLLISION_EXPIRE, (b) ->
        {
            this.component.expireOnImpact = b.getValue();
            this.editor.dirty();
        });

        this.fields.add(this.enabled, this.drag, this.bounciness, this.radius, this.expire);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_COLLISION_TITLE;
    }

    @Override
    public void beforeSave(ParticleScheme scheme)
    {
        this.component.enabled = this.enabled.getValue() ? MolangParser.ONE : MolangParser.ZERO;
    }

    @Override
    protected ParticleComponentMotionCollision getComponent(ParticleScheme scheme)
    {
        this.wasPresent = this.scheme.get(ParticleComponentMotionCollision.class) != null;

        return scheme.getOrCreate(ParticleComponentMotionCollision.class);
    }

    @Override
    protected void fillData()
    {
        this.enabled.setValue(this.wasPresent);
        this.drag.setValue(this.component.collisionDrag);
        this.bounciness.setValue(this.component.bounciness);
        this.radius.setValue(this.component.radius);
        this.expire.setValue(this.component.expireOnImpact);
    }
}
