package mchorse.bbs.game.scripts.ui;

import mchorse.bbs.BBSData;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.ui.framework.UIBaseMenu;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.ui.IUIChangesHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserInterfaceContext
{
    public MapType data = new MapType();
    public Object object;
    public UserInterface ui;
    public IUIChangesHandler changesHandler;

    private String script = "";
    private String function = "";

    private Map<String, UIElement> elements;

    private Set<String> reservedData;

    private String last = "";
    private boolean closed;
    private String hotkey = "";
    private String context = "";
    private Long dirty;

    public static UserInterfaceContext create(UserInterface ui, Object object)
    {
        String function = ui.script.isEmpty() ? ui.function : (ui.function.isEmpty() ? "main" : ui.function);
        UserInterfaceContext uiContext = new UserInterfaceContext(ui, object, ui.script, function);

        uiContext.clearChanges();

        return uiContext;
    }

    public UserInterfaceContext(UserInterface ui)
    {
        this.ui = ui;
    }

    public UserInterfaceContext(UserInterface ui, Object object, String script, String function)
    {
        this.ui = ui;
        this.object = object;
        this.script = script == null ? "" : script;
        this.function = function == null ? "" : function;
    }

    public void setup(UIBaseMenu menu)
    {
        this.changesHandler = (IUIChangesHandler) menu;

        UIElement area = this.ui.root.create(this);

        area.relative(menu.main).full();
        menu.main.add(area);
    }

    /* Data sync code */

    public UIComponent getById(String id)
    {
        return this.getByIdRecursive(id, this.ui.root);
    }

    private UIComponent getByIdRecursive(String id, UIComponent component)
    {
        for (UIComponent child : component.getChildComponents())
        {
            if (child.id.equals(id))
            {
                return child;
            }

            UIComponent result = this.getByIdRecursive(id, child);

            if (result != null)
            {
                return result;
            }
        }

        return null;
    }

    public void clearChanges()
    {
        this.clearChangesRecursive(this.ui.root);
    }

    private void clearChangesRecursive(UIComponent component)
    {
        for (UIComponent child : component.getChildComponents())
        {
            child.clearChanges();

            this.clearChangesRecursive(child);
        }
    }

    public MapType compileChanges()
    {
        MapType data = new MapType();

        this.compileChangesRecursive(data, this.ui.root);

        return data;
    }

    private void compileChangesRecursive(MapType data, UIComponent component)
    {
        for (UIComponent child : component.getChildComponents())
        {
            if (!child.id.isEmpty())
            {
                this.compileComponent(data, child);
            }

            this.compileChangesRecursive(data, child);
        }
    }

    private void compileComponent(MapType data, UIComponent component)
    {
        Set<String> changes = component.getChanges();

        if (changes.isEmpty())
        {
            return;
        }

        MapType full = component.toData();
        MapType partial = new MapType();

        for (String key : changes)
        {
            if (full.has(key))
            {
                partial.put(key, full.get(key));
            }
        }

        data.put(component.id, partial);
    }

    public void populateDefaultData()
    {
        this.populateDefaultDataRecursive(this.ui.root);
    }

    private void populateDefaultDataRecursive(UIComponent component)
    {
        for (UIComponent child : component.getChildComponents())
        {
            child.populateData(this.data);

            this.populateDefaultDataRecursive(child);
        }
    }

    /* Getters */

    public String getLast()
    {
        return this.last;
    }

    public String getHotkey()
    {
        return this.hotkey;
    }

    public String getContext()
    {
        return this.context;
    }

    public boolean isClosed()
    {
        return this.closed;
    }

    public boolean isDirty()
    {
        if (this.dirty == null)
        {
            return false;
        }

        return System.currentTimeMillis() >= this.dirty;
    }

    public boolean isDirtyInProgress()
    {
        return this.dirty != null;
    }

    /* Client side code */

    public void registerElement(String id, UIElement element, boolean reserved)
    {
        if (this.elements == null)
        {
            this.elements = new HashMap<String, UIElement>();
        }

        this.elements.put(id, element);

        if (reserved)
        {
            if (this.reservedData == null)
            {
                this.reservedData = new HashSet<String>();
            }

            this.reservedData.add(id);
        }
    }

    public UIElement getElement(String target)
    {
        return this.elements == null ? null : this.elements.get(target);
    }

    public <T extends UIElement> T getElement(String target, Class<T> clazz)
    {
        UIElement element = this.getElement(target);

        return element == null || element.getClass() != clazz ? null : clazz.cast(element);
    }

    public void sendKey(String action)
    {
        if (this.dirty != null)
        {
            this.sendToServer();
        }
        else
        {
            MapType data = new MapType();

            data.putString("hotkey", action);

            this.handleNewData(data);
        }
    }

    public void sendContext(String action)
    {
        if (this.dirty != null)
        {
            this.sendToServer();
        }
        else
        {
            MapType data = new MapType();

            data.putString("context", action);

            this.handleNewData(data);
        }
    }

    public void dirty(String id, long delay)
    {
        this.last = id;

        if (delay <= 0)
        {
            this.dirty = null;
            this.sendToServer();
        }
        else
        {
            this.dirty = System.currentTimeMillis() + delay;
        }
    }

    public void sendToServer()
    {
        this.dirty = null;

        MapType data = new MapType();

        data.put("data", this.data);
        data.putString("last", this.last);
        data.putString("hotkey", this.hotkey);
        data.putString("context", this.context);

        MapType oldData = this.data;

        this.data = new MapType();

        if (this.reservedData != null)
        {
            for (String key : this.reservedData)
            {
                if (!oldData.has(key))
                {
                    continue;
                }

                this.data.put(key, oldData.get(key));
            }
        }

        this.hotkey = "";

        this.handleNewData(data);
    }

    /* Server side code */

    public void handleNewData(MapType data)
    {
        this.data.combine(data.getMap("data"));
        this.last = data.getString("last");
        this.hotkey = data.getString("hotkey");
        this.context = data.getString("context");

        if (this.handleScript())
        {
            this.sendToPlayer();
        }
        else
        {
            this.clearChanges();
        }
    }

    public void sendToPlayer()
    {
        MapType changes = this.compileChanges();

        if (!changes.keys().isEmpty())
        {
            this.changesHandler.handleUIChanges(changes);
        }

        this.clearChanges();
    }

    public void close()
    {
        if (this.isDirtyInProgress())
        {
            this.sendToPlayer();
        }

        this.closed = true;
        this.last = "";

        this.handleScript();
    }

    private boolean handleScript()
    {
        if (this.script.isEmpty() || this.function.isEmpty())
        {
            return false;
        }

        try
        {
            BBSData.getScripts().execute(this.script, this.function, new DataContext(this.object));

            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }
}