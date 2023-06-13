package mchorse.app.ui;

import mchorse.bbs.ui.utils.keys.KeyCombo;
import org.lwjgl.glfw.GLFW;

public class KeysApp
{
    public static final KeyCombo WELCOME = new KeyCombo(UIKeysApp.WELCOME_TITLE, GLFW.GLFW_KEY_BACKSLASH).categoryKey("welcome");
    public static final KeyCombo PREV_PAGE = new KeyCombo(UIKeysApp.WELCOME_KEYS_PREV, GLFW.GLFW_KEY_LEFT).categoryKey("welcome");
    public static final KeyCombo NEXT_PAGE = new KeyCombo(UIKeysApp.WELCOME_KEYS_NEXT, GLFW.GLFW_KEY_RIGHT).categoryKey("welcome");
}