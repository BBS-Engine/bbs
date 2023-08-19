package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.framework.elements.input.UITrackpad;
import mchorse.bbs.ui.utils.UI;
import mchorse.bbs.ui.utils.icons.Icons;

public class UIRecordPanel extends UIDataDashboardPanel<Record> implements IUIRecordEditorDelegate
{
    public UIRecordEditor timeline;
    public UITrackpad length;

    public UIRecordPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.timeline = new UIRecordEditor(this);
        this.timeline.relative(this.editor).y(1F, -110).w(1F).h(110);

        this.editor.add(this.timeline);
        this.overlay.namesList.setFileIcon(Icons.EDITOR);

        this.length = new UITrackpad((v) -> this.data.length.set(v.intValue()));
        this.length.limit(0).integer();

        this.options.fields.add(UI.label(IKey.lazy("Length")), this.length);

        this.addOptions();

        this.fill(null);
    }

    @Override
    public boolean supportsCursor()
    {
        return false;
    }

    @Override
    public int getCursor()
    {
        return 0;
    }

    @Override
    public void setCursor(int tick)
    {}

    /* @Override
    public void clickTick(int tick)
    {}

    @Override
    public void openAction(Action action)
    {
        if (this.actionPanel != null)
        {
            if (this.actionPanel.action == action)
            {
                return;
            }

            this.actionPanel.disappear();
            this.actionPanel.removeFromParent();
        }

        this.actionPanel = null;

        if (action == null)
        {
            return;
        }

        try
        {
            Class<? extends UIActionPanel> clazz = BBS.getFactoryActions().getData(action).panelUI;

            if (clazz == null)
            {
                return;
            }

            this.actionPanel = (UIActionPanel) clazz.getConstructors()[0].newInstance();

            this.actionPanel.fill(action);
            this.actionPanel.relative(this.editor).x(1F, -200).w(200).hTo(this.timeline.area);
            this.editor.add(this.actionPanel);
            this.editor.resize();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    } */

    @Override
    public void fill(Record data)
    {
        super.fill(data);

        this.timeline.setRecord(data);

        if (data != null)
        {
            this.length.setValue(data.length.get());
        }
    }

    @Override
    public ContentType getType()
    {
        return ContentType.RECORDS;
    }

    @Override
    public IKey getTitle()
    {
        return UIKeys.RECORD_EDITOR_TITLE;
    }
}