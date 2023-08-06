package mchorse.bbs.ui.tileset;

import mchorse.bbs.BBS;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UISearchList;
import mchorse.bbs.ui.framework.elements.overlay.UIConfirmOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.math.MathUtils;
import mchorse.bbs.voxel.tilesets.BlockSet;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactoryData;

import java.util.List;

public class UIBlockModelFactoriesOverlayPanel extends UIOverlayPanel
{
    public UISearchList<BlockModelFactory> searchList;
    public UIBlockModelFactoryList list;

    public UIIcon add;
    public UIIcon replace;
    public UIIcon remove;

    private UITileSetEditorPanel menu;

    public UIBlockModelFactoriesOverlayPanel(UITileSetEditorPanel menu, IKey title)
    {
        super(title);

        this.menu = menu;

        this.add = new UIIcon(Icons.BLOCK, this::addModel);
        this.add.tooltip(UIKeys.TILE_SET_LIST_ADD_MODEL);
        this.replace = new UIIcon(Icons.REFRESH, this::replaceModel);
        this.replace.tooltip(UIKeys.TILE_SET_LIST_REPLACE_MODEL);
        this.remove = new UIIcon(Icons.TRASH, this::removeModel);
        this.remove.tooltip(UIKeys.TILE_SET_LIST_REMOVE_MODEL);

        this.list = new UIBlockModelFactoryList((l) -> this.menu.selectBlock(l.get(0)));
        this.list.sorting().afterDrop((l) -> this.menu.dirty()).setList(menu.getBlockSet().factories);
        this.list.context((m) ->
        {
            m.shadow().action(Icons.COPY, UIKeys.TILE_SET_LIST_CONTEXT_COPY, () ->
            {
                Window.setClipboard(BBS.getFactoryBlockModels().toData(this.menu.panel.model), "_BlockModelFactoriesCopy");
            });

            MapType map = Window.getClipboardMap("_BlockModelFactoriesCopy");

            if (map != null)
            {
                m.action(Icons.PASTE, UIKeys.TILE_SET_LIST_CONTEXT_PASTE, () ->
                {
                    this.addModel(BBS.getFactoryBlockModels().fromData(map));
                });
            }
        });

        this.searchList = new UISearchList<>(this.list);
        this.searchList.label(UIKeys.SEARCH);
        this.searchList.relative(this.content).full().x(6).w(1F, -12);

        if (this.menu.panel != null)
        {
            this.list.setCurrentScroll(menu.panel.model);
        }

        this.icons.add(this.add, this.replace, this.remove);
        this.content.add(this.searchList);
    }

    private void addModel(UIIcon b)
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            menu.shadow();

            for (Link key : BBS.getFactoryBlockModels().getKeys())
            {
                BlockModelFactoryData data = BBS.getFactoryBlockModels().getData(key);

                menu.action(data.icon, UIKeys.C_BLOCK_MODEL.get(key), () -> this.addModel(BBS.getFactoryBlockModels().create(key)));
            }
        });
    }

    private void addModel(BlockModelFactory model)
    {
        this.menu.getBlockSet().registerFactory(model);

        this.list.update();
        this.list.setCurrentScroll(model);

        this.menu.selectBlock(model);
        this.menu.dirty();
    }

    private void replaceModel(UIIcon b)
    {
        this.getContext().replaceContextMenu((menu) ->
        {
            menu.shadow();

            for (Link key : BBS.getFactoryBlockModels().getKeys())
            {
                BlockModelFactoryData data = BBS.getFactoryBlockModels().getData(key);

                menu.action(data.icon, UIKeys.C_BLOCK_MODEL.get(key), () -> this.replaceModel(BBS.getFactoryBlockModels().create(key)));
            }
        });
    }

    private void replaceModel(BlockModelFactory model)
    {
        BlockSet blockSet = this.menu.getBlockSet();
        int index = blockSet.factories.indexOf(this.menu.panel.model);
        BlockModelFactory old = blockSet.factories.get(index);

        model.fromData(old.toData());
        blockSet.factories.set(index, model);
        blockSet.rebuild();

        this.list.update();
        this.list.setCurrentScroll(model);

        this.menu.selectBlock(model);
        this.menu.dirty();
    }

    private void removeModel(UIIcon b)
    {
        UIOverlay.addOverlay(this.getContext(), new UIConfirmOverlayPanel(
            UIKeys.REMOVE,
            UIKeys.TILE_SET_LIST_REMOVE_MODEL_WARNING,
            (confirm) ->
            {
                if (confirm)
                {
                    this.removeModel();
                }
            }
        ));
    }

    private void removeModel()
    {
        if (this.menu.panel != null)
        {
            List<BlockModelFactory> factories = this.menu.getBlockSet().factories;
            int index = factories.indexOf(this.menu.panel.model);

            factories.remove(index);
            this.menu.dirty();

            this.menu.selectBlock(factories.get(MathUtils.clamp(index, 0, factories.size() - 1)));

            this.list.update();
            this.list.setCurrentScroll(this.menu.panel.model);
        }
    }
}