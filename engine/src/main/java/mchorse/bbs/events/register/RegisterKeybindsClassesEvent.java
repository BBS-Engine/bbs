package mchorse.bbs.events.register;

import java.util.List;

public class RegisterKeybindsClassesEvent
{
    private final List<Class> classes;

    public RegisterKeybindsClassesEvent(List<Class> classes)
    {
        this.classes = classes;
    }

    public void register(Class clazz)
    {
        this.classes.add(clazz);
    }
}