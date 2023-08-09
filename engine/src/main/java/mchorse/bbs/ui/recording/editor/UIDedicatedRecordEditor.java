package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.BBSData;
import mchorse.bbs.recording.data.Record;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.ui.camera.IUIClipsDelegate;
import mchorse.bbs.ui.camera.IUIEmbeddedView;
import mchorse.bbs.ui.framework.elements.UIElement;

public class UIDedicatedRecordEditor extends UIElement implements IUIEmbeddedView, IUIRecordEditorDelegate
{
    public UIRecordEditor timeline;

    private IUIClipsDelegate editor;
    private SceneClip clip;
    private Record record;

    public UIDedicatedRecordEditor(IUIClipsDelegate editor, SceneClip clip)
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
        return this.editor.getCursor() - this.clip.tick.get();
    }

    @Override
    public void setCursor(int tick)
    {
        this.editor.setCursor(tick + this.clip.tick.get());
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
