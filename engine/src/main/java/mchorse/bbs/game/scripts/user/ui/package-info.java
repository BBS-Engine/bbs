/**
 * Welcome to BBS' in-game scripting documentation!
 *
 * <p>Scripts are JavaScript (ES 5.1) programs that allow you to program game
 * logic.</p>
 *
 * <p>Scripts are stored in game engine's <code>data/scripts/</code> folder.
 * The filename without extension of the script is its ID. Depending on
 * the configuration of the script, there might be an additional JSON file
 * with extra non-code data.</p>
 *
 * <p>Here are global variable(s) that are provided by BBS:</p>
 *
 * <ul>
 *     <li><code>bbs</code>, it's a {@link mchorse.bbs.game.scripts.user.IScriptBBS}.
 *     It allows you to create and query different data structures.</li>
 * </ul>
 *
 * <p>There are currently two packages available in BBS:</p>
 *
 * <ul>
 *     <li><code>Scripting API</code> section covers functions that allow you to interact with
 *     game engine's mechanics to some extent.</li>
 *     <li><code>UI API</code> section covers functions that allow you to create custom user
 *     intrfaces (UI) which can be used for plethora of things.</li>
 * </ul>
 *
 * <p>Big thanks to TorayLife for scripting API suggestions.</p>
 */

package mchorse.bbs.game.scripts.user.ui;