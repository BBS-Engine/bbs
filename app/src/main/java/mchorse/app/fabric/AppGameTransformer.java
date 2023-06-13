package mchorse.app.fabric;

import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.game.patch.GameTransformer;

public class AppGameTransformer extends GameTransformer
{
    public AppGameTransformer(GamePatch... patches)
    {
        super(patches);
    }

    @Override
    public byte[] transform(String className)
    {
        return null;
    }
}