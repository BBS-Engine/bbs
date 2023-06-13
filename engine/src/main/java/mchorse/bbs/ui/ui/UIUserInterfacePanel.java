package mchorse.bbs.ui.ui;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.UserInterface;
import mchorse.bbs.game.scripts.ui.UserInterfaceContext;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.components.UIParentComponent;
import mchorse.bbs.game.scripts.ui.utils.UIRootComponent;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UIRenderable;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.ui.components.UIComponentPanel;
import mchorse.bbs.ui.utils.Area;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Pair;
import mchorse.bbs.utils.Timer;
import mchorse.bbs.utils.colors.Colors;

import java.util.List;

public class UIUserInterfacePanel extends UIDataDashboardPanel<UserInterface>
{
    public UIElement root;
    public UIElement editorPane;
    public UIUITreeList uiList;
    public UIComponentPanel panel;

    public UIToggle background;
    public UIToggle closable;
    public UIButton script;
    public UITextbox function;

    private UserInterfaceContext uiContext;
    private UIComponent component;

    private Timer timer = new Timer(100);

    public UIUserInterfacePanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.editorPane = new UIElement();
        this.editorPane.markContainer().relative(this.iconBar).w(200).h(1F).anchorX(1F);

        this.uiList = new UIUITreeList((l) -> this.pickComponent(l.get(0).component, false));
        this.uiList.background().relative(this.editorPane).w(1F).h(16 * 8);
        this.uiList.context((menu) ->
        {
            menu.shadow();

            boolean isParent = this.component instanceof UIParentComponent;
            boolean isRoot = this.component instanceof UIRootComponent;

            if (isParent)
            {
                menu.action(Icons.ADD, UIKeys.UI_CONTEXT_ADD, this::addComponents);
            }

            if (!isRoot)
            {
                menu.action(Icons.COPY, UIKeys.UI_CONTEXT_COPY, () ->
                {
                    Window.setClipboard(BBS.getFactoryUIComponents().toData(this.component), "_UIComponentCopy");
                });
            }

            if (isParent)
            {
                MapType data = Window.getClipboardMap("_UIComponentCopy");

                if (data != null)
                {
                    menu.action(Icons.PASTE, UIKeys.UI_CONTEXT_PASTE, () -> this.pasteComponent(data));
                }
            }

            if (!isRoot)
            {
                menu.action(Icons.REMOVE, UIKeys.UI_CONTEXT_REMOVE, this::removeComponent);
            }
        });

        this.background = new UIToggle(UIKeys.UI_OPTIONS_BACKGROUND, (b) -> this.data.background = b.getValue());
        this.background.tooltip(UIKeys.UI_OPTIONS_BACKGROUND_TOOLTIP);

        this.closable = new UIToggle(UIKeys.UI_OPTIONS_CLOSABLE, (b) -> this.data.closable = b.getValue());
        this.closable.tooltip(UIKeys.UI_OPTIONS_CLOSABLE_TOOLTIP);

        this.script = new UIButton(ContentType.SCRIPTS.getPickLabel(), (b) ->
        {
            UIDataUtils.openPicker(this.getContext(), ContentType.SCRIPTS, this.data.script, (str) -> this.data.script = str);
        });

        this.function = new UITextbox(40, (str) -> this.data.function = str);

        this.addOptions();
        this.options.fields.add(this.background, this.closable);
        this.options.fields.add(UI.label(UIKeys.UI_OPTIONS_SCRIPT).marginTop(8), this.script);
        this.options.fields.add(UI.label(UIKeys.UI_OPTIONS_FUNCTION).marginTop(4), this.function);

        this.editorPane.add(new UIRenderable(this::renderEditorPaneBackground), this.uiList);
        this.editor.w(1F, -200);
        this.editor.add(this.editorPane);
        this.add(this.root);

        this.overlay.namesList.setFileIcon(Icons.MORE);

        this.fill(null);
    }

    private void addComponents()
    {
        this.getContext().replaceContextMenu((subMenu) ->
        {
            for (Link link : BBS.getFactoryUIComponents().getKeys())
            {
                subMenu.shadow().action(Icons.ADD, UIKeys.UI_CONTEXT_ADD_A.format(UIKeys.C_UI_COMPONENTS.get(link)), () ->
                {
                    UIComponent newComponent = BBS.getFactoryUIComponents().create(link);

                    newComponent.w.offset = 20;
                    newComponent.h.offset = 20;
                    this.component.getChildComponents().add(newComponent);
                    this.needsUpdate();

                    this.uiList.fill(this.data);
                    this.pickComponent(newComponent, true);
                });
            }
        });
    }

    private void pasteComponent(MapType data)
    {
        UIComponent newComponent = BBS.getFactoryUIComponents().fromData(data);

        if (newComponent != null)
        {
            this.component.getChildComponents().add(newComponent);
            this.needsUpdate();

            this.uiList.fill(this.data);
            this.pickComponent(newComponent, true);
        }
    }

    private void removeComponent()
    {
        for (UIUITreeList.UILeaf leaf : this.uiList.getList())
        {
            if (leaf.component.getChildComponents().contains(this.component))
            {
                leaf.component.getChildComponents().remove(this.component);
                this.needsUpdate();

                this.uiList.fill(this.data);
                this.pickComponent(leaf.component, true);

                break;
            }
        }
    }

    private void pickComponent(UIComponent component, boolean select)
    {
        this.component = component;

        if (this.panel != null)
        {
            this.panel.removeFromParent();
        }

        if (component instanceof UIRootComponent)
        {
            return;
        }

        try
        {
            Class<? extends UIComponentPanel> clazz = BBS.getFactoryUIComponents().getData(component);
            UIComponentPanel panel = clazz.getConstructor(UIUserInterfacePanel.class).newInstance(this);
            int scroll = 0;

            if (this.panel != null)
            {
                this.panel.removeFromParent();

                scroll = this.panel.scroll.scroll;
            }

            this.panel = panel;
            this.panel.fill(component);
            this.panel.relative(this.uiList).y(1F).w(1F).hTo(this.editorPane.area, 1F);

            this.editorPane.add(this.panel);
            this.editorPane.resize();

            this.panel.scroll.scrollTo(scroll);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (select)
        {
            this.uiList.setCurrentScroll(component);
        }
    }

    public void needsUpdate()
    {
        this.timer.mark();
    }

    private void rebuildUI()
    {
        if (this.root != null)
        {
            this.root.removeFromParent();
        }

        this.root = data.root.create(this.uiContext);
        this.root.relative(this.editor).full();
        this.root.setEnabled(false);

        this.editor.prepend(this.root);
        this.editor.resize();
    }

    @Override
    public boolean needsBackground()
    {
        return this.data == null ? super.needsBackground() : this.data.background;
    }

    @Override
    protected IKey getTitle()
    {
        return UIKeys.UI_TITLE;
    }

    @Override
    public ContentType getType()
    {
        return ContentType.UIS;
    }

    @Override
    public void fill(UserInterface data)
    {
        super.fill(data);

        this.editorPane.setVisible(data != null);

        if (data != null)
        {
            this.uiContext = new UserInterfaceContext(data);

            this.background.setValue(data.background);
            this.closable.setValue(data.closable);
            this.function.setText(data.function);

            this.uiList.fill(data);
            this.pickComponent(data.root, true);
            this.rebuildUI();
        }
    }

    @Override
    protected boolean subMouseClicked(UIContext context)
    {
        Pair<UIElement, UIElement> hovered = this.getHoverElement(context);

        if (hovered.a != null && context.mouseButton == 0)
        {
            this.pickComponent((UIComponent) hovered.a.getCustomValue("component"), true);

            return true;
        }

        return super.subMouseClicked(context);
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        if (this.editorPane.isVisible())
        {
            this.editorPane.area.render(context.draw, Colors.A50);
        }

        super.renderBackground(context);
    }

    private void renderEditorPaneBackground(UIContext context)
    {
        Pair<UIElement, UIElement> hovered = this.getHoverElement(context);

        if (hovered.a != null)
        {
            Area area = hovered.a.area;

            context.draw.outline(area.x - 1, area.y - 1, area.ex() + 1, area.ey() + 1, Colors.A100);
            context.draw.box(area.x, area.y, area.ex(), area.ey(), Colors.setA(Colors.ACTIVE, 0.5F));
        }

        if (hovered.b != null && hovered.b != hovered.a)
        {
            Area area = hovered.b.area;

            context.draw.outline(area.x - 1, area.y - 1, area.ex() + 1, area.ey() + 1, Colors.A100);
            context.draw.box(area.x, area.y, area.ex(), area.ey(), Colors.setA(Colors.POSITIVE, 0.25F));
        }

        if (this.timer.checkReset())
        {
            context.render.postRunnable(this::rebuildUI);
        }

        if (this.editorPane.canBeSeen())
        {
            this.editorPane.area.render(context.draw, Colors.A25);
        }
    }

    private Pair<UIElement, UIElement> getHoverElement(UIContext context)
    {
        UIElement hovered = null;
        UIElement selected = null;

        if (this.root != null)
        {
            List<UIElement> children = this.root.getChildren(UIElement.class);

            for (UIElement element : children)
            {
                Object value = element.getCustomValue("component");

                if (element.area.isInside(context) && value instanceof UIComponent)
                {
                    hovered = element;
                }
                if (value == this.component)
                {
                    selected = element;
                }
            }
        }

        return new Pair<UIElement, UIElement>(hovered, selected);
    }
}