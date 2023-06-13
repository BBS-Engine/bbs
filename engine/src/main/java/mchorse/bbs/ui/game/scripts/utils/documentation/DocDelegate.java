package mchorse.bbs.ui.game.scripts.utils.documentation;

public class DocDelegate extends DocEntry
{
    public DocEntry delegate;

    public DocDelegate(DocEntry delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public String getName()
    {
        return "../";
    }

    @Override
    public DocEntry getEntry()
    {
        return this.delegate;
    }
}