package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSData;
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.ui.camera.IUICameraWorkDelegate;
import mchorse.bbs.ui.camera.IUIEmbeddedView;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.ui.framework.elements.UIElement;
import mchorse.bbs.ui.recording.editor.actions.UIActionPanel;
import mchorse.bbs.utils.Range;

import java.util.function.Consumer;

public class UIDedicatedRecordEditor extends UIElement implements IRecordEditor, IUIEmbeddedView
{
    public UIRecordTimeline timeline;

    public UIActionPanel actionPanel;
    public UIFrame framePanel;

    private IUICameraWorkDelegate editor;
    private SceneClip clip;
    private Record record;

    public UIDedicatedRecordEditor(IUICameraWorkDelegate editor, SceneClip clip)
    {
        super();

        this.editor = editor;
        this.clip = clip;

        this.timeline = new UIRecordTimeline(this);
        this.timeline.relative(this).full();

        this.markContainer().add(this.timeline);
    }

    private void updateTimelineFlex()
    {
        if (this.framePanel == null && this.actionPanel == null)
        {
            this.timeline.resetFlex().relative(this).full();
        }
        else
        {
            this.timeline.resetFlex().relative(this).wTo(this.area, 1F, -200).h(1F);
        }
    }

    @Override
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
            this.framePanel.relative(this).x(1F, -200).w(200).h(1F);
            this.add(this.framePanel);
        }

        this.updateTimelineFlex();
        this.resize();
    }

    @Override
    public void editFrame(Consumer<Frame> consumer)
    {
        Range range = this.timeline.calculateRange();

        for (int i = range.min; i <= range.max; i++)
        {
            consumer.accept(this.record.frames.get(i));
        }
    }

    @Override
    public void setTick(int tick)
    {
        tick -= this.clip.tick.get();

        this.timeline.cursor = tick;

        this.timeline.scroll.scrollIntoView(tick * this.timeline.scroll.scrollItemSize, 2);
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
        this.openAction(null);
        this.openFrame(null);
    }

    @Override
    public void render(UIContext context)
    {
        if (this.actionPanel != null || this.framePanel != null)
        {
            context.batcher.box(this.area.ex() - 200, this.area.y, this.area.ex(), this.area.ey(), 0xaa000000);
        }

        super.render(context);
    }
}
