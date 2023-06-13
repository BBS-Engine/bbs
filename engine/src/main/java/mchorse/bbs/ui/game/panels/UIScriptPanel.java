package mchorse.bbs.ui.game.panels;

import mchorse.bbs.BBSData;
import mchorse.bbs.bridge.IBridgePlayer;
import mchorse.bbs.bridge.IBridgeWorld;
import mchorse.bbs.game.scripts.Script;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.game.utils.DataContext;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.Keys;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.dashboard.panels.UIDataRunDashboardPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.framework.elements.buttons.UIToggle;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs.ui.game.scripts.UIDocumentationOverlayPanel;
import mchorse.bbs.ui.game.scripts.UILibrariesOverlayPanel;
import mchorse.bbs.ui.game.scripts.UITextEditor;
import mchorse.bbs.ui.game.scripts.utils.UIScriptUtils;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Direction;

import java.util.HashMap;
import java.util.Map;

public class UIScriptPanel extends UIDataRunDashboardPanel<Script>
{
    public UIIcon docs;
    public UIIcon libraries;
    public UITextEditor code;
    public UIToggle unique;

    /**
     * A map of last remembered vertical scrolled within other scripts
     */
    private Map<String, Integer> lastScrolls = new HashMap<String, Integer>();

    public UIScriptPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.docs = new UIIcon(Icons.HELP, this::openDocumentation);
        this.docs.tooltip(UIKeys.SCRIPTS_DOCUMENTATION_TITLE, Direction.LEFT);
        this.libraries = new UIIcon(Icons.MORE, this::openLibraries);
        this.libraries.tooltip(UIKeys.SCRIPTS_LIBRARIES_TOOLTIP, Direction.LEFT);

        this.iconBar.add(this.docs, this.libraries);

        this.code = new UITextEditor(null);
        this.code.background().context((menu) -> UIScriptUtils.createScriptContextMenu(menu, this.code));
        this.code.keys().ignoreFocus().register(Keys.SCRIPT_WORD_WRAP, this::toggleWordWrap).category(UIDataDashboardPanel.KEYS_CATEGORY);

        this.unique = new UIToggle(UIKeys.SCRIPTS_UNIQUE, (b) -> this.data.unique = b.getValue());

        this.code.relative(this.editor).full();

        this.editor.add(this.code);
        this.overlay.namesList.setFileIcon(Icons.PROPERTIES);

        this.addOptions();
        this.options.fields.add(this.unique);

        this.fill(null);
    }

    private void toggleWordWrap()
    {
        this.code.wrap();
        this.code.recalculate();
        this.code.horizontal.clamp();
        this.code.vertical.clamp();
    }

    private void openDocumentation(UIIcon element)
    {
        UIDocumentationOverlayPanel panel = new UIDocumentationOverlayPanel();

        UIOverlay.addOverlay(this.getContext(), panel, 0.7F, 0.9F);
    }

    @Override
    protected void run()
    {
        this.save();
        this.save = false;

        UIContext context = this.getContext();
        DataContext dataContext;

        if (context.menu.bridge.get(IBridgePlayer.class).getController() == null)
        {
            dataContext = new DataContext(context.menu.bridge.get(IBridgeWorld.class).getWorld());
        }
        else
        {
            dataContext = new DataContext(context.menu.bridge.get(IBridgePlayer.class).getController());
        }

        try
        {
            BBSData.getScripts().execute(this.data.getId(), "", dataContext);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void openLibraries(UIIcon element)
    {
        UILibrariesOverlayPanel overlay = new UILibrariesOverlayPanel(this.data);

        UIOverlay.addOverlay(this.getContext(), overlay, 0.4F, 0.6F);
    }

    @Override
    public ContentType getType()
    {
        return ContentType.SCRIPTS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.PANELS_SCRIPTS;
    }

    @Override
    public void fillDefaultData(Script data)
    {
        super.fillDefaultData(data);

        data.code = "function main(c)\n{\n    // Code...\n    var s = c.getSubject();\n}";
    }

    @Override
    public void fill(Script data)
    {
        String last = this.data == null ? null : this.data.getId();

        super.fill(data);

        this.editor.setVisible(data != null);
        this.libraries.setEnabled(this.data != null);

        if (data != null)
        {
            if (!this.code.getText().equals(data.code))
            {
                if (last != null)
                {
                    this.lastScrolls.put(last, this.code.vertical.scroll);
                }

                this.code.setText(data.code);

                if (last != null)
                {
                    Integer scroll = this.lastScrolls.get(data.getId());

                    if (scroll != null)
                    {
                        this.code.vertical.scroll = scroll;
                    }
                }
            }

            this.unique.setValue(data.unique);
        }
    }

    @Override
    protected void preSave()
    {
        this.data.code = this.code.getText();
    }

    @Override
    public void appear()
    {
        super.appear();

        this.code.updateHighlighter();
    }
}