package mchorse.bbs.film.tts;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.ListType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.film.Film;

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

    private static Map<String, String> voices = new HashMap<>();

    private final String token;
    private final Film screenplay;
    private final Consumer<TTSGenerateResult> callback;

    public ElevenLabsAPI(String token, Film film, Consumer<TTSGenerateResult> callback)
    {
        this.token = token;
        this.screenplay = film;
        this.callback = callback;
    }

    public static Map<String, String> getVoices()
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
                String name = map.getString("name").toLowerCase();
                String id = map.getString("voice_id");

                voices.put(name, id);
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
    public static void generate(Film film, Consumer<TTSGenerateResult> callback)
    {
        String token = getToken();

        if (token.trim().isEmpty())
        {
            callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.TOKEN_MISSING));

            return;
        }

        if (thread != null)
        {
            callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.ERROR, "The process is still in progress!"));

            return;
        }

        thread = new Thread(new ElevenLabsAPI(token, film, callback));
        thread.start();
    }

    /**
     * Collect voice IDs specified in screenplay's metadata
     */
    private static Map<String, String> collectVoices(List<ScreenplayReply> replies, Map<String, String> metadata)
    {
        Map<String, String> voices = new HashMap<>();

        for (ScreenplayReply reply : replies)
        {
            String key = reply.name.toLowerCase();

            if (voices.containsKey(key))
            {
                continue;
            }

            String metadataVoice = metadata.get("Voice-" + key.toUpperCase());

            if (metadataVoice == null || metadataVoice.isEmpty())
            {
                return null;
            }

            voices.put(key, metadataVoice);
        }

        return voices;
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
        List<ScreenplayReply> replies = this.screenplay.parseReplies();
        Map<String, String> voices = getVoices();

        if (voices.isEmpty())
        {
            this.callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.VOICE_IS_MISSING));

            thread = null;

            return;
        }

        for (ScreenplayReply reply : replies)
        {
            if (!voices.containsKey(reply.name.toLowerCase()))
            {
                TTSGenerateResult result = new TTSGenerateResult(TTSGenerateResult.Status.VOICE_IS_MISSING);

                result.missingVoices.add(reply.name);
                this.callback.accept(result);

                thread = null;

                return;
            }
        }

        this.callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.INITIALIZED));

        int i = 0;
        String lastChapter = "";
        File folder = BBS.getAssetsPath("audio/elevenlabs/" + this.screenplay.getId());

        for (ScreenplayReply reply : replies)
        {
            if (!reply.chapter.equals(lastChapter))
            {
                i = 1;
                lastChapter = reply.chapter;
            }

            String filename = (reply.chapter + "_" + i + ".mp3").replaceAll("[^\\w\\d. _-]", "_");
            File file = new File(folder, filename);

            try
            {
                String voiceID = voices.get(reply.name.toLowerCase());

                HttpURLConnection connection = (HttpURLConnection) new URL(TTS_URL + voiceID).openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "audio/mpeg");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("xi-api-key", this.token);
                connection.setDoOutput(true);

                fillJSONData(connection, reply.reply);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK)
                {
                    writeToFile(connection, file);

                    i += 1;
                    lastChapter = reply.chapter;

                    this.callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.GENERATED, "Voice line " + filename + " was generated!"));
                }
                else
                {
                    this.callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.ERROR, "The server returned status code: " + responseCode));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        this.callback.accept(new TTSGenerateResult(TTSGenerateResult.Status.SUCCESS, folder));

        thread = null;
    }
}