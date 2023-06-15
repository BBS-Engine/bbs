package mchorse.bbs.ui.world.repl;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.utils.UIText;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIReplText extends UIText
{
    private boolean odd;

    public UIReplText(boolean odd, int vertical)
    {
        super();

        this.odd = odd;

        this.padding(10, vertical);

        this.context((menu) -> menu.action(Icons.COPY, UIKeys.REPL_CONTEXT_COPY_OUTPUT, () -> Window.setClipboard(this.getText().get())));
    }

    @Override
    public void render(UIContext context)
    {
        if (this.odd)
        {
            this.area.render(context.batcher, Colors.A50);
        }

        super.render(context);
    }
}
