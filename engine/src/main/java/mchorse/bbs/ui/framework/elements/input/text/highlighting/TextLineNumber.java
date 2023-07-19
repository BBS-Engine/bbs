package mchorse.bbs.ui.framework.elements.input.text.highlighting;

public class TextLineNumber
{
    public String line;
    public int x;
    public int y;
    public boolean render;

    public void set(String line, int x, int y)
    {
        this.line = line;
        this.x = x;
        this.y = y;
        this.render = true;
    }
}