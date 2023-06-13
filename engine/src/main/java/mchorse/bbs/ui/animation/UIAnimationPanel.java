package mchorse.bbs.ui.animation;

import mchorse.bbs.BBS;
import mchorse.bbs.animation.Animation;
import mchorse.bbs.animation.AnimationModel;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.camera.Camera;
import mchorse.bbs.core.input.MouseInput;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.forms.forms.BodyPart;
import mchorse.bbs.forms.forms.Form;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.IFlightSupported;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.keyframes.UISheet;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.utils.StencilFormFramebuffer;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.MapUtils;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.keyframes.KeyframeChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UIAnimationPanel extends UIDataDashboardPanel<Animation> implements IFlightSupported
{
    public UIDopeSheetEditor graph;
    public UIScrollView quickArea;
    public UIIcon showKeyframes;
    public UITrackpad time;

    public UITrackpad duration;
    public UIElement form;
    public UINestedEdit pickForm;

    public UIIcon plause;
    public UIIcon models;

    private boolean playing;
    private int tick;
    private long lastTick;
    private String lastChannel = "";

    private AnimationModel model;
    private Set<String> channels = new HashSet<String>();
    private Map<UITrackpad, KeyframeChannel> quick = new HashMap<UITrackpad, KeyframeChannel>();

    private StencilFormFramebuffer stencil = new StencilFormFramebuffer();

    public UIAnimationPanel(UIDashboard dashboard)
    {
        super(dashboard);

        int h = 160;
        int w = 80;

        this.graph = new UIDopeSheetEditor(this);
        this.graph.relative(this.editor).y(1F, -20).w(1F, -w).h(h - 20).anchorY(1F);
        this.quickArea = UI.scrollView(5, 5);
        this.quickArea.scroll.cancelScrolling();
        this.quickArea.relative(this.editor).x(1F, -w).y(1F).wh(w, h).anchorY(1F);
        this.showKeyframes = new UIIcon(Icons.MORE, this::showKeyframesOverlay);
        this.showKeyframes.tooltip(UIKeys.ANIMATION_KEYFRAMES_TOOLTIP, Direction.RIGHT).relative(this.graph).anchorY(1F);
        this.time = new UITrackpad((v) -> this.setTick(v.intValue())).integer();
        this.time.relative(this.graph).y(1F).w(1F);

        this.duration = new UITrackpad((v) ->
        {
            this.data.duration = v.intValue();
            this.graph.keyframes.duration = this.data.duration;
        });
        this.duration.limit(0).integer();
        this.pickForm = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this, editing, this.model.form, this::setForm);
        });
        this.form = UI.column(UI.label(UIKeys.ANIMATION_FORM), this.pickForm).marginTop(12);

        this.plause = new UIIcon(Icons.PLAY, (b) -> this.setPlaying(!this.playing));
        this.models = new UIIcon(Icons.POSE, (b) -> this.openModels());

        this.editor.add(this.graph, this.quickArea, this.showKeyframes, this.time);
        this.iconBar.add(this.models, this.plause);
        this.overlay.namesList.setFileIcon(Icons.CURVES);

        this.addOptions();
        this.options.fields.add(UI.label(UIKeys.ANIMATION_DURATION), this.duration);
        this.options.fields.add(this.form);

        this.fill(null);

        this.keys().register(Keys.PLAUSE, () -> this.plause.clickItself()).active(() -> this.data != null);
        this.keys().register(Keys.PREV, () -> this.setTickFill(this.tick - 1)).active(() -> this.data != null);
        this.keys().register(Keys.NEXT, () -> this.setTickFill(this.tick + 1)).active(() -> this.data != null);
    }

    private void showKeyframesOverlay(UIIcon b)
    {
        UIStringOverlayPanel keyframes = new UIStringOverlayPanel(UIKeys.ANIMATION_KEYFRAMES, false, this.collectKeys(), this::pickKeyframes);

        keyframes.set(this.lastChannel);

        UIOverlay.addOverlay(this.getContext(), keyframes);
    }

    @Override
    public boolean canPause()
    {
        return false;
    }

    @Override
    public void appear()
    {
        super.appear();

        this.stencil.setup();
        this.stencil.resize(Window.width, Window.height);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.stencil.resize(Window.width, Window.height);
    }

    private void setForm(Form form)
    {
        this.model.form = FormUtils.copy(form);

        this.pickModel(this.model);
    }

    private void openModels()
    {
        UIAnimationModelsOverlayPanel overlay = new UIAnimationModelsOverlayPanel(this.data, this::pickModel);

        UIOverlay.addOverlay(this.getContext(), overlay.set(MapUtils.getKey(this.data.models, this.model)), 0.4F, 0.6F);
    }

    private void pickModel(String key)
    {
        this.pickModel(this.data.models.get(key));
    }

    private void pickModel(AnimationModel model)
    {
        this.model = model;

        this.editor.setVisible(this.model != null);
        this.form.setVisible(this.model != null);

        if (this.model != null)
        {
            this.pickForm.setForm(this.model.form);
            this.pickKeyframes("general");
        }
    }

    private Collection<String> collectKeys()
    {
        this.channels.clear();
        this.channels.addAll(this.model.getAvailableKeys());

        Set<String> unique = new HashSet<String>();

        for (String a : this.channels)
        {
            int endIndex = a.lastIndexOf('.');

            if (endIndex == -1)
            {
                continue;
            }

            String withoutLast = a.substring(0, endIndex);

            unique.add(withoutLast);
        }

        unique.add("general");

        return unique;
    }

    private void pickKeyframes(String key)
    {
        this.lastChannel = key;

        List<String> keys = new ArrayList<String>();

        if (key.equals("general"))
        {
            keys.addAll(AnimationModel.KEYS);
        }
        else
        {
            for (String k : this.channels)
            {
                String prefix = key + ".";

                if (k.startsWith(prefix) && k.lastIndexOf('.') == prefix.length() - 1)
                {
                    keys.add(k);
                }
            }
        }

        Collections.sort(keys);

        /* Create dope sheets */
        List<UISheet> sheets = new ArrayList<UISheet>();

        for (String k : keys)
        {
            int dot = k.lastIndexOf('.');

            sheets.add(new UISheet(k, IKey.str(dot == - 1 ? k : k.substring(dot + 1)), this.getColor(k), this.getChannel(k)));
        }

        this.graph.setSheets(sheets);

        /* Create quick editor */
        this.quick.clear();
        this.quickArea.removeAll();

        for (UISheet sheet : sheets)
        {
            UITrackpad trackpad = new UITrackpad((v) -> sheet.channel.insert(this.tick, v));

            trackpad.h(16);
            trackpad.tooltip(IKey.str(sheet.id));
            trackpad.textbox.setColor(this.getColor(sheet.id));
            this.quick.put(trackpad, sheet.channel);
            this.quickArea.add(trackpad);
        }

        this.updateQuickArea();
    }

    private int getColor(String k)
    {
        if (k.endsWith("x") || k.endsWith(".r"))
        {
            return Colors.RED;
        }
        else if (k.endsWith("y") || k.endsWith(".g"))
        {
            return Colors.GREEN;
        }
        else if (k.endsWith("z") || k.endsWith(".b"))
        {
            return Colors.BLUE;
        }

        return Colors.ACTIVE;
    }

    private void updateQuickArea()
    {
        for (Map.Entry<UITrackpad, KeyframeChannel> entry : this.quick.entrySet())
        {
            entry.getKey().setValue(entry.getValue().interpolate(this.tick));
        }

        this.quickArea.resize();
    }

    private KeyframeChannel getChannel(String key)
    {
        KeyframeChannel channel = this.model.keyframes.get(key);

        if (channel == null)
        {
            channel = new KeyframeChannel();
            channel.insert(0, this.model.getDefaultValue(key));

            this.model.keyframes.put(key, channel);
        }

        return channel;
    }

    private void setPlaying(boolean playing)
    {
        this.playing = playing;

        this.plause.both(playing ? Icons.PAUSE : Icons.PLAY);
    }

    public void setTickFill(int tick)
    {
        this.time.setValue(tick);
        this.setTick(tick);
    }

    public void setTick(int tick)
    {
        this.tick = tick;

        this.updateQuickArea();
    }

    public int getTick()
    {
        return this.tick;
    }

    @Override
    public boolean needsBackground()
    {
        return false;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.ANIMATIONS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_ANIMATIONS;
    }

    @Override
    public void fill(Animation data)
    {
        super.fill(data);

        this.setTickFill(0);
        this.setPlaying(false);
        this.models.setEnabled(data != null);
        this.plause.setEnabled(data != null);
        this.editor.setVisible(data != null);

        if (data != null)
        {
            this.duration.setValue(data.duration);
            this.graph.keyframes.duration = data.duration;

            this.pickModel(data.models.isEmpty() ? null : data.models.keySet().iterator().next());
            this.graph.keyframes.resetView();
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.area.isInside(context) && context.mouseButton == 1 && this.stencil.hasPicked())
        {
            this.pickKeyframes(this.getPath(this.stencil.getPicked()));
        }

        return super.subMouseClicked(context);
    }

    private String getPath(Pair<Form, String> pair)
    {
        Form previous = pair.a;
        Form form = pair.a.getParent();
        List<String> path = new ArrayList<String>();

        if (!pair.b.isEmpty())
        {
            path.add(pair.b);
            path.add("bones");
        }

        while (form != null)
        {
            int index = 0;
            List<BodyPart> all = form.parts.getAll();

            for (int i = 0; i < all.size(); i++)
            {
                if (all.get(i).getForm() == previous)
                {
                    index = i;

                    break;
                }
            }

            path.add(String.valueOf(index));

            previous = form;
            form = form.getParent();
        }

        Collections.reverse(path);

        return String.join(".", path);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.editor.isVisible())
        {
            this.quickArea.area.render(context.draw, Colors.A75);

            int x = this.showKeyframes.area.ex();
            int y = this.showKeyframes.area.my() - context.font.getHeight() / 2;

            context.draw.textCard(context.font, this.lastChannel, x, y);
        }

        if (this.model != null && this.editor.area.isInside(context))
        {
            Camera camera = this.dashboard.bridge.get(IBridgeCamera.class).getCamera();
            Camera oldCamera = context.render.getCamera();
            float currentTime = this.tick + (this.playing ? context.getTransition() : 0);

            context.render.getUBO().update(camera.projection, camera.view);
            context.render.setCamera(camera);

            GLStates.setupDepthFunction3D();

            MouseInput mouseInput = BBS.getEngine().mouse;

            this.stencil.apply(context);
            this.model.render(context.render, currentTime);
            this.stencil.pick(mouseInput.x, Window.height - mouseInput.y);
            this.stencil.unbind(context);

            GLStates.setupDepthFunction2D();

            context.render.setCamera(oldCamera);
            context.render.getUBO().update(context.render.projection, Matrices.EMPTY_4F);

            GLStates.resetViewport();

            this.renderStencilPreview(context);
        }

        super.render(context);
    }

    private void renderStencilPreview(UIContext context)
    {
        if (!this.stencil.hasPicked())
        {
            return;
        }

        int index = this.stencil.getIndex();
        Texture texture = this.stencil.getFramebuffer().getMainTexture();
        Pair<Form, String> pair = this.stencil.getPicked();
        int w = texture.width;
        int h = texture.height;

        texture.bind();

        Shader shader = context.render.getPickingShaders().get(VBOAttributes.VERTEX_UV_RGBA_2D);

        CommonShaderAccess.setTarget(shader, index);
        context.draw.customTextured(shader, Colors.WHITE, 0, 0, 0, h, this.dashboard.width, this.dashboard.height, w, h, w, 0);

        if (pair != null)
        {
            String label = pair.a.getIdOrName();

            if (!pair.b.isEmpty())
            {
                label += " - " + pair.b;
            }

            context.draw.textCard(context.font, label, context.mouseX + 12, context.mouseY + 8);
        }
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        UIContext uiContext = this.getContext();

        if (this.lastTick != uiContext.getTick())
        {
            this.lastTick = uiContext.getTick();

            if (this.playing)
            {
                this.setTickFill(this.tick + 1);

                if (this.tick >= this.data.duration)
                {
                    this.setTickFill(0);
                    this.setPlaying(false);
                }
            }
        }

        if (this.data != null)
        {
            float transition = context.getTransition();
            float currentTime = this.tick + (this.playing ? transition : 0);

            this.data.render(context, currentTime);
        }
    }
}