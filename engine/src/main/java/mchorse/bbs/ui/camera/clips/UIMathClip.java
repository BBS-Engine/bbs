package mchorse.bbs.ui.camera.clips;

import mchorse.bbs.camera.clips.modifiers.MathClip;
import mchorse.bbs.data.types.IntType;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.camera.utils.UITextboxHelp;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.colors.Colors;

public class UIMathClip extends UIClip<MathClip>
{
    public UITextboxHelp expression;
    public UIBitToggle active;

    public UIMathClip(MathClip clip, UICameraPanel editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.expression = new UITextboxHelp(1000, (str) ->
        {
            this.editor.postUndo(this.undo(this.clip.expression, new StringType(str)));
            this.expression.setColor(!this.clip.expression.isErrored() ? Colors.WHITE : Colors.RED);
        });
        this.expression.link("https://github.com/mchorse/aperture/wiki/Math-Expressions").tooltip(UIKeys.CAMERA_PANELS_MATH);

        this.active = new UIBitToggle((value) -> this.editor.postUndo(this.undo(this.clip.active, new IntType(value)))).all();
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView expression = this.createScroll();

        expression.add(UI.label(UIKeys.CAMERA_PANELS_EXPRESSION).background(), this.expression, this.active);

        this.panels.registerPanel(expression, UIKeys.CAMERA_PANELS_EXPRESSION, Icons.GRAPH);
        this.panels.setPanel(expression);

        super.registerPanels();
    }

    @Override
    public void fillData()
    {
        super.fillData();

        this.expression.setText(this.clip.expression.toString());
        this.expression.setColor(Colors.WHITE);
        this.active.setValue(this.clip.active.get());
    }
}