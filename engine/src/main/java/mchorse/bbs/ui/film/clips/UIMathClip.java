package mchorse.bbs.ui.film.clips;

import mchorse.bbs.camera.clips.modifiers.MathClip;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.IUIClipsDelegate;
import mchorse.bbs.ui.film.clips.widgets.UIBitToggle;
import mchorse.bbs.ui.film.utils.UITextboxHelp;
import mchorse.bbs.utils.colors.Colors;

public class UIMathClip extends UIClip<MathClip>
{
    public UITextboxHelp expression;
    public UIBitToggle active;

    public UIMathClip(MathClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.expression = new UITextboxHelp(1000, (str) ->
        {
            this.clip.expression.setExpression(str);
            this.expression.setColor(!this.clip.expression.isErrored() ? Colors.WHITE : Colors.RED);
        });
        this.expression.link("https://github.com/mchorse/aperture/wiki/Math-Expressions").tooltip(UIKeys.CAMERA_PANELS_MATH);

        this.active = new UIBitToggle((value) -> this.clip.active.set(value)).all();
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(UIClip.label(UIKeys.CAMERA_PANELS_EXPRESSION).marginTop(12), this.expression, this.active);
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