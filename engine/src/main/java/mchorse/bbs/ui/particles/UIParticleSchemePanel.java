package mchorse.bbs.ui.particles;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.math.molang.expressions.MolangExpression;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.emitter.ParticleEmitter;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeAppearanceSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeCollisionSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeCurvesSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeExpirationSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeGeneralSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeInitializationSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeLifetimeSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeLightingSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeMotionSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeRateSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeShapeSection;
import mchorse.bbs.ui.particles.sections.UIParticleSchemeSpaceSection;
import mchorse.bbs.ui.particles.utils.MolangSyntaxHighlighter;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UIParticleSchemePanel extends UIDataDashboardPanel<ParticleScheme>
{
    /**
     * Default particle placeholder that comes with the engine.
     */
    public static final Link PARTICLE_PLACEHOLDER = Link.assets("particles/default_placeholder.json");

    public UITextEditor textEditor;
    public UIParticleSchemeRenderer renderer;
    public UIScrollView sectionsView;

    public List<UIParticleSchemeSection> sections = new ArrayList<UIParticleSchemeSection>();

    private String molangId;

    public UIParticleSchemePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.renderer = new UIParticleSchemeRenderer();
        this.renderer.relative(this).wTo(this.iconBar.getFlex()).h(1F);

        this.textEditor = new UITextEditor(null).highlighter(new MolangSyntaxHighlighter());
        this.textEditor.background().relative(this.editor).y(1F, -60).w(1F).h(60);
        this.sectionsView = UI.scrollView(20, 10);
        this.sectionsView.scroll.cancelScrolling().opposite().scrollSpeed *= 3;
        this.sectionsView.relative(this.editor).w(200).hTo(this.textEditor.area);

        this.prepend(new UIRenderable(this::drawOverlay));
        this.prepend(this.renderer);
        this.editor.add(this.textEditor, this.sectionsView);

        UIIcon close = new UIIcon(Icons.CLOSE, (b) -> this.editMoLang(null, null, null));

        close.relative(this.textEditor).x(1F, -20);
        this.textEditor.add(close);
        this.overlay.namesList.setFileIcon(Icons.PARTICLE);

        this.addSection(new UIParticleSchemeGeneralSection(this));
        this.addSection(new UIParticleSchemeCurvesSection(this));
        this.addSection(new UIParticleSchemeSpaceSection(this));
        this.addSection(new UIParticleSchemeInitializationSection(this));
        this.addSection(new UIParticleSchemeRateSection(this));
        this.addSection(new UIParticleSchemeLifetimeSection(this));
        this.addSection(new UIParticleSchemeShapeSection(this));
        this.addSection(new UIParticleSchemeMotionSection(this));
        this.addSection(new UIParticleSchemeExpirationSection(this));
        this.addSection(new UIParticleSchemeAppearanceSection(this));
        this.addSection(new UIParticleSchemeLightingSection(this));
        this.addSection(new UIParticleSchemeCollisionSection(this));

        this.fill(null);
    }

    public void editMoLang(String id, Consumer<String> callback, MolangExpression expression)
    {
        this.molangId = id;
        this.textEditor.callback = callback;
        this.textEditor.setText(expression == null ? "" : expression.toString());
        this.textEditor.setVisible(callback != null);

        if (callback != null)
        {
            this.sectionsView.hTo(this.textEditor.area);
        }
        else
        {
            this.sectionsView.h(1F);
        }

        this.sectionsView.resize();
    }

    @Override
    protected IKey getTitle()
    {
        return UIKeys.SNOWSTORM_TITLE;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.PARTICLES;
    }

    public void dirty()
    {
        this.renderer.emitter.setupVariables();
    }

    private void addSection(UIParticleSchemeSection section)
    {
        this.sections.add(section);
        this.sectionsView.add(section);
    }

    @Override
    public void fill(ParticleScheme data)
    {
        super.fill(data);

        this.editMoLang(null, null, null);

        if (this.data != null)
        {
            this.renderer.setScheme(this.data);

            for (UIParticleSchemeSection section : this.sections)
            {
                section.setScheme(this.data);
            }

            this.sectionsView.resize();
        }
    }

    @Override
    public void fillDefaultData(ParticleScheme data)
    {
        super.fillDefaultData(data);

        try
        {
            InputStream asset = BBS.getProvider().getAsset(PARTICLE_PLACEHOLDER);
            MapType map = DataToString.mapFromString(IOUtils.readText(asset));

            ParticleScheme.PARSER.fromData(data, map);
        }
        catch (Exception e)
        {}
    }

    @Override
    public void appear()
    {
        super.appear();

        this.textEditor.updateHighlighter();
    }

    @Override
    public void close()
    {
        if (this.renderer.emitter != null)
        {
            this.renderer.emitter.particles.clear();
        }
    }

    @Override
    public void resize()
    {
        super.resize();

        /* Renderer needs to be resized again because iconBar is in front, and wTo() doesn't
         * work earlier for some reason... */
        this.renderer.resize();
    }

    private void drawOverlay(UIContext context)
    {
        /* Draw debug info */
        if (this.editor.isVisible())
        {
            ParticleEmitter emitter = this.renderer.emitter;
            String label = emitter.particles.size() + "P - " + emitter.age + "A";

            int y = (this.textEditor.isVisible() ? this.textEditor.area.y : this.area.ey()) - 12;

            context.batcher.textShadow(label, this.editor.area.ex() - 4 - context.font.getWidth(label), y);
        }
    }

    @Override
    public void render(UIContext context)
    {
        super.render(context);

        if (this.molangId != null)
        {
            int w = context.font.getWidth(this.molangId);

            context.batcher.textCard(context.font, this.molangId, this.textEditor.area.ex() - 6 - w, this.textEditor.area.ey() - 6 - context.font.getHeight());
        }
    }
}