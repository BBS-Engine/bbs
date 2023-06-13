package mchorse.bbs.ui.game.scripts;

import mchorse.bbs.data.DataToString;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.framework.elements.UIScrollView;
import mchorse.bbs.ui.framework.elements.overlay.UIOverlayPanel;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocClass;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocDelegate;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocEntry;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocList;
import mchorse.bbs.ui.game.scripts.utils.documentation.DocMethod;
import mchorse.bbs.ui.game.scripts.utils.documentation.Docs;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UIDocumentationOverlayPanel extends UIOverlayPanel
{
    private static Docs docs;
    private static DocEntry top;
    private static DocEntry entry;

    public UIDocEntryList list;
    public UIScrollView documentation;

    public static List<DocClass> search(String text)
    {
        List<DocClass> list = new ArrayList<DocClass>();

        for (DocClass docClass : getDocs().classes)
        {
            if (docClass.getMethod(text) != null)
            {
                list.add(docClass);
            }
        }

        return list;
    }

    public static Docs getDocs()
    {
        parseDocs();

        return docs;
    }

    private static void parseDocs()
    {
        if (docs != null)
        {
            return;
        }

        InputStream stream = UIDocumentationOverlayPanel.class.getResourceAsStream("/assets/docs.json");

        docs = Docs.fromData(DataToString.mapFromString(IOUtils.readText(stream)));
        entry = null;

        docs.copyMethods("UILabelBaseComponent", "UIButtonComponent", "UILabelComponent", "UITextComponent", "UITextareaComponent", "UITextboxComponent", "UIToggleComponent");
        docs.remove("UIParentComponent");
        docs.remove("UILabelBaseComponent");

        DocList topPackage = new DocList();
        DocList scripting = new DocList();
        DocList ui = new DocList();

        topPackage.doc = docs.getPackage("mchorse.bbs.game.scripts.user.ui").doc;
        scripting.name = "Scripting API";
        scripting.doc = docs.getPackage("mchorse.bbs.game.scripts.user").doc;
        scripting.parent = topPackage;
        ui.name = "UI API";
        ui.doc = docs.getPackage("mchorse.bbs.game.scripts.ui.components").doc;
        ui.parent = topPackage;

        for (DocClass docClass : docs.classes)
        {
            docClass.setup();

            if (docClass.name.contains("ui.components") || docClass.name.endsWith(".Graphic"))
            {
                ui.entries.add(docClass);
                docClass.parent = ui;
            }
            else if (!docClass.name.endsWith("Graphic"))
            {
                scripting.entries.add(docClass);
                docClass.parent = scripting;
            }
        }

        topPackage.entries.add(scripting);
        topPackage.entries.add(ui);

        top = topPackage;
    }

    public UIDocumentationOverlayPanel()
    {
        this(null);
    }

    public UIDocumentationOverlayPanel(DocEntry entry)
    {
        super(UIKeys.SCRIPTS_DOCUMENTATION_TITLE);

        this.list = new UIDocEntryList((l) -> this.pick(l.get(0)));
        this.documentation = UI.scrollView(5, 10);

        this.list.relative(this.content).w(120).h(1F);
        this.documentation.relative(this.content).x(120).w(1F, -120).h(1F);

        this.content.add(this.list, this.documentation);

        this.setupDocs(entry);
    }

    private void pick(DocEntry entryIn)
    {
        boolean isMethod = entryIn instanceof DocMethod;

        entryIn = entryIn.getEntry();
        List<DocEntry> entries = entryIn.getEntries();
        boolean wasSame = this.list.getList().size() >= 2 && this.list.getList().get(1).parent == entryIn.parent;

        /* If the list isn't the same or if the the current item got double clicked
         * to enter into the section */
        if (entry == entryIn || !wasSame)
        {
            this.list.clear();

            if (entryIn.parent != null)
            {
                this.list.add(new DocDelegate(entryIn.parent));
            }

            this.list.add(entries);
            this.list.sort();

            if (isMethod)
            {
                this.list.setCurrentScroll(entryIn);
            }
        }

        this.fill(entryIn);
    }

    private void fill(DocEntry entryIn)
    {
        if (!(entryIn instanceof DocMethod))
        {
            entry = entryIn;
        }

        this.documentation.scroll.scrollTo(0);
        this.documentation.removeAll();
        entryIn.fillIn(this.documentation);

        this.resize();
    }

    private void setupDocs(DocEntry in)
    {
        parseDocs();

        if (in != null)
        {
            entry = in;
        }
        else if (entry == null)
        {
            entry = top;
        }

        this.pick(entry);
    }

}