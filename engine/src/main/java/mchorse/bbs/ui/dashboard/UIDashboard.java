package mchorse.bbs.ui.dashboard;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.camera.OrbitCamera;
import mchorse.bbs.camera.controller.OrbitCameraController;
import mchorse.bbs.events.register.RegisterDashboardPanels;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.GLStates;
import mchorse.bbs.graphics.MatrixStack;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.CommonShaderAccess;
import mchorse.bbs.graphics.shaders.Shader;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.text.builders.ITextBuilder;
import mchorse.bbs.graphics.vao.VAO;
import mchorse.bbs.graphics.vao.VAOBuilder;
import mchorse.bbs.graphics.vao.VBOAttributes;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.settings.ui.UISettingsOverlayPanel;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.animation.UIAnimationPanel;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanels;
import mchorse.bbs.ui.dashboard.textures.UITextureManagerPanel;
import mchorse.bbs.ui.dashboard.utils.UIGraphPanel;
import mchorse.bbs.ui.dashboard.utils.UIOrbitCamera;
import mchorse.bbs.ui.font.UIFontPanel;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.UIRenderingContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.particles.UIParticleSchemePanel;
import mchorse.bbs.ui.recording.editor.UIRecordPanel;
import mchorse.bbs.ui.recording.scene.UIScenePanel;
import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.ui.world.entities.UIEntitiesPanel;
import mchorse.bbs.ui.world.objects.UIWorldObjectsPanel;
import mchorse.bbs.ui.world.settings.UIWorldSettingsPanel;
import mchorse.bbs.ui.world.worlds.UIWorldsOverlayPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Matrices;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.world.World;
import mchorse.bbs.world.entities.Entity;
import mchorse.bbs.world.entities.architect.EntityArchitect;
import mchorse.bbs.world.entities.components.BasicComponent;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class UIDashboard extends UIBaseMenu
{
    private UIDashboardPanels panels;

    public UIIcon settings;
    public UIIcon worlds;

    /* Camera data */
    public final UIOrbitCamera orbitUI = new UIOrbitCamera();
    public final OrbitCamera orbit = this.orbitUI.orbit;
    public final OrbitCameraController camera = new OrbitCameraController(this.orbit, 5);

    private Entity walker;
    private boolean displayAxes = true;

    public UIDashboard(IBridge bridge)
    {
        super(bridge);

        World world = bridge.get(IBridgeWorld.class).getWorld();

        this.orbitUI.setControl(true);
        this.orbit.position.set(world.settings.cameraPosition);
        this.orbit.rotation.set(world.settings.cameraRotation);

        /* Setup panels */
        this.panels = new UIDashboardPanels();
        this.panels.getEvents().register(UIDashboardPanels.PanelEvent.class, (e) ->
        {
            this.orbitUI.setControl(this.panels.isFlightSupported());

            if (e.lastPanel instanceof UICameraPanel)
            {
                this.orbit.setup(this.bridge.get(IBridgeCamera.class).getCamera());
            }
        });
        this.panels.relative(this.viewport).full();
        this.registerPanels();

        BBS.events.post(new RegisterDashboardPanels(this));

        this.main.add(this.panels);

        this.settings = new UIIcon(Icons.SETTINGS, (b) ->
        {
            UIOverlay.addOverlayRight(this.context, new UISettingsOverlayPanel(), 240);
        });
        this.settings.tooltip(UIKeys.CONFIG_TITLE, Direction.TOP);
        this.worlds = new UIIcon(Icons.GLOBE, (b) ->
        {
            UIOverlay.addOverlay(this.context, new UIWorldsOverlayPanel(this.bridge));
        });
        this.worlds.tooltip(UIKeys.WORLD_WORLDS, Direction.TOP);

        this.panels.pinned.add(this.settings, this.worlds);
        this.getRoot().prepend(this.orbitUI);

        /* Register keys */
        IKey category = UIKeys.DASHBOARD_CATEGORY;

        this.overlay.keys().register(Keys.TOGGLE_VISIBILITY, this.main::toggleVisible).category(category);
        this.overlay.keys().register(Keys.WORLD_RECORD_REPLAY, () -> this.getPanel(UIScenePanel.class).record()).category(category);
        this.overlay.keys().register(Keys.WORLD_PLAYBACK_SCENE, () -> this.getPanel(UIScenePanel.class).plause()).category(category);
        this.overlay.keys().register(Keys.WORLD_SAVE, this::saveWorld).category(category);
        this.overlay.keys().register(Keys.WORLD_TOGGLE_WALK, this::toggleWalkMode).category(category);
        this.overlay.keys().register(Keys.WORLD_TOGGLE_AXES, () -> this.displayAxes = !this.displayAxes).category(category);
        this.overlay.keys().register(Keys.WORLD_CYCLE_PANELS, this::cyclePanels).category(category);
        this.overlay.keys().register(Keys.DASHBOARD_WORLD_EDITOR, () -> this.panels.setPanel(this.panels.getPanel(UIWorldEditorPanel.class))).category(category);
    }

    private void saveWorld()
    {
        this.bridge.get(IBridgeWorld.class).getWorld().saveAll(false);
        UIUtils.playClick();
    }

    public boolean isWalkMode()
    {
        return this.walker != null;
    }

    public void toggleWalkMode()
    {
        if (this.walker == null)
        {
            Vector3d finalPosition = this.orbit.getFinalPosition();

            this.walker = EntityArchitect.createDummy();
            this.walker.basic.setHitboxSize(0.5F, 1.2F);
            this.walker.basic.sneakMultiplier = 0.75F;
            this.walker.basic.setPosition(finalPosition.x, finalPosition.y - this.walker.basic.getEyeHeight(), finalPosition.z);
            this.walker.basic.prevPosition.set(this.walker.basic.position);
            this.orbit.position.set(finalPosition);
        }
        else
        {
            this.walker = null;
        }

        this.orbit.distance = 0F;
    }

    private void cyclePanels()
    {
        List<UIDashboardPanel> panels = this.panels.panels;

        int direction = Window.isShiftPressed() ? -1 : 1;
        int index = panels.indexOf(this.panels.panel);
        int newIndex = MathUtils.cycler(index + direction, 0, panels.size() - 1);

        this.setPanel(panels.get(newIndex));
        UIUtils.playClick();
    }

    public UIDashboardPanels getPanels()
    {
        return this.panels;
    }

    public void reloadWorld(World world)
    {
        for (UIDashboardPanel panel : this.panels.panels)
        {
            panel.reloadWorld();
        }

        this.orbitUI.setControl(true);
        this.orbit.position.set(world.settings.cameraPosition);
        this.orbit.rotation.set(world.settings.cameraRotation);
    }

    @Override
    public Link getMenuId()
    {
        return Link.bbs("dashboard");
    }

    @Override
    public boolean canPause()
    {
        return this.panels.panel != null && this.panels.panel.canPause();
    }

    @Override
    public boolean canRefresh()
    {
        return this.panels.panel != null && this.panels.panel.canRefresh();
    }

    @Override
    public void onOpen(UIBaseMenu oldMenu)
    {
        super.onOpen(oldMenu);

        if (oldMenu != this)
        {
            this.panels.open();
            this.setPanel(this.panels.panel);
        }

        this.bridge.get(IBridgeCamera.class).getCameraController().add(this.camera);
    }

    @Override
    public void onClose(UIBaseMenu nextMenu)
    {
        super.onClose(nextMenu);

        if (nextMenu != this)
        {
            this.panels.close();
        }

        this.orbit.reset();
        this.bridge.get(IBridgeCamera.class).getCameraController().remove(this.camera);
    }

    @Override
    protected void closeMenu()
    {
        if (!this.main.isVisible())
        {
            this.main.setVisible(true);

            return;
        }

        IBridgePlayer playerBridge = this.bridge.get(IBridgePlayer.class);

        if (playerBridge.isCreative())
        {
            playerBridge.setCreative(false);
        }

        super.closeMenu();
    }

    protected void registerPanels()
    {
        this.panels.registerPanel(new UIWorldSettingsPanel(this), UIKeys.WORLD_SETTINGS, Icons.GEAR);

        this.panels.registerPanel(new UIWorldEditorPanel(this), UIKeys.WORLD_WORLD_EDITOR, Icons.BLOCK).marginLeft(10);
        this.panels.registerPanel(new UIWorldObjectsPanel(this), UIKeys.WORLD_OBJECT_EDITOR, Icons.SPHERE);
        this.panels.registerPanel(new UIEntitiesPanel(this), UIKeys.WORLD_ENTITY_EDITOR, Icons.POSE);

        this.panels.registerPanel(new UITileSetEditorPanel(this), UIKeys.TILE_SET_TITLE, Icons.STAIR);

        this.panels.registerPanel(new UIAnimationPanel(this), UIKeys.PANELS_ANIMATIONS, Icons.CURVES).marginLeft(10);
        this.panels.registerPanel(new UIScenePanel(this), UIKeys.PANELS_SCENES, Icons.SCENE);
        this.panels.registerPanel(new UIRecordPanel(this), UIKeys.PANELS_RECORDS, Icons.EDITOR);
        this.panels.registerPanel(new UICameraPanel(this), UIKeys.PANELS_CAMERAS, Icons.FRUSTUM);

        this.panels.registerPanel(new UIParticleSchemePanel(this), UIKeys.PANELS_PARTICLES, Icons.PARTICLE).marginLeft(10);
        this.panels.registerPanel(new UIFontPanel(this), UIKeys.FONT_EDITOR_TITLE, Icons.FONT);
        this.panels.registerPanel(new UITextureManagerPanel(this), UIKeys.TEXTURES_TOOLTIP, Icons.MATERIAL);
        this.panels.registerPanel(new UIGraphPanel(this), UIKeys.GRAPH_TOOLTIP, Icons.GRAPH);

        this.setPanel(this.getPanel(UIWorldEditorPanel.class));
    }

    public <T> T getPanel(Class<T> clazz)
    {
        return this.panels.getPanel(clazz);
    }

    public void setPanel(UIDashboardPanel panel)
    {
        this.panels.setPanel(panel);
    }

    @Override
    public boolean handleKey(int key, int scanCode, int action, int mods)
    {
        if (this.panels.isFlightSupported() && this.walker != null)
        {
            if (key == GLFW.GLFW_KEY_SPACE && action == GLFW.GLFW_PRESS)
            {
                float factor = Window.isCtrlPressed() ? 1F : 0.4F;

                this.walker.basic.velocity.y += factor;
                this.walker.basic.grounded = false;
            }
            else if (key == GLFW.GLFW_KEY_LEFT_SHIFT)
            {
                if (action == GLFW.GLFW_PRESS)
                {
                    this.walker.basic.sneak = true;
                }
                else if (action == GLFW.GLFW_RELEASE)
                {
                    this.walker.basic.sneak = false;
                }
            }
        }

        return super.handleKey(key, scanCode, action, mods);
    }

    @Override
    public void update()
    {
        super.update();

        if (this.panels.panel != null)
        {
            this.panels.panel.update();
        }

        if (this.panels.isFlightSupported() && this.walker != null)
        {
            this.walker.setWorld(this.bridge.get(IBridgeWorld.class).getWorld());
            this.walker.update();

            Vector3i position = this.orbit.getVelocityPosition();
            Vector3f direction = new Vector3f();
            float factor = Window.isCtrlPressed() ? 3F : Window.isAltPressed() ? 0.2F : 0.5F;

            factor *= this.walker.basic.grounded ? 1 / 0.7F : 1 / 0.95F;

            direction.x = position.x * -factor;
            direction.z = position.z * -factor;

            Matrices.rotate(direction, 0, -this.orbit.rotation.y);

            BasicComponent basic = this.walker.basic;

            basic.velocity.x = Interpolations.lerp(basic.velocity.x, direction.x, 0.25F);
            basic.velocity.z = Interpolations.lerp(basic.velocity.z, direction.z, 0.25F);
        }
    }

    @Override
    protected void preRenderMenu(UIRenderingContext context)
    {
        if (!this.main.isVisible())
        {
            return;
        }

        if (this.panels.panel != null && this.panels.panel.needsBackground())
        {
            this.background(context);
        }
        else
        {
            context.batcher.gradientVBox(0, 0, this.width, this.height / 8, Colors.A25, 0);
            context.batcher.gradientVBox(0, this.height - this.height / 8, this.width, this.height, 0, Colors.A25);
        }
    }

    private void background(UIRenderingContext context)
    {
        Link background = BBSSettings.backgroundImage.get();
        int color = BBSSettings.backgroundColor.get();

        if (background == null)
        {
            context.batcher.box(0, 0, this.width, this.height, color);
        }
        else
        {
            context.batcher.texturedBox(context.getTextures().getTexture(background), color, 0, 0, this.width, this.height, 0, 0, this.width, this.height, this.width, this.height);
        }
    }

    @Override
    public void renderMenu(UIRenderingContext context, int mouseX, int mouseY)
    {
        super.renderMenu(context, mouseX, mouseY);

        if (this.orbitUI.canControl() && this.walker != null)
        {
            BasicComponent basic = this.walker.basic;
            Vector3d position = new Vector3d(basic.prevPosition).lerp(basic.position, context.getTransition());

            this.orbit.position.set(position).add(0, basic.getEyeHeight(), 0);
        }
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        super.renderInWorld(context);

        if (this.panels.panel != null)
        {
            this.panels.panel.renderInWorld(context);
        }

        if (this.main.isVisible() && this.orbit.distance > 0.1F && this.displayAxes && this.panels.isFlightSupported())
        {
            this.renderWorldAxes(context);
        }
    }

    public void renderWorldAxes(RenderingContext context)
    {
        Vector3f relative = context.getCamera().getRelative(this.orbit.position);

        final float axisSize = 0.75F;
        final float axisOffset = 0.045F;
        final float outlineSize = axisSize + 0.015F;
        final float outlineOffset = axisOffset + 0.015F;
        final float labelOffset = axisSize - 0.075F;

        context.stack.push();
        context.stack.translate(relative);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_RGBA);

        CommonShaderAccess.setModelView(shader, context.stack);

        context.stack.pop();

        /* Draw axes */
        VAOBuilder builder = context.getVAO().setup(shader);

        GLStates.depthMask(false);

        builder.begin();

        Draw.fillBox(builder, 0, -outlineOffset, -outlineOffset, outlineSize, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineSize, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, 0, -outlineOffset, outlineOffset, outlineSize, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineSize, -outlineOffset, outlineOffset, 0, outlineOffset, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, 0, outlineOffset, outlineOffset, outlineSize, 0, 0, 0);
        Draw.fillBox(builder, -outlineOffset, -outlineOffset, -outlineSize, outlineOffset, outlineOffset, 0, 0, 0, 0);

        builder.render();

        GLStates.depthMask(true);

        builder.begin();

        Draw.fillBox(builder, 0, -axisOffset, -axisOffset, axisSize, axisOffset, axisOffset, 1, 0, 0);
        Draw.fillBox(builder, -axisSize, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, 0.75F, 0, 0.25F);
        Draw.fillBox(builder, -axisOffset, 0, -axisOffset, axisOffset, axisSize, axisOffset, 0, 1, 0);
        Draw.fillBox(builder, -axisOffset, -axisSize, -axisOffset, axisOffset, 0, axisOffset, 0, 0.75F, 0.25F);
        Draw.fillBox(builder, -axisOffset, -axisOffset, 0, axisOffset, axisOffset, axisSize, 0, 0, 1);
        Draw.fillBox(builder, -axisOffset, -axisOffset, -axisSize, axisOffset, axisOffset, 0, 0.25F, 0, 0.75F);

        builder.render();

        if (this.orbit.distance < 8)
        {
            GLStates.depthTest(false);
            GLStates.cullFaces(false);

            this.renderWorldAxisLabel(context, "-X", -labelOffset, 0, 0, relative);
            this.renderWorldAxisLabel(context, "+X", labelOffset, 0, 0, relative);
            this.renderWorldAxisLabel(context, "+Y", 0, labelOffset, 0, relative);
            this.renderWorldAxisLabel(context, "-Y", 0, -labelOffset, 0, relative);
            this.renderWorldAxisLabel(context, "-Z", 0, 0, -labelOffset, relative);
            this.renderWorldAxisLabel(context, "+Z", 0, 0, labelOffset, relative);

            GLStates.depthTest(true);
            GLStates.cullFaces(true);
        }
    }

    private void renderWorldAxisLabel(RenderingContext context, String label, float x, float y, float z, Vector3f relative)
    {
        final float scale = 0.125F / 16F;
        MatrixStack stack = context.stack;

        stack.push();
        stack.translate(relative);
        stack.translate(x, y, z);
        stack.scale(scale, -scale, scale);
        stack.rotateY(-this.orbit.rotation.y);
        stack.rotateX(this.orbit.rotation.x);

        Shader shader = context.getShaders().get(VBOAttributes.VERTEX_UV_RGBA);

        CommonShaderAccess.setModelView(shader, stack);

        stack.pop();

        FontRenderer font = context.getFont();

        font.bindTexture(context);

        VAOBuilder builder = context.getVAO().setup(shader, VAO.INDICES);

        builder.begin();
        font.buildVAO(-font.getWidth(label) / 2, -font.getHeight() / 2, label, builder, ITextBuilder.colored3D.setup(Colors.A100));
        builder.render();
    }
}