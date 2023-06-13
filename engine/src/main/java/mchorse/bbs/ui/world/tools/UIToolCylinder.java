package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.processor.CylinderProcessor;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;

public class UIToolCylinder extends UIToolProcessorPainter
{
    public UIToolCylinder(UIWorldEditorPanel editor)
    {
        super(editor);

        this.size = 5;
        this.sizeElement.setValue(5);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.CYLINDER, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_CYLINDER, Direction.RIGHT);

        return icon;
    }

    @Override
    protected Processor createProcessor(RayTraceResult result)
    {
        return new CylinderProcessor(this.variantToPlace, false).collect();
    }
}