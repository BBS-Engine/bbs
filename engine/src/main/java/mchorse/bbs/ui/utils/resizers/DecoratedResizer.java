package mchorse.bbs.ui.utils.resizers;

public abstract class DecoratedResizer extends BaseResizer
{
    public IResizer resizer;

    public DecoratedResizer(IResizer resizer)
    {
        this.resizer = resizer;
    }
}