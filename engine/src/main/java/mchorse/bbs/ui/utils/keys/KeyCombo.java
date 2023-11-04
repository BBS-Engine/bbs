package mchorse.bbs.ui.utils.keys;

import mchorse.bbs.l10n.keys.IKey;

import java.util.ArrayList;
import java.util.List;

public class KeyCombo
{
    public String id = "";
    public IKey label;
    public IKey category = IKey.EMPTY;
    public String categoryKey = "all";
    public boolean repeatable;
    public List<Integer> keys = new ArrayList<>();

    public KeyCombo(String id, IKey label, int... keys)
    {
        this(label, keys);

        this.id = id;
    }

    public KeyCombo(IKey label, int... keys)
    {
        this.label = label;

        this.set(keys);
    }

    private void set(int... keys)
    {
        this.keys.clear();

        for (int key : keys)
        {
            this.keys.add(key);
        }
    }

    public KeyCombo repeatable()
    {
        this.repeatable = true;

        return this;
    }

    public KeyCombo category(IKey category)
    {
        this.category = category;

        return this;
    }

    public KeyCombo categoryKey(String categoryKey)
    {
        this.categoryKey = categoryKey;

        return this;
    }

    public int getMainKey()
    {
        return this.keys.isEmpty() ? -1 : this.keys.get(0);
    }

    public String getKeyCombo()
    {
        StringBuilder label = new StringBuilder(KeyCodes.getName(this.getMainKey()));

        for (int i = 1; i < this.keys.size(); i++)
        {
            label.insert(0, KeyCodes.getName(this.keys.get(i)) + " + ");
        }

        return label.toString();
    }

    public void copy(KeyCombo combo)
    {
        this.keys.clear();
        this.keys.addAll(combo.keys);
    }
}