package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Colors;

import java.util.function.Consumer;

public abstract class UIParticleSchemeSection extends UIElement
{
    public UILabel title;
    public UIElement fields;

    protected ParticleScheme scheme;
    protected UIParticleSchemePanel editor;

    public UIParticleSchemeSection(UIParticleSchemePanel editor)
    {
        super();

        this.editor = editor;
        this.title = UI.label(this.getTitle()).background(() -> Colors.A50 | BBSSettings.primaryColor.get());
        this.fields = new UIElement();
        this.fields.column().stretch().vertical().height(20);

        this.column().stretch().vertical();
        this.add(this.title, this.fields);
    }

    protected void resizeParent()
    {
        this.getParent().resize();
    }

    public UIParticleSchemePanel getEditor()
    {
        return this.editor;
    }

    public void dirty()
    {
        this.editor.dirty();
    }

    public abstract IKey getTitle();

    public void editMoLang(String id, Consumer<String> callback, MolangExpression expression)
    {
        this.editor.editMoLang(id, callback, expression);
    }

    public MolangExpression parse(String string, MolangExpression old)
    {
        if (string.isEmpty())
        {
            return MolangParser.ZERO;
        }

        try
        {
            MolangExpression expression = this.scheme.parser.parseExpression(string);

            this.editor.dirty();

            return expression;
        }
        catch (Exception e)
        {}

        return old;
    }

    public ParticleScheme getScheme()
    {
        return this.scheme;
    }

    public void setScheme(ParticleScheme scheme)
    {
        this.scheme = scheme;
    }

    public void beforeSave(ParticleScheme scheme)
    {}

    /**
     * Toggle visibility of the field section
     */
    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.title.area.isInside(context))
        {
            if (this.fields.hasParent())
            {
                this.fields.removeFromParent();
            }
            else
            {
                this.add(this.fields);
            }

            this.resizeParent();

            return true;
        }

        return super.subMouseClicked(context);
    }
}