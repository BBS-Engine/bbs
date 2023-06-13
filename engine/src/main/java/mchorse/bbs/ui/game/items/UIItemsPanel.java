package mchorse.bbs.ui.game.items;

import mchorse.bbs.BBS;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.forms.FormUtils;
import mchorse.bbs.game.items.Item;
import mchorse.bbs.game.items.ItemEntry;
import mchorse.bbs.game.items.ItemManager;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UISidebarDashboardPanel;
import mchorse.bbs.ui.forms.UIFormPalette;
import mchorse.bbs.ui.forms.UINestedEdit;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextarea;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.input.text.utils.TextLine;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIStringOverlayPanel;
import mchorse.bbs.ui.tileset.UIUVEditorOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;
import org.joml.Vector2i;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UIItemsPanel extends UISidebarDashboardPanel
{
    public UIItemsOverlayPanel overlay;
    public UIIcon open;
    public UIIcon save;
    public UIIcon atlas;

    public UIScrollView fields;
    public UIButton editRender;
    public UINestedEdit editForm;
    public UIToggle extruded;
    public UIColor frameColor;
    public UIButton changeType;
    public UITextbox displayName;
    public UITextarea<TextLine> description;
    public UITrackpad maxStack;
    public UIItemEditor itemEditor;

    private ItemManager items;
    private ItemEntry entry;
    private boolean dirty;

    public UIItemsPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.overlay = new UIItemsOverlayPanel(UIKeys.PANELS_ITEMS, this);
        this.items = BBS.getItems();

        this.open = new UIIcon(Icons.MORE, (b) ->
        {
            UIOverlay.addOverlay(this.getContext(), this.overlay);
        });
        this.save = new UIIcon(Icons.SAVED, this::save);
        this.atlas = new UIIcon(Icons.MATERIAL, (b) -> this.openAtlasPicker());

        this.fields = UI.scrollView(5, 10);
        this.fields.scroll.opposite();
        this.fields.relative(this).w(200).h(1F);

        this.editRender = new UIButton(UIKeys.ITEM_PANEL_EDIT_RENDER_TILE, this::editRender);
        this.editForm = new UINestedEdit((editing) ->
        {
            UIFormPalette.open(this, editing, this.entry.render.form, (form) ->
            {
                this.entry.render.form = FormUtils.copy(form);
                this.editForm.setForm(form);
                this.dirty();
            });
        });
        this.editForm.tooltip(UIKeys.ITEM_PANEL_FORM_TOOLTIP);
        this.extruded = new UIToggle(UIKeys.ITEM_PANEL_EXTRUDED, (b) ->
        {
            this.entry.render.extruded = b.getValue();
            this.dirty();
        });
        this.extruded.tooltip(UIKeys.ITEM_PANEL_EXTRUDED_TOOLTIP);
        this.frameColor = new UIColor((c) ->
        {
            this.entry.render.frameColor = c;
            this.dirty();
        });
        this.changeType = new UIButton(UIKeys.ITEM_PANEL_CHANGE_TYPE, this::changeType);
        this.displayName = new UITextbox(100, (t) ->
        {
            this.entry.item.setDisplayName(t);
            this.dirty();
        });
        this.description = new UITextarea<TextLine>((t) ->
        {
            this.entry.item.setDescription(t);
            this.dirty();
        });
        this.description.background().h(100);
        this.maxStack = new UITrackpad((v) ->
        {
            this.entry.item.setMaxStack(v.intValue());
            this.dirty();
        });
        this.maxStack.limit(1).integer();

        this.iconBar.add(this.open, this.atlas, this.save);
        this.editor.add(this.fields);

        this.fields.add(UI.label(UIKeys.ITEM_PANEL_DISPLAY).background(), this.editRender);
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_FORM), this.editForm, this.extruded);
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_FRAME_COLOR), this.frameColor);
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_GENERAL).background().marginTop(8));
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_DISPLAY_NAME).marginTop(8), this.displayName);
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_DESCRIPTION).marginTop(8), this.description);
        this.fields.add(UI.label(UIKeys.ITEM_PANEL_MAX_STACK).marginTop(8), this.maxStack, this.changeType);

        this.fill(null, true);
    }

    public ItemManager getItems()
    {
        return this.items;
    }

    public ItemEntry getEntry()
    {
        return this.entry;
    }

    public void dirty()
    {
        this.dirty(true);
    }

    public void dirty(boolean dirty)
    {
        this.dirty = dirty;

        this.save.both(this.dirty ? Icons.SAVE : Icons.SAVED);
    }

    private void openAtlasPicker()
    {
        UITexturePicker.open(this.editor, this.items.getAtlas(), (l) ->
        {
            Texture texture = BBS.getTextures().getTexture(l);

            if (texture != null)
            {
                this.items.setAtlas(l);
                this.dirty();
            }
        });
    }

    private void save(UIIcon b)
    {
        File file = BBS.getAssetsPath("items.json");
        MapType data = this.items.toData();

        DataToString.writeSilently(file, data, true);
        this.items.generateExtruded();

        this.dirty(false);
    }

    private void editRender(UIButton b)
    {
        UIUVEditorOverlayPanel panel = new UIUVEditorOverlayPanel(UIKeys.TILE_SET_GENERAL_EDIT_UV, this.items.getAtlas(), this::dirty);
        Vector2i uv = this.entry.render.uv;

        panel.uv.setUVZoom(uv, 16, 16, 3);

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void changeType(UIButton b)
    {
        UIStringOverlayPanel panel = new UIStringOverlayPanel(
            UIKeys.ITEM_PANEL_CHANGE_TYPE_TITLE,
            BBS.getFactoryItems().getStringKeys(),
            this::changeType
        );

        Link type = BBS.getFactoryItems().getTypeSilent(this.entry.item);

        panel.set(type == null ? "" : type.toString());

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void changeType(String type)
    {
        Item item = new Item();

        if (!type.isEmpty())
        {
            try
            {
                item = BBS.getFactoryItems().create(Link.create(type));
            }
            catch (Exception e)
            {}
        }

        item.fromData(this.entry.item.toData());
        item.setId(this.entry.item.getId());
        this.entry.item = item;

        this.fill(this.entry, true);

        this.dirty();
    }

    public void fill(ItemEntry itemEntry, boolean select)
    {
        this.entry = itemEntry;

        this.fields.setVisible(itemEntry != null);

        if (this.itemEditor != null)
        {
            this.itemEditor.removeFromParent();
            this.itemEditor = null;
        }

        if (itemEntry != null)
        {
            this.editForm.setForm(itemEntry.render.form);
            this.extruded.setValue(itemEntry.render.extruded);
            this.frameColor.setColor(itemEntry.render.frameColor);
            this.displayName.setText(itemEntry.item.getDisplayName());
            this.description.setText(itemEntry.item.getDescription());
            this.maxStack.setValue(itemEntry.item.getMaxStack());

            try
            {
                Class<? extends UIItemEditor> data = BBS.getFactoryItems().getData(itemEntry.item);

                if (data != null)
                {
                    this.itemEditor = data.getConstructor(this.getClass(), itemEntry.item.getClass()).newInstance(this, itemEntry.item);

                    this.fields.add(this.itemEditor.marginTop(8));
                    this.fields.resize();
                }
            }
            catch (Exception e)
            {}
        }

        if (select)
        {
            this.overlay.itemList.list.setCurrentScroll(itemEntry);
        }
    }

    @Override
    public void requestNames()
    {
        ItemEntry entry = this.overlay.itemList.list.getCurrentFirst();
        List<ItemEntry> entryList = new ArrayList<ItemEntry>(this.items.items.values());

        this.overlay.itemList.list.clear();
        this.overlay.itemList.list.add(entryList);
        this.overlay.itemList.list.sort();

        this.overlay.itemList.list.setCurrentScroll(entry);
    }
}