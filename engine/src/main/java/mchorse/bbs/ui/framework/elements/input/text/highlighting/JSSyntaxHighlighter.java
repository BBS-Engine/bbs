package mchorse.bbs.ui.framework.elements.input.text.highlighting;

import java.util.Arrays;
import java.util.HashSet;

public class JSSyntaxHighlighter extends BaseSyntaxHighlighter
{
    public JSSyntaxHighlighter()
    {
        super();

        this.primaryKeywords = new HashSet<>(Arrays.asList("break", "continue", "switch", "case", "default", "try", "catch", "delete", "do", "while", "else", "finally", "if", "else", "for", "each", "in", "instanceof", "new", "throw", "typeof", "with", "yield", "return"));
        this.secondaryKeywords = new HashSet<>(Arrays.asList("const", "function", "var", "let", "prototype", "Math", "JSON", "bbs"));
        this.special = new HashSet<>(Arrays.asList("this", "arguments"));
        this.typeKeyswords = new HashSet<>(Arrays.asList("true", "false", "null", "undefined"));
    }
}