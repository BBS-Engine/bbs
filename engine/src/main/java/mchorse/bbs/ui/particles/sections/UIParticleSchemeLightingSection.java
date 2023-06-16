package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.Constant;
import mchorse.bbs.math.molang.MolangParser;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.math.molang.expressions.MolangValue;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceLighting;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceTinting;
import mchorse.bbs.particles.components.appearance.colors.Gradient;
import mchorse.bbs.particles.components.appearance.colors.Solid;
import mchorse.bbs.particles.components.appearance.colors.Tint;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.particles.utils.UIGradientEditor;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;

import java.util.Arrays;

public class UIParticleSchemeLightingSection extends UIParticleSchemeSection
{
    public UICirculate mode;
    public UIColor color;
    public UIButton r;
    public UIButton g;
    public UIButton b;
    public UIButton a;
    public UIToggle lighting;

    public UIGradientEditor gradientEditor;
    public UIElement gradient;
    public UIColor gradientColor;
    public UIButton gradientInterpolant;

    public UIElement channels;

    private ParticleComponentAppearanceTinting component;
    private Tint[] cache = new Tint[3];
    private int previous;

    public UIParticleSchemeLightingSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.mode = new UICirculate((b) -> this.changeMode(b.getValue()));
        this.mode.addLabel(UIKeys.SNOWSTORM_LIGHTING_SOLID);
        this.mode.addLabel(UIKeys.SNOWSTORM_LIGHTING_EXPRESSION);
        this.mode.addLabel(UIKeys.SNOWSTORM_LIGHTING_GRADIENT);

        this.color = new UIColor((color) ->
        {
            Solid solid = this.getSolid();
            Color original = this.color.picker.color;

            solid.r = this.set(solid.r, original.r);
            solid.g = this.set(solid.g, original.g);
            solid.b = this.set(solid.b, original.b);
            solid.a = this.set(solid.a, original.a);
            this.editor.dirty();
        });
        this.color.withAlpha();

        this.r = new UIButton(IKey.str("R"), (b) ->
        {
            Solid solid = this.getSolid();

            this.editMoLang("lighting.r", (str) -> solid.r = this.parse(str, solid.r), solid.r);
        });
        this.r.color(Colors.RED).tooltip(UIKeys.SNOWSTORM_LIGHTING_RED);

        this.g = new UIButton(IKey.str("G"), (b) ->
        {
            Solid solid = this.getSolid();

            this.editMoLang("lighting.g", (str) -> solid.g = this.parse(str, solid.g), solid.g);
        });
        this.g.color(Colors.GREEN).tooltip(UIKeys.SNOWSTORM_LIGHTING_GREEN);

        this.b = new UIButton(IKey.str("B"), (b) ->
        {
            Solid solid = this.getSolid();

            this.editMoLang("lighting.b", (str) -> solid.b = this.parse(str, solid.b), solid.b);
        });
        this.b.color(Colors.BLUE).tooltip(UIKeys.SNOWSTORM_LIGHTING_BLUE);

        this.a = new UIButton(IKey.str("A"), (b) ->
        {
            Solid solid = this.getSolid();

            this.editMoLang("lighting.a", (str) -> solid.a = this.parse(str, solid.a), solid.a);
        });
        this.a.color(0xff1a1a1a).tooltip(UIKeys.SNOWSTORM_LIGHTING_ALPHA);

        this.lighting = new UIToggle(UIKeys.SNOWSTORM_LIGHTING_LIGHTING, (b) -> this.editor.dirty());

        this.gradientColor = new UIColor(this::setGradientColor).withAlpha();
        this.gradientEditor = new UIGradientEditor(this, this.gradientColor);
        this.gradientInterpolant = new UIButton(UIKeys.SNOWSTORM_LIGHTING_INTERPOLANT, (b) ->
        {
            Gradient gradient = (Gradient) this.component.color;

            this.editMoLang("lighting.interpolant", (str) -> gradient.interpolant = this.parse(str, gradient.interpolant), gradient.interpolant);
        });
        this.gradient = UI.row(this.gradientColor, this.gradientInterpolant);

        UILabel label = UI.label(UIKeys.SNOWSTORM_MODE, 20).labelAnchor(0, 0.5F);

        this.channels = UI.row(5, 0, 20, this.r, this.g, this.b, this.a);

        this.fields.add(this.lighting);
        this.fields.add(UI.row(5, 0, 20, label, this.mode));
    }

    private void changeMode(int value)
    {
        if (this.cache[this.previous] == null)
        {
            this.cache[this.previous] = this.component.color;
        }

        Tint cached = this.cache[value];

        if (cached == null)
        {
            if (value == 2)
            {
                cached = new Gradient();
            }
            else
            {
                cached = new Solid();
            }

            this.cache[value] = cached;
        }

        this.component.color = cached;

        this.fillData();
        this.editor.dirty();

        this.previous = value;
    }

    private void setGradientColor(int color)
    {
        this.gradientEditor.setColor(color);
    }

    private MolangExpression set(MolangExpression expression, float value)
    {
        if (expression == MolangParser.ZERO || expression == MolangParser.ONE)
        {
            return new MolangValue(null, new Constant(value));
        }

        if (!(expression instanceof MolangValue))
        {
            expression = new MolangValue(null, new Constant(0));
        }

        MolangValue v = (MolangValue) expression;

        if (!(v.expression instanceof Constant))
        {
            v.expression = new Constant(0);
        }

        v.expression.set(value);

        return expression;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_LIGHTING_TITLE;
    }

    private Solid getSolid()
    {
        return (Solid) this.component.color;
    }

    @Override
    public void beforeSave(ParticleScheme scheme)
    {
        if (this.lighting.getValue())
        {
            scheme.getOrCreate(ParticleComponentAppearanceLighting.class);
        }
        else
        {
            scheme.remove(ParticleComponentAppearanceLighting.class);
        }
    }

    @Override
    public void setScheme(ParticleScheme scheme)
    {
        super.setScheme(scheme);

        Arrays.fill(this.cache, null);

        this.component = scheme.getOrCreate(ParticleComponentAppearanceTinting.class);
        this.lighting.setValue(scheme.get(ParticleComponentAppearanceLighting.class) != null);

        if (this.component.color instanceof Solid)
        {
            Solid solid = this.getSolid();

            if (solid.isConstant())
            {
                this.setMode(0);
            }
            else
            {
                this.setMode(1);
            }
        }
        else if (this.component.color instanceof Gradient)
        {
            this.setMode(2);
        }

        this.fillData();
    }

    private void setMode(int value)
    {
        this.previous = value;

        this.mode.setValue(value);
    }

    public void fillData()
    {
        this.gradientEditor.removeFromParent();
        this.gradient.removeFromParent();
        this.color.removeFromParent();
        this.color.picker.removeFromParent();
        this.channels.removeFromParent();

        if (this.mode.getValue() == 0)
        {
            Solid solid = (Solid) this.component.color;

            this.color.picker.color.set((float) solid.r.get(), (float) solid.g.get(), (float) solid.b.get(), (float) solid.a.get());

            this.fields.add(this.color);
        }
        else if (this.mode.getValue() == 2)
        {
            this.gradientEditor.setGradient((Gradient) this.component.color);

            this.fields.add(this.gradientEditor);
            this.fields.add(this.gradient);
        }
        else
        {
            this.fields.add(this.channels);
        }

        this.resizeParent();
    }
}