package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.Variable;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.ParticleCurve;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.particles.utils.UICurveEditor;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Map;

public class UIParticleSchemeCurvesSection extends UIParticleSchemeSection
{
    public UIElement curves;
    public UIIcon add;

    public UIParticleSchemeCurvesSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.curves = UI.column();
        this.add = new UIIcon(Icons.ADD, (b) -> this.addCurve());
        this.add.tooltip(UIKeys.SNOWSTORM_CURVES_ADD);

        this.fields.add(this.curves, this.add);
    }

    private void addCurve()
    {
        String name = "curve1";
        int i = 1;

        while (this.scheme.curves.containsKey(name))
        {
            name = "curve" + (i++);
        }

        MolangParser parser = this.scheme.parser;
        ParticleCurve curve = new ParticleCurve();
        UICurveEditor curveEditor = new UICurveEditor(this);

        if (!parser.variables.containsKey(name))
        {
            parser.variables.put(name, new Variable(name, 0));
        }

        try
        {
            curve.input = parser.parseExpression("variable.particle_age");
            curve.range = parser.parseExpression("variable.particle_lifetime");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        curve.variable = parser.variables.get(name);
        this.scheme.curves.put(name, curve);

        curveEditor.fill(curve);
        this.curves.add(curveEditor);
        this.editor.resize();
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_CURVES_TITLE;
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        this.curves.removeAll();

        for (Map.Entry<String, ParticleCurve> entry : scheme.curves.entrySet())
        {
            UICurveEditor curve = new UICurveEditor(this);

            curve.fill(entry.getValue());
            this.curves.add(curve);
        }
    }
}