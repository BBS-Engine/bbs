package mchorse.bbs.game.scripts;

public class ScriptResult
{
    public String print;
    public Object output;

    public ScriptResult(String print, Object output)
    {
        this.print = print;
        this.output = output;
    }

    public String getPrint()
    {
        if (this.print.isEmpty() && this.output != null)
        {
            return this.output.toString();
        }

        return this.print;
    }
}