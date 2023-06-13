package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.processor.SprayProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;

public class UIToolSpray extends UIToolProcessorPainter
{
    public UITrackpad chance;

    public UIToolSpray(UIWorldEditorPanel editor)
    {
        super(editor);

        this.chance = new UITrackpad(null);
        this.chance.setValue(30);
        this.chance.limit(0D, 100D);

        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_SPRAY_CHANCE), this.chance);
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.SPRAY, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_SPRAY, Direction.RIGHT);

        return icon;
    }

    @Override
    protected Processor createProcessor(RayTraceResult result)
    {
        return new SprayProcessor(this.variantToPlace, (float) this.chance.getValue() / 100F).collect();
    }
}