package mchorse.bbs.animation.clip;

import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.game.utils.UIDataUtils;

public class UIAnimationClip extends UIClip<AnimationClip>
{
    public UIButton pick;

    public UIAnimationClip(AnimationClip clip, IUICameraWorkDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        this.pick = new UIButton(UIKeys.OVERLAYS_ANIMATION, (b) ->
        {
            UIDataUtils.openPicker(this.getContext(), ContentType.ANIMATIONS, this.clip.animation.get(), (name) ->
            {
                this.getContext().menu.bridge.get(IBridgeAnimations.class).getAnimations().remove(this.clip.animation.get());

                this.editor.postUndo(this.undo(this.clip.animation, (animation) -> animation.set(name)));
            });
        });
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        this.panels.add(this.pick);
    }
}