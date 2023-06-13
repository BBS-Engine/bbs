package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.meta.ParticleComponentLocalSpace;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;

public class UIParticleSchemeSpaceSection extends UIParticleSchemeComponentSection<ParticleComponentLocalSpace>
{
    public UIToggle position;
    public UIToggle rotation;

    public UIParticleSchemeSpaceSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.position = new UIToggle(UIKeys.SNOWSTORM_SPACE_POSITION, (b) ->
        {
            this.component.position = b.getValue();
            this.editor.dirty();
        });

        this.rotation = new UIToggle(UIKeys.SNOWSTORM_SPACE_ROTATION, (b) ->
        {
            this.component.rotation = b.getValue();
            this.editor.dirty();
        });

        this.fields.add(this.position, this.rotation);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_SPACE_TITLE;
    }

    @Override
    protected ParticleComponentLocalSpace getComponent(ParticleScheme scheme)
    {
        return scheme.getOrCreate(ParticleComponentLocalSpace.class);
    }

    @Override
    protected void fillData()
    {
        this.position.setValue(this.component.position);
        this.rotation.setValue(this.component.rotation);
    }
}