package mchorse.bbs.ui.tileset.panels;

import mchorse.bbs.BBS;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.tileset.UITileSetEditorPanel;
import mchorse.bbs.ui.tileset.UIUVEditorOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import org.joml.Vector2i;

public class UIModelBlockFactory <T extends BlockModelFactory> extends UIElement
{
    public UIScrollView view;

    public UILabel title;
    public UIButton rename;
    public UIToggle collision;
    public UIToggle opaque;
    public UIToggle ao;
    public UIColor color;
    public UITrackpad lighting;
    public UIButton all;

    public UITrackpad cX;
    public UITrackpad cY;
    public UITrackpad cZ;
    public UITrackpad cW;
    public UITrackpad cH;
    public UITrackpad cD;

    public UITileSetEditorPanel editor;
    public T model;

    public UIModelBlockFactory(UITileSetEditorPanel editor)
    {
        super();

        this.editor = editor;

        this.view = UI.scrollView(5, 10);
        this.view.relative(this).w(1F).h(1F);

        this.rename = new UIButton(UIKeys.TILE_SET_GENERAL_RENAME, (b) -> this.showRename());
        this.collision = new UIToggle(UIKeys.TILE_SET_GENERAL_COLLISION, (b) ->
        {
            this.model.collision = b.getValue();
            this.editor.dirty();
        });
        this.opaque = new UIToggle(UIKeys.TILE_SET_GENERAL_OPAQUE, (b) ->
        {
            this.model.opaque = b.getValue();
            this.recompile();
        });
        this.ao = new UIToggle(UIKeys.TILE_SET_GENERAL_BAKED_AO, (b) ->
        {
            this.model.ao = b.getValue();
            this.recompile();
        });
        this.color = new UIColor((c) ->
        {
            this.model.color.set(c, false);
            this.recompile();
        });
        this.color.tooltip(UIKeys.TILE_SET_GENERAL_TINT);
        this.lighting = new UITrackpad((v) ->
        {
            this.model.lighting = v.intValue();
            this.recompile();
        });
        this.lighting.limit(0, 15, true).tooltip(UIKeys.TILE_SET_GENERAL_LIGHTING);
        this.all = new UIButton(UIKeys.TILE_SET_GENERAL_ALL, (b) -> this.editUV(this.model.allUV));

        this.title = UI.label(IKey.EMPTY).background();

        this.view.add(this.title.marginBottom(6));
        this.view.add(this.rename, this.collision, this.opaque, this.ao, this.color, this.lighting);
        this.view.add(this.all);
        this.add(this.view);
    }

    protected void addCollisionBoxFields()
    {
        this.cX = new UITrackpad((v) ->
        {
            this.model.collisionBox.x = v;
            this.editor.dirty();
        });
        this.cX.tooltip(UIKeys.TILE_SET_GENERAL_X_OFFSET);
        this.cY = new UITrackpad((v) ->
        {
            this.model.collisionBox.y = v;
            this.editor.dirty();
        });
        this.cY.tooltip(UIKeys.TILE_SET_GENERAL_Y_OFFSET);
        this.cZ = new UITrackpad((v) ->
        {
            this.model.collisionBox.z = v;
            this.editor.dirty();
        });
        this.cZ.tooltip(UIKeys.TILE_SET_GENERAL_Z_OFFSET);
        this.cW = new UITrackpad((v) ->
        {
            this.model.collisionBox.w = v;
            this.editor.dirty();
        }).limit(0);
        this.cW.tooltip(UIKeys.TILE_SET_GENERAL_COLLISION_W);
        this.cH = new UITrackpad((v) ->
        {
            this.model.collisionBox.h = v;
            this.editor.dirty();
        }).limit(0);
        this.cH.tooltip(UIKeys.TILE_SET_GENERAL_COLLISION_H);
        this.cD = new UITrackpad((v) ->
        {
            this.model.collisionBox.d = v;
            this.editor.dirty();
        }).limit(0);
        this.cD.tooltip(UIKeys.TILE_SET_GENERAL_COLLISION_D);

        this.view.add(UI.label(UIKeys.TILE_SET_GENERAL_COLLISION_BOX).background().marginTop(12));
        this.view.add(UI.row(this.cX, this.cY, this.cZ));
        this.view.add(UI.row(this.cW, this.cH, this.cD));
    }

    private void updateTitle()
    {
        IKey type = UIKeys.C_BLOCK_MODEL.get(BBS.getFactoryBlockModels().getType(this.model));

        this.title.label = IKey.raw("%s (%s)").format(type, this.model.blockId.toString());
    }

    private void showRename()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(UIKeys.GENERAL_RENAME, UIKeys.TILE_SET_GENERAL_RENAME_DESCRIPTION, this::renameBlockId);

        panel.text.setText(this.model.blockId.toString());
        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void renameBlockId(String s)
    {
        if (!s.contains(Link.SOURCE_SEPARATOR))
        {
            return;
        }

        this.model.blockId = LinkUtils.create(s);

        this.editor.dirty();
        this.updateTitle();
    }

    protected void editUV(Vector2i uv)
    {
        UIUVEditorOverlayPanel panel = new UIUVEditorOverlayPanel(UIKeys.TILE_SET_GENERAL_EDIT_UV, this.editor.getBlockSet().atlas, this::recompile);

        panel.uv.setUVZoom(uv, 16, 16, 3);

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    public void recompile()
    {
        this.model.compile();
        this.editor.dirty();
    }

    public void fill(T model)
    {
        this.model = model;

        this.collision.setValue(model.collision);
        this.opaque.setValue(model.opaque);
        this.ao.setValue(model.ao);
        this.color.setColor(model.color.getRGBColor());
        this.lighting.setValue(model.lighting);

        if (this.cX != null)
        {
            this.cX.setValue(model.collisionBox.x);
            this.cY.setValue(model.collisionBox.y);
            this.cZ.setValue(model.collisionBox.z);
            this.cW.setValue(model.collisionBox.w);
            this.cH.setValue(model.collisionBox.h);
            this.cD.setValue(model.collisionBox.d);
        }

        this.updateTitle();
    }
}