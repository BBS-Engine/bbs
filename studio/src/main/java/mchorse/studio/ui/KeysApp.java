package mchorse.studio.ui;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.utils.keys.KeyCombo;
import org.lwjgl.glfw.GLFW;

/**
 * IF THE KEYS DON'T APPEAR IN THE CONFIGURATION MENU, you used wrong constructor!
 * Use {@link KeyCombo#KeyCombo(String, IKey, int...)} intead of {@link KeyCombo#KeyCombo(IKey, int...)}!
 */
public class KeysApp
{
    public static final KeyCombo WELCOME = new KeyCombo("welcome", UIKeysApp.WELCOME_TITLE, GLFW.GLFW_KEY_BACKSLASH).categoryKey("welcome");
    public static final KeyCombo PREV_PAGE = new KeyCombo("prev_page", UIKeysApp.WELCOME_KEYS_PREV, GLFW.GLFW_KEY_LEFT).categoryKey("welcome");
    public static final KeyCombo NEXT_PAGE = new KeyCombo("next_page", UIKeysApp.WELCOME_KEYS_NEXT, GLFW.GLFW_KEY_RIGHT).categoryKey("welcome");
}