package mchorse.bbs.game.scripts.user.global;

/**
 * Script camera.
 *
 * <p>You can access it in the script as <code>bbs.camera</code> or <code>bbs.getCamera()</code>.
 */
public interface IScriptCamera
{
    /**
     * Play a camera profile by given ID.
     *
     * <pre>{@code
     *    if (bbs.camera.play("test"))
     *    {
     *        bbs.send("Camera profile \"test\" has played!");
     *    }
     *    else
     *    {
     *        bbs.send("Either camera profile \"test\" doesn't exist or another camera profile is playing!");
     *    }
     * }</pre>
     */
    public default boolean play(String profile)
    {
        return this.play(profile, false);
    }

    /**
     * Play a camera profile by given ID. Optionally, you can force
     * playing that camera profile with a second boolean argument.
     *
     * <pre>{@code
     *    bbs.camera.play("test", true);
     * }</pre>
     */
    public boolean play(String profile, boolean force);

    /**
     * Checks whether a camera profile is currently playing.
     */
    public boolean isPlaying();

    /**
     * Stop currently played camera profile.
     *
     * <pre>{@code
     *    if (bbs.camera.isPlaying())
     *    {
     *        bbs.camera.stop();
     *    }
     * }</pre>
     */
    public void stop();

    /**
     * Lock the camera. This allows to control the camera with other methods in this
     * camera API. The player still can control their character. The default camera
     * position (if not changed with other methods) is whatever was camera's position
     * at the moment of locking.
     *
     * <pre>{@code
     *    bbs.camera.lock();
     * }</pre>
     */
    public void lock();

    /**
     * Check whether camera is currently locked.
     */
    public boolean isLocked();

    /**
     * Place locked camera at given position (XYZ) with given camera angles (in degrees).
     * The camera must be locked in order for it to work.
     *
     * <pre>{@code
     *    bbs.camera.lock();
     *    bbs.camera.set(0, 4, 0, 15, 0, 0, 70);
     * }</pre>
     */
    public void set(double x, double y, double z, float yaw, float pitch, float roll, float fov);

    /**
     * Unlocks the camera.
     *
     * <pre>{@code
     *    if (bbs.camera.isLocked())
     *    {
     *        bbs.camera.unlock();
     *    }
     * }</pre>
     */
    public void unlock();
}