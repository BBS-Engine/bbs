package mchorse.bbs.animation.clip;

import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.game.utils.UIDataUtils;

public class UIAnimationClip extends UIClip<AnimationClip>
{
    public UIButton pick;

    public UIAnimationClip(AnimationClip clip, UICameraPanel editor)
    {
        super(clip, editor);

        this.pick = new UIButton(UIKeys.OVERLAYS_ANIMATION, (b) ->
        {
            UIDataUtils.openPicker(this.getContext(), ContentType.ANIMATIONS, this.clip.animation.get(), (name) ->
            {
                this.getContext().menu.bridge.get(IBridgeAnimations.class).getAnimations().remove(this.clip.animation.get());

                this.editor.postUndo(this.undo(this.clip.animation, new StringType(name)));
            });
        });

        this.right.add(this.pick);
    }
}