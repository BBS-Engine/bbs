package mchorse.bbs.ui.framework.elements.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager
{
    protected Map<Class, List<Consumer>> events = new HashMap<Class, List<Consumer>>();

    public <T extends UIEvent> void register(Class<T> event, Consumer<T> callback)
    {
        if (callback == null)
        {
            return;
        }

        List<Consumer> events = this.events.computeIfAbsent(event, (k) -> new ArrayList<Consumer>());

        events.add(callback);
    }

    public void emit(UIEvent event)
    {
        List<Consumer> events = this.events.get(event.getClass());

        if (events != null)
        {
            for (Consumer callback : events)
            {
                callback.accept(event);
            }
        }
    }

    public void remove(Class event)
    {
        this.events.remove(event);
    }
}