package mchorse.bbs.ui.particles.sections;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.particles.ParticleScheme;
import mchorse.bbs.particles.components.appearance.ParticleComponentAppearanceBillboard;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UICirculate;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.utils.UI;

public class UIParticleSchemeAppearanceSection extends UIParticleSchemeComponentSection<ParticleComponentAppearanceBillboard>
{
    public UICirculate mode;
    public UILabel modeLabel;

    public UIButton sizeW;
    public UIButton sizeH;
    public UIButton uvX;
    public UIButton uvY;
    public UIButton uvW;
    public UIButton uvH;

    public UIElement flipbook;
    public UITrackpad stepX;
    public UITrackpad stepY;
    public UITrackpad fps;
    public UIButton max;
    public UIToggle stretch;
    public UIToggle loop;

    public UIParticleSchemeAppearanceSection(UIParticleSchemePanel parent)
    {
        super(parent);

        this.mode = new UICirculate((b) ->
        {
            this.component.flipbook = this.mode.getValue() == 1;
            this.updateElements();
            this.editor.dirty();
        });
        this.mode.addLabel(UIKeys.SNOWSTORM_APPEARANCE_REGULAR);
        this.mode.addLabel(UIKeys.SNOWSTORM_APPEARANCE_ANIMATED);
        this.modeLabel = UI.label(UIKeys.SNOWSTORM_MODE, 20).labelAnchor(0, 0.5F);

        this.sizeW = new UIButton(UIKeys.SNOWSTORM_APPEARANCE_WIDTH, (b) -> this.editMoLang("appearance.width", (str) -> this.component.sizeW = this.parse(str, this.component.sizeW), this.component.sizeW));
        this.sizeH = new UIButton(UIKeys.SNOWSTORM_APPEARANCE_HEIGHT, (b) -> this.editMoLang("appearance.height", (str) -> this.component.sizeH = this.parse(str, this.component.sizeH), this.component.sizeH));

        this.uvX = new UIButton(UIKeys.GENERAL_X, (b) -> this.editMoLang("appearance.uv_x", (str) -> this.component.uvX = this.parse(str, this.component.uvX), this.component.uvX));
        this.uvX.tooltip(UIKeys.SNOWSTORM_APPEARANCE_UV_X);
        this.uvY = new UIButton(UIKeys.GENERAL_Y, (b) -> this.editMoLang("appearance.uv_y", (str) -> this.component.uvY = this.parse(str, this.component.uvY), this.component.uvY));
        this.uvY.tooltip(UIKeys.SNOWSTORM_APPEARANCE_UV_Y);
        this.uvW = new UIButton(UIKeys.SNOWSTORM_APPEARANCE_WIDTH, (b) -> this.editMoLang("appearance.uv_w", (str) -> this.component.uvW = this.parse(str, this.component.uvW), this.component.uvW));
        this.uvW.tooltip(UIKeys.SNOWSTORM_APPEARANCE_UV_W);
        this.uvH = new UIButton(UIKeys.SNOWSTORM_APPEARANCE_HEIGHT, (b) -> this.editMoLang("appearance.uv_h", (str) -> this.component.uvH = this.parse(str, this.component.uvH), this.component.uvH));
        this.uvH.tooltip(UIKeys.SNOWSTORM_APPEARANCE_UV_H);

        this.stepX = new UITrackpad((value) ->
        {
            this.component.stepX = value.floatValue();
            this.editor.dirty();
        });
        this.stepX.tooltip(UIKeys.SNOWSTORM_APPEARANCE_STEP_X);
        this.stepY = new UITrackpad((value) ->
        {
            this.component.stepY = value.floatValue();
            this.editor.dirty();
        });
        this.stepY.tooltip(UIKeys.SNOWSTORM_APPEARANCE_STEP_Y);
        this.fps = new UITrackpad((value) ->
        {
            this.component.fps = value.floatValue();
            this.editor.dirty();
        });
        this.fps.tooltip(UIKeys.SNOWSTORM_APPEARANCE_FPS);
        this.max = new UIButton(UIKeys.SNOWSTORM_APPEARANCE_FRAMES, (b) -> this.editMoLang("appearance.max_frames", (str) -> this.component.maxFrame = this.parse(str, this.component.maxFrame), this.component.maxFrame));
        this.max.tooltip(UIKeys.SNOWSTORM_APPEARANCE_MAX);

        this.stretch = new UIToggle(UIKeys.SNOWSTORM_APPEARANCE_STRETCH, (b) ->
        {
            this.component.stretchFPS = b.getValue();
            this.editor.dirty();
        });
        this.stretch.tooltip(UIKeys.SNOWSTORM_APPEARANCE_STRETCH_TOOLTIP);
        this.loop = new UIToggle(UIKeys.SNOWSTORM_APPEARANCE_LOOP, (b) ->
        {
            this.component.loop = b.getValue();
            this.editor.dirty();
        });
        this.loop.tooltip(UIKeys.SNOWSTORM_APPEARANCE_LOOP_TOOLTIP);

        this.flipbook = new UIElement();
        this.flipbook.column().vertical().stretch();
        this.flipbook.add(UI.label(UIKeys.SNOWSTORM_APPEARANCE_ANIMATED, 20).labelAnchor(0, 1F));
        this.flipbook.add(UI.row(5, 0, 20, this.stepX, this.stepY));
        this.flipbook.add(UI.row(5, 0, 20, this.fps, this.max));
        this.flipbook.add(UI.row(5, 0, 20, this.stretch, this.loop));

        this.fields.add(UI.row(5, 0, 20, this.modeLabel, this.mode));
        this.fields.add(UI.label(UIKeys.SNOWSTORM_APPEARANCE_SIZE, 20).labelAnchor(0, 1F));
        this.fields.add(UI.row(this.sizeW, this.sizeH));
        this.fields.add(UI.label(UIKeys.SNOWSTORM_APPEARANCE_MAPPING, 20).labelAnchor(0, 1F));
        this.fields.add(UI.row(this.uvX, this.uvY), UI.row(this.uvW, this.uvH));
    }

    private void updateElements()
    {
        this.flipbook.removeFromParent();

        if (this.component.flipbook)
        {
            this.fields.add(this.flipbook);
        }

        this.resizeParent();
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.SNOWSTORM_APPEARANCE_TITLE;
    }

    @Override
    protected ParticleComponentAppearanceBillboard getComponent(ParticleScheme scheme)
    {
        return scheme.getOrCreate(ParticleComponentAppearanceBillboard.class);
    }

    @Override
    protected void fillData()
    {
        super.fillData();

        this.mode.setValue(this.component.flipbook ? 1 : 0);

        this.stepX.setValue(this.component.stepX);
        this.stepY.setValue(this.component.stepY);
        this.fps.setValue(this.component.fps);

        this.stretch.setValue(this.component.stretchFPS);
        this.loop.setValue(this.component.loop);

        this.updateElements();
    }
}