package mchorse.bbs.film.tts;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ElevenLabsResult
{
    public Status status;
    public File folder;
    public List<String> missingVoices = new ArrayList<>();
    public String message;

    public ElevenLabsResult(Status status)
    {
        this.status = status;
    }

    public ElevenLabsResult(Status status, File folder)
    {
        this.status = status;
        this.folder = folder;
    }

    public ElevenLabsResult(Status status, String message)
    {
        this.status = status;
        this.message = message;
    }

    public static enum Status
    {
        INITIALIZED, SUCCESS, VOICE_IS_MISSING, TOKEN_MISSING, GENERATED, ERROR;
    }
}