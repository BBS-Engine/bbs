package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBS;
import mchorse.bbs.game.utils.ContentType;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.dashboard.UIDashboard;
import mchorse.bbs.ui.dashboard.panels.UIDataDashboardPanel;
import mchorse.bbs.ui.recording.editor.actions.UIActionPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.utils.Range;

import java.util.function.Consumer;

public class UIRecordPanel extends UIDataDashboardPanel<Record> implements IRecordEditor
{
    public UIRecordTimeline timeline;

    public UIActionPanel actionPanel;
    public UIFrame framePanel;

    public UIRecordPanel(UIDashboard dashboard)
    {
        super(dashboard);

        this.timeline = new UIRecordTimeline(this);
        this.timeline.relative(this.editor).y(1F, -110).w(1F).h(110);

        this.editor.add(this.timeline);
        this.overlay.namesList.setFileIcon(Icons.EDITOR);

        this.fill(null);
    }

    @Override
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
    }

    @Override
    public void openFrame(Frame frame)
    {
        if (this.framePanel != null)
        {
            this.framePanel.removeFromParent();
        }

        this.framePanel = null;

        if (frame != null)
        {
            this.framePanel = new UIFrame(this);

            this.framePanel.fill(frame);
            this.framePanel.relative(this.editor).x(1F, -200).w(200).hTo(this.timeline.area);
            this.editor.add(this.framePanel);
            this.editor.resize();
        }
    }

    @Override
    public void editFrame(Consumer<Frame> consumer)
    {
        Range range = this.timeline.calculateRange();

        for (int i = range.min; i <= range.max; i++)
        {
            consumer.accept(this.data.frames.get(i));
        }
    }

    @Override
    public void fill(Record data)
    {
        super.fill(data);

        this.timeline.setRecord(data);
        this.openAction(null);
        this.openFrame(null);
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