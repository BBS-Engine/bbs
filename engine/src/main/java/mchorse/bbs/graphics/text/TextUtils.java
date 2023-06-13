package mchorse.bbs.graphics.text;

public class TextUtils
{
    public static String processColoredText(String text)
    {
        if (!text.contains("["))
        {
            return text;
        }
        else
        {
            StringBuilder builder = new StringBuilder();
            int i = 0;

            for(int c = text.length(); i < c; i++)
            {
                char character = text.charAt(i);

                if (character == '\\' && i < c - 1 && text.charAt(i + 1) == '[')
                {
                    builder.append('[');
                    i += 1;
                }
                else
                {
                    builder.append(character == '[' ? FontRenderer.FORMATTING_CHARACTER : character);
                }
            }

            return builder.toString();
        }
    }
}
