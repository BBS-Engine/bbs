package mchorse.bbs.ui.game.scripts.utils.documentation;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.text.Font;
import mchorse.bbs.graphics.text.FontRenderer;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.game.scripts.UITextEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class DocEntry
{
    public DocEntry parent;

    public String name = "";
    public String doc = "";

    public static String processCode(String code)
    {
        List<String> strings = new ArrayList<String>(Arrays.asList(code.split("\n")));
        int first = 0;

        /* Find first non-empty string */
        for (String string : strings)
        {
            if (string.trim().isEmpty())
            {
                first += 1;
            }
            else
            {
                break;
            }
        }

        /* Once first string is found, find the first string's indentation*/
        String firstLine = strings.get(first);
        int indent = 0;

        for (int i = 0; i < firstLine.length(); i++)
        {
            if (firstLine.charAt(i) == ' ')
            {
                indent += 1;
            }
            else
            {
                break;
            }
        }

        /* Remove last string which should contain "}</pre>" */
        strings.remove(strings.size() - 1);

        /* Remove the first line's indentation from the rest of the code */
        if (indent > 0)
        {
            for (int i = 0; i < strings.size(); i++)
            {
                String string = strings.get(i);

                if (string.length() > indent)
                {
                    strings.set(i, string.substring(indent));
                }
            }
        }

        return String.join("\n", strings).trim();
    }

    public static void process(String doc, UIScrollView target)
    {
        String[] splits = doc.split("\n{2,}");
        boolean parsing = false;
        String code = "";

        for (String line : splits)
        {
            if (line.trim().startsWith("<pre>{@code"))
            {
                parsing = true;
                line = line.trim().substring("<pre>{@code".length() + 1);
            }

            if (parsing)
            {
                code += "\n\n" + line;
            }
            else
            {
                boolean p = line.trim().startsWith("<p>");

                line = line.replaceAll("\n", "").trim();
                line = line.replaceAll("<b>", "");
                line = line.replaceAll("<i>", Font.FORMAT_ITALIC);
                line = line.replaceAll("<s>", "");
                line = line.replaceAll("<code>", Font.FORMAT_LIGHT_GRAY);
                line = line.replaceAll("<li> *", "\n- ");
                line = line.replaceAll("</(b|i|s|code|ul|li)>", Font.FORMAT_RESET);
                line = line.replaceAll("</?(p|ul|li)>", "");
                line = line.replaceAll("\\{@link +[^}]+\\.([^}]+)}", Font.FORMAT_ORANGE + "$1" + Font.FORMAT_RESET);
                line = line.replaceAll("\\{@link +([^}]*)#([^}]+)}", Font.FORMAT_ORANGE + "$1" + Font.FORMAT_RESET + "." + Font.FORMAT_ORANGE + "$2" + Font.FORMAT_RESET);
                line = line.replaceAll("\\{@link ([^}]+)}", Font.FORMAT_ORANGE + "$1" + Font.FORMAT_RESET);
                line = line.replaceAll("&lt;", "<");
                line = line.replaceAll("&gt;", ">");
                line = line.replaceAll("&amp;", "&");

                UIText text = new UIText(line.trim().replaceAll(" {2,}", " "));

                if (p)
                {
                    text.marginTop(12);
                }

                target.add(text);
            }

            if (line.trim().endsWith("}</pre>"))
            {
                UITextEditor editor = new UITextEditor(null);
                String text = processCode(code).replaceAll(FontRenderer.FORMATTING_STRING, "\\\\u00A7");

                editor.setText(text);
                editor.background().h(editor.getLines().size() * 12 + 20);
                editor.getHighlighter().setStyle(BBSSettings.scriptEditorSyntaxStyle.get());

                if (!target.getChildren().isEmpty())
                {
                    editor.marginTop(6);
                }

                target.add(editor);

                parsing = false;
                code = "";
            }
        }
    }

    public String getName()
    {
        int index = this.name.lastIndexOf(".");

        if (index < 0)
        {
            return this.name;
        }

        return this.name.substring(index + 1);
    }

    public void fillIn(UIScrollView target)
    {
        process(this.doc, target);
    }

    public List<DocEntry> getEntries()
    {
        return Collections.emptyList();
    }

    public DocEntry getEntry()
    {
        return this;
    }

    public void fromData(MapType data)
    {
        this.name = data.getString("name");
        this.doc = data.getString("doc");
    }
}