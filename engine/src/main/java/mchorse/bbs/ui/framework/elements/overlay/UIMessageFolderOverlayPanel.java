package mchorse.bbs.ui.framework.elements.overlay;

import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.ui.framework.elements.buttons.UIIcon;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.ui.utils.icons.Icons;

import java.io.File;

public class UIMessageFolderOverlayPanel extends UIMessageOverlayPanel
{
    public UIIcon folder;

    private File file;

    public UIMessageFolderOverlayPanel(IKey title, IKey message, File file)
    {
        super(title, message);

        this.file = file;

        this.folder = new UIIcon(Icons.FOLDER, (b) -> UIUtils.openFolder(this.file));

        this.icons.add(this.folder);
    }
}