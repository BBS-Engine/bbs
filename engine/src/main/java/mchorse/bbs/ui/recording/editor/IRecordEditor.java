package mchorse.bbs.ui.recording.editor;

import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.data.Frame;

import java.util.function.Consumer;

public interface IRecordEditor
{
    public void clickTick(int tick);

    public void openAction(Action action);

    public void openFrame(Frame frame);

    public void editFrame(Consumer<Frame> consumer);
}