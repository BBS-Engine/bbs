package mchorse.bbs.ui.framework.elements.input;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.keys.Keybind;
import mchorse.bbs.utils.colors.Colors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIKeybinds extends UIScrollView
{
    public Map<String, KeybindCategory> keybinds = new HashMap<String, KeybindCategory>();

    public UIKeybinds()
    {
        super();

        this.markContainer();
        this.scroll.opposite = true;
        this.scroll.cancelScrolling();
    }

    public void addKeybind(Keybind keybind)
    {
        IKey categoryKey = keybind.getCategory();
        KeybindCategory category = this.keybinds.get(categoryKey.get());

        if (category == null)
        {
            category = new KeybindCategory(categoryKey);
            this.keybinds.put(categoryKey.get(), category);
        }

        category.add(keybind);
    }

    @Override
    public void render(UIContext context)
    {
        int cx = 40;

        context.draw.box(this.area.x, this.area.y, cx, this.area.ey(), Colors.A75);
        context.draw.gradientHBox(cx, this.area.y, this.area.ex(), this.area.ey(), Colors.A75, 0);

        super.render(context);
    }

    @Override
    protected void preRender(UIContext context)
    {
        super.preRender(context);

        int x = this.area.x + 10;
        int y = this.area.y + 10;
        int i = 0;

        KeybindCategory general = this.keybinds.get("");

        i = general == null ? i : general.render(context, x, y, i) + 10;

        for (KeybindCategory category : this.keybinds.values())
        {
            if (category != general)
            {
                i = category.render(context, x, y, i) + 10;
            }
        }

        this.keybinds.clear();
        this.scroll.scrollSize = i + 3;
        this.scroll.clamp();
    }

    public static class KeybindCategory
    {
        public IKey title;
        public List<Keybind> keybinds = new ArrayList<Keybind>();
        public boolean shouldClean;

        public KeybindCategory(IKey title)
        {
            this.title = title;
        }

        public void add(Keybind keybind)
        {
            if (this.shouldClean)
            {
                this.keybinds.clear();
                this.shouldClean = false;
            }

            this.keybinds.add(keybind);
        }

        public int render(UIContext context, int x, int y, int i)
        {
            int color = Colors.A100 | BBSSettings.primaryColor.get();

            String title = this.title.get();

            if (!title.isEmpty())
            {
                context.draw.box(x - 10, y + i - 2, x + context.font.getWidth(title) + 2, y + i + context.font.getHeight() + 2, color);
                context.font.render(context.render, title, x, y + i);
                i += 14;
            }

            for (Keybind keybind : this.keybinds)
            {
                String combo = keybind.getKeyCombo();
                int w = context.font.getWidth(combo);

                context.draw.box(x - 2, y + i - 2, x + w + 2, y + i + context.font.getHeight() + 2, color);
                context.font.render(context.render, combo, x, y + i);
                context.font.renderWithShadow(context.render, keybind.getLabel().get(), x + w + 5, y + i);
                i += 14;
            }

            return i;
        }
    }
}