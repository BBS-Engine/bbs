package mchorse.bbs.l10n;

import mchorse.bbs.BBS;
import mchorse.bbs.BBSSettings;
import mchorse.bbs.data.DataToString;
import mchorse.bbs.data.types.BaseType;
import mchorse.bbs.data.types.MapType;
import mchorse.bbs.events.L10nReloadEvent;
import mchorse.bbs.l10n.keys.IKey;
import mchorse.bbs.l10n.keys.LangKey;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.Label;
import mchorse.bbs.utils.IOUtils;
import mchorse.bbs.utils.Pair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class L10n
{
    public static final String DEFAULT_LANGUAGE = "en_US";

    private Map<String, LangKey> strings = new HashMap<>();
    private Set<Function<String, List<Link>>> langFiles = new LinkedHashSet<>();
    private List<Pair<String, String>> supportedLanguages;

    public L10n()
    {
        this.reloadSupportedLanguages(Collections.emptyList());
    }

    public void reloadSupportedLanguages(List<Pair<String, String>> additionalLanguages)
    {
        this.supportedLanguages = new ArrayList<>();
        this.supportedLanguages.addAll(Arrays.asList(
            // new Pair<>("Deutsch (de_DE)", "de_DE"),
            new Pair<>("English (en_US)", "en_US")
            // new Pair<>("Português (pt_BR)", "pt_BR"),
            // new Pair<>("Русский (ru_RU)", "ru_RU"),
            // new Pair<>("Українська (uk_UA)", "uk_UA")
        ));
        this.supportedLanguages.addAll(additionalLanguages);
    }

    public Map<String, LangKey> getStrings()
    {
        return this.strings;
    }

    public List<String> getSupportedLanguageCodes()
    {
        List<String> codes = new ArrayList<>();

        for (Pair<String, String> pair : this.supportedLanguages)
        {
            codes.add(pair.b);
        }

        return codes;
    }

    public List<Label<String>> getSupportedLanguageLabels()
    {
        List<Label<String>> labels = new ArrayList<>();

        for (Pair<String, String> pair : this.supportedLanguages)
        {
            labels.add(new Label<>(IKey.raw(pair.a), pair.b));
        }

        return labels;
    }

    public List<Link> getAllLinks(String lang)
    {
        List<Link> links = new ArrayList<>();

        for (Function<String, List<Link>> function : this.langFiles)
        {
            links.addAll(function.apply(lang));
        }

        return links;
    }

    @Deprecated
    public void register(Link link)
    {
        System.err.println("L10n.register(Link) is deprecated in favor of Link.register(Function<String, Link>) for multi-language support!");

        this.register((lang) -> Collections.singletonList(link));
    }

    public void registerOne(Function<String, Link> function)
    {
        this.langFiles.add((lang) -> Collections.singletonList(function.apply(lang)));
    }

    public void register(Function<String, List<Link>> function)
    {
        this.langFiles.add(function);
    }

    public void reload()
    {
        this.reload(BBSSettings.language.get(), BBS.getProvider());
    }

    public void reload(String language, AssetProvider provider)
    {
        List<Link> links = this.getAllLinks(DEFAULT_LANGUAGE);

        if (!language.equals(DEFAULT_LANGUAGE))
        {
            links.addAll(this.getAllLinks(language));
        }

        for (Link link : links)
        {
            try
            {
                System.out.println("Loading language file \"" + link + "\".");

                this.load(link, provider.getAsset(link));
            }
            catch (Exception e)
            {
                System.err.println("Failed to load " + link + " language file!");
                e.printStackTrace();
            }
        }

        BBS.events.post(new L10nReloadEvent(this));
    }

    public void load(Link origin, InputStream stream)
    {
        MapType map = DataToString.mapFromString(IOUtils.readText(stream));

        for (Map.Entry<String, BaseType> entry : map)
        {
            if (entry.getValue().isString())
            {
                String string = entry.getValue().asString();
                LangKey langKey = this.strings.get(entry.getKey());

                if (langKey == null)
                {
                    langKey = new LangKey(origin, entry.getKey(), string);
                }
                else
                {
                    langKey.setOrigin(origin);
                    langKey.content = string;
                }

                this.strings.put(entry.getKey(), langKey);
            }
        }
    }

    public void overwrite(MapType strings)
    {
        for (Map.Entry<String, BaseType> entry : strings)
        {
            LangKey key = this.strings.get(entry.getKey());

            if (key != null && entry.getValue().isString())
            {
                key.content = entry.getValue().asString();
            }
        }
    }

    public LangKey getKey(String key)
    {
        return this.getKey(key, key);
    }

    public LangKey getKey(String key, String content)
    {
        LangKey langKey = this.strings.computeIfAbsent(key, (k) -> new LangKey(null, k, content));

        langKey.wasRequested = true;

        return langKey;
    }
}