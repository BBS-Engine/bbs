package mchorse.bbs.ui.ui.graphics;

import mchorse.bbs.BBS;
import mchorse.bbs.game.scripts.ui.graphics.ImageGraphic;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.ui.UIUserInterfacePanel;
import mchorse.bbs.ui.utils.UI;

public class UIImageGraphicPanel extends UIGraphicPanel<ImageGraphic>
{
    public UIButton pickImage;
    public UITrackpad w;
    public UITrackpad h;

    public UIImageGraphicPanel(UIUserInterfacePanel panel)
    {
        super(panel);

        this.pickImage = new UIButton(UIKeys.UI_GRAPHICS_PICK_IMAGE, (b) ->
        {
            UITexturePicker.open(panel, this.graphic.picture, (l) ->
            {
                Texture texture = BBS.getTextures().getTexture(l);

                this.graphic.picture = l;
                this.graphic.width = texture.width;
                this.graphic.height = texture.height;

                this.fill(this.graphic);
            });
        });

        this.w = new UITrackpad((v) -> this.graphic.width = v.intValue()).limit(0).integer();
        this.w.tooltip(UIKeys.UI_GRAPHICS_IMAGE_WIDTH);

        this.h = new UITrackpad((v) -> this.graphic.height = v.intValue()).limit(0).integer();
        this.h.tooltip(UIKeys.UI_GRAPHICS_IMAGE_HEIGHT);

        this.add(this.pickImage);
        this.add(UI.label(UIKeys.UI_GRAPHICS_IMAGE_SIZE));
        this.add(UI.row(this.w, this.h));
    }

    @Override
    public void fill(ImageGraphic graphic)
    {
        super.fill(graphic);

        this.w.setValue(graphic.width);
        this.h.setValue(graphic.height);
    }
}