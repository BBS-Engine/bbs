package mchorse.bbs.ui.dashboard.utils;

import mchorse.bbs.graphics.line.LineBuilder;
import mchorse.bbs.graphics.line.SolidColorLineRenderer;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.math.IExpression;
import mchorse.bbs.math.MathBuilder;
import mchorse.bbs.math.Variable;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.UICanvas;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.colors.Colors;

public class UIGraphPanel extends UIDashboardPanel
{
    public UIGraphCanvas canvas;
    public UITextbox expression;
    public UIIcon help;

    public UIGraphPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.canvas = new UIGraphCanvas();
        this.expression = new UITextbox(10000, this.canvas::parseExpression);
        this.help = new UIIcon(Icons.HELP, (b) -> UIUtils.openWebLink("https://github.com/mchorse/aperture/wiki/Math-Expressions"));
        this.help.tooltip(UIKeys.GRAPH_HELP, Direction.TOP);

        String first = "sin(x)";

        this.expression.setText(first);
        this.canvas.parseExpression(first);

        this.expression.relative(this).x(10).y(1F, -30).w(1F, -20).h(20);
        this.canvas.relative(this).full();
        this.help.relative(this.expression).x(1F, -19).y(1).wh(18, 18);

        this.expression.add(this.help);
        this.add(this.canvas, this.expression);
    }

    public static class UIGraphCanvas extends UICanvas
    {
        private MathBuilder builder;
        private Variable x;
        private boolean first = true;

        public IExpression expression;

        public UIGraphCanvas()
        {
            super();

            this.builder = new MathBuilder();
            this.x = this.builder.register("x");

            this.scaleY.inverse = true;
        }

        public void parseExpression(String expression)
        {
            try
            {
                this.expression = this.builder.parse(expression);
            }
            catch (Exception e)
            {
                this.expression = null;
            }
        }

        @Override
        public void resize()
        {
            super.resize();

            if (this.first)
            {
                this.scaleX.view(-10, 10);
                this.scaleX.calculateMultiplier();
                this.scaleY.view(-10, 10);
                this.scaleY.calculateMultiplier();

                this.first = false;
            }
        }

        @Override
        protected void renderCanvas(UIContext context)
        {
            this.area.render(context.batcher, Colors.A50);

            this.renderVerticalGrid(context);
            this.renderHorizontalGridAndGraph(context);
        }

        private void renderVerticalGrid(UIContext context)
        {
            /* Draw vertical grid */
            int ty = (int) this.scaleY.from(this.area.ey());
            int by = (int) this.scaleY.from(this.area.y - 12);

            int min = Math.min(ty, by) - 1;
            int max = Math.max(ty, by) + 1;
            int mult = this.scaleY.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int y = (int) this.scaleY.to(min + j * mult);

                if (y >= this.area.ey())
                {
                    continue;
                }

                context.batcher.box(this.area.x, y, this.area.ex(), y + 1, Colors.setA(Colors.WHITE, 0.25F));
                context.batcher.text(String.valueOf(min + j * mult), this.area.x + 4, y + 4);
            }
        }

        private void renderHorizontalGridAndGraph(UIContext context)
        {
            /* Draw scaling grid */
            int tx = (int) this.scaleX.from(this.area.ex());
            int bx = (int) this.scaleX.from(this.area.x);

            int min = Math.min(tx, bx) - 1;
            int max = Math.max(tx, bx) + 1;
            int mult = this.scaleX.getMult();

            min -= min % mult + mult;
            max -= max % mult - mult;

            for (int j = 0, c = (max - min) / mult; j < c; j++)
            {
                int x = (int) this.scaleX.to(min + j * mult);

                if (x >= this.area.ex())
                {
                    break;
                }

                context.batcher.box(x, this.area.y, x + 1, this.area.ey(), Colors.setA(Colors.WHITE, 0.25F));
                context.batcher.text(String.valueOf(min + j * mult), x + 4, this.area.y + 4);
            }

            if (this.expression == null)
            {
                return;
            }

            if (Window.isMouseButtonPressed(0) && !context.isFocused())
            {
                int mouseX = context.mouseX;
                double x = this.scaleX.from(mouseX);

                this.x.set(x);

                double y = this.expression.get().doubleValue();
                int y1 = context.mouseY;
                int y2 = (int) this.scaleY.to(y) + 1;
                boolean isNaN = Double.isNaN(y);

                if (y1 < y2)
                {
                    y1 -= 12;
                }

                String coordinate = "(" + UITrackpad.format(x) + ", " + (isNaN ? "undefined" : UITrackpad.format(y)) + ")";

                if (!isNaN)
                {
                    context.batcher.box(mouseX, Math.min(y1, y2), mouseX + 1, Math.max(y1, y2), Colors.CURSOR);
                }

                int y3 = y1 < y2 ? y1 : y1 - 12;
                int w = context.font.getWidth(coordinate);

                mouseX += 1;

                context.batcher.box(mouseX, y3, mouseX + w + 4, y3 + 12, Colors.WHITE);
                context.batcher.text(coordinate, mouseX + 2, y3 + 2, 0);
            }

            LineBuilder line = new LineBuilder(1F);
            double step = 1;

            for (double j = this.area.x - step; j < this.area.ex() + step; j += step)
            {
                double previous = this.scaleX.from(j);

                this.x.set(previous);
                double y1 = this.expression.get().doubleValue();

                line.add((float) j, (float) this.scaleY.to(y1));
            }

            line.render(context.batcher, SolidColorLineRenderer.get(0F, 0.5F, 1F, 1F));
        }
    }
}
