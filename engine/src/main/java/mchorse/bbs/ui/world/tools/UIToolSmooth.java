package mchorse.bbs.ui.world.tools;

import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.world.UIWorldEditorPanel;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.voxel.processor.Processor;
import mchorse.bbs.voxel.processor.SmoothProcessor;
import mchorse.bbs.voxel.raytracing.RayTraceResult;

public class UIToolSmooth extends UIToolProcessorPainter
{
    public UITrackpad kernelRadius;
    public UITrackpad kernelSigma;

    private float[][] kernel;

    public UIToolSmooth(UIWorldEditorPanel editor)
    {
        super(editor);

        this.kernelRadius = new UITrackpad((v) -> this.recalculate());
        this.kernelRadius.limit(1, 10, true);
        this.kernelRadius.setValue(1);
        this.kernelSigma = new UITrackpad((v) -> this.recalculate());
        this.kernelSigma.limit(0, 100);
        this.kernelSigma.setValue(0.84925F);

        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_SMOOTH_RADIUS), this.kernelRadius);
        this.panel.add(UI.label(UIKeys.WORLD_EDITOR_TOOLS_SMOOTH_FACTOR), this.kernelSigma);

        this.recalculate();
    }

    @Override
    public UIIcon createButton()
    {
        UIIcon icon = new UIIcon(Icons.GRAPH, null);

        icon.tooltip(UIKeys.WORLD_EDITOR_TOOLS_SMOOTH, Direction.RIGHT);

        return icon;
    }

    private void recalculate()
    {
        this.kernel = SmoothProcessor.generateBlurKernel((int) this.kernelRadius.getValue(), (float) this.kernelSigma.getValue());
    }

    @Override
    protected Processor createProcessor(RayTraceResult result)
    {
        return new SmoothProcessor(this.variantToPlace, this.kernel);
    }
}