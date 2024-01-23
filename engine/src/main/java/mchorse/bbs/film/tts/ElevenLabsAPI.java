package mchorse.bbs.film.tts;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.camera.clips.misc.VoicelineClip;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.UIFilmPanel;
import mchorse.bbs.ui.framework.UIContext;
import mchorse.bbs.utils.FFMpegUtils;
import mchorse.bbs.utils.StringUtils;
import mchorse.bbs.utils.colors.Colors;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ElevenLabsAPI implements Runnable
{
    public static final String TTS_URL = "https://api.elevenlabs.io/v1/text-to-speech/";
    public static final String VOICES_URL = "https://api.elevenlabs.io/v1/voices";

    private static Thread thread;

    private static Map<String, ElevenLabsVoice> voices = new HashMap<>();

    private final String token;
    private final File folder;
    private final List<VoicelineClip> voiceLines;
    private final Consumer<ElevenLabsResult> callback;

    public ElevenLabsAPI(String token, File folder, List<VoicelineClip> voiceLines, Consumer<ElevenLabsResult> callback)
    {
        this.token = token;
        this.folder = folder;
        this.voiceLines = voiceLines;
        this.callback = callback;
    }

    public static Map<String, ElevenLabsVoice> getVoices()
    {
        if (voices.isEmpty())
        {
            try
            {
                fetchVoices();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return voices;
    }

    private static void fetchVoices() throws Exception
    {
        HttpURLConnection connection = (HttpURLConnection) new URL(VOICES_URL).openConnection();
        String token = getToken();

        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("xi-api-key", token);

        int responseCode = connection.getResponseCode();

        String json = new String(readConnection(connection));
        MapType data = DataToString.mapFromString(json);

        if (responseCode != HttpURLConnection.HTTP_OK)
        {
            throw new IllegalStateException("Couldn't get a list of voices! " + responseCode);
        }

        if (data != null)
        {
            ListType voicesList = data.getList("voices");

            for (BaseType type : voicesList)
            {
                if (!type.isMap())
                {
                    continue;
                }

                MapType map = type.asMap();
                ElevenLabsVoice voice = new ElevenLabsVoice();

                voice.fromData(map);
                voices.put(voice.name.toLowerCase(), voice);
            }
        }
    }

    private static String getToken()
    {
        return BBSSettings.elevenLabsToken.get();
    }

    public static void generateStandard(UIContext context, File folder, List<VoicelineClip> actions, Consumer<ElevenLabsResult> callback)
    {
        try
        {
            generate(UIFilmPanel.getVoiceLines().getFolder(), actions, (result) ->
            {
                if (result.status == ElevenLabsResult.Status.INITIALIZED)
                {
                    context.notify(UIKeys.VOICE_LINE_NOTIFICATIONS_COMMENCING, Colors.BLUE | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.GENERATED)
                {
                    context.notify(result.message, Colors.BLUE | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.ERROR)
                {
                    context.notify(UIKeys.VOICE_LINE_NOTIFICATIONS_ERROR_GENERATING.format(result.message), Colors.RED | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.TOKEN_MISSING)
                {
                    context.notify(UIKeys.VOICE_LINE_NOTIFICATIONS_MISSING_TOKEN, Colors.RED | Colors.A100);
                }
                else if (result.status == ElevenLabsResult.Status.VOICE_IS_MISSING)
                {
                    context.notify(!result.missingVoices.isEmpty()
                        ? UIKeys.VOICE_LINE_NOTIFICATIONS_MISSING_VOICES.format(String.join(", ", result.missingVoices))
                        : UIKeys.VOICE_LINE_NOTIFICATIONS_ERROR_LOADING_VOICES, Colors.RED | Colors.A100);
                }

                callback.accept(result);
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Generate audio voice lines from a film using ElevenLabs API
     */
    public static void generate(File folder, List<VoicelineClip> actions, Consumer<ElevenLabsResult> callback)
    {
        String token = getToken();

        if (token.trim().isEmpty())
        {
            callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.TOKEN_MISSING));

            return;
        }

        if (thread != null)
        {
            callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.ERROR, UIKeys.VOICE_LINE_NOTIFICATIONS_IN_PROGRESS));

            return;
        }

        thread = new Thread(new ElevenLabsAPI(token, folder, actions, callback));
        thread.start();
    }

    /**
     * Fill JSON data for the request
     */
    private static void fillJSONData(HttpURLConnection connection, String voiceLine) throws IOException
    {
        MapType data = new MapType();
        MapType voiceSettings = new MapType();

        voiceSettings.putFloat("stability", 0.5F);
        voiceSettings.putFloat("similarity_boost", 0.5F);

        data.putString("text", voiceLine);
        data.putString("model_id", "eleven_monolingual_v1");
        data.put("voice_settings", voiceSettings);

        String json = DataToString.toString(data, true);

        try (OutputStream output = connection.getOutputStream())
        {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);

            output.write(input, 0, input.length);
        }
    }

    /**
     * Write response body to a file
     */
    private static void writeToFile(HttpURLConnection connection, File file) throws Exception
    {
        byte[] bytes = readConnection(connection);

        file.getParentFile().mkdirs();

        try (FileOutputStream stream = new FileOutputStream(file))
        {
            stream.write(bytes);
        }
    }

    private static byte[] readConnection(HttpURLConnection connection) throws Exception
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        try (InputStream input = connection.getInputStream())
        {
            int read;
            byte[] readBytes = new byte[8];

            while ((read = input.read(readBytes)) > 0)
            {
                bytes.write(readBytes, 0, read);
            }
        }

        return bytes.toByteArray();
    }

    @Override
    public void run()
    {
        Map<String, ElevenLabsVoice> voices = getVoices();

        if (voices.isEmpty())
        {
            this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.VOICE_IS_MISSING));

            thread = null;

            return;
        }

        for (VoicelineClip voiceLine : this.voiceLines)
        {
            if (!voices.containsKey(voiceLine.voice.get().toLowerCase()))
            {
                ElevenLabsResult result = new ElevenLabsResult(ElevenLabsResult.Status.VOICE_IS_MISSING);

                result.missingVoices.add(voiceLine.voice.get());
                this.callback.accept(result);

                thread = null;

                return;
            }
        }

        this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.INITIALIZED));

        for (VoicelineClip voiceLine : this.voiceLines)
        {
            File file = this.getFile(voiceLine);

            try
            {
                String voiceID = voices.get(voiceLine.voice.get().toLowerCase()).id;

                HttpURLConnection connection = (HttpURLConnection) new URL(TTS_URL + voiceID).openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "audio/mpeg");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("xi-api-key", this.token);
                connection.setDoOutput(true);

                fillJSONData(connection, voiceLine.content.get());

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    writeToFile(connection, file);

                    File wav = new File(StringUtils.removeExtension(file.getAbsolutePath()) + ".wav");

                    try
                    {
                        FFMpegUtils.execute(folder, "-i", file.getAbsolutePath(), wav.getAbsolutePath());
                        file.delete();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    voiceLine.variant.set(wav.getName());

                    this.callback.accept(new ElevenLabsResult(
                        ElevenLabsResult.Status.GENERATED,
                        UIKeys.VOICE_LINE_NOTIFICATIONS_GENERATED.format(voiceLine.uuid.get()),
                        voiceLine
                    ));
                }
                else
                {
                    this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.ERROR, UIKeys.VOICE_LINE_NOTIFICATIONS_ERROR_SERVER.format(responseCode)));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.SUCCESS, this.folder));

        thread = null;
    }

    private File getFile(VoicelineClip voiceLine)
    {
        int i = 1;
        File folder = new File(this.folder, voiceLine.uuid.get());
        File file = new File(folder, i + ".wav");

        folder.mkdirs();

        while (file.exists())
        {
            i += 1;

            file = new File(folder, i + ".wav");
        }

        return new File(StringUtils.removeExtension(file.getAbsolutePath()) + ".mp3");
    }
}