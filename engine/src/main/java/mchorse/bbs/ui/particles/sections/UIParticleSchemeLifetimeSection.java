package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetime;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeExpression;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeLooping;
import mchorse.bbs.particles.components.lifetime.ParticleComponentLifetimeOnce;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;

public class UIParticleSchemeLifetimeSection extends UIParticleSchemeModeSection<ParticleComponentLifetime>
{
    public UIButton active;
    public UIButton expiration;

    public UIParticleSchemeLifetimeSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.active = new UIButton(UIKeys.SNOWSTORM_LIFETIME_TIME, (b) ->
        {
            this.editMoLang("lifetime.active_time", (str) -> this.component.activeTime = this.parse(str, this.component.activeTime), this.component.activeTime);
        });
        this.active.tooltip(IKey.EMPTY);
        this.expiration = new UIButton(UIKeys.SNOWSTORM_EXPRESSION, (b) ->
        {
            if (this.component instanceof ParticleComponentLifetimeLooping)
            {
                ParticleComponentLifetimeLooping component = (ParticleComponentLifetimeLooping) this.component;

                this.editMoLang("lifetime.sleep_time", (str) -> component.sleepTime = this.parse(str, component.sleepTime), component.sleepTime);
            }
            else
            {
                ParticleComponentLifetimeExpression component = (ParticleComponentLifetimeExpression) this.component;

                this.editMoLang("lifetime.expiration", (str) -> component.expiration = this.parse(str, component.expiration), component.expiration);
            }

            this.editor.dirty();
        });
        this.expiration.tooltip(IKey.EMPTY);

        this.fields.add(this.active);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_LIFETIME_TITLE;
    }

    @Override
    protected void fillModes(UICirculate button)
    {
        button.addLabel(UIKeys.SNOWSTORM_LIFETIME_EXPRESSION);
        button.addLabel(UIKeys.SNOWSTORM_LIFETIME_LOOPING);
        button.addLabel(UIKeys.SNOWSTORM_LIFETIME_ONCE);
    }

    @Override
    protected void restoreInfo(ParticleComponentLifetime component, ParticleComponentLifetime old)
    {
        component.activeTime = old.activeTime;
    }

    @Override
    protected Class<ParticleComponentLifetime> getBaseClass()
    {
        return ParticleComponentLifetime.class;
    }

    @Override
    protected Class getDefaultClass()
    {
        return ParticleComponentLifetimeLooping.class;
    }

    @Override
    protected Class getModeClass(int value)
    {
        if (value == 0)
        {
            return ParticleComponentLifetimeExpression.class;
        }
        else if (value == 1)
        {
            return ParticleComponentLifetimeLooping.class;
        }

        return ParticleComponentLifetimeOnce.class;
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        boolean once = this.component instanceof ParticleComponentLifetimeOnce;

        this.expiration.setVisible(!once);

        if (this.component instanceof ParticleComponentLifetimeExpression)
        {
            this.expiration.tooltip(UIKeys.SNOWSTORM_LIFETIME_EXPIRATION_EXPRESSION);
            this.active.tooltip(UIKeys.SNOWSTORM_LIFETIME_ACTIVE_EXPRESSION);
        }
        else if (this.component instanceof ParticleComponentLifetimeLooping)
        {
            this.expiration.tooltip(UIKeys.SNOWSTORM_LIFETIME_SLEEP_TIME);
            this.active.tooltip(UIKeys.SNOWSTORM_LIFETIME_ACTIVE_LOOPING);
        }
        else
        {
            this.active.tooltip(UIKeys.SNOWSTORM_LIFETIME_ACTIVE_ONCE);
        }

        this.expiration.removeFromParent();

        if (!once)
        {
            this.fields.add(this.expiration);
        }

        this.resizeParent();
    }
}