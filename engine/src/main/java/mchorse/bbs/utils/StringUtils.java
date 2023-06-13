package mchorse.bbs.utils;

import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;
import org.joml.Vector4f;

public class StringUtils
{
    public static String combinePaths(String a, String b)
    {
        return combinePaths(a, b, "/");
    }

    public static String combinePaths(String a, String b, String delimeter)
    {
        return a.isEmpty() ? b : a + delimeter + b;
    }

    /**
     * Left pad
     */
    public static String leftPad(String input, int totalLength, String pad)
    {
        return repeat(pad, totalLength - input.length()) + input;
    }

    /**
     * Right pad
     */
    public static String rightPad(String input, int totalLength, String pad)
    {
        return input + repeat(pad, totalLength - input.length());
    }

    /**
     * Repeat given strings so the length would be of given total length
     */
    public static String repeat(String string, int total)
    {
        if (total <= 0 || string.isEmpty())
        {
            return "";
        }

        int length = string.length();

        if (total < length)
        {
            return string.substring(0, total);
        }

        StringBuilder builder = new StringBuilder();

        while (total >= 0)
        {
            builder.append(total < length ? string.substring(0, total) : string);

            total -= length;
        }

        return builder.toString();
    }

    public static int countMatches(String string, String match)
    {
        int c = 0;
        int i = 0;

        while (i != -1)
        {
            i = string.indexOf(match, i + 1);
            c += 1;
        }

        return c;
    }

    public static int parseHex(String string)
    {
        return parseHex(string.toCharArray());
    }

    /**
     * Parse hexadecimal integer from given string (limited to 8 symbols).
     * It's also case insensitive.
     */
    public static int parseHex(char[] chars)
    {
        int result = 0;
        int length = chars.length;

        if (length > 8)
        {
            throw new NumberFormatException("Given string \"" + new String(chars) + "\" is longer than 8 symbols...");
        }

        for (int i = 0; i < length; i++)
        {
            char letter = chars[(length - 1) - i];
            int value = hexCharToInt(letter);

            if (value < 0)
            {
                throw new NumberFormatException("Given string \"" + new String(chars) + "\" isn't a hex number...");
            }

            result |= value << (i * 4);
        }

        return result;
    }

    /**
     * Converts given hex character (0-9, a-f or A-F) to an int. The resulted
     * value should be 0..15.
     */
    public static int hexCharToInt(char character)
    {
        if (character >= '0' && character <= '9')
        {
            return character - '0';
        }
        else if (character >= 'a' && character <= 'f')
        {
            return character - 'a' + 10;
        }
        else if (character >= 'A' && character <= 'F')
        {
            return character - 'A' + 10;
        }

        return -1;
    }

    public static String removeExtension(String path)
    {
        int lastDot = path.lastIndexOf('.');

        return lastDot >= 0 ? path.substring(0, lastDot) : path;
    }

    public static String replaceExtension(String path, String extension)
    {
        path = removeExtension(path);

        path += "." + extension;

        return path;
    }

    public static String fileName(String path)
    {
        int lastSlash = path.lastIndexOf('/');

        return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
    }

    /* Stringify vectors */

    public static String vector4fToString(Vector4f vector)
    {
        return "(" + vector.x + ", " + vector.y + ", " + vector.z + ", " + vector.w + ")";
    }

    public static String vector3fToString(Vector3f vector)
    {
        return "(" + vector.x + ", " + vector.y + ", " + vector.z + ")";
    }

    public static String vector4dToString(Vector4d vector)
    {
        return "(" + vector.x + ", " + vector.y + ", " + vector.z + ", " + vector.w + ")";
    }

    public static String vector3dToString(Vector3d vector)
    {
        return "(" + vector.x + ", " + vector.y + ", " + vector.z + ")";
    }
}