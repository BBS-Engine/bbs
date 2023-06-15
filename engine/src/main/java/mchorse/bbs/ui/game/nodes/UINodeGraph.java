package mchorse.bbs.ui.game.nodes;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.utils.factory.IFactory;
import mchorse.bbs.game.utils.nodes.Node;
import mchorse.bbs.game.utils.nodes.NodeRelation;
import mchorse.bbs.game.utils.nodes.NodeSystem;
import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UICanvas;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.context.ContextMenuManager;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import mchorse.bbs.ui.utils.keys.Keybind;
import mchorse.bbs.utils.colors.Color;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.joml.Vectors;
import mchorse.bbs.utils.math.Interpolations;
import mchorse.bbs.utils.math.MathUtils;
import org.joml.Intersectionf;
import org.joml.Vector2d;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class UINodeGraph <T extends Node, D> extends UICanvas
{
    public static final IKey KEYS_CATEGORY = UIKeys.NODES_KEYS_EDITOR;
    public static final IKey ADD_CATEGORY = UIKeys.NODES_KEYS_ADD;

    public NodeSystem<T, D> system;

    private List<T> selected = new ArrayList<T>();
    private boolean lastSelected;
    private boolean selecting;
    private int lastNodeX;
    private int lastNodeY;

    private T output;
    private T input;

    private Color a = new Color();
    private Color b = new Color();

    private boolean notifyAboutMain;
    private long tick;
    private int average;
    private int prevAverage;

    private Consumer<T> callback;

    public UINodeGraph(IFactory<T, D> factory, Consumer<T> callback)
    {
        super();

        this.callback = callback;

        this.context((menu) ->
        {
            UIContext context = this.getContext();

            int x = (int) this.fromX(context.mouseX);
            int y = (int) this.fromY(context.mouseY);

            menu.action(Icons.ADD, UIKeys.NODES_CONTEXT_ADD, () ->
            {
                context.replaceContextMenu((adds) ->
                {
                    for (Link key : this.system.getFactory().getKeys())
                    {
                        IKey label = UIKeys.NODES_CONTEXT_ADD_NODE.format(IKey.lang("bbs.node_types." + key));
                        int color = this.getColor(key);

                        adds.action(Icons.ADD, label, color, () -> this.addNode(key, x, y));
                    }
                });
            });

            if (!this.selected.isEmpty())
            {
                menu.action(Icons.COPY, UIKeys.NODES_CONTEXT_COPY, this::copyNodes);
            }

            this.addPaste(menu, x, y);

            if (!this.selected.isEmpty())
            {
                menu.action(Icons.DOWNLOAD, UIKeys.NODES_CONTEXT_MAIN, this::markMain);
                menu.action(Icons.REVERSE, UIKeys.NODES_CONTEXT_SORT, this::sortInputs);
                menu.action(Icons.MINIMIZE, UIKeys.NODES_CONTEXT_TIE, this::tieSelected);
                menu.action(Icons.MAXIMIZE, UIKeys.NODES_CONTEXT_UNTIE, this::untieSelected);
                menu.action(Icons.REMOVE, UIKeys.NODES_CONTEXT_REMOVE, Colors.NEGATIVE, this::removeSelected);
            }
        });

        this.keys().register(Keys.NODES_TIE, this::tieSelected).inside().category(KEYS_CATEGORY);
        this.keys().register(Keys.NODES_UNTIE, this::untieSelected).inside().category(KEYS_CATEGORY);
        this.keys().register(Keys.NODES_MAIN, this::markMain).inside().category(KEYS_CATEGORY);
        this.keys().register(Keys.NODES_SORT, this::sortInputs).inside().category(KEYS_CATEGORY);

        int keycode = GLFW.GLFW_KEY_1;

        for (Link key : factory.getKeys())
        {
            KeyCombo combo = new KeyCombo(UIKeys.NODES_CONTEXT_ADD_NODE.format(UIKeys.C_NODE.get(key)), keycode, GLFW.GLFW_KEY_LEFT_CONTROL);
            Keybind keybind = this.keys().register(combo, () ->
            {
                UIContext context = this.getContext();

                this.addNode(key, (int) this.fromX(context.mouseX), (int) this.fromY(context.mouseY));
            });

            keybind.inside().category(ADD_CATEGORY);
            keycode += 1;
        }
    }

    public UINodeGraph<T, D> notifyAboutMain()
    {
        this.notifyAboutMain = true;

        return this;
    }

    /* Copy/paste */

    private void copyNodes()
    {
        MapType data = new MapType();
        ListType list = new ListType();
        MapType relations = new MapType();

        for (T node : this.selected)
        {
            MapType nodeMap = this.system.getFactory().toData(node);

            list.add(nodeMap);

            List<T> children = this.system.getChildren(node);

            for (T child : children)
            {
                if (this.selected.contains(child))
                {
                    ListType relation;
                    String key = node.getId().toString();

                    if (relations.has(key))
                    {
                        relation = relations.getList(key);
                    }
                    else
                    {
                        relation = new ListType();
                        relations.put(key, relation);
                    }

                    relation.addString(child.getId().toString());
                }
            }
        }

        data.put("nodes", list);
        data.put("relations", relations);
        Window.setClipboard(data, "_CopyNodes");
    }

    private void addPaste(ContextMenuManager menu, int x, int y)
    {
        MapType data = Window.getClipboardMap("_CopyNodes");

        if (data == null)
        {
            return;
        }

        ListType nodesList = data.getList("nodes");
        MapType relationsMap = data.getMap("relations");

        List<T> nodes = new ArrayList<T>();
        Map<String, T> mapping = new HashMap<String, T>();

        for (int i = 0; i < nodesList.size(); i++)
        {
            MapType nodeMap = nodesList.getMap(i);
            String id = nodeMap.getString("id");

            nodeMap.remove("id");

            T node = this.system.getFactory().fromData(nodeMap);

            mapping.put(id, node);
            nodes.add(node);
        }

        int nx = nodes.get(0).x;
        int ny = nodes.get(0).y;

        menu.action(Icons.PASTE, UIKeys.NODES_CONTEXT_PASTE, () ->
        {
            this.selected.clear();

            for (T node : nodes)
            {
                this.system.add(node);

                node.x = node.x - nx + x;
                node.y = node.y - ny + y;

                this.select(node, true);
            }

            for (String key : relationsMap.keys())
            {
                ListType relations = relationsMap.getList(key);
                T output = mapping.get(key);

                for (int i = 0; i < relations.size(); i++)
                {
                    T input = mapping.get(relations.getString(i));

                    if (output != null && input != null)
                    {
                        this.system.tie(output, input);
                    }
                }
            }
        });
    }

    /* CRUD */

    private void addNode(Link key, int x, int y)
    {
        T node = this.system.getFactory().create(key);

        if (node != null)
        {
            node.x = x;
            node.y = y;

            this.system.add(node);
            this.select(node);
        }
    }

    private void removeSelected()
    {
        for (T selected : this.selected)
        {
            this.system.remove(selected);
        }

        if (this.system.main != null && this.selected.contains(this.system.main))
        {
            this.system.main = null;
        }

        this.select(null);
    }

    private void tieSelected()
    {
        if (this.selected.size() <= 1)
        {
            return;
        }

        T last = this.selected.get(this.selected.size() - 1);
        List<T> nodes = new ArrayList<T>(this.selected);

        nodes.remove(last);
        nodes.sort(Comparator.comparingInt(a -> a.x));

        for (T node : nodes)
        {
            this.system.tie(last, node);
        }
    }

    private void untieSelected()
    {
        if (this.selected.isEmpty())
        {
            return;
        }

        if (this.selected.size() == 1)
        {
            this.system.relations.remove(this.selected.get(0).getId());
        }
        else if (this.selected.size() == 2)
        {
            /* Untying from both sides */
            T a = this.selected.get(0);
            T b = this.selected.get(1);

            this.system.untie(a, b);
            this.system.untie(b, a);
        }
        else
        {
            T last = this.selected.get(this.selected.size() - 1);

            for (int i = 0; i < this.selected.size() - 1; i++)
            {
                this.system.untie(last, this.selected.get(i));
            }
        }
    }

    private void markMain()
    {
        if (this.selected.isEmpty())
        {
            return;
        }

        this.system.main = this.selected.get(this.selected.size() - 1);
    }

    private void sortInputs()
    {
        if (this.selected.size() != 1)
        {
            return;
        }

        T node = this.selected.get(0);
        List<NodeRelation<T>> relations = this.system.relations.get(node.getId());

        if (relations != null)
        {
            relations.sort(Comparator.comparingInt(a -> a.input.x));
        }
    }

    public void setNode(T node)
    {
        if (this.callback != null)
        {
            this.callback.accept(node);
        }
    }

    public void select(T node)
    {
        this.select(node, false);
    }

    public void select(T node, boolean add)
    {
        if (!add)
        {
            this.selected.clear();
        }

        if (node != null)
        {
            this.selected.add(node);
        }

        this.setNode(node);
    }

    public Area getNodeArea(T node)
    {
        return this.getNodeArea(node, Area.SHARED);
    }

    public Area getNodeArea(T node, Area area)
    {
        int x1 = this.toX(node.x - 60);
        int y1 = this.toY(node.y - 35);
        int x2 = this.toX(node.x + 60);
        int y2 = this.toY(node.y + 35);

        area.setPoints(x1, y1, x2, y2);

        return area;
    }

    public Area getNodeOutletArea(Area nodeArea, boolean output)
    {
        int y = output ? 7 : -7;

        int x1 = nodeArea.mx() - 4;
        int y1 = nodeArea.y(output ? 1F : 0F) - 4 + y;
        int x2 = nodeArea.mx() + 4;
        int y2 = nodeArea.y(output ? 1F : 0F) + 4 + y;

        Area area = new Area();

        area.setPoints(x1, y1, x2, y2);

        return area;
    }

    public NodeRelation<T> getClosestRelation(T node)
    {
        NodeRelation<T> closest = null;
        float distance = Float.POSITIVE_INFINITY;
        Area area = this.getNodeArea(node);
        Area a = new Area();
        Area b = new Area();

        for (List<NodeRelation<T>> relations : this.system.relations.values())
        {
            for (NodeRelation<T> relation : relations)
            {
                if (relation.input == node || relation.output == node)
                {
                    return null;
                }

                int mx = (relation.input.x + relation.output.x) / 2;
                int my = (relation.input.y + relation.output.y) / 2;

                this.getNodeArea(relation.output, a);
                this.getNodeArea(relation.input, b);

                int dx = mx - node.x;
                int dy = my - node.y;
                float d = (float) Math.sqrt(dx * dx + dy * dy);

                int intersectionResult = Intersectionf.intersectLineSegmentAab(
                    a.mx(), a.my(), 0,
                    b.mx(), b.my(), 0,
                    area.x, area.y, -0.1F,
                    area.ex(), area.ey(), 0.1F,
                    Vectors.TEMP_2F
                );

                if (intersectionResult != Intersectionf.OUTSIDE && d < distance)
                {
                    distance = Math.min(d, distance);
                    closest = relation;
                }
            }
        }

        return closest;
    }

    public boolean isConnecting()
    {
        return this.output != null || this.input != null;
    }

    public void set(NodeSystem<T, D> system)
    {
        boolean same = this.system != null && system != null && this.system.getId().equals(system.getId());

        this.system = system;

        if (system != null && !same)
        {
            int x = system.main == null ? 0 : system.main.x;
            int y = system.main == null ? 0 : system.main.y;

            if (system.main == null && !system.nodes.isEmpty())
            {
                for (T node : system.nodes.values())
                {
                    x += node.x;
                    y += node.y;
                }

                x /= system.nodes.size();
                y /= system.nodes.size();
            }

            this.scaleX.setShift(x);
            this.scaleY.setShift(y);
            this.scaleX.setZoom(0.5F);
            this.scaleY.setZoom(0.5F);
        }

        if (same)
        {
            List<UUID> ids = this.selected.stream().map(Node::getId).collect(Collectors.toList());

            this.selected.clear();

            for (UUID uuid : ids)
            {
                this.selected.add(this.system.nodes.get(uuid));
            }

            this.setNode(this.selected.isEmpty() ? null : this.selected.get(this.selected.size() - 1));
        }
        else
        {
            this.selected.clear();
        }
    }

    @Override
    public boolean subMouseClicked(UIContext context)
    {
        if (this.system == null)
        {
            return false;
        }

        if (context.mouseButton == 0)
        {
            this.lastNodeX = (int) this.fromX(context.mouseX);
            this.lastNodeY = (int) this.fromY(context.mouseY);
            boolean shift = Window.isShiftPressed();
            boolean selected = false;
            List<T> nodes = new ArrayList<T>(this.system.nodes.values());

            Collections.reverse(nodes);

            for (T node : nodes)
            {
                Area nodeArea = this.getNodeArea(node);

                if (nodeArea.isInside(context))
                {
                    if (shift)
                    {
                        if (!this.selected.contains(node))
                        {
                            this.select(node, true);

                            selected = true;
                        }
                        else
                        {
                            this.selected.remove(node);
                            this.select(node, true);

                            selected = true;
                        }
                    }
                    else
                    {
                        if (!this.selected.contains(node))
                        {
                            this.select(node);
                        }

                        selected = true;
                    }

                    this.lastSelected = true;
                }
                else
                {
                    Area output = this.getNodeOutletArea(nodeArea, true);
                    Area input = this.getNodeOutletArea(nodeArea, false);

                    if (output.isInside(context))
                    {
                        this.output = node;
                    }
                    else if (input.isInside(context) && this.system.main != node)
                    {
                        this.input = node;
                    }

                    if (this.isConnecting())
                    {
                        return false;
                    }
                }
            }

            if (shift)
            {
                this.selecting = true;
            }
            else if (!selected)
            {
                this.select(null);
            }
        }

        return super.subMouseClicked(context);
    }

    @Override
    public boolean subMouseReleased(UIContext context)
    {
        if (this.isConnecting())
        {
            boolean output = this.output != null;

            for (T node : this.system.nodes.values())
            {
                Area nodeArea = this.getNodeArea(node);
                Area outlet = this.getNodeOutletArea(nodeArea, !output);

                if (outlet.isInside(context))
                {
                    if (output)
                    {
                        this.input = node;
                    }
                    else
                    {
                        this.output = node;
                    }

                    break;
                }
            }
        }

        if (this.selecting)
        {
            Area area = new Area();
            boolean wasSelected = !this.selected.isEmpty();

            area.setPoints(this.lastX, this.lastY, context.mouseX, context.mouseY);

            for (T node : this.system.nodes.values())
            {
                Area nodeArea = this.getNodeArea(node);

                if (nodeArea.intersects(area) && !this.selected.contains(node))
                {
                    this.selected.add(0, node);
                }
            }

            if (!wasSelected && !this.selected.isEmpty())
            {
                this.setNode(this.selected.get(this.selected.size() - 1));
            }
        }
        else if (this.output != null && this.input != null && this.input != this.output)
        {
            this.system.tie(this.output, this.input);
        }
        else if (this.selected.size() == 1)
        {
            T node = this.selected.get(0);
            NodeRelation<T> closest = this.getClosestRelation(node);

            if (closest != null)
            {
                this.system.untie(closest.output, closest.input);
                this.system.tie(closest.output, node);
                this.system.tie(node, closest.input);
            }
        }

        this.lastSelected = false;
        this.selecting = false;
        this.output = this.input = null;

        return super.subMouseReleased(context);
    }

    @Override
    protected void dragging(UIContext context)
    {
        super.dragging(context);

        if (this.dragging && this.mouse == 0 && this.lastSelected && !this.selected.isEmpty())
        {
            int lastNodeX = (int) this.fromX(context.mouseX);
            int lastNodeY = (int) this.fromY(context.mouseY);

            for (T node : this.selected)
            {
                node.x += lastNodeX - this.lastNodeX;
                node.y += lastNodeY - this.lastNodeY;
            }

            this.lastNodeX = lastNodeX;
            this.lastNodeY = lastNodeY;
        }
    }

    @Override
    public void render(UIContext context)
    {
        if (this.area.isInside(context) && !context.isFocused())
        {
            float steps = this.prevAverage <= 0 ? 1 : this.prevAverage;
            float step = 15 / steps;
            float x = Window.isKeyPressed(GLFW.GLFW_KEY_LEFT) ? -step : (Window.isKeyPressed(GLFW.GLFW_KEY_RIGHT) ? step : 0);
            float y = Window.isKeyPressed(GLFW.GLFW_KEY_UP) ? -step : (Window.isKeyPressed(GLFW.GLFW_KEY_DOWN) ? step : 0);

            if (x != 0)
            {
                this.scaleX.setShift(x / this.scaleX.getZoom() + this.scaleX.getShift());
            }

            if (y != 0)
            {
                this.scaleY.setShift(y / this.scaleY.getZoom() + this.scaleY.getShift());
            }

            /* Limiting speed, so it wouldn't go crazy fast for people who play on
             * absurd frame rates (like 300 or something like that) */
            this.average += 1;

            if (this.tick < context.getTick())
            {
                this.tick = context.getTick();
                this.prevAverage = this.average;
                this.average = 0;
            }
        }

        super.render(context);

        if (this.system.nodes.isEmpty())
        {
            int w = this.area.w / 2;

            context.batcher.wallText(context.font, UIKeys.NODES_INFO_EMPTY_NODES.get(), this.area.mx(w), this.area.my(), 0xffffff, w, 12, 0.5F, 0.5F);
        }
        else if (this.notifyAboutMain && this.system.main == null)
        {
            String label = UIKeys.NODES_INFO_EMPTY_MAIN.get();
            int w = context.font.getWidth(label);

            context.batcher.box(this.area.x + 4, this.area.y + 4, this.area.x + 24 + w, this.area.y + 20, Colors.A50);
            context.batcher.icon(Icons.EXCLAMATION, 0xffff0010, this.area.x + 4, this.area.y + 4);
            context.batcher.textShadow(label, this.area.x + 20, this.area.y + 8, 0xff0010);
        }
    }

    @Override
    protected void renderCanvas(UIContext context)
    {
        super.renderCanvas(context);

        if (this.system == null)
        {
            return;
        }

        int thickness = BBSSettings.nodeThickness.get();

        T lastSelected = this.selected.isEmpty() ? null : this.selected.get(this.selected.size() - 1);
        List<Vector2d> positions = new ArrayList<Vector2d>();

        /* Draw connections */
        if (thickness > 0)
        {
            LineBuilder<Integer> lines = new LineBuilder<Integer>(thickness / 2F);

            this.renderConnections(context, lines, positions, lastSelected);

            lines.render(context.batcher, (b, point) -> b.xy(point.x, point.y).rgba(Colors.COLOR.set(point.user)));
        }

        /* Draw node boxes */
        Area main = null;

        for (T node : this.system.nodes.values())
        {
            Area nodeArea = this.getNodeArea(node);

            if (nodeArea.w > 25)
            {
                this.renderOutlets(context, node, nodeArea);
            }

            boolean hover = Area.SHARED.isInside(context);
            int index = this.selected.indexOf(node);

            int colorBg = hover ? 0xff080808 : 0xff000000;
            int colorFg = 0xaa000000 + this.getColor(node);

            if (index >= 0)
            {
                int colorSh = index == this.selected.size() - 1 ? 0x0088ff : 0x0022aa;

                context.batcher.dropShadow(nodeArea.x + 4, nodeArea.y + 4, nodeArea.ex() - 4, nodeArea.ey() - 4, 8, 0xff000000 + colorSh, colorSh);
            }

            context.batcher.box(nodeArea.x + 1, nodeArea.y, nodeArea.ex() - 1, nodeArea.ey(), colorBg);
            context.batcher.box(nodeArea.x, nodeArea.y + 1, nodeArea.ex(), nodeArea.ey() - 1, colorBg);
            context.batcher.outline(nodeArea.x + 3, nodeArea.y + 3, nodeArea.ex() - 3, nodeArea.ey() - 3, colorFg);

            if (node == this.system.main)
            {
                main = new Area();
                main.copy(nodeArea);
            }
        }

        for (T node : this.system.nodes.values())
        {
            Area nodeArea = this.getNodeArea(node);
            String title = node.getTitle();

            if (!title.isEmpty() && nodeArea.w > 40)
            {
                title = title.replaceAll("\n", " ");

                if (title.length() > 37)
                {
                    title = title.substring(0, 37) + "§r...";
                }

                context.batcher.textCard(context.font, title, nodeArea.mx() - context.font.getWidth(title) / 2, nodeArea.my() - 4);
            }
        }

        /* Draw selected node's indices */
        for (int i = 0; i < positions.size(); i++)
        {
            Vector2d pos = positions.get(i);
            String label = String.valueOf(i);

            context.batcher.textShadow(label, (int) pos.x - context.font.getWidth(label) / 2, (int) pos.y - 4, this.getIndexLabelColor(lastSelected, i));
        }

        /* Draw main entry node icon */
        if (main != null)
        {
            context.batcher.outlinedIcon(Icons.DOWNLOAD, main.mx(), main.y - 4, 0.5F, 1F);
        }

        /* Draw selection */
        if (this.selecting)
        {
            context.batcher.box(this.lastX, this.lastY, context.mouseX, context.mouseY, Colors.setA(Colors.ACTIVE, 0.25F));
        }
    }

    protected abstract int getColor(Link type);

    protected abstract int getColor(T node);

    private void renderOutlets(UIContext context, T node, Area nodeArea)
    {
        Area output = this.getNodeOutletArea(nodeArea, true);
        Area input = this.getNodeOutletArea(nodeArea, false);

        boolean insideO = output.isInside(context);
        boolean insideI = input.isInside(context);

        int colorO = Colors.mulRGB(0xffffff, insideO ? 1F : 0.6F);
        int colorI = Colors.mulRGB(0xffffff, insideI ? 1F : 0.6F);

        if (this.output == node)
        {
            colorO = Colors.ACTIVE;

            if (insideI) colorI = Colors.NEGATIVE;
        }
        else if (this.output != null)
        {
            if (insideO) colorO = Colors.NEGATIVE;
            if (insideI) colorI = Colors.POSITIVE;
        }

        if (this.input == node)
        {
            colorI = Colors.ACTIVE;

            if (insideO) colorO = Colors.NEGATIVE;
        }
        else if (this.input != null)
        {
            if (insideI) colorI = Colors.NEGATIVE;
            if (insideO) colorO = Colors.POSITIVE;
        }

        context.batcher.outline(output.x, output.y, output.ex(), output.ey(), 0xff000000 + colorO);

        if (this.system.main != node)
        {
            context.batcher.outline(input.x, input.y, input.ex(), input.ey(), 0xff000000 + colorI);
        }
    }

    private void renderConnections(UIContext context, LineBuilder lines, List<Vector2d> positions, T lastSelected)
    {
        NodeRelation<T> closest = null;

        if (this.dragging && this.selected.size() == 1)
        {
            T node = this.selected.get(0);

            closest = this.getClosestRelation(node);
        }

        for (List<NodeRelation<T>> relations : this.system.relations.values())
        {
            for (int r = 0; r < relations.size(); r++)
            {
                NodeRelation<T> relation = relations.get(r);

                Area output = this.getNodeOutletArea(this.getNodeArea(relation.output), true);
                Area input = this.getNodeOutletArea(this.getNodeArea(relation.input), false);

                int x1 = input.mx();
                int y1 = input.my();
                int x2 = output.mx();
                int y2 = output.my();

                this.renderConnection(lines, context, relation.output, r, x1, y1, x2, y2, false, relation == closest);

                if (relation.output == lastSelected)
                {
                    positions.add(new Vector2d((x1 + x2) / 2F, (y1 + y2) / 2F));
                }
            }
        }

        if (this.isConnecting())
        {
            T node = this.output == null ? this.input : this.output;
            Area area = this.getNodeArea(node);
            Area outlet = this.getNodeOutletArea(area, node == this.output);

            int x1 = context.mouseX;
            int y1 = context.mouseY;
            int x2 = outlet.mx();
            int y2 = outlet.my();

            List<NodeRelation<T>> list = this.system.relations.get(node.getId());

            this.renderConnection(lines, context, node, list == null ? 0 : list.size(), x1, y1, x2, y2, true, false);
        }
    }

    /**
     * Draw the connection line
     */
    private void renderConnection(LineBuilder<Integer> lines, UIContext context, T node, int r, int x1, int y1, int x2, int y2, boolean forceLine, boolean selected)
    {
        float factor = context.getTickTransition() / 60F;
        final float segments = 8F;

        float opacity = this.getNodeActiveColorOpacity(node, r);
        int c1 = BBSSettings.nodePulseBackgroundPrimaryColor.get() ? BBSSettings.primaryColor.get() : BBSSettings.nodePulseBackgroundColor.get();
        int c2 = this.getNodeActiveColor(node, r);

        if (selected)
        {
            c1 = c2 = Colors.POSITIVE;
        }

        lines.push();

        for (int i = 0; i < segments; i ++)
        {
            float factor1 = i / segments;
            float factor2 = (i + 1) / segments;
            float color1 = 1 - MathUtils.clamp(Math.abs((1 - factor1) - (factor % 1)) / 0.2F, 0F, 1F);
            float color2 = 1 - MathUtils.clamp(Math.abs((1 - factor2) - (factor % 1)) / 0.2F, 0F, 1F);

            color1 = Math.max(color1, 1 - MathUtils.clamp(Math.abs(((1 - factor1) + 1) - (factor % 1)) / 0.2F, 0F, 1F));
            color2 = Math.max(color2, 1 - MathUtils.clamp(Math.abs(((1 - factor2) + 1) - (factor % 1)) / 0.2F, 0F, 1F));

            color1 = Math.max(color1, 1 - MathUtils.clamp(Math.abs(((1 - factor1) - 1) - (factor % 1)) / 0.2F, 0F, 1F));
            color2 = Math.max(color2, 1 - MathUtils.clamp(Math.abs(((1 - factor2) - 1) - (factor % 1)) / 0.2F, 0F, 1F));

            Colors.interpolate(this.a, c1, c2, color1, false);
            Colors.interpolate(this.b, c1, c2, color2, false);

            this.a.a = opacity;
            this.b.a = opacity;

            if (y2 <= y1 || forceLine)
            {
                lines.add(Interpolations.lerp(x1, x2, factor1), Interpolations.lerp(y1, y2, factor1), a.getARGBColor())
                    .add(Interpolations.lerp(x1, x2, factor2), Interpolations.lerp(y1, y2, factor2), b.getARGBColor());
            }
            else
            {
                if (i == segments / 2)
                {
                    lines.add(Interpolations.lerp(x1, x2, 0.5F), y1, a.getARGBColor())
                        .add(Interpolations.lerp(x1, x2, 0.5F), y2, b.getARGBColor());
                }
                else
                {
                    int y = i < segments / 2 ? y1 : y2;

                    lines.add(Interpolations.lerp(x1, x2, i == segments / 2 + 1 ? 0.5F : factor1), y, a.getARGBColor())
                        .add(Interpolations.lerp(x1, x2, i == segments / 2 - 1 ? 0.5F : factor2), y, b.getARGBColor());
                }
            }
        }
    }

    protected int getIndexLabelColor(T lastSelected, int i)
    {
        return 0xffffff;
    }

    protected int getNodeActiveColor(T output, int r)
    {
        return Colors.ACTIVE;
    }

    protected float getNodeActiveColorOpacity(T output, int r)
    {
        return 0.75F;
    }
}