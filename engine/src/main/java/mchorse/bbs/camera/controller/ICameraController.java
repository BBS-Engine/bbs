package mchorse.bbs.camera.controller;

import mchorse.bbs.camera.Camera;

public interface ICameraController
{
    public void setup(Camera camera, float transition);

    /**
     * Get camera controller priority. The camera controller with highest
     * priority will get picked.
     */
    public default int getPriority()
    {
        return 0;
    }
}