package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.processor.FillProcessor;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;

public class UIToolCube extends UIToolProcessorPainter
{
    public UIToolCube(UIWorldEditorPanel editor)
    {
        super(editor);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.BLOCK, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_CUBE, Direction.RIGHT);

        return icon;
    }

    @Override
    protected Processor createProcessor(RayTraceResult result)
    {
        return new FillProcessor(this.variantToPlace, false).collect();
    }
}