package mchorse.sandbox.ui.l10n;

import mchorse.sandbox.ui.UIKeysApp;
import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.l10n.L10nUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.l10n.keys.LangKey;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.input.text.UITextbox;
import mchorse.bbs.ui.framework.elements.overlay.UILabelOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UITextareaOverlayPanel;
import mchorse.bbs.ui.framework.elements.utils.UILabel;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.colors.Colors;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UILanguageEditorOverlayPanel extends UIOverlayPanel
{
    private static String referenceLanguage = L10n.DEFAULT_LANGUAGE;

    public UIIcon save;
    public UIIcon folder;
    public UIIcon changeReference;
    public UIIcon copy;
    public UIIcon paste;
    public UILabel completion;

    public UIIcon missing;
    public UITextbox search;
    public UIScrollView keysView;

    private File target;
    private List<UILanguageKey> keys = new ArrayList<UILanguageKey>();
    private boolean viewMissing;
    private long updateCompletion;

    private File markedFile;
    private MapType markedKeys = new MapType();

    public static File getLangEditorFolder()
    {
        return BBS.getAssetsPath("lang_editor/" + BBSSettings.language.get());
    }

    public UILanguageEditorOverlayPanel()
    {
        super(UIKeysApp.LANGUAGE_EDITOR_TITLE.format(BBSSettings.language.get()));

        this.target = getLangEditorFolder();
        this.target.mkdirs();
        this.markedFile = new File(getLangEditorFolder(), "marked.json");

        this.save = new UIIcon(Icons.SAVED, (b) -> this.save());
        this.save.tooltip(UIKeysApp.LANGUAGE_EDITOR_SAVE).wh(16, 16);
        this.folder = new UIIcon(Icons.FOLDER, (b) -> UIUtils.openFolder(this.target));
        this.folder.tooltip(UIKeysApp.LANGUAGE_EDITOR_FOLDER).wh(16, 16);
        this.changeReference = new UIIcon(Icons.REFRESH, (b) -> this.changeReference());
        this.changeReference.tooltip(UIKeysApp.LANGUAGE_EDITOR_REFERENCE).wh(16, 16);
        this.copy = new UIIcon(Icons.COPY, (b) -> this.copy());
        this.copy.tooltip(UIKeysApp.LANGUAGE_EDITOR_COPY).wh(16, 16);
        this.paste = new UIIcon(Icons.PASTE, (b) -> this.paste());
        this.paste.tooltip(UIKeysApp.LANGUAGE_EDITOR_PASTE).wh(16, 16);
        this.completion = UI.label(IKey.EMPTY);
        this.completion.background(Colors.A50 | BBSSettings.primaryColor.get()).labelAnchor(1F, 0.5F);
        this.completion.relative(this.icons).x(-4).wh(160, 16).anchorX(1F);

        this.missing = new UIIcon(Icons.SEARCH, (b) -> this.viewOnlyMissing());
        this.missing.tooltip(UIKeysApp.LANGUAGE_EDITOR_MISSING);
        this.search = new UITextbox(this::search);
        this.search.placeholder(UIKeys.SEARCH);
        this.keysView = UI.scrollView(10, 5);

        this.missing.relative(this.content).x(1F, -20);
        this.search.relative(this.content).w(1F, -20);
        this.keysView.relative(this.content).y(20).w(1F).h(1F, -20);

        this.icons.add(this.save, this.folder, this.changeReference, this.paste, this.copy);
        this.content.add(this.search, this.missing, this.keysView);
        this.add(this.completion);

        this.buildEditor();
        this.readMarkedStrings();
        this.updateCompletionLabel();
    }

    public boolean hasMarked(String key)
    {
        return this.markedKeys.has(key);
    }

    public void setMarked(String key, boolean marked)
    {
        if (marked)
        {
            this.markedKeys.putBool(key, true);
        }
        else
        {
            this.markedKeys.remove(key);
        }

        this.dirty();

        DataToString.writeSilently(this.markedFile, this.markedKeys, true);
    }

    private void readMarkedStrings()
    {
        try
        {
            this.markedKeys = DataToString.mapFromString(IOUtils.readText(this.markedFile));
        }
        catch (Exception e)
        {}
    }

    private void rebuildEditor()
    {
        this.keys.clear();
        this.keysView.removeAll();
        this.buildEditor();
        this.keysView.resize();
    }

    /**
     * Collect all the strings and add them to the list
     */
    private void buildEditor()
    {
        L10n l10n = BBS.getL10n();
        MapType base = this.compile(l10n, L10n.DEFAULT_LANGUAGE);
        MapType reference = referenceLanguage.equals(L10n.DEFAULT_LANGUAGE) ? null : this.compile(l10n, referenceLanguage);

        List<LangKey> keys = new ArrayList<LangKey>(l10n.getStrings().values());

        L10nUtils.sortList(keys);

        for (LangKey key : keys)
        {
            String referenceString = reference == null ? "" : reference.getString(key.key);
            UILanguageKey ui = new UILanguageKey(this, key, base.getString(key.key, key.key), referenceString, this::dirty);

            this.keys.add(ui);
            this.keysView.add(ui);
        }
    }

    private MapType compile(L10n l10n, String lang)
    {
        MapType base = new MapType();

        for (Link link : l10n.getAllLinks(lang))
        {
            if (link.source.equals("assets") && link.path.startsWith("lang_editor/"))
            {
                continue;
            }

            try
            {
                String string = IOUtils.readText(BBS.getProvider().getAsset(link));

                base.combine(DataToString.mapFromString(string));
            }
            catch (Exception e)
            {}
        }

        return base;
    }

    private void dirty()
    {
        this.save.both(Icons.SAVE);

        this.updateCompletion = this.getContext().getTick() + 20;
    }

    private void updateCompletionLabel()
    {
        int i = 0;

        for (UILanguageKey key : this.keys)
        {
            if (!key.isStillSame())
            {
                i += 1;
            }
        }

        float p = i / (float) this.keys.size();

        String percentage = UITrackpad.format(p * 100) + "% (" + i + "/" + this.keys.size() + ")";

        this.completion.label = IKey.str(percentage);
        this.updateCompletion = 0;
    }

    private void save()
    {
        Map<String, LangKey> keyMap = new HashMap<String, LangKey>();

        for (UILanguageKey key : this.keys)
        {
            if (!key.isStillSame() && !this.markedKeys.has(key.getLangKey().key))
            {
                LangKey langKey = key.getLangKey();

                keyMap.put(langKey.key, langKey);
            }
        }

        L10nUtils.compile(this.target, keyMap);

        this.save.both(Icons.SAVED);
    }

    private void changeReference()
    {
        List<Label<String>> labels = BBS.getL10n().getSupportedLanguageLabels();
        UILabelOverlayPanel<String> panel = new UILabelOverlayPanel<String>(UIKeysApp.LANGUAGE_EDITOR_REFERENCE_TITLE, labels, (str) ->
        {
            referenceLanguage = str.value;

            this.rebuildEditor();
            this.updateCompletionLabel();
            this.search("");
        });

        panel.set(referenceLanguage);
        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void copy()
    {
        List<LangKey> keys = new ArrayList<LangKey>();
        MapType translated = new MapType(false);

        for (UILanguageKey key : this.keys)
        {
            if (!key.isStillSame())
            {
                keys.add(key.getLangKey());
            }
        }

        L10nUtils.sortList(keys);

        for (LangKey key : keys)
        {
            translated.putString(key.key, key.content);
        }

        String string = DataToString.toString(translated, true);
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length + 1);

        buffer.put(bytes);
        buffer.put((byte) '\0');
        buffer.flip();

        GLFW.glfwSetClipboardString(Window.getWindow(), buffer);

        MemoryUtil.memFree(buffer);
    }

    private void paste()
    {
        UITextareaOverlayPanel panel = new UITextareaOverlayPanel(UIKeys.PASTE, UIKeysApp.LANGUAGE_EDITOR_PASTE_DESCRIPTION, (t) ->
        {
            MapType map = DataToString.mapFromString(t);

            if (map == null)
            {
                return;
            }

            BBS.getL10n().overwrite(map);

            this.rebuildEditor();
            this.updateCompletionLabel();
            this.dirty();
        });

        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void viewOnlyMissing()
    {
        this.viewMissing = !this.viewMissing;

        this.search(this.search.getText());
    }

    private void search(String s)
    {
        boolean all = s.isEmpty();

        this.keysView.removeAll();

        for (UILanguageKey ui : this.keys)
        {
            LangKey langKey = ui.getLangKey();

            if (this.viewMissing && !ui.isStillSame())
            {
                continue;
            }

            if (all || langKey.key.contains(s) || langKey.content.contains(s))
            {
                this.keysView.add(ui);
            }
        }

        this.keysView.resize();
    }

    @Override
    protected void renderBackground(UIContext context)
    {
        if (this.updateCompletion > 0 && this.updateCompletion <= context.getTick())
        {
            this.updateCompletionLabel();
        }

        super.renderBackground(context);

        if (this.viewMissing)
        {
            this.missing.area.render(context.batcher, Colors.A50 | BBSSettings.primaryColor.get());
        }
    }
}