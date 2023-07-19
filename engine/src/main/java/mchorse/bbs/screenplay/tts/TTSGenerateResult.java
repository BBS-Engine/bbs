package mchorse.bbs.screenplay.tts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TTSGenerateResult
{
    public Status status;
    public File folder;
    public List<String> missingVoices = new ArrayList<String>();

    public TTSGenerateResult(Status status)
    {
        this(status, null);
    }

    public TTSGenerateResult(Status status, File folder)
    {
        this.status = status;
        this.folder = folder;
    }

    public static enum Status
    {
        SUCCESS, VOICE_IS_MISSING, TOKEN_MISSING, ERROR;
    }
}