package mchorse.bbs.ui.recording.editor.tools;

import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.math.Variable;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.recording.editor.utils.UIProcessToolOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

public class ProcessTool extends Tool
{
    public MathBuilder builder;
    public Variable initial;
    public Variable value;
    public Variable tick;
    public Variable from;
    public Variable to;
    public Variable factor;

    public ProcessTool()
    {
        super(Icons.CURVES, Keys.RECORD_TOOL_PROCESS);

        this.builder = new MathBuilder();

        this.builder.register(this.initial = new Variable("initial", 0));
        this.builder.register(this.value = new Variable("value", 0));
        this.builder.register(this.tick = new Variable("tick", 0));
        this.builder.register(this.from = new Variable("from", 0));
        this.builder.register(this.to = new Variable("to", 0));
        this.builder.register(this.factor = new Variable("factor", 0));
    }

    @Override
    public boolean canApply(ToolContext context)
    {
        return context.timeline.hasSelectionRange();
    }

    @Override
    public boolean apply(ToolContext context)
    {
        UIProcessToolOverlayPanel panel = new UIProcessToolOverlayPanel(
            UIKeys.RECORD_EDITOR_TOOLS_PROCESS_MODAL_TITLE,
            UIKeys.RECORD_EDITOR_TOOLS_PROCESS_MODAL_DESCRIPTION,
            (pair) -> this.process(context, pair.a, pair.b)
        );

        UIOverlay.addOverlay(context.timeline.getContext(), panel, 0.5F, 0.8F);

        return true;
    }

    private void process(ToolContext context, String property, String expression)
    {
        Range range = context.timeline.calculateRange();
        Record record = context.record;
        double initial = Frame.get(property, record.frames.get(range.min));
        IExpression math;

        try
        {
            math = this.builder.parse(expression);
        }
        catch (Exception e)
        {
            return;
        }

        this.from.set(range.min);
        this.to.set(range.max);

        for (int i = range.min; i <= range.max; i++)
        {
            Frame frame = record.frames.get(i);

            this.initial.set(initial);
            this.value.set(Frame.get(property, frame));
            this.tick.set(i);
            this.factor.set((i - range.min) / (double) (range.max - range.min));

            Frame.set(property, frame, math.get().doubleValue());
        }
    }
}