package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.expiration.ParticleComponentExpireInBlocks;
import mchorse.bbs.particles.components.expiration.ParticleComponentExpireNotInBlocks;
import mchorse.bbs.particles.components.expiration.ParticleComponentKillPlane;
import mchorse.bbs.particles.components.expiration.ParticleComponentParticleLifetime;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.utils.UI;

public class UIParticleSchemeExpirationSection extends UIParticleSchemeSection
{
    public UICirculate mode;
    public UIButton expression;

    public UITrackpad a;
    public UITrackpad b;
    public UITrackpad c;
    public UITrackpad d;

    private ParticleComponentParticleLifetime lifetime;
    private ParticleComponentKillPlane plane;
    private ParticleComponentExpireInBlocks inBlocks;
    private ParticleComponentExpireNotInBlocks notInBlocks;

    public UIParticleSchemeExpirationSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.mode = new UICirculate((b) ->
        {
            this.lifetime.max = this.mode.getValue() == 1;
            this.updateTooltip();
            this.editor.dirty();
        });
        this.mode.addLabel(UIKeys.SNOWSTORM_EXPIRATION_EXPRESSION);
        this.mode.addLabel(UIKeys.SNOWSTORM_EXPIRATION_MAX);

        this.expression = new UIButton(UIKeys.SNOWSTORM_EXPRESSION, (b) ->
        {
            this.editMoLang("expiration.lifetime", (str) -> this.lifetime.expression = this.parse(str, this.lifetime.expression), this.lifetime.expression);
        });
        this.expression.tooltip(IKey.EMPTY);

        this.a = new UITrackpad((value) ->
        {
            this.plane.a = value.floatValue();
            this.editor.dirty();
        });
        this.a.tooltip(IKey.raw("Ax"));
        this.b = new UITrackpad((value) ->
        {
            this.plane.b = value.floatValue();
            this.editor.dirty();
        });
        this.b.tooltip(IKey.raw("By"));
        this.c = new UITrackpad((value) ->
        {
            this.plane.c = value.floatValue();
            this.editor.dirty();
        });
        this.c.tooltip(IKey.raw("Cz"));
        this.d = new UITrackpad((value) ->
        {
            this.plane.d = value.floatValue();
            this.editor.dirty();
        });
        this.d.tooltip(IKey.raw("D"));

        this.fields.add(UI.row(5, 0, 20, UI.label(UIKeys.SNOWSTORM_MODE, 20).labelAnchor(0, 0.5F), this.mode));
        this.fields.add(this.expression);
        this.fields.add(UI.label(UIKeys.SNOWSTORM_EXPIRATION_KILL_PLANE, 20).labelAnchor(0, 1F)
            .tooltip(UIKeys.SNOWSTORM_EXPIRATION_KILL_PLANE_TOOLTIP));
        this.fields.add(UI.row(5, 0, 20, this.a, this.b));
        this.fields.add(UI.row(5, 0, 20, this.c, this.d));
    }

    private void updateTooltip()
    {
        this.expression.tooltip(this.lifetime.max
            ? UIKeys.SNOWSTORM_EXPIRATION_MAX_TOOLTIP
            : UIKeys.SNOWSTORM_EXPIRATION_EXPRESSION_TOOLTIP);
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_EXPIRATION_TITLE;
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        this.lifetime = scheme.getOrCreate(ParticleComponentParticleLifetime.class);
        this.plane = scheme.getOrCreate(ParticleComponentKillPlane.class);
        this.inBlocks = scheme.getOrCreate(ParticleComponentExpireInBlocks.class);
        this.notInBlocks = scheme.getOrCreate(ParticleComponentExpireNotInBlocks.class);

        this.mode.setValue(this.lifetime.max ? 1 : 0);
        this.updateTooltip();

        this.a.setValue(this.plane.a);
        this.b.setValue(this.plane.b);
        this.c.setValue(this.plane.c);
        this.d.setValue(this.plane.d);
    }

    /* TODO: reimplement block selection */
}