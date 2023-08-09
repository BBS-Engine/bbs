package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBSData;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.camera.IUIEmbeddedView;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.recording.editor.actions.UIActionPanel;

public class UIDedicatedRecordEditor extends UIElement implements IUIEmbeddedView, IUIRecordEditorDelegate
{
    public UIRecordEditor timeline;

    public UIActionPanel actionPanel;

    private IUICameraWorkDelegate editor;
    private SceneClip clip;
    private Record record;

    public UIDedicatedRecordEditor(IUICameraWorkDelegate editor, SceneClip clip)
    {
        super();

        this.editor = editor;
        this.clip = clip;

        this.timeline = new UIRecordEditor(this);
        this.timeline.relative(this).full();

        this.markContainer().add(this.timeline);
    }

    @Override
    public boolean supportsCursor()
    {
        return true;
    }

    @Override
    public int getCursor()
    {
        return this.editor.getCursor();
    }

    @Override
    public void setCursor(int tick)
    {
        this.editor.setTickAndNotify(tick + this.clip.tick.get());
    }

    /* @Override
    public void clickTick(int tick)
    {
        this.editor.setTickAndNotify(tick + this.clip.tick.get());
    }

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

        if (action != null)
        {
            try
            {
                Class<? extends UIActionPanel> clazz = BBS.getFactoryActions().getData(action).panelUI;

                if (clazz != null)
                {
                    this.actionPanel = (UIActionPanel) clazz.getConstructors()[0].newInstance();

                    this.actionPanel.fill(action);
                    this.actionPanel.relative(this).x(1F, -200).w(200).h(1F);
                    this.add(this.actionPanel);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.updateTimelineFlex();
        this.resize();
    } */

    @Override
    public void setTick(int tick)
    {
        tick -= this.clip.tick.get();

        /* this.timeline.cursor = tick;
        this.timeline.scroll.scrollIntoView(tick * this.timeline.scroll.scrollItemSize, 2); */
    }

    @Override
    public void close()
    {
        BBSData.getRecords().save(this.record);
    }

    public void fill(Record record)
    {
        this.record = record;

        this.timeline.setRecord(record);
    }
}
