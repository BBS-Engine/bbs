package mchorse.bbs.animation.clip;

import mchorse.bbs.bridge.IBridgeAnimations;
import mchorse.bbs.data.types.StringType;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.UICameraPanel;
import mchorse.bbs.ui.camera.clips.UIClip;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.game.utils.UIDataUtils;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIAnimationClip extends UIClip<AnimationClip>
{
    public UIButton pick;

    public UIAnimationClip(AnimationClip clip, UICameraPanel editor)
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

                this.editor.postUndo(this.undo(this.clip.animation, new StringType(name)));
            });
        });
    }

    @Override
    protected void registerPanels()
    {
        UIScrollView animation = this.createScroll();

        animation.add(this.pick);

        this.panels.registerPanel(animation, UIKeys.C_CLIP.get(Link.bbs("animation")), Icons.CURVES);
        this.panels.setPanel(animation);

        super.registerPanels();
    }
}