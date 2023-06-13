package mchorse.bbs.bridge;

import mchorse.bbs.core.Engine;

/**
 * Bridge interface.
 *
 * This interface provides all the necessary objects and resources may
 * be needed by the engine code, without coupling app code in the
 * game engine.
 */
public interface IBridge
{
    public Engine getEngine();

    public <T> T get(Class<T> apiInterface);
}