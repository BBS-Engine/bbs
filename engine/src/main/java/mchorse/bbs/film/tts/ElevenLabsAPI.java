package mchorse.bbs.film.tts;

import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.film.screenplay.ScreenplayAction;
import mchorse.bbs.utils.FFMpegUtils;
import mchorse.bbs.utils.StringUtils;

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
    private final List<ScreenplayAction> actions;
    private final Consumer<ElevenLabsResult> callback;

    public ElevenLabsAPI(String token, File folder, List<ScreenplayAction> actions, Consumer<ElevenLabsResult> callback)
    {
        this.token = token;
        this.folder = folder;
        this.actions = actions;
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

    /**
     * Generate audio voice lines from a film using ElevenLabs API
     */
    public static void generate(File folder, List<ScreenplayAction> actions, Consumer<ElevenLabsResult> callback)
    {
        String token = getToken();

        if (token.trim().isEmpty())
        {
            callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.TOKEN_MISSING));

            return;
        }

        if (thread != null)
        {
            callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.ERROR, "The process is still in progress!"));

            return;
        }

        thread = new Thread(new ElevenLabsAPI(token, folder, actions, callback));
        thread.start();
    }

    /**
     * Fill JSON data for the request
     */
    private static void fillJSONData(HttpURLConnection connection, String reply) throws IOException
    {
        MapType data = new MapType();
        MapType voiceSettings = new MapType();

        voiceSettings.putFloat("stability", 0.5F);
        voiceSettings.putFloat("similarity_boost", 0.5F);

        data.putString("text", reply);
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

        for (ScreenplayAction reply : this.actions)
        {
            if (!voices.containsKey(reply.voice.get().toLowerCase()))
            {
                ElevenLabsResult result = new ElevenLabsResult(ElevenLabsResult.Status.VOICE_IS_MISSING);

                result.missingVoices.add(reply.voice.get());
                this.callback.accept(result);

                thread = null;

                return;
            }
        }

        this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.INITIALIZED));

        for (ScreenplayAction action : this.actions)
        {
            File file = this.getFile(action);

            try
            {
                String voiceID = voices.get(action.voice.get().toLowerCase()).id;

                HttpURLConnection connection = (HttpURLConnection) new URL(TTS_URL + voiceID).openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "audio/mpeg");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("xi-api-key", this.token);
                connection.setDoOutput(true);

                fillJSONData(connection, action.content.get());

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    writeToFile(connection, file);

                    this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.GENERATED, "Voice line " + action.uuid.get() + " was generated!"));

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

                    action.variant.set(wav.getName());
                }
                else
                {
                    this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.ERROR, "The server returned status code: " + responseCode));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.callback.accept(new ElevenLabsResult(ElevenLabsResult.Status.SUCCESS, folder));

        thread = null;
    }

    private File getFile(ScreenplayAction action)
    {
        int i = 1;
        File folder = new File(this.folder, action.uuid.get());
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