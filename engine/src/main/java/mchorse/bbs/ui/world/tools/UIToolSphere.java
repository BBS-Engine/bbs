package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.processor.SphereProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;

public class UIToolSphere extends UIToolProcessorPainter
{
    public UIToolSphere(UIWorldEditorPanel editor)
    {
        super(editor);

        this.size = 5;
        this.sizeElement.setValue(5);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.SPHERE, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_SPHERE, Direction.RIGHT);

        return icon;
    }

    @Override
    protected Processor createProcessor(RayTraceResult result)
    {
        return new SphereProcessor(this.variantToPlace, false).collect();
    }
}