package mchorse.sandbox.fabric;

import net.fabricmc.loader.impl.game.patch.GamePatch;
import net.fabricmc.loader.impl.game.patch.GameTransformer;

public class SandboxGameTransformer extends GameTransformer
{
    public SandboxGameTransformer(GamePatch... patches)
    {
        super(patches);
    }

    @Override
    public byte[] transform(String className)
    {
        return null;
    }
}