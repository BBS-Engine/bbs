package mchorse.sandbox.ui.utility;

import mchorse.sandbox.ui.UIKeysApp;
import mchorse.sandbox.ui.l10n.UILanguageEditorOverlayPanel;
import mchorse.bbs.BBS;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.l10n.L10nUtils;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.buttons.UIButton;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.framework.elements.overlay.UIMessageOverlayPanel;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.StringUtils;

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

        UIButton shaders = new UIButton(UIKeysApp.UTILITY_RELOAD_SHADERS, (b) ->
        {
            this.print("Reloading shaders!");
            BBS.getShaders().reload();
            this.close();
        });
        UIButton textures = new UIButton(UIKeysApp.UTILITY_RELOAD_TEXTURES, (b) ->
        {
            this.print("Reloading textures!");
            BBS.getTextures().reload();
            this.close();
        });
        UIButton language = new UIButton(UIKeysApp.UTILITY_RELOAD_LANG, (b) ->
        {
            this.print("Reloading languages!");
            BBS.getL10n().reload();
            this.close();
        });
        UIButton models = new UIButton(UIKeysApp.UTILITY_RELOAD_MODELS, (b) ->
        {
            this.print("Reloading models");
            BBS.getModels().reload();
            this.close();
        });
        UIButton sounds = new UIButton(UIKeysApp.UTILITY_RELOAD_SOUNDS, (b) ->
        {
            this.print("Reloading sounds");
            BBS.getSounds().deleteSounds();
            this.close();
        });
        UIButton terrain = new UIButton(UIKeysApp.UTILITY_RELOAD_TERRAIN, (b) ->
        {
            this.print("Forcing chunk loader");
            this.getContext().menu.bridge.get(IBridgeWorld.class).getWorld().chunks.buildChunks(BBS.getRender(), true);
            this.close();
        });

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

       this.view.add(UI.label(UIKeysApp.UTILITY_RELOAD_LABEL), UI.row(shaders, textures), UI.row(language, models), UI.row(sounds, terrain).marginBottom(8));
       this.view.add(UI.column(UI.label(UIKeysApp.UTILITY_RESIZE_WINDOW), UI.row(this.width, this.height)).marginBottom(8));
       this.view.add(UI.label(UIKeysApp.UTILITY_LANG_LABEL), UI.row(analyze, compile), langEditor);
       this.content.add(this.view);
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

        UIMessageOverlayPanel panel = new UIMessageOverlayPanel(UIKeys.SUCCESS, UIKeysApp.UTILITY_COMPILE_LANG_DESCRIPTION);
        UIButton open = new UIButton(UIKeysApp.UTILITY_OPEN_EXPORT, (b) ->
        {
            panel.close();
            UIUtils.openFolder(BBS.getExportFolder());
        });

        open.relative(panel).x(0.5F).y(1F, -10).w(120).anchor(0.5F, 1F);
        panel.content.add(open);

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