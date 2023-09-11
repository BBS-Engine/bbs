package mchorse.studio.ui.utility;

import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10nUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageFolderOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.StringUtils;
import mchorse.studio.ui.UIKeysApp;
import mchorse.studio.ui.l10n.UILanguageEditorOverlayPanel;

import java.io.File;

public class UIUtilityOverlayPanel extends UIOverlayPanel
{
    public Runnable callback;

    public UIScrollView view;
    public UITrackpad width;
    public UITrackpad height;

    public UIUtilityOverlayPanel(IKey title, Runnable callback)
    {
        super(title);

        this.callback = callback;

        this.view = UI.scrollView(5, 10, 140);
        this.view.relative(this.content).full();

        UIButton openGameDirectory = new UIButton(IKey.lazy("Game"), (b) -> this.openFolder(BBS.getGameFolder()));
        UIButton openAudioDirectory = new UIButton(IKey.lazy("Audio"), (b) -> this.openFolder(BBS.getAssetsPath("audio")));
        UIButton openModelsDirectory = new UIButton(IKey.lazy("Models"), (b) -> this.openFolder(BBS.getAssetsPath("models")));

        UIIcon shaders = new UIIcon(Icons.SPHERE, (b) ->
        {
            this.print("Reloading shaders!");
            BBS.getShaders().reload();
            this.close();
        });
        shaders.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_SHADERS);
        UIIcon textures = new UIIcon(Icons.MATERIAL, (b) ->
        {
            this.print("Reloading textures!");
            BBS.getTextures().reload();
            this.close();
        });
        textures.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_TEXTURES);
        UIIcon language = new UIIcon(Icons.GLOBE, (b) ->
        {
            this.print("Reloading languages!");
            BBS.getL10n().reload();
            this.close();
        });
        language.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_LANG);
        UIIcon models = new UIIcon(Icons.POSE, (b) ->
        {
            this.print("Reloading models");
            BBS.getModels().reload();
            this.close();
        });
        models.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_MODELS);
        UIIcon sounds = new UIIcon(Icons.SOUND, (b) ->
        {
            this.print("Reloading sounds");
            BBS.getSounds().deleteSounds();
            this.close();
        });
        sounds.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_SOUNDS);
        UIIcon terrain = new UIIcon(Icons.TREE, (b) ->
        {
            this.print("Forcing chunk loader");
            this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld().chunks.buildChunks(BBS.getRender(), true);
            this.close();
        });
        terrain.w(0).tooltip(UIKeysApp.UTILITY_RELOAD_TERRAIN);

        this.width = new UITrackpad((v) ->
        {
            Window.setSize((int) this.width.getValue(), (int) this.height.getValue());
        });
        this.height = new UITrackpad((v) ->
        {
            Window.setSize((int) this.width.getValue(), (int) this.height.getValue());
        });

       this.width.delayedInput().limit(2, 4096, true).values(2, 1, 10).setValue(Window.width);
       this.height.delayedInput().limit(2, 4096, true).values(2, 1, 10).setValue(Window.height);

       UIButton analyze = new UIButton(UIKeysApp.UTILITY_ANALYZE_LANG, (b) -> this.analyzeLanguageStrings());
       UIButton compile = new UIButton(UIKeysApp.UTILITY_COMPILE_LANG, (b) -> this.compileLanguageStrings());
       UIButton langEditor = new UIButton(UIKeysApp.UTILITY_LANG_EDITOR, (b) -> this.openLangEditor());

       this.view.add(UI.label(IKey.lazy("Open folder...")), UI.row(openGameDirectory, openModelsDirectory, openAudioDirectory).marginBottom(8));
       this.view.add(UI.label(UIKeysApp.UTILITY_RELOAD_LABEL), UI.row(shaders, textures, language, models, sounds, terrain).marginBottom(8));
       this.view.add(UI.column(UI.label(UIKeysApp.UTILITY_RESIZE_WINDOW), UI.row(this.width, this.height)).marginBottom(8));
       this.view.add(UI.label(UIKeysApp.UTILITY_LANG_LABEL), UI.row(analyze, compile), langEditor);
       this.content.add(this.view);
    }

    private void openFolder(File gameFolder)
    {
        gameFolder.mkdirs();

        UIUtils.openFolder(gameFolder);
    }

    private void openLangEditor()
    {
        UIContext context = this.getContext();

        this.close();

        UIOverlay.addOverlay(context, new UILanguageEditorOverlayPanel(), 0.6F, 0.9F);
    }

    private void analyzeLanguageStrings()
    {
        this.print(L10nUtils.analyzeStrings(BBS.getL10n()));
    }

    private void compileLanguageStrings()
    {
        L10nUtils.compile(BBS.getExportFolder(), BBS.getL10n().getStrings());

        UIMessageFolderOverlayPanel panel = new UIMessageFolderOverlayPanel(UIKeys.SUCCESS, UIKeysApp.UTILITY_COMPILE_LANG_DESCRIPTION, BBS.getExportFolder());
        UIOverlay.addOverlay(this.getContext(), panel);
    }

    private void print(String string)
    {
        int longest = 0;
        String[] splits = string.split("\n");

        for (String s : splits)
        {
            longest = Math.max(s.length(), longest);
        }

        String separator = StringUtils.repeat("-", longest);

        System.out.println(separator);

        for (String s : splits)
        {
            System.out.println(s);
        }

        System.out.println(separator);
    }

    @Override
    public void resize()
    {
        super.resize();

        this.width.setValue(Window.width);
        this.height.setValue(Window.height);
    }

    @Override
    public void onClose()
    {
        super.onClose();

        if (this.callback != null)
        {
            this.callback.run();
        }
    }
}