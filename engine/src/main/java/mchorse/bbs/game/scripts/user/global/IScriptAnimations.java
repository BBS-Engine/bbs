package mchorse.bbs.game.scripts.user.global;

/**
 * Script animations.
 *
 * <p>You can access it in the script as <code>bbs.animations</code> or <code>bbs.getAnimations()</code>.
 */
public interface IScriptAnimations
{
    /**
     * Play an animation by ID.
     *
     * <pre>{@code
     *    bbs.animations.play("epic_fight_scene");
     * }</pre>
     *
     * @return Whether animation was started.
     */
    public boolean play(String id);

    /**
     * Check whether given animation is playing.
     */
    public boolean isPlaying(String id);

    /**
     * Stop an animation playing by given ID.
     *
     * <pre>{@code
     *    bbs.animations.stop("epic_fight_scene");
     * }</pre>
     */
    public void stop(String id);
}
