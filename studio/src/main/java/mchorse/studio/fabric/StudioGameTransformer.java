package mchorse.studio.fabric;

import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.game.patch.GameTransformer;

public class StudioGameTransformer extends GameTransformer
{
    public StudioGameTransformer(GamePatch... patches)
    {
        super(patches);
    }

    @Override
    public byte[] transform(String className)
    {
        return null;
    }
}