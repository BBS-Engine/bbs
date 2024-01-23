package mchorse.bbs.film.tts;

import mchorse.bbs.camera.clips.misc.VoicelineClip;
import mchorse.bbs.l10n.keys.IKey;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ElevenLabsResult
{
    public Status status;
    public File folder;
    public List<String> missingVoices = new ArrayList<>();
    public IKey message;
    public VoicelineClip clip;

    public ElevenLabsResult(Status status)
    {
        this.status = status;
    }

    public ElevenLabsResult(Status status, File folder)
    {
        this.status = status;
        this.folder = folder;
    }

    public ElevenLabsResult(Status status, IKey message)
    {
        this(status, message, null);
    }

    public ElevenLabsResult(Status status, IKey message, VoicelineClip clip)
    {
        this.status = status;
        this.message = message;
        this.clip = clip;
    }

    public static enum Status
    {
        INITIALIZED, SUCCESS, VOICE_IS_MISSING, TOKEN_MISSING, GENERATED, ERROR;
    }
}