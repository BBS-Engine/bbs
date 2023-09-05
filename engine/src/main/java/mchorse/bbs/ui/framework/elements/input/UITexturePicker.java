package mchorse.bbs.ui.framework.elements.input;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.texture.Texture;
import mchorse.bbs.graphics.window.IFileDropListener;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.textures.UITextureEditor;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.list.UIFileLinkList;
import mchorse.bbs.ui.framework.elements.input.list.UIFilteredLinkList;
import mchorse.bbs.ui.framework.elements.input.multilink.UIMultiLinkEditor;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.utils.EventPropagation;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Timer;
import mchorse.bbs.utils.colors.Colors;
import mchorse.bbs.utils.resources.FilteredLink;
import mchorse.bbs.utils.resources.LinkUtils;
import mchorse.bbs.utils.resources.MultiLink;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

/**
 * Texture picker GUI
 * 
 * This bad boy allows picking a texture from the file browser, and also 
 * it allows creating multi-skins. See {@link MultiLink} for more information.
 */
public class UITexturePicker extends UIElement implements IFileDropListener
{
    public UIElement right;
    public UITextbox text;
    public UIIcon close;
    public UIIcon folder;
    public UIIcon pixelEdit;
    public UIFileLinkList picker;

    public UIButton multi;
    public UIFilteredLinkList multiList;
    public UIMultiLinkEditor editor;
    public UITextureEditor pixelEditor;

    public UIElement buttons;
    public UIIcon add;
    public UIIcon remove;
    public UIIcon edit;

    public Consumer<Link> callback;

    public MultiLink multiLink;
    public FilteredLink currentFiltered;
    public Link current;

    private Timer lastTyped = new Timer(1000);
    private Timer lastChecked = new Timer(1000);
    private String typed = "";

    public static UITexturePicker open(UIElement parent, Link current, Consumer<Link> callback)
    {
        if (!parent.getChildren(UITexturePicker.class).isEmpty())
        {
            return null;
        }

        UITexturePicker picker = new UITexturePicker(callback);

        picker.fill(current);
        picker.relative(parent).full();
        picker.resize();

        parent.add(picker);

        return picker;
    }

    public UITexturePicker(Consumer<Link> callback)
    {
        super();

        this.right = new UIElement();
        this.text = new UITextbox(1000, (str) -> this.selectCurrent(str.isEmpty() ? null : LinkUtils.create(str)));
        this.text.delayedInput().context((menu) ->
        {
            Link location = this.parseLink();

            menu.action(Icons.COPY, UIKeys.TEXTURE_EDITOR_CONTEXT_COPY, this::copyLink);

            if (location != null)
            {
                menu.action(Icons.PASTE, UIKeys.TEXTURE_EDITOR_CONTEXT_PASTE, () -> this.pasteLink(location));
            }
        });
        this.close = new UIIcon(Icons.CLOSE, (b) -> this.close());
        this.folder = new UIIcon(Icons.FOLDER, (b) -> this.openFolder());
        this.folder.tooltip(UIKeys.TEXTURE_OPEN_FOLDER, Direction.BOTTOM);
        this.pixelEdit = new UIIcon(Icons.EDIT, (b) -> this.togglePixelEditor());
        this.picker = new UIFileLinkList(this::selectCurrent) {
            @Override
            public void setPath(Link folder, boolean fastForward)
            {
                super.setPath(folder, fastForward);

                UITexturePicker.this.updateFolderButton();
            }
        };
        this.picker.filter((l) -> l.path.endsWith("/") || l.path.endsWith(".png")).cancelScrollEdge();

        this.multi = new UIButton(UIKeys.TEXTURE_MULTISKIN, (b) -> this.toggleMulti());
        this.multiList = new UIFilteredLinkList((list) -> this.setFilteredLink(list.get(0)));
        this.multiList.sorting();

        this.editor = new UIMultiLinkEditor(this);
        this.editor.setVisible(false);

        this.buttons = new UIElement();
        this.add = new UIIcon(Icons.ADD, (b) -> this.addMulti());
        this.remove = new UIIcon(Icons.REMOVE, (b) -> this.removeMulti());
        this.edit = new UIIcon(Icons.EDIT, (b) -> this.toggleEditor());

        UIElement icons = UI.row(0, this.pixelEdit, this.folder, this.close);

        icons.relative(this).x(1F, -10).y(10).w(60).h(20).anchorX(1F);

        this.right.relative(this).full();
        this.text.relative(this.multi).x(1F, 20).wTo(icons.area).h(20);
        this.picker.relative(this.right).set(10, 30, 0, 0).w(1, -10).h(1, -30);

        this.multi.relative(this).set(10, 10, 100, 20);
        this.multiList.relative(this).set(10, 35, 100, 0).hTo(this.buttons.getFlex());
        this.editor.relative(this).set(120, 0, 0, 0).w(1F, -120).h(1F);

        this.buttons.relative(this).y(1F, -20).wTo(this.right.area).h(20);
        this.add.relative(this.buttons).set(0, 0, 20, 20);
        this.remove.relative(this.add).set(20, 0, 20, 20);
        this.edit.relative(this.buttons).wh(20, 20).x(1F, -20);

        this.right.add(icons, this.text, this.picker);
        this.buttons.add(this.add, this.remove, this.edit);
        this.add(this.multi, this.multiList, this.right, this.editor, this.buttons);

        this.callback = callback;

        this.fill(null);
        this.markContainer().eventPropagataion(EventPropagation.BLOCK);
    }

    private Link parseLink()
    {
        MapType map = Window.getClipboardMap();

        return map == null ? null : LinkUtils.create(map.get("link"));
    }

    private void copyLink()
    {
        BaseType base = LinkUtils.toData(this.multiLink != null ? this.multiLink : this.current);

        if (base == null)
        {
            Window.setClipboard("");
        }
        else
        {
            MapType map = new MapType();

            map.put("link", base);

            Window.setClipboard(map);
        }
    }

    private void pasteLink(Link location)
    {
        this.setMulti(location, true);
    }

    public void close()
    {
        boolean wasVisible = this.hasParent();

        this.editor.close();
        this.removeFromParent();

        if (this.callback != null && wasVisible)
        {
            if (this.multiLink != null)
            {
                this.multiLink.recalculateId();
            }

            this.callback.accept(this.multiLink != null ? this.multiLink : this.current);
        }
    }

    @Override
    public void acceptFilePaths(String[] paths)
    {
        File target = BBS.getProvider().getFile(this.picker.path);

        if (target == null || !target.isDirectory())
        {
            return;
        }

        for (String path : paths)
        {
            File file = new File(path);

            if (file.isFile())
            {
                String name = file.getName();
                File copy = IOUtils.findNonExistingFile(new File(target, name));

                try
                {
                    Files.copy(file.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        this.refresh();
    }

    public void refresh()
    {
        this.picker.update();
        this.updateFolderButton();
    }

    public void openFolder()
    {
        File target = BBS.getProvider().getFile(this.picker.path);

        if (target != null && target.isDirectory())
        {
            UIUtils.openFolder(target);
        }
    }

    public void togglePixelEditor()
    {
        if (this.current == null || this.multiLink != null)
        {
            return;
        }

        if (this.pixelEditor == null)
        {
            this.pixelEditor = new UITextureEditor();
            this.pixelEditor.fillTexture(this.current);
            this.pixelEditor.setEditing(true);

            UIIcon close = new UIIcon(Icons.CLOSE, (b) -> this.togglePixelEditor());

            this.pixelEditor.savebar.add(close);
            this.pixelEditor.relative(this).full();
            this.pixelEditor.resize();

            this.add(this.pixelEditor);
        }
        else
        {
            this.pixelEditor.fillTexture(null);
            this.pixelEditor.removeFromParent();
            this.pixelEditor = null;
        }

        this.right.setVisible(this.pixelEditor == null);
        this.multi.setVisible(this.pixelEditor == null);
    }

    public void updateFolderButton()
    {
        File target = BBS.getProvider().getFile(this.picker.path);

        this.folder.setEnabled(target != null && target.isDirectory());
    }

    public void fill(Link link)
    {
        this.setMulti(link, false);
    }

    /**
     * Add a {@link Link} to the MultiLink
     */
    private void addMulti()
    {
        FilteredLink filtered = this.currentFiltered.copyFiltered();

        this.multiList.add(filtered);
        this.multiList.setIndex(this.multiList.getList().size() - 1);
        this.setFilteredLink(this.multiList.getCurrent().get(0));
    }

    /**
     * Remove currently selected {@link Link} from multiLink
     */
    private void removeMulti()
    {
        int index = this.multiList.getIndex();

        if (index >= 0 && this.multiList.getList().size() > 1)
        {
            this.multiList.getList().remove(index);
            this.multiList.update();
            this.multiList.setIndex(index - 1);

            if (this.multiList.getIndex() >= 0)
            {
                this.setFilteredLink(this.multiList.getCurrent().get(0));
            }
        }
    }

    private void setFilteredLink(FilteredLink location)
    {
        this.currentFiltered = location;
        this.displayCurrent(location.path);
        this.editor.setLink(location);
    }

    private void toggleEditor()
    {
        this.editor.toggleVisible();
        this.right.setVisible(!this.editor.isVisible());

        if (this.editor.isVisible())
        {
            this.editor.resetView();
        }
    }

    /**
     * Display current resource location (it's just for visual, not 
     * logic)
     */
    protected void displayCurrent(Link link)
    {
        this.current = link;

        this.text.setText(link == null ? "" : link.toString());
        this.text.textbox.moveCursorToStart();

        this.picker.setPath(link == null ? null : link.parent());
        this.picker.setCurrent(link);
    }

    /**
     * Select current resource location
     */
    protected void selectCurrent(Link link)
    {
        if (link != null && !BBS.getTextures().has(link))
        {
            return;
        }

        this.current = link;

        if (this.multiLink != null)
        {
            if (link == null && this.multiLink.children.size() == 1)
            {
                this.currentFiltered.path = null;
                this.toggleMulti();
            }
            else
            {
                this.currentFiltered.path = link;
            }
        }
        else if (this.callback != null)
        {
            this.callback.accept(link);
        }

        this.picker.setCurrent(link);
        this.text.setText(link.toString());
    }

    protected void toggleMulti()
    {
        if (this.multiLink != null)
        {
            this.setMulti(this.multiLink.children.get(0).path, true);
        }
        else if (this.current != null)
        {
            this.setMulti(new MultiLink(this.current.toString()), true);
        }
        else
        {
            UIFileLinkList.FileLink link = this.picker.getCurrentFirst();

            if (link != null)
            {
                this.setMulti(link.link, true);
            }
        }
    }

    protected void setMulti(Link skin, boolean notify)
    {
        if (this.editor.isVisible())
        {
            this.toggleEditor();
        }

        boolean show = skin instanceof MultiLink;

        if (show)
        {
            this.multiLink = (MultiLink) ((MultiLink) skin).copy();
            this.setFilteredLink(this.multiLink.children.get(0));

            this.multiList.setIndex(this.multiLink.children.isEmpty() ? -1 : 0);
            this.multiList.setList(this.multiLink.children);

            if (this.current != null)
            {
                this.multiList.setIndex(0);
            }

            this.right.x(120).w(1F, -120);
        }
        else
        {
            this.multiLink = null;

            this.right.x(0).w(1F);
            this.displayCurrent(skin);
        }

        if (notify)
        {
            if (show && this.callback != null)
            {
                this.multiLink.recalculateId();
                this.callback.accept(skin);
            }
            else
            {
                this.selectCurrent(skin);
            }
        }

        this.multiList.setVisible(show);
        this.buttons.setVisible(show);

        this.resize();
        this.updateFolderButton();
    }

    @Override
    public boolean subKeyPressed(UIContext context)
    {
        if (context.isPressed(GLFW.GLFW_KEY_ENTER))
        {
            UIFileLinkList.FileLink link = this.picker.getCurrentFirst();

            if (link != null && link.folder)
            {
                this.picker.setPath(link.link);
            }
            else if (link != null)
            {
                this.selectCurrent(link.link);
            }

            this.typed = "";

            return true;
        }
        else if (context.isHeld(GLFW.GLFW_KEY_UP))
        {
            return this.moveCurrent(-1, Window.isShiftPressed());
        }
        else if (context.isHeld(GLFW.GLFW_KEY_DOWN))
        {
            return this.moveCurrent(1, Window.isShiftPressed());
        }
        else if (context.isPressed(GLFW.GLFW_KEY_ESCAPE))
        {
            this.close();

            return true;
        }

        return super.subKeyPressed(context);
    }

    protected boolean moveCurrent(int factor, boolean top)
    {
        int index = this.picker.getIndex() + factor;
        int length = this.picker.getList().size();

        if (index < 0) index = length - 1;
        else if (index >= length) index = 0;

        if (top) index = factor > 0 ? length - 1 : 0;

        this.picker.setIndex(index);
        this.picker.scroll.scrollIntoView(index * this.picker.scroll.scrollItemSize);
        this.typed = "";

        return true;
    }

    @Override
    public boolean subTextInput(UIContext context)
    {
        return this.pickByTyping(context, context.getInputCharacter());
    }

    protected boolean pickByTyping(UIContext context, char inputChar)
    {
        if (!context.font.hasCharacter(inputChar))
        {
            return false;
        }

        if (this.lastTyped.checkReset())
        {
            this.typed = "";
        }

        this.typed += Character.toString(inputChar);
        this.lastTyped.mark();

        for (UIFileLinkList.FileLink entry : this.picker.getList())
        {
            String name = entry.title;

            if (name.startsWith(this.typed))
            {
                this.picker.setCurrentScroll(entry);

                return true;
            }
        }

        return true;
    }

    @Override
    public void render(UIContext context)
    {
        /* Refresh the list */
        if (this.lastChecked.checkRepeat())
        {
            File file = BBS.getProvider().getFile(this.picker.path);
            int scroll = this.picker.scroll.scroll;

            if (file != null)
            {
                UIFileLinkList.FileLink selected = this.picker.getCurrentFirst();

                this.picker.setPath(this.picker.path, false);

                if (selected != null)
                {
                    this.picker.setCurrent(selected.link);
                }
            }

            this.picker.scroll.scrollTo(scroll);
        }

        /* Draw the background */
        context.batcher.gradientVBox(this.area.x, this.area.y, this.area.ex(), this.area.ey(), Colors.A50, Colors.A100);

        if (this.multiList.isVisible())
        {
            context.batcher.box(this.area.x, this.area.y, this.area.x + 120, this.area.ey(), 0xff181818);
            context.batcher.box(this.area.x, this.area.y, this.area.x + 120, this.area.y + 30, Colors.A25);
            context.batcher.gradientVBox(this.area.x, this.area.ey() - 20, this.buttons.area.ex(), this.area.ey(), 0, Colors.A50);
        }

        if (this.editor.isVisible())
        {
            this.edit.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
        }

        super.render(context);

        /* Draw the overlays */
        if (this.right.isVisible())
        {
            if (this.picker.getList().isEmpty())
            {
                String label = UIKeys.TEXTURE_NO_DATA.get();
                int w = context.font.getWidth(label);

                context.batcher.text(label, this.picker.area.mx(w), this.picker.area.my() - 8);
            }

            if (!this.lastTyped.check() && this.lastTyped.enabled)
            {
                int w = context.font.getWidth(this.typed);
                int x = this.text.area.x;
                int y = this.text.area.ey();

                context.batcher.box(x, y, x + w + 4, y + 4 + context.font.getHeight(), Colors.A50 | BBSSettings.primaryColor.get());
                context.batcher.textShadow(this.typed, x + 2, y + 2);
            }

            Link link = this.current;

            /* Draw preview */
            if (link != null)
            {
                Texture texture = context.render.getTextures().getTexture(link);

                int w = texture.width;
                int h = texture.height;

                int x = this.area.ex();
                int y = this.area.ey();
                int fw = w;
                int fh = h;

                if (fw > 128 || fh > 128)
                {
                    fw = fh = 128;

                    if (w > h)
                    {
                        fh = (int) ((h / (float) w) * fw);
                    }
                    else if (h > w)
                    {
                        fw = (int) ((w / (float) h) * fh);
                    }
                }

                x -= fw + 10;
                y -= fh + 10;

                context.batcher.iconArea(Icons.CHECKBOARD, x, y, fw, fh);
                context.batcher.fullTexturedBox(texture, x, y, fw, fh);
            }
        }
    }
}