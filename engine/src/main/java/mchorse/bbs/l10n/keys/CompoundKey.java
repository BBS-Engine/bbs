package mchorse.bbs.l10n.keys;

import java.util.ArrayList;
import java.util.List;

public class CompoundKey implements IKey
{
    public List<IKey> keys;

    public CompoundKey(List<IKey> keys)
    {
        this.keys = new ArrayList<>(keys);
    }

    @Override
    public String get()
    {
        StringBuilder builder = new StringBuilder();

        for (IKey key : this.keys)
        {
            builder.append(key.get());
        }

        return builder.toString();
    }
}