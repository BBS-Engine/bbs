package mchorse.bbs.ui.particles.utils;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.Variable;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.particles.ParticleCurve;
import mchorse.bbs.particles.ParticleCurveType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeSection;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.util.Map;

public class UICurveEditor extends UIElement
{
    private UIParticleSchemeSection section;

    public UILabel name;
    public UIIcon rename;
    public UIIcon delete;
    public UICirculate type;
    public UICurve curve;
    public UIButton input;
    public UIButton range;

    private ParticleCurve particleCurve;

    public UICurveEditor(UIParticleSchemeSection section)
    {
        this.section = section;

        this.name = UI.label(IKey.EMPTY, 20).labelAnchor(0, 0.5F).background();
        this.rename = new UIIcon(Icons.EDIT, this::rename);
        this.rename.tooltip(UIKeys.SNOWSTORM_CURVES_RENAME);
        this.delete = new UIIcon(Icons.REMOVE, this::remove);
        this.delete.tooltip(UIKeys.SNOWSTORM_CURVES_REMOVE);
        this.type = new UICirculate((b) -> this.changeType(b.getValue()));

        for (ParticleCurveType type : ParticleCurveType.values())
        {
            this.type.addLabel(UIKeys.C_CURVE_TYPE.get(type.id));
        }

        this.curve = new UICurve(section);
        this.input = new UIButton(UIKeys.SNOWSTORM_CURVES_INPUT, (b) -> this.section.editMoLang("curve." + this.particleCurve.variable.getName() + ".input", (str) -> this.particleCurve.input = this.section.parse(str, this.particleCurve.input), this.particleCurve.input));
        this.range = new UIButton(UIKeys.SNOWSTORM_CURVES_RANGE, (b) -> this.section.editMoLang("curve." + this.particleCurve.variable.getName() + ".range", (str) -> this.particleCurve.range = this.section.parse(str, this.particleCurve.range), this.particleCurve.range));

        this.curve.h(100);

        this.column().vertical().stretch();
        this.add(UI.row(0, this.name, this.rename, this.delete));
        this.add(UI.row(UI.label(UIKeys.SNOWSTORM_CURVES_TYPE, 20).labelAnchor(0, 0.5F), this.type));
        this.add(this.curve, UI.row(this.input, this.range));
    }

    private void rename(UIIcon b)
    {
        String oldName = this.particleCurve.variable.getName();
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.GENERAL_RENAME,
            UIKeys.SNOWSTORM_CURVES_RENAME_OVERLAY,
            (newName) ->
            {
                if (newName.isEmpty() || newName.contains(" "))
                {
                    return;
                }

                Map<String, ParticleCurve> curves = this.section.getScheme().curves;
                MolangParser parser = this.section.getScheme().parser;

                if (!parser.variables.containsKey(newName))
                {
                    parser.variables.put(newName, new Variable(newName, 0));
                }

                curves.put(newName, curves.remove(oldName));
                this.particleCurve.variable = parser.variables.get(newName);
                this.name.label = IKey.raw(newName);
                this.section.dirty();
            }
        );

        panel.text.setText(oldName);

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void remove(UIIcon b)
    {
        this.removeFromParent();
        this.section.getScheme().curves.remove(this.particleCurve.variable.getName());
        this.section.getEditor().resize();
        this.section.dirty();
    }

    private void changeType(int value)
    {
        this.particleCurve.type = ParticleCurveType.values()[value];
        this.section.dirty();
    }

    public void fill(ParticleCurve curve)
    {
        this.particleCurve = curve;

        this.name.label = IKey.raw(curve.variable.getName());
        this.type.setValue(curve.type.ordinal());
        this.curve.fill(curve);
    }
}