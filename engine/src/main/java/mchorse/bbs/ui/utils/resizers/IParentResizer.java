package mchorse.bbs.ui.utils.resizers;

import mchorse.bbs.ui.utils.Area;

public interface IParentResizer
{
    public void apply(Area area, IResizer resizer, ChildResizer child);
}
