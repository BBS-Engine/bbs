package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.ParticleMaterial;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceBillboard;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.utils.UI;

public class UIParticleSchemeGeneralSection extends UIParticleSchemeSection
{
    public UITextbox identifier;
    public UIButton pick;
    public UICirculate material;

    public UIParticleSchemeGeneralSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.identifier = new UITextbox(100, (str) ->
        {
            this.scheme.identifier = str;
            this.editor.dirty();
        });
        this.identifier.tooltip(UIKeys.SNOWSTORM_GENERAL_IDENTIFIER);

        this.pick = new UIButton(UIKeys.SNOWSTORM_GENERAL_PICK, (b) ->
        {
            UIElement container = this.getParentContainer();
            UITexturePicker.open(container, this.scheme.texture, (link) ->
            {
                if (link == null)
                {
                    link = ParticleScheme.DEFAULT_TEXTURE;
                }

                this.setTextureSize(link);
                this.scheme.texture = link;
                this.editor.dirty();
            });
        });

        this.material = new UICirculate((b) ->
        {
            this.scheme.material = ParticleMaterial.values()[this.material.getValue()];
            this.editor.dirty();
        });
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_OPAQUE);
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_ALPHA);
        this.material.addLabel(UIKeys.SNOWSTORM_GENERAL_PARTICLES_BLEND);

        this.fields.add(this.identifier, UI.row(5, 0, 20, this.pick, this.material));
    }

    private void setTextureSize(Link link)
    {
        ParticleComponentAppearanceBillboard component = this.scheme.get(ParticleComponentAppearanceBillboard.class);

        if (component == null)
        {
            return;
        }

        Texture texture = BBS.getTextures().getTexture(link);

        component.textureWidth = texture.width;
        component.textureHeight = texture.height;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_GENERAL_TITLE;
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        this.identifier.setText(scheme.identifier);
        this.material.setValue(scheme.material.ordinal());
    }
}