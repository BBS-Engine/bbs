/**
 * UI API is an Application Programming Inteface (API) which allows you
 * to create custom graphical User Intefaces (UI). It could be used for
 * plethora of things:
 *
 * <p>Custom dialogue system, disabling user input, diaries (discovering
 * notes, logs, lore fragments, etc.), mini-games, unlocking doors,
 * point-and-click games, player creation screen, custom administration
 * tools, custom quest quest offers, and so on.</p>
 *
 * <p><b>All</b> UI components are based off {@link mchorse.bbs.game.scripts.ui.components.UIComponent},
 * therefore they all have UIComponent's methods.</p>
 *
 * <p>Here is a really basic example that drops an item upon pressing
 * a button:</p>
 *
 * <pre>{@code
 *    function main(c)
 *    {
 *        var ui = bbs.ui.create(c, "handler").background();
 *        var button = ui.button("Press me!").id("button");
 *
 *        button.rxy(0.5, 0.5).wh(160, 20).anchor(0.5);
 *        bbs.ui.open(ui);
 *    }
 *
 *    function handler(c)
 *    {
 *        var uiContext = bbs.ui.getUIContext();
 *
 *        if (uiContext.getLast() === "button")
 *        {
 *            bbs.send("You pressed a button!");
 *        }
 *    }
 * }</pre>
 */

package mchorse.bbs.game.scripts.ui.components;