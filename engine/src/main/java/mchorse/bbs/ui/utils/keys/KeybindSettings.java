package mchorse.bbs.ui.utils.keys;

import mchorse.bbs.BBS;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.settings.values.ValueKeyCombo;
import mchorse.bbs.events.register.RegisterKeybindsClassesEvent;
import mchorse.bbs.ui.Keys;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeybindSettings
{
    private static final List<Class> classes = new ArrayList<>();

    public static void registerClasses()
    {
        classes.add(Keys.class);

        BBS.events.post(new RegisterKeybindsClassesEvent(classes));
    }

    public static void register(SettingsBuilder builder)
    {
        Map<String, List<KeyCombo>> combos = new HashMap<>();

        for (Class clazz : classes)
        {
            readKeyCombos(combos, clazz);
        }

        List<String> keys = new ArrayList<>(combos.keySet());

        keys.sort(Comparator.comparing((a) -> a));


        for (String key : keys)
        {
            List<KeyCombo> comboList = combos.get(key);

            builder.category(key);

            for (KeyCombo combo : comboList)
            {
                builder.register(new ValueKeyCombo(combo.label.getKey(), combo));
            }
        }
    }

    private static void readKeyCombos(Map<String, List<KeyCombo>> combos, Class clazz)
    {
        for (Field field : clazz.getDeclaredFields())
        {
            if (field.getType() != KeyCombo.class)
            {
                continue;
            }

            try
            {
                KeyCombo combo = (KeyCombo) field.get(null);
                List<KeyCombo> comboList = combos.computeIfAbsent(combo.categoryKey, (k) -> new ArrayList<>());

                comboList.add(combo);
            }
            catch (Exception e)
            {}
        }
    }
}