package mchorse.bbs.ui.recording.editor;

public interface IUIRecordEditorDelegate
{
    public boolean supportsCursor();

    public int getCursor();

    public void setCursor(int tick);
}