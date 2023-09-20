package mchorse.bbs.ui.world;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.events.register.RegisterWorldEditorToolsEvent;
import mchorse.bbs.graphics.Draw;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.ui.world.tools.UITool;
import mchorse.bbs.ui.world.tools.UIToolArc;
import mchorse.bbs.ui.world.tools.UIToolCube;
import mchorse.bbs.ui.world.tools.UIToolCylinder;
import mchorse.bbs.ui.world.tools.UIToolExtrude;
import mchorse.bbs.ui.world.tools.UIToolFloodFill;
import mchorse.bbs.ui.world.tools.UIToolLine;
import mchorse.bbs.ui.world.tools.UIToolPaste;
import mchorse.bbs.ui.world.tools.UIToolSelection;
import mchorse.bbs.ui.world.tools.UIToolSmooth;
import mchorse.bbs.ui.world.tools.UIToolSphere;
import mchorse.bbs.ui.world.tools.UIToolSpray;
import mchorse.bbs.ui.world.utils.WorldEditorChunkProxy;
import mchorse.bbs.utils.Axis;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.utils.undo.UndoManager;
import mchorse.bbs.voxel.Chunk;
import mchorse.bbs.voxel.ChunkBuilder;
import mchorse.bbs.voxel.blocks.IBlockVariant;
import mchorse.bbs.voxel.processor.CopyProcessor;
import mchorse.bbs.voxel.processor.CylinderProcessor;
import mchorse.bbs.voxel.processor.FillProcessor;
import mchorse.bbs.voxel.processor.PasteProcessor;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.processor.SphereProcessor;
import mchorse.bbs.voxel.processor.WallProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;
import mchorse.bbs.voxel.raytracing.RayTraceType;
import mchorse.bbs.voxel.raytracing.RayTracer;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.undo.ChunkProxy;
import mchorse.bbs.voxel.utils.BlockSelection;
import mchorse.bbs.world.World;
import org.joml.Matrix3f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.function.Supplier;

public class UIWorldEditorPanel extends UIWorldPanel
{
    /* UI fields */
    public UIButton block;

    public UIIcon factory;
    public UIIcon mask;
    public UIIcon undo;
    public UIIcon redo;

    public UIScrollView toolBar;

    public UIScrollView actionBar;
    public UIIcon toggleSelection;
    public UIIcon copy;
    public UIIcon cut;
    public UIIcon paste;
    public UIIcon rotateC;
    public UIIcon rotateCC;
    public UIIcon flipH;
    public UIIcon flipV;
    public UIIcon hollow;
    public UIIcon fillWall;
    public UIIcon fillCube;
    public UIIcon fillSphere;
    public UIIcon fillCylinder;
    public UIIcon fillClear;

    public UIElement selectionBar;
    public UITrackpad minX;
    public UITrackpad minY;
    public UITrackpad minZ;
    public UITrackpad sizeX;
    public UITrackpad sizeY;
    public UITrackpad sizeZ;

    public UIBlockPicker picker;

    /* Data stuff */
    public final RayTraceResult result = new RayTraceResult();

    private IBlockVariant variantToPlace;
    public boolean hollowFill;
    public boolean factoryVariant;

    /* Selection */
    public boolean selected;
    private boolean selecting;
    private BlockSelection selection = new BlockSelection();
    private Chunk buffer;

    /* Tools */
    private Function<RayTraceResult, Boolean> rayHandle = (result) -> this.currentTool.handleRayTracer(result);
    private List<UITool> tools = new ArrayList<>();
    private UITool currentTool;
    private boolean activeTool;
    private int noHitDistance = 5;
    private int hitDistance = 1;

    /* Undo-redo */
    private UndoManager<World> undoManager = new UndoManager<>();
    private ChunkProxy proxy;

    public UIWorldEditorPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.proxy = new WorldEditorChunkProxy(this, dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks, this.undoManager);

        /* Setup UI fields */
        this.block = new UIButton(UIKeys.WORLD_EDITOR_ACTIONS_PICK, this::openBlockPicker);
        this.block.relative(this).x(35).y(20).w(100);

        this.factory = new UIIcon(Icons.REFRESH, (b) -> this.factoryVariant = !this.factoryVariant);
        this.factory.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_AUTO, Direction.RIGHT);
        this.mask = new UIIcon(Icons.MATERIAL, (b) -> this.getContext().replaceContextMenu(new UIMaskContextMenu(this.proxy)));
        this.mask.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_MASK, Direction.RIGHT);

        this.undo = new UIIcon(Icons.UNDO, this::undo);
        this.undo.tooltip(UIKeys.WORLD_EDITOR_CONTEXT_UTILITY_UNDO, Direction.RIGHT);
        this.redo = new UIIcon(Icons.REDO, this::redo);
        this.redo.tooltip(UIKeys.WORLD_EDITOR_CONTEXT_UTILITY_REDO, Direction.RIGHT);

        this.toolBar = new UIScrollView();
        this.toolBar.preRender((context) ->
        {
            this.currentTool.button.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());

            if (this.factoryVariant)
            {
                this.factory.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
            }

            if (this.proxy.isMaskEnabled())
            {
                this.mask.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
            }
        });
        this.toolBar.scroll.cancelScrolling().opposite();
        this.toolBar.relative(this).w(30).h(1F).column(0).scroll().vertical().height(20).padding(5);

        this.actionBar = new UIScrollView();
        this.actionBar.preRender((context) ->
        {
            if (this.selectionBar.isVisible())
            {
                this.toggleSelection.area.render(context.batcher, Colors.A50);
            }
        });
        this.actionBar.scroll.cancelScrolling();
        this.actionBar.relative(this).x(1F).w(30).h(1F).anchorX(1F).column(0).scroll().vertical().height(20).padding(5);
        this.toggleSelection = new UIIcon(Icons.FULLSCREEN, (b) -> this.selectionBar.toggleVisible());
        this.toggleSelection.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_VIEW_SELECTION, Direction.LEFT);
        this.copy = new UIIcon(Icons.COPY, this::copy);
        this.copy.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_COPY, Direction.LEFT);
        this.cut = new UIIcon(Icons.CUT, this::cut);
        this.cut.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_CUT, Direction.LEFT);
        this.paste = new UIIcon(Icons.PASTE, this::paste);
        this.paste.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_PASTE, Direction.LEFT);
        this.rotateC = new UIIcon(Icons.SHIFT_FORWARD, (b) -> this.rotate(1));
        this.rotateC.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_ROTATE, Direction.LEFT);
        this.rotateCC = new UIIcon(Icons.SHIFT_BACKWARD, (b) -> this.rotate(-1));
        this.rotateCC.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_ROTATE_CC, Direction.LEFT);
        this.flipH = new UIIcon(Icons.CONVERT, (b) -> this.flip(new Matrix3f().scale(-1, 1, 1), Axis.X));
        this.flipH.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FLIP_H, Direction.LEFT);
        this.flipV = new UIIcon(Icons.EXCHANGE, (b) -> this.flip(new Matrix3f().scale(1, -1, 1), Axis.Y));
        this.flipV.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FLIP_V, Direction.LEFT);
        this.hollow = new UIIcon(Icons.STOP, (b) ->
        {
            this.hollowFill = !this.hollowFill;
            this.hollow.both(this.hollowFill ? Icons.OUTLINE : Icons.STOP);
        });
        this.hollow.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_HOLLOW, Direction.LEFT).marginTop(10);
        this.fillWall = new UIIcon(Icons.BRICKS, (b) -> this.process(new WallProcessor(this.getVariant())));
        this.fillWall.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FILL_WALL, Direction.LEFT);
        this.fillCube = new UIIcon(Icons.BLOCK, (b) -> this.process(new FillProcessor(this.getVariant(), this.hollowFill)));
        this.fillCube.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FILL_CUBE, Direction.LEFT);
        this.fillSphere = new UIIcon(Icons.SPHERE, (b) -> this.process(new SphereProcessor(this.getVariant(), this.hollowFill)));
        this.fillSphere.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FILL_SPHERE, Direction.LEFT);
        this.fillCylinder = new UIIcon(Icons.CYLINDER, (b) -> this.process(new CylinderProcessor(this.getVariant(), this.hollowFill)));
        this.fillCylinder.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_FILL_CYLINDER, Direction.LEFT);
        this.fillClear = new UIIcon(Icons.CLOSE, this::fillClear);
        this.fillClear.tooltip(UIKeys.WORLD_EDITOR_ACTIONS_CLEAR, Direction.LEFT);

        this.selectionBar = new UIElement();
        this.selectionBar.setVisible(false);
        this.selectionBar.relative(this).x(1F, -25).y(5).w(120).anchorX(1F).column().vertical().stretch().padding(10);
        this.minX = new UITrackpad((v) -> this.setPosition(v.intValue(), (int) this.minY.getValue(), (int) this.minZ.getValue())).integer();
        this.minY = new UITrackpad((v) -> this.setPosition((int) this.minX.getValue(), v.intValue(), (int) this.minZ.getValue())).integer();
        this.minZ = new UITrackpad((v) -> this.setPosition((int) this.minX.getValue(), (int) this.minY.getValue(), v.intValue())).integer();
        this.sizeX = new UITrackpad((v) -> this.setSize(v.intValue(), (int) this.sizeY.getValue(), (int) this.sizeZ.getValue())).limit(1).integer();
        this.sizeY = new UITrackpad((v) -> this.setSize((int) this.sizeX.getValue(), v.intValue(), (int) this.sizeZ.getValue())).limit(1).integer();
        this.sizeZ = new UITrackpad((v) -> this.setSize((int) this.sizeX.getValue(), (int) this.sizeY.getValue(), v.intValue())).limit(1).integer();

        this.picker = new UIBlockPicker(dashboard.bridge.get(IBridgeWorld.class).getChunkBuilder().models, this::setVariant);
        this.picker.relative(this).xy(0.5F, 0.5F).wh(20 + UIBlockPicker.BLOCK_SLOT_SIZE * 8, 20 + UIBlockPicker.BLOCK_SLOT_SIZE * 4).anchor(0.5F);

        this.tools.add(new UIToolSelection(this));
        this.tools.add(new UIToolCube(this));
        this.tools.add(new UIToolSphere(this));
        this.tools.add(new UIToolCylinder(this));
        this.tools.add(new UIToolLine(this));
        this.tools.add(new UIToolPaste(this));
        this.tools.add(new UIToolSmooth(this));
        this.tools.add(new UIToolSpray(this));
        this.tools.add(new UIToolFloodFill(this));
        this.tools.add(new UIToolExtrude(this));
        this.tools.add(new UIToolArc(this));

        BBS.events.post(new RegisterWorldEditorToolsEvent(this.tools));

        this.toolBar.add(this.factory, this.mask);

        for (UITool tool : this.tools)
        {
            tool.button.callback = this::switchTool;

            this.toolBar.add(tool.button);
        }

        this.toolBar.add(this.undo.marginTop(10), this.redo);
        this.actionBar.add(this.toggleSelection, this.copy, this.cut, this.paste, this.rotateC, this.rotateCC, this.flipH, this.flipV);
        this.actionBar.add(this.hollow, this.fillCube, this.fillWall, this.fillSphere, this.fillCylinder, this.fillClear);
        this.selectionBar.add(UI.label(UIKeys.WORLD_EDITOR_SELECTION_ORIGIN), this.minX, this.minY, this.minZ);
        this.selectionBar.add(UI.label(UIKeys.WORLD_EDITOR_SELECTION_SIZE), this.sizeX, this.sizeY, this.sizeZ);
        this.add(this.block, this.toolBar, this.actionBar, this.selectionBar);
        this.switchTool(this.tools.get(1).button);

        /* Registering keys */
        IKey toolsCategory = UIKeys.WORLD_EDITOR_CONTEXT_TOOLS_TITLE;
        IKey selectionCategory = UIKeys.WORLD_EDITOR_CONTEXT_SELECTION_TITLE;

        int i = 1;

        for (UITool tool : this.tools)
        {
            KeyCombo combo = UIUtils.createCombo(tool.button.tooltip.getLabel(), GLFW.GLFW_KEY_0, i);

            this.keys().register(combo, tool.button::clickItself).category(toolsCategory);

            i += 1;
        }

        Supplier<Boolean> collides = () -> !this.result.type.isMissed();
        Supplier<Boolean> selected = () -> this.selected;
        Supplier<Boolean> hasCopy = () -> this.buffer != null;

        this.keys().register(Keys.WE_DESELECT, this::deselect).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_MASK, () ->
        {
            if (Window.isShiftPressed())
            {
                this.getContext().replaceContextMenu(new UIMaskContextMenu(this.proxy));
            }
            else
            {
                this.proxy.setMaskEnabled(!this.proxy.getMaskEnabled());
            }

            UIUtils.playClick();
        });
        this.keys().register(Keys.WE_FILL_CUBE, this.fillCube::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_FILL_SPHERE, this.fillSphere::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_FILL_CYLINDER, this.fillCylinder::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_CLEAR, this.fillClear::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_COPY, this.copy::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_CUT, this.cut::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_PASTE, this.paste::clickItself).category(selectionCategory).active(selected);
        this.keys().register(Keys.WE_ROTATE, this.rotateC::clickItself).category(selectionCategory).active(hasCopy);
        this.keys().register(Keys.WE_ROTATE_CC, this.rotateCC::clickItself).category(selectionCategory).active(hasCopy);
        this.keys().register(Keys.WE_FLIP_H, this.flipH::clickItself).category(selectionCategory).active(hasCopy);
        this.keys().register(Keys.WE_FLIP_V, this.flipV::clickItself).category(selectionCategory).active(hasCopy);

        this.keys().register(Keys.WE_MOVE_TO_SELECTION, this::moveSelectionCenter).active(selected);
        this.keys().register(Keys.WE_MOVE_TO_CENTER, this::moveCenter).active(collides);
        this.keys().register(Keys.WE_PICK, this::pickBlock).active(collides);
        this.keys().register(Keys.WE_BLOCKS, () -> this.block.clickItself());
        this.keys().register(Keys.WE_SELECT_FLOATING, this::selectFloating).active(collides).category(selectionCategory);

        this.keys().register(Keys.UNDO, this.undo::clickItself);
        this.keys().register(Keys.REDO, this.redo::clickItself);
        this.keys().register(Keys.WE_RELOAD_CHUNKS, () -> this.dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks.rebuild());

        this.setVariant(this.proxy.getSet().get(1));
    }

    private void openBlockPicker(UIButton uiButton)
    {
        this.picker.resize();
        this.add(this.picker);
    }

    public void setVariant(IBlockVariant variant)
    {
        this.variantToPlace = variant;

        this.picker.removeFromParent();
    }

    public IBlockVariant getVariant(RayTraceResult result)
    {
        if (this.factoryVariant)
        {
            return this.variantToPlace.getModel().factory.getVariantForBuilding(result);
        }

        return this.variantToPlace;
    }

    public IBridge getBridge()
    {
        return this.dashboard.bridge;
    }

    public IBlockVariant getVariant()
    {
        return this.variantToPlace;
    }

    public ChunkProxy getProxy()
    {
        return this.proxy;
    }

    public BlockSelection getSelection()
    {
        return this.selection;
    }

    public Chunk getBuffer()
    {
        return this.buffer;
    }

    public void setBuffer(Chunk chunk)
    {
        this.buffer = chunk;
    }

    private void switchTool(UIIcon b)
    {
        for (UITool tool : this.tools)
        {
            if (tool.button == b)
            {
                if (this.currentTool != null)
                {
                    UIElement panel = this.currentTool.getPanel();

                    if (panel != null)
                    {
                        panel.removeFromParent();
                    }
                }

                this.currentTool = tool;

                UIElement panel = tool.getPanel();

                if (panel != null)
                {
                    panel.relative(this.block).y(25);
                    panel.resize();

                    this.add(panel);
                }

                return;
            }
        }
    }

    private void deselect()
    {
        this.selected = false;
        this.selection = new BlockSelection();
        this.updateSelection();
    }

    private void copy(UIIcon b)
    {
        Vector3i size = this.selection.getSize();

        this.buffer = new Chunk(size.x, size.y, size.z, this.proxy.getAir());

        this.process(new CopyProcessor(this.buffer));
    }

    private void paste(UIIcon b)
    {
        if (this.buffer == null)
        {
            return;
        }

        this.proxy.begin();
        new PasteProcessor(this.buffer).process(this.selection.getMin(), this.proxy);
        this.proxy.end();
    }

    private void cut(UIIcon b)
    {
        this.copy(b);
        this.fillClear(b);
    }

    private void rotate(int direction)
    {
        this.applyMatrixToBuffer(new Matrix3f().rotateY(-direction * MathUtils.PI / 2F), (a) ->
        {
            BlockModelFactory factory = a.getModel().factory;

            return factory == null ? a : factory.rotateVariant(a, direction > 0);
        });
    }

    private void flip(Matrix3f matrix, Axis axis)
    {
        this.applyMatrixToBuffer(matrix, (a) ->
        {
            BlockModelFactory factory = a.getModel().factory;

            return factory == null ? a : factory.flipVariant(a, axis);
        });
    }

    private void applyMatrixToBuffer(Matrix3f matrix, Function<IBlockVariant, IBlockVariant> filter)
    {
        if (this.buffer == null)
        {
            return;
        }

        Vector3f min = new Vector3f(0, 0, 0);
        Vector3f max = new Vector3f(this.buffer.w - 1, this.buffer.h - 1, this.buffer.d - 1);

        matrix.transform(min);
        matrix.transform(max);

        Vector3i size = new Vector3i(
            Math.round(Math.abs(max.x - min.x) + 1),
            Math.round(Math.abs(max.y - min.y) + 1),
            Math.round(Math.abs(max.z - min.z) + 1)
        );

        Chunk chunk = new Chunk(size.x, size.y, size.z, this.proxy.getAir());
        Vector3f transform = new Vector3f();

        Vectors.min(min, max, transform);
        Vectors.max(min, max, max);

        min.set(transform);

        for (int x = 0; x < this.buffer.w; x++)
        {
            for (int y = 0; y < this.buffer.h; y++)
            {
                for (int z = 0; z < this.buffer.d; z++)
                {
                    IBlockVariant block = this.buffer.getBlock(x, y, z);
                    int lighting = this.buffer.getLighting(x, y, z);

                    transform.set(x, y, z);
                    matrix.transform(transform);

                    int x1 = (int) Math.floor(transform.x - min.x);
                    int y1 = (int) Math.floor(transform.y - min.y);
                    int z1 = (int) Math.floor(transform.z - min.z);

                    chunk.setBlock(x1, y1, z1, filter.apply(block));
                    chunk.setLighting(x1, y1, z1, lighting);
                }
            }
        }

        this.buffer = chunk;
    }

    private void fillClear(UIIcon b)
    {
        this.process(new FillProcessor(this.proxy.getAir(), false));
    }

    private void setPosition(int x, int y, int z)
    {
        this.selection.setPosition(x, y, z);
    }

    private void setSize(int x, int y, int z)
    {
        this.selection.setSize(x, y, z);
    }

    private void process(Processor processor)
    {
        Vector3i max = new Vector3i(this.selection.getMax()).sub(1, 1, 1);

        this.proxy.begin();
        processor.process(this.selection.getMin(), max, this.proxy);
        this.proxy.end();
    }

    private void pickBlock()
    {
        this.setVariant(this.proxy.getChunks().getBlock(this.result.block.x, this.result.block.y, this.result.block.z));
    }

    private void selectFloating()
    {
        boolean upOnly = Window.isShiftPressed();
        Vector3i block = this.result.block;
        Vector3i min = new Vector3i(block);
        Vector3i max = new Vector3i(block);
        Set<Vector3i> checked = new HashSet<>();
        Stack<Vector3i> toCheck = new Stack<>();

        toCheck.add(block);

        while (!toCheck.isEmpty())
        {
            Vector3i p = toCheck.pop();
            Vector3i top = new Vector3i(p).add(0, 1, 0);
            Vector3i bottom = new Vector3i(p).add(0, -1, 0);
            Vector3i left = new Vector3i(p).add(1, 0, 0);
            Vector3i right = new Vector3i(p).add(-1, 0, 0);
            Vector3i front = new Vector3i(p).add(0, 0, 1);
            Vector3i back = new Vector3i(p).add(0, 0, -1);

            if (this.canTraverseFurther(top, block, checked)) toCheck.add(top);
            if (!upOnly && this.canTraverseFurther(bottom, block, checked)) toCheck.add(bottom);
            if (this.canTraverseFurther(left, block, checked)) toCheck.add(left);
            if (this.canTraverseFurther(right, block, checked)) toCheck.add(right);
            if (this.canTraverseFurther(front, block, checked)) toCheck.add(front);
            if (this.canTraverseFurther(back, block, checked)) toCheck.add(back);

            Vectors.min(min, p, min);
            Vectors.max(max, p, max);

            checked.add(p);
        }

        this.selected = true;
        this.selection.setA(min);
        this.selection.setB(max);
        this.updateSelection();
    }

    private boolean canTraverseFurther(Vector3i block, Vector3i origin, Set<Vector3i> checked)
    {
        int max = Math.max(Math.max(Math.abs(origin.x - block.x), Math.abs(origin.y - block.y)), Math.abs(origin.z - block.z));

        return max <= 50 && this.proxy.getChunks().hasBlock(block.x, block.y, block.z) && !checked.contains(block);
    }

    private void moveCenter()
    {
        this.dashboard.orbit.position.set(this.result.block.x, this.result.block.y, this.result.block.z).add(0.5D, 0.5D, 0.5D);
    }

    private void moveSelectionCenter()
    {
        this.dashboard.orbit.position.set(this.selection.getCenter());
    }

    private void undo(UIIcon b)
    {
        this.undoManager.undo(this.dashboard.bridge.get(IBridgeWorld.class).getWorld());
    }

    private void redo(UIIcon b)
    {
        this.undoManager.redo(this.dashboard.bridge.get(IBridgeWorld.class).getWorld());
    }

    public void updateSelection()
    {
        Vector3i min = this.selection.getMin();
        Vector3i max = this.selection.getMax();

        this.minX.setValue(min.x);
        this.minY.setValue(min.y);
        this.minZ.setValue(min.z);
        this.sizeX.setValue(max.x - min.x);
        this.sizeY.setValue(max.y - min.y);
        this.sizeZ.setValue(max.z - min.z);
    }

    @Override
    public void reloadWorld()
    {
        super.reloadWorld();

        this.proxy = new WorldEditorChunkProxy(this, dashboard.bridge.get(IBridgeWorld.class).getWorld().chunks, this.undoManager);
    }

    @Override
    public void update()
    {
        if (this.activeTool && !this.selecting)
        {
            this.currentTool.drag(this.result);
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (((context.mouseButton == 0 && !Window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) || context.mouseButton == 1) && !this.result.type.isMissed())
        {
            if (Window.isCtrlPressed() && context.mouseButton == 0)
            {
                this.selecting = true;
                this.selected = true;
                this.selection.setA(this.result.block);
                this.selection.setB(this.result.block);

                return true;
            }

            if (Window.isAltPressed() && context.mouseButton == 0 && this.selected)
            {
                Vector3i size = this.selection.getSize();

                this.selection.setPosition(this.result.block.x - size.x / 2, this.result.block.y + 1, this.result.block.z - size.z / 2);
                this.updateSelection();

                return true;
            }

            this.activeTool = true;
            this.currentTool.begin(this.result, context.mouseButton);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseScrolled(UIContext context)
    {
        if (Window.isCtrlPressed())
        {
            if (this.result.normal.equals(Vectors.EMPTY_3I))
            {
                this.noHitDistance = Math.max(this.noHitDistance + (int) Math.copySign(1, context.mouseWheel), 0);
            }
            else
            {
                this.hitDistance = Math.max(this.hitDistance + (int) Math.copySign(1, context.mouseWheel), 1);
            }

            return true;
        }

        return this.currentTool.mouseScrolled(context.mouseWheel);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.activeTool && this.currentTool.lastMouseButton == context.mouseButton)
        {
            this.activeTool = false;
            this.currentTool.end(this.result);
        }

        if (this.selecting)
        {
            this.selected = this.selection.getSize().length() > 1;
            this.updateSelection();
        }

        if (context.mouseButton == 0)
        {
            this.selecting = false;
        }

        return super.subMouseReleased(context);
    }

    @Override
    public void render(UIContext context)
    {
        FontRenderer font = context.font;

        if (this.selecting && !this.result.type.isMissed())
        {
            this.selection.setB(this.result.block);
            this.updateSelection();
        }

        if (!this.variantToPlace.isAir())
        {
            this.renderBlockPreview(context, font);
        }

        if (this.selectionBar.isVisible())
        {
            this.selectionBar.area.render(context.batcher, Colors.A50);
        }

        super.render(context);

        int x = this.area.x + 12;
        int y = this.area.h - 12 - context.font.getHeight();

        if (this.selected)
        {
            Vector3i size = this.selection.getSize();
            String label = size.x + "x" + size.y + "x" + size.z + " (" + (size.x * size.y * size.z) + " blocks)";

            context.batcher.textCard(font, label, x, y, Colors.WHITE, Colors.A50, 2);

            y -= 12;
        }

        if (this.result.type == RayTraceType.BLOCK)
        {
            Vector3i block = this.result.block;
            String label = "(" + block.x + ", " + block.y + ", " + block.z + ")";

            context.batcher.textCard(font, label, x, y, Colors.WHITE, Colors.A50, 2);
        }
    }

    private void renderBlockPreview(UIContext context, FontRenderer font)
    {
        int x = this.block.area.ex() + 17;
        int y = this.block.area.y + 10;
        int scale = 20;

        context.batcher.dropCircleShadow(x, y, scale, 6, Colors.A100, Colors.A100);

        ChunkBuilder blockBuilder = this.dashboard.bridge.get(IBridgeWorld.class).getChunkBuilder();

        blockBuilder.renderInUI(context, this.getVariant(), x, y, scale);

        context.batcher.textShadow(font, this.variantToPlace.getLink().toString(), this.block.area.x, this.block.area.y - 12);
    }

    @Override
    public void renderInWorld(RenderingContext context)
    {
        if (!this.canBeSeen())
        {
            return;
        }

        Vector3f direction = context.getCamera().getMouseDirection();
        Vector3d position = context.getCamera().position;

        RayTracer.trace(this.result, this.proxy.getChunks(), position, direction, 128, true, this.rayHandle);

        if (this.result.type.isMissed())
        {
            Vector3d newPosition = new Vector3d(direction);

            newPosition.mul(this.noHitDistance);
            newPosition.add(position);

            this.result.type = RayTraceType.BLOCK;
            this.result.block.set((int) Math.floor(newPosition.x), (int) Math.floor(newPosition.y), (int) Math.floor(newPosition.z));
            this.result.normal.set(0, 0, 0);
            this.result.hit.set(newPosition);
            this.result.origin.set(position);
        }

        if (!this.result.type.isMissed())
        {
            this.result.normal.mul(this.hitDistance);
            Draw.renderBlockAABB(context, this.proxy.getChunks(), this.result.block.x, this.result.block.y, this.result.block.z);
            Draw.renderBlockAABB(context, this.proxy.getChunks(), this.result.block.x + this.result.normal.x, this.result.block.y + this.result.normal.y, this.result.block.z + this.result.normal.z);
            this.currentTool.render(context, this.result);
        }

        if (this.selected)
        {
            Vector3i size = this.selection.getSize();
            Vector3i min = this.selection.getMin();

            Draw.renderBox(context, min.x, min.y, min.z, size.x, size.y, size.z, 0, 0.5F, 1F);
        }
    }
}