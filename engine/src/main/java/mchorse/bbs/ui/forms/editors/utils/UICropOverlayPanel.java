package mchorse.bbs.ui.forms.editors.utils;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import org.joml.Vector4f;

public class UICropOverlayPanel extends UIOverlayPanel
{
    public UICropEditor cropEditor;

    public UICropOverlayPanel(Link texture, Vector4f crop)
    {
        super(UIKeys.FORMS_CROP_TITLE);

        Texture t = BBS.getTextures().getTexture(texture);

        int w = t.width;
        int h = t.height;

        this.cropEditor = new UICropEditor();
        this.cropEditor.fill(texture, crop);
        this.cropEditor.setSize(w, h);
        this.cropEditor.relative(this.content).x(-10).w(1F, 20).h(1F);
        this.cropEditor.scaleX.setZoom(1F / (Math.max(w, h) / 128F));
        this.cropEditor.scaleY.setZoom(1F / (Math.max(w, h) / 128F));
        this.content.add(this.cropEditor);
    }
}
