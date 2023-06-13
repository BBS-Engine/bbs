package mchorse.bbs.ui.dashboard.textures;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.PNGEncoder;
import mchorse.bbs.utils.resources.Pixels;

import java.io.File;

public class UITextureEditor extends UIPixelsEditor
{
    public UIElement savebar;
    public UIIcon save;

    private Link texture;
    private boolean dirty;

    public UITextureEditor()
    {
        super();

        this.savebar = new UIElement();
        this.savebar.relative(this).x(1F).h(30).anchorX(1F).row(0).resize().padding(5);
        this.save = new UIIcon(Icons.SAVE, (b) -> this.saveTexture());

        this.savebar.add(this.save);

        this.add(this.savebar);
    }

    public Link getTexture()
    {
        return this.texture;
    }

    public boolean isDirty()
    {
        return this.dirty;
    }

    public void dirty()
    {
        this.setDirty(true);
    }

    public void setDirty(boolean dirty)
    {
        this.dirty = dirty;

        this.save.both(dirty ? Icons.SAVE : Icons.SAVED);
    }

    @Override
    protected void wasChanged()
    {
        this.dirty();
    }

    private void saveTexture()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.EXPORT,
            UIKeys.TEXTURES_SAVE,
            this::saveTexture
        );

        panel.text.setText(this.texture.toString());

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void saveTexture(String path)
    {
        Link link = Link.create(path);

        if (!link.source.equals("assets") || !link.path.endsWith(".png"))
        {
            UIOverlay.addOverlay(this.getContext(), new UIMessageOverlayPanel(
                UIKeys.ERROR,
                UIKeys.TEXTURES_SAVE_WRONG_PATH
            ));

            return;
        }

        File file = BBS.getAssetsPath(link.path);

        if (path.contains("/"))
        {
            file.getParentFile().mkdirs();
        }

        Pixels pixels = this.getPixels();

        try
        {
            PNGEncoder.writeToFile(pixels, file);
            UIMessageOverlayPanel panel = new UIMessageOverlayPanel(
                UIKeys.TEXTURES_EXPORT_OVERLAY_TITLE,
                UIKeys.TEXTURES_EXPORT_OVERLAY_SUCCESS.format(file.getName())
            );

            UIButton open = new UIButton(UIKeys.TEXTURES_EXPORT_OVERLAY_OPEN_FOLDER, (b) ->
            {
                panel.close();
                UIUtils.openFolder(file.getParentFile());
            });

            open.relative(panel).x(0.5F).y(1F, -10).w(100).anchor(0.5F, 1F);
            panel.content.add(open);

            UIOverlay.addOverlay(this.getContext(), panel);

            this.setDirty(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();

            UIOverlay.addOverlay(this.getContext(), new UIMessageOverlayPanel(
                UIKeys.TEXTURES_EXPORT_OVERLAY_TITLE,
                UIKeys.TEXTURES_EXPORT_OVERLAY_ERROR.format(file.getName())
            ));
        }
    }

    public void fillTexture(Link texture)
    {
        if (this.getPixels() != null)
        {
            this.getPixels().delete();
        }

        this.texture = texture;

        if (texture != null)
        {
            Texture t = BBS.getTextures().getTexture(texture);

            this.fillPixels(Pixels.fromTexture(t));
            this.setDirty(false);
        }
    }

    @Override
    protected Texture getRenderTexture(UIContext context)
    {
        return this.isEditing() ? super.getRenderTexture(context) : context.render.getTextures().getTexture(this.texture);
    }
}