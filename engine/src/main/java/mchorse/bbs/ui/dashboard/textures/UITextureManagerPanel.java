package mchorse.bbs.ui.dashboard.textures;

import mchorse.bbs.BBS;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UISidebarDashboardPanel;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.utils.icons.Icons;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UITextureManagerPanel extends UISidebarDashboardPanel
{
    private static final List<Link> FORBIDDEN_EXPORT = Collections.emptyList();

    public UITextureEditor viewer;

    public UIIcon edit;

    private UITextureManagerOverlayPanel overlay;

    private Link link;

    public UITextureManagerPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.viewer = new UITextureEditor();
        this.viewer.relative(this.editor).full();

        UIIcon icon = new UIIcon(Icons.MORE, (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), this.overlay);
        });

        this.edit = new UIIcon(Icons.EDIT, (b) -> this.viewer.toggleEditor());

        this.editor.add(this.viewer);
        this.iconBar.add(icon, this.edit);

        this.overlay = new UITextureManagerOverlayPanel(UIKeys.TEXTURES_TITLE, this);

        this.pickLink(null);
    }

    public Link getLink()
    {
        return this.link;
    }

    public void pickLink(Link link)
    {
        boolean forbidden = FORBIDDEN_EXPORT.contains(link);

        this.overlay.linear.setEnabled(link != null);
        this.overlay.copy.setEnabled(link != null);
        this.overlay.export.setEnabled(link != null && !forbidden);
        this.overlay.refresh.setEnabled(link != null);
        this.edit.setEnabled(link != null && !forbidden);
        this.overlay.export.tooltip(forbidden
            ? UIKeys.TEXTURES_EXPORT_FORBIDDEN
            : UIKeys.TEXTURES_EXPORT);
        this.edit.tooltip(forbidden
            ? UIKeys.TEXTURES_EDIT_FORBIDDEN
            : UIKeys.TEXTURES_EDIT);
        this.viewer.setVisible(link != null);

        if (link == null)
        {
            this.link = null;
        }
        else
        {
            try
            {
                BBS.getTextures().bind(link);

                this.overlay.linkLinear = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER) == GL11.GL_LINEAR;
                this.link = link;

                this.viewer.fillTexture(this.link);
            }
            catch (Exception e)
            {}
        }
    }

    @Override
    public void requestNames()
    {
        Map<Link, Texture> map = BBS.getTextures().textures;

        this.overlay.textures.list.clear();
        this.overlay.textures.list.getList().addAll(map.keySet());
        this.overlay.textures.list.sort();
        this.overlay.textures.list.update();

        if (this.link == null && !this.overlay.textures.list.getList().isEmpty())
        {
            this.link = this.overlay.textures.list.getList().get(0);
        }

        this.pickLink(this.link);
        this.overlay.textures.list.setCurrent(this.link);
    }
}