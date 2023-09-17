package mchorse.bbs.ui.utils.keys;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.utils.OS;
import org.lwjgl.glfw.GLFW;

public class KeyCodes
{
    public static String getName(int keyCode)
    {
        if (keyCode <= 0)
        {
            return "N/A";
        }

        if (BBSSettings.forceQwerty.get())
        {
            switch (keyCode)
            {
                case 48: return "0";
                case 49: return "1";
                case 50: return "2";
                case 51: return "3";
                case 52: return "4";
                case 53: return "5";
                case 54: return "6";
                case 55: return "7";
                case 56: return "8";
                case 57: return "9";
                case 65: return "A";
                case 66: return "B";
                case 67: return "C";
                case 68: return "D";
                case 69: return "E"; /* Nice! */
                case 70: return "F";
                case 71: return "G";
                case 72: return "H";
                case 73: return "I";
                case 74: return "J";
                case 75: return "K";
                case 76: return "L";
                case 77: return "M";
                case 78: return "N";
                case 79: return "O";
                case 80: return "P";
                case 81: return "Q";
                case 82: return "R";
                case 83: return "S";
                case 84: return "T";
                case 85: return "U";
                case 86: return "V";
                case 87: return "W";
                case 88: return "X";
                case 89: return "Y";
                case 90: return "Z";
            }
        }

        switch (keyCode)
        {
            case 32: return "Space";
            case 256: return "Escape";
            case 257: return "Enter";
            case 258: return "Tab";
            case 259: return "Backspace";
            case 261: return "Delete";
            case 262: return "Right";
            case 263: return "Left";
            case 264: return "Down";
            case 265: return "Up";
            case 266: return "Page up";
            case 267: return "Page down";
            case 268: return "Home";
            case 269: return "End";
            case 280: return "Capslock";
            case 282: return "Numlock";
            /* Shift */
            case 340: return "\u21e7";
            /* Control */
            case 341: return "^";
            /* Alt/Option */
            case 342: return "\u2325";
            /* Command/Windows */
            case 343: return OS.CURRENT == OS.MACOS ? "\u2318" : "\u229e";
            case 344: return "R.\u21e7";
            case 345: return "R.^";
            case 346: return "R.\u2325";
            case 347: return "R." + (OS.CURRENT == OS.MACOS ? "\u2318" : "\u229e");
        }

        String name = GLFW.glfwGetKeyName(keyCode, 0);

        if (keyCode >= 320 && keyCode <= 336)
        {
            switch (keyCode)
            {
                case 335: name = "Enter"; break;
                case 336: name = "="; break;
            }

            return "Num " + name;
        }

        if (name == null)
        {
            if (keyCode >= 290 && keyCode <= 308)
            {
                return "F" + (keyCode - 289);
            }

            name = "Key " + keyCode;
        }

        return name.length() == 1 ? name.toUpperCase() : name;
    }
}