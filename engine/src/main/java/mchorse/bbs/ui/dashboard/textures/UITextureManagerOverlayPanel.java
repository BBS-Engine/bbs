package mchorse.bbs.ui.dashboard.textures;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UILinkList;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageFolderOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.PNGEncoder;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.opengl.GL11;

import java.io.File;

public class UITextureManagerOverlayPanel extends UIOverlayPanel
{
    public UIIcon linear;
    public UIIcon copy;
    public UIIcon export;
    public UIIcon refresh;

    public UISearchList<Link> textures;

    private UITextureManagerPanel panel;
    private File exportFolder;

    public boolean linkLinear;

    public static File getFirstAvailableFile(File folder, String name)
    {
        File file = new File(folder, name + ".png");
        int index = 0;

        while (file.exists())
        {
            index += 1;
            file = new File(folder, name + index + ".png");
        }

        return file;
    }

    public UITextureManagerOverlayPanel(IKey title, UITextureManagerPanel panel)
    {
        super(title);

        this.panel = panel;
        this.exportFolder = BBS.getExportFolder();

        this.textures = new UISearchList<>(new UILinkList((rl) ->
        {
            if (this.panel.viewer.isDirty())
            {
                UIOverlay.addOverlay(this.getContext(), new UIConfirmOverlayPanel(
                    UIKeys.TEXTURES_DISCARD_TITLE,
                    UIKeys.TEXTURES_DISCARD_DESCRIPTION,
                    (confirm) -> this.panel.pickLink(confirm ? rl.get(0) : this.panel.viewer.getTexture())
                ));
            }
            else
            {
                this.panel.pickLink(rl.get(0));
            }
        }));
        this.textures.label(UIKeys.SEARCH);
        this.textures.relative(this.content).full().x(6).w(1F, -12);

        this.linear = new UIIcon(Icons.GRAPH, (b) -> this.toggleLinear());
        this.linear.tooltip(UIKeys.TEXTURES_LINEAR, Direction.LEFT);
        this.copy = new UIIcon(Icons.COPY, (b) -> this.copy());
        this.copy.tooltip(UIKeys.TEXTURES_COPY, Direction.LEFT);
        this.export = new UIIcon(Icons.EXTERNAL, (b) -> this.export());
        this.export.tooltip(UIKeys.TEXTURES_EXPORT, Direction.LEFT);
        this.refresh = new UIIcon(Icons.REFRESH, (b) -> this.remove());
        this.refresh.tooltip(UIKeys.TEXTURES_REFRESH, Direction.LEFT);

        this.icons.add(this.linear, this.copy, this.export, this.refresh);
        this.content.add(this.textures);
    }

    private void toggleLinear()
    {
        Texture texture = BBS.getTextures().getTexture(this.panel.getLink());

        this.linkLinear = !this.linkLinear;

        texture.bind();
        texture.setFilter(this.linkLinear ? GL11.GL_LINEAR : GL11.GL_NEAREST);
    }

    private void copy()
    {
        Link link = this.textures.list.getCurrentFirst();

        if (link == null)
        {
            return;
        }

        Window.setClipboard(link.toString());
    }

    private void export()
    {
        Link link = this.textures.list.getCurrentFirst();

        if (link == null)
        {
            return;
        }

        String name = StringUtils.removeExtension(StringUtils.fileName(link.path));
        File folder = this.exportFolder;
        File file = getFirstAvailableFile(folder, name);

        folder.mkdirs();

        Pixels pixels = this.panel.viewer.getPixels();

        try
        {
            PNGEncoder.writeToFile(pixels, file);
            UIMessageFolderOverlayPanel panel = new UIMessageFolderOverlayPanel(
                UIKeys.TEXTURES_EXPORT_OVERLAY_TITLE,
                UIKeys.TEXTURES_EXPORT_OVERLAY_SUCCESS.format(file.getName()),
                this.exportFolder
            );

            panel.folder.tooltip(UIKeys.TEXTURES_EXPORT_OVERLAY_OPEN_FOLDER, Direction.LEFT);

            UIOverlay.addOverlay(this.getContext(), panel);
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

    private void remove()
    {
        if (this.panel.getLink() == null)
        {
            return;
        }

        BBS.getTextures().textures.remove(this.panel.getLink()).delete();

        this.panel.pickLink(this.textures.list.getCurrentFirst());
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        super.renderBackground(context);

        if (this.linkLinear)
        {
            this.linear.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
        }
    }
}