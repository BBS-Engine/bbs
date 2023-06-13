package mchorse.bbs.game.scripts.user.global;

/**
 * Script client.
 *
 * <p>You can access it in the script as <code>bbs.client</code> or <code>bbs.getClient()</code>.
 */
public interface IScriptClient
{
    /**
     * Reload all client components.
     *
     * <pre>{@code
     *    bbs.client.reload();
     * }</pre>
     */
    public default void reload()
    {
        this.reload(null);
    }

    /**
     * <p>Reload specific client's component or all of them. Available arguments:</p>
     *
     * <ul>
     *     <li><code>textures</code> - reload texture manager.</li>
     *     <li><code>language</code> - reload language strings.</li>
     *     <li><code>models</code> - reload loaded models.</li>
     * </ul>
     *
     * <pre>{@code
     *    bbs.client.reload("textures");
     * }</pre>
     */
    public void reload(String component);

    /**
     * Set window's size.
     *
     * <pre>{@code
     *    bbs.client.setWindowSize(640, 480);
     * }</pre>
     */
    public void setWindowSize(int w, int h);

    /**
     * Start a video playback.
     *
     * @param path Path to resource within asset provider system like <code>assets:videos/funny_cat.mp4</code>
     * @return Whether video was successfully started to play.
     */
    public boolean playVideo(String path);

    /**
     * Stop video playback.
     *
     * @param path Path to the resource that was already loaded by playVideo method.
     * @return Whether video was stopped.
     */
    public boolean stopVideo(String path);
}