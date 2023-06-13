package mchorse.bbs.ui.game.utils;

import mchorse.bbs.bridge.IBridgeCamera;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;
import org.joml.Vector3i;

import java.util.List;

public class UIBlockList extends UIElement
{
    private List<Vector3i> posList;

    public UIBlockList()
    {
        super();

        this.column().stretch().vertical();
    }

    public void addBlockPos()
    {
        Vector3i pos = new Vector3i().set(this.getContext().menu.bridge.get(IBridgeCamera.class).getCamera().position);

        this.posList.add(pos);
        this.add(this.create(pos));

        this.getParentContainer().resize();
    }

    public void set(List<Vector3i> posList)
    {
        this.posList = posList;

        this.removeAll();

        for (Vector3i pos : posList)
        {
            UIBlock posElement = this.create(pos);

            this.add(posElement);
        }

        this.getParentContainer().resize();
    }

    private UIBlock create(Vector3i pos)
    {
        UIBlock posElement = new UIBlock(null);

        posElement.set(pos);
        posElement.callback = (blockPos) ->
        {
            this.posList.set(this.getChildren().indexOf(posElement), blockPos);
        };
        posElement.context((menu) ->
        {
            menu.action(Icons.REMOVE, UIKeys.BLOCK_POS_CONTEXT_REMOVE, Colors.NEGATIVE, () -> this.removeBlock(posElement));
        });

        return posElement;
    }

    private void removeBlock(UIBlock element)
    {
        int index = this.getChildren().indexOf(element);

        this.remove(element);
        this.posList.remove(index);

        this.getParentContainer().resize();
    }
}