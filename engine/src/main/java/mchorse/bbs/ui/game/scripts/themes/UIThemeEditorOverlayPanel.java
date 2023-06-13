package mchorse.bbs.ui.game.scripts.themes;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.input.UIColor;
import mchorse.bbs.ui.framework.elements.input.list.UIList;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UIEditorOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIPromptOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.scripts.highlighting.SyntaxStyle;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public class UIThemeEditorOverlayPanel extends UIEditorOverlayPanel<UIThemeEditorOverlayPanel.SyntaxStyleEntry>
{
    public static final String CODE_SAMPLE = "/* Main function */\n" +
        "function main(e) {\n" +
        "    // Set subject's position one block higher\n" +
        "    var pos = e.subject().position();\n" +
        "    \n" +
        "    this.orange = \"Hello, world!\";\n" +
        "    \n" +
        "    e.send(this.orange);\n" +
        "    e.subject().position(pos.x, pos.y + 1, pos.z);\n" +
        "}";

    public UIIcon open;

    public UITextbox title;
    public UIToggle shadow;
    public UIColor primary;
    public UIColor secondary;
    public UIColor identifier;
    public UIColor special;
    public UIColor strings;
    public UIColor comments;
    public UIColor numbers;
    public UIColor other;
    public UIColor lineNumbers;
    public UIColor background;
    public UITextEditor preview;

    public UIThemeEditorOverlayPanel()
    {
        super(UIKeys.SYNTAX_THEME_MAIN);

        this.open = new UIIcon(Icons.FOLDER, (b) -> Themes.open());
        this.open.tooltip(UIKeys.SYNTAX_THEME_FOLDER).wh(16, 16);

        this.title = new UITextbox(100, (s) -> this.item.style.title = s);
        this.shadow = new UIToggle(UIKeys.SYNTAX_THEME_SHADOW, (b) -> this.item.style.shadow = b.getValue());
        this.primary = new UIColor((c) ->
        {
            this.item.style.primary = c;
            this.preview.resetHighlight();
        });
        this.primary.tooltip(UIKeys.SYNTAX_THEME_COLORS_PRIMARY);
        this.secondary = new UIColor((c) ->
        {
            this.item.style.secondary = c;
            this.preview.resetHighlight();
        });
        this.secondary.tooltip(UIKeys.SYNTAX_THEME_COLORS_SECONDARY);
        this.identifier = new UIColor((c) ->
        {
            this.item.style.identifier = c;
            this.preview.resetHighlight();
        });
        this.identifier.tooltip(UIKeys.SYNTAX_THEME_COLORS_IDENTIFIER);
        this.special = new UIColor((c) ->
        {
            this.item.style.special = c;
            this.preview.resetHighlight();
        });
        this.special.tooltip(UIKeys.SYNTAX_THEME_COLORS_SPECIAL);
        this.strings = new UIColor((c) ->
        {
            this.item.style.strings = c;
            this.preview.resetHighlight();
        });
        this.strings.tooltip(UIKeys.SYNTAX_THEME_COLORS_STRINGS);
        this.comments = new UIColor((c) ->
        {
            this.item.style.comments = c;
            this.preview.resetHighlight();
        });
        this.comments.tooltip(UIKeys.SYNTAX_THEME_COLORS_COMMENTS);
        this.numbers = new UIColor((c) ->
        {
            this.item.style.numbers = c;
            this.preview.resetHighlight();
        });
        this.numbers.tooltip(UIKeys.SYNTAX_THEME_COLORS_NUMBERS);
        this.other = new UIColor((c) ->
        {
            this.item.style.other = c;
            this.preview.resetHighlight();
        });
        this.other.tooltip(UIKeys.SYNTAX_THEME_COLORS_OTHER);
        this.lineNumbers = new UIColor((c) -> this.item.style.lineNumbers = c);
        this.lineNumbers.tooltip(UIKeys.SYNTAX_THEME_BACKGROUND_COLORS_LINE_NUMBERS);
        this.background = new UIColor((c) -> this.item.style.background = c);
        this.background.tooltip(UIKeys.SYNTAX_THEME_BACKGROUND_COLORS_BACKGROUND);
        this.preview = new UITextEditor(null);

        this.editor.add(UI.label(UIKeys.SYNTAX_THEME_TITLE), this.title, this.shadow);
        this.editor.add(UI.label(UIKeys.SYNTAX_THEME_COLORS_TITLE).marginTop(12));
        this.editor.add(UI.row(this.primary, this.secondary));
        this.editor.add(UI.row(this.identifier, this.special));
        this.editor.add(UI.row(this.strings, this.comments));
        this.editor.add(UI.row(this.numbers, this.other));
        this.editor.add(UI.label(UIKeys.SYNTAX_THEME_BACKGROUND_COLORS_TITLE).marginTop(12));
        this.editor.add(UI.row(this.lineNumbers, this.background));

        this.content.h(0.5F);
        this.preview.relative(this).y(0.5F, 28).w(1F).hTo(this.area, 1F);
        this.preview.setText(CODE_SAMPLE);

        this.add(this.preview.background());
        this.icons.add(this.open);

        this.loadThemes();
    }

    private void loadThemes()
    {
        /* Load theme files from the folder */
        for (File file : Themes.themes())
        {
            SyntaxStyle style = Themes.readTheme(file);

            if (style == null)
            {
                continue;
            }

            SyntaxStyleEntry entry = new SyntaxStyleEntry(file, style);

            this.list.add(entry);
        }

        /* If there are no files, just load the default one */
        if (this.list.getList().isEmpty())
        {
            this.list.add(new SyntaxStyleEntry(Themes.themeFile("monokai.json"), new SyntaxStyle()));
        }

        for (SyntaxStyleEntry entry : this.list.getList())
        {
            if (entry.file.getName().equals(BBSSettings.scriptEditorSyntaxStyle.getFile()))
            {
                this.pickItem(entry, true);

                break;
            }
        }

        /* Just in case if something went wrong with the config */
        if (this.list.isDeselected())
        {
            this.list.setIndex(0);
            this.pickItem(this.list.getCurrentFirst(), true);
        }
    }

    @Override
    protected UIList<SyntaxStyleEntry> createList()
    {
        return new UISyntaxStyleListElement((l) -> this.pickItem(l.get(0), false));
    }

    @Override
    protected IKey getAddLabel()
    {
        return UIKeys.SYNTAX_THEME_CONTEXT_ADD;
    }

    @Override
    protected IKey getRemoveLabel()
    {
        return UIKeys.SYNTAX_THEME_CONTEXT_REMOVE;
    }

    @Override
    protected void addItem()
    {
        UIPromptOverlayPanel panel = new UIPromptOverlayPanel(
            UIKeys.ADD,
            UIKeys.SYNTAX_THEME_MODAL_ADD,
            this::addNewTheme
        );

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void addNewTheme(String string)
    {
        File file = Themes.themeFile(string);

        if (!file.isFile())
        {
            SyntaxStyle style = new SyntaxStyle();
            SyntaxStyleEntry entry = new SyntaxStyleEntry(file, style);

            style.title = "";
            this.list.add(entry);
            this.list.update();
            this.pickItem(entry, true);
        }
    }

    @Override
    protected void removeItem()
    {
        this.list.getCurrentFirst().file.delete();

        super.removeItem();
    }

    @Override
    protected void pickItem(SyntaxStyleEntry item, boolean select)
    {
        item.save();
        BBSSettings.scriptEditorSyntaxStyle.set(item.file.getName(), item.style);

        this.preview.getHighlighter().setStyle(item.style);
        this.preview.resetHighlight();

        super.pickItem(item, select);
    }

    @Override
    protected void fillData(SyntaxStyleEntry item)
    {
        this.title.setText(item.style.title);
        this.shadow.setValue(item.style.shadow);
        this.primary.setColor(item.style.primary);
        this.secondary.setColor(item.style.secondary);
        this.identifier.setColor(item.style.identifier);
        this.special.setColor(item.style.special);
        this.strings.setColor(item.style.strings);
        this.comments.setColor(item.style.comments);
        this.numbers.setColor(item.style.numbers);
        this.lineNumbers.setColor(item.style.lineNumbers);
        this.background.setColor(item.style.background);
        this.other.setColor(item.style.other);
    }

    @Override
    public void onClose()
    {
        SyntaxStyleEntry item = this.list.getCurrentFirst();

        item.save();
        BBSSettings.scriptEditorSyntaxStyle.set(item.file.getName(), item.style);

        super.onClose();
    }

    public static class UISyntaxStyleListElement extends UIList<SyntaxStyleEntry>
    {
        public UISyntaxStyleListElement(Consumer<List<SyntaxStyleEntry>> callback)
        {
            super(callback);

            this.scroll.scrollItemSize = 16;
        }

        @Override
        protected String elementToString(int i, SyntaxStyleEntry element)
        {
            if (element.style.title.trim().isEmpty())
            {
                return element.file.getName();
            }

            return element.style.title + " (" + element.file.getName() + ")";
        }
    }

    public static class SyntaxStyleEntry
    {
        public File file;
        public SyntaxStyle style;

        public SyntaxStyleEntry(File file, SyntaxStyle style)
        {
            this.file = file;
            this.style = style;
        }

        public void save()
        {
            Themes.writeTheme(this.file, this.style);
        }
    }
}