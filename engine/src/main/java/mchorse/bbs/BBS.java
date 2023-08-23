package mchorse.bbs;

import mchorse.bbs.audio.SoundManager;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.clips.ClipFactoryData;
import mchorse.bbs.camera.clips.converters.DollyToKeyframeConverter;
import mchorse.bbs.camera.clips.converters.DollyToPathConverter;
import mchorse.bbs.camera.clips.converters.IdleConverter;
import mchorse.bbs.camera.clips.converters.IdleToDollyConverter;
import mchorse.bbs.camera.clips.converters.IdleToKeyframeConverter;
import mchorse.bbs.camera.clips.converters.IdleToPathConverter;
import mchorse.bbs.camera.clips.converters.PathToDollyConverter;
import mchorse.bbs.camera.clips.converters.PathToKeyframeConverter;
import mchorse.bbs.camera.clips.misc.AudioClip;
import mchorse.bbs.camera.clips.misc.SubtitleClip;
import mchorse.bbs.camera.clips.modifiers.AngleClip;
import mchorse.bbs.camera.clips.modifiers.DragClip;
import mchorse.bbs.camera.clips.modifiers.LookClip;
import mchorse.bbs.camera.clips.modifiers.MathClip;
import mchorse.bbs.camera.clips.modifiers.OrbitClip;
import mchorse.bbs.camera.clips.modifiers.RemapperClip;
import mchorse.bbs.camera.clips.modifiers.ShakeClip;
import mchorse.bbs.camera.clips.modifiers.TranslateClip;
import mchorse.bbs.camera.clips.overwrite.CircularClip;
import mchorse.bbs.camera.clips.overwrite.DollyClip;
import mchorse.bbs.camera.clips.overwrite.IdleClip;
import mchorse.bbs.camera.clips.overwrite.KeyframeClip;
import mchorse.bbs.camera.clips.overwrite.PathClip;
import mchorse.bbs.core.Engine;
import mchorse.bbs.cubic.model.ModelManager;
import mchorse.bbs.events.register.RegisterCoreEvent;
import mchorse.bbs.events.register.RegisterFactoriesEvent;
import mchorse.bbs.events.register.RegisterFormsEvent;
import mchorse.bbs.events.register.RegisterL10nEvent;
import mchorse.bbs.events.register.RegisterSettingsEvent;
import mchorse.bbs.forms.FormArchitect;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.categories.ModelFormCategory;
import mchorse.bbs.forms.categories.ParticleFormCategory;
import mchorse.bbs.forms.categories.RecentFormCategory;
import mchorse.bbs.forms.forms.BillboardForm;
import mchorse.bbs.forms.forms.BlockForm;
import mchorse.bbs.forms.forms.ExtrudedForm;
import mchorse.bbs.forms.forms.LabelForm;
import mchorse.bbs.forms.forms.LightForm;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.forms.forms.StructureForm;
import mchorse.bbs.graphics.FramebufferManager;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.ShaderManager;
import mchorse.bbs.graphics.text.FontManager;
import mchorse.bbs.graphics.text.format.BoldFontFormat;
import mchorse.bbs.graphics.text.format.ColorFontFormat;
import mchorse.bbs.graphics.text.format.IFontFormat;
import mchorse.bbs.graphics.text.format.ItalicFontFormat;
import mchorse.bbs.graphics.text.format.RainbowFontFormat;
import mchorse.bbs.graphics.text.format.ResetFontFormat;
import mchorse.bbs.graphics.text.format.ShakeFontFormat;
import mchorse.bbs.graphics.text.format.WaveFontFormat;
import mchorse.bbs.graphics.texture.TextureManager;
import mchorse.bbs.graphics.vao.VAOManager;
import mchorse.bbs.l10n.L10n;
import mchorse.bbs.recording.RecordComponent;
import mchorse.bbs.recording.clips.FormActionClip;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.resources.packs.ExternalAssetsSourcePack;
import mchorse.bbs.resources.packs.InternalAssetsSourcePack;
import mchorse.bbs.settings.Settings;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.settings.SettingsManager;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.film.clips.UIAngleClip;
import mchorse.bbs.ui.film.clips.UIAudioClip;
import mchorse.bbs.ui.film.clips.UICircularClip;
import mchorse.bbs.ui.film.clips.UIDollyClip;
import mchorse.bbs.ui.film.clips.UIDragClip;
import mchorse.bbs.ui.film.clips.UIIdleClip;
import mchorse.bbs.ui.film.clips.UIKeyframeClip;
import mchorse.bbs.ui.film.clips.UILookClip;
import mchorse.bbs.ui.film.clips.UIMathClip;
import mchorse.bbs.ui.film.clips.UIOrbitClip;
import mchorse.bbs.ui.film.clips.UIPathClip;
import mchorse.bbs.ui.film.clips.UIRemapperClip;
import mchorse.bbs.ui.film.clips.UIShakeClip;
import mchorse.bbs.ui.film.clips.UISubtitleClip;
import mchorse.bbs.ui.film.clips.UITranslateClip;
import mchorse.bbs.ui.font.format.UIBaseFontFormat;
import mchorse.bbs.ui.font.format.UIColorFontFormat;
import mchorse.bbs.ui.forms.editors.forms.UIBillboardForm;
import mchorse.bbs.ui.forms.editors.forms.UIBlockForm;
import mchorse.bbs.ui.forms.editors.forms.UIExtrudedForm;
import mchorse.bbs.ui.forms.editors.forms.UILabelForm;
import mchorse.bbs.ui.forms.editors.forms.UILightForm;
import mchorse.bbs.ui.forms.editors.forms.UIModelForm;
import mchorse.bbs.ui.forms.editors.forms.UIParticleForm;
import mchorse.bbs.ui.forms.editors.forms.UIStructureForm;
import mchorse.bbs.ui.recording.editor.actions.UIFormAction;
import mchorse.bbs.ui.tileset.panels.UIModelBlockCombined;
import mchorse.bbs.ui.tileset.panels.UIModelBlockEach;
import mchorse.bbs.ui.tileset.panels.UIModelBlockFactory;
import mchorse.bbs.ui.tileset.panels.UIModelBlockVertical;
import mchorse.bbs.ui.tileset.panels.UIModelBlockWithCollision;
import mchorse.bbs.ui.utils.icons.Icon;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeybindSettings;
import mchorse.bbs.ui.world.entities.components.UIBasicEntityComponent;
import mchorse.bbs.ui.world.entities.components.UIEntityComponent;
import mchorse.bbs.ui.world.entities.components.UIFormEntityComponent;
import mchorse.bbs.ui.world.objects.objects.UICameraWorldObject;
import mchorse.bbs.ui.world.objects.objects.UIPropWorldObject;
import mchorse.bbs.ui.world.objects.objects.UIWorldObject;
import mchorse.bbs.utils.clips.Clip;
import mchorse.bbs.utils.factory.MapFactory;
import mchorse.bbs.voxel.StructureManager;
import mchorse.bbs.voxel.generation.Generator;
import mchorse.bbs.voxel.generation.GeneratorDefault;
import mchorse.bbs.voxel.generation.GeneratorFlat;
import mchorse.bbs.voxel.generation.GeneratorVoid;
import mchorse.bbs.voxel.tilesets.factory.BlockModelAll;
import mchorse.bbs.voxel.tilesets.factory.BlockModelCombined;
import mchorse.bbs.voxel.tilesets.factory.BlockModelCrop;
import mchorse.bbs.voxel.tilesets.factory.BlockModelEach;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactory;
import mchorse.bbs.voxel.tilesets.factory.BlockModelFactoryData;
import mchorse.bbs.voxel.tilesets.factory.BlockModelPlant;
import mchorse.bbs.voxel.tilesets.factory.BlockModelSlab;
import mchorse.bbs.voxel.tilesets.factory.BlockModelStair;
import mchorse.bbs.voxel.tilesets.factory.BlockModelVertical;
import mchorse.bbs.world.entities.components.BasicComponent;
import mchorse.bbs.world.entities.components.CollisionComponent;
import mchorse.bbs.world.entities.components.Component;
import mchorse.bbs.world.entities.components.FormComponent;
import mchorse.bbs.world.objects.CameraObject;
import mchorse.bbs.world.objects.PropObject;
import mchorse.bbs.world.objects.WorldObject;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.function.Consumer;

/**
 * BBS's global god object.
 */
public class BBS
{
    public static final EventBus events = EventBus.builder()
        .logNoSubscriberMessages(false)
        .sendNoSubscriberEvent(false)
        .build();

    private static Engine engine;
    private static File gameFolder;
    private static File assetsFolder;
    private static File configFolder;
    private static File dataFolder;

    /* Core services */
    private static AssetProvider provider;
    private static VAOManager vaos;
    private static ShaderManager shaders;
    private static TextureManager textures;
    private static SoundManager sounds;
    private static FontManager fonts;
    private static FramebufferManager framebuffers;

    /* Foundation services */
    private static SettingsManager configs;
    private static FormArchitect forms;
    private static ModelManager models;
    private static RenderingContext render = new RenderingContext();
    private static L10n l10n;
    private static StructureManager structures;

    /* Data factories */
    private static MapFactory<WorldObject, Class<? extends UIWorldObject>> factoryWorldObjects;
    private static MapFactory<Clip, ClipFactoryData> factoryClips;
    private static MapFactory<Clip, ClipFactoryData> factoryActions;
    private static MapFactory<BlockModelFactory, BlockModelFactoryData> factoryBlockModels;
    private static MapFactory<Generator, Void> factoryGenerators;
    private static MapFactory<Component, Class<? extends UIEntityComponent>> factoryEntityComponents;
    private static MapFactory<IFontFormat, Class<? extends UIBaseFontFormat>> factoryFontFormats;

    /* Getters */

    public static Engine getEngine()
    {
        return engine;
    }

    public static IBridge getEngineAsBridge()
    {
        return engine instanceof IBridge ? (IBridge) engine : null;
    }

    /**
     * Main folder, where all the other folders are located.
     */
    public static File getGameFolder()
    {
        return gameFolder;
    }

    public static File getGamePath(String path)
    {
        return new File(gameFolder, path);
    }

    /**
     * Assets folder within game's folder. It's used to store any assets that can
     * be loaded by {@link #provider}.
     */
    public static File getAssetsFolder()
    {
        return assetsFolder;
    }

    public static File getAssetsPath(String path)
    {
        return new File(assetsFolder, path);
    }

    /**
     * Config folder within game's folder. It's used to store any configuration
     * files.
     */
    public static File getConfigFolder()
    {
        return configFolder;
    }

    public static File getConfigPath(String path)
    {
        return new File(configFolder, path);
    }

    /**
     * Data folder within game's folder. It's used to store game data like
     * quests, dialogues, states, player data, etc. anything related to game
     * basically.
     */
    public static File getDataFolder()
    {
        return dataFolder;
    }

    public static File getDataPath(String path)
    {
        return new File(dataFolder, path);
    }

    public static File getExportFolder()
    {
        return getGamePath("export");
    }

    public static AssetProvider getProvider()
    {
        return provider;
    }

    public static VAOManager getVAOs()
    {
        return vaos;
    }

    public static ShaderManager getShaders()
    {
        return shaders;
    }

    public static TextureManager getTextures()
    {
        return textures;
    }

    public static SoundManager getSounds()
    {
        return sounds;
    }

    public static SettingsManager getConfigs()
    {
        return configs;
    }

    public static FontManager getFonts()
    {
        return fonts;
    }

    public static FramebufferManager getFramebuffers()
    {
        return framebuffers;
    }

    public static FormArchitect getForms()
    {
        return forms;
    }

    public static ModelManager getModels()
    {
        return models;
    }

    public static RenderingContext getRender()
    {
        return render;
    }

    public static L10n getL10n()
    {
        return l10n;
    }

    public static StructureManager getStructures()
    {
        return structures;
    }

    public static MapFactory<WorldObject, Class<? extends UIWorldObject>> getFactoryWorldObjects()
    {
        return factoryWorldObjects;
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryClips()
    {
        return factoryClips;
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryActions()
    {
        return factoryActions;
    }

    public static MapFactory<BlockModelFactory, BlockModelFactoryData> getFactoryBlockModels()
    {
        return factoryBlockModels;
    }

    public static MapFactory<Generator, Void> getFactoryGenerators()
    {
        return factoryGenerators;
    }

    public static MapFactory<Component, Class<? extends UIEntityComponent>> getFactoryEntityComponents()
    {
        return factoryEntityComponents;
    }

    public static MapFactory<IFontFormat, Class<? extends UIBaseFontFormat>> getFactoryFontFormats()
    {
        return factoryFontFormats;
    }

    /**
     * Register core services
     */
    public static void registerCore(Engine theEngine, File gameDirectory)
    {
        engine = theEngine;
        gameFolder = gameDirectory;
        assetsFolder = new File(gameDirectory, "assets");
        configFolder = new File(gameDirectory, "config");
        dataFolder = new File(gameDirectory, "data");

        provider = new AssetProvider();
        provider.register(new ExternalAssetsSourcePack("assets", assetsFolder).providesFiles());
        provider.register(new InternalAssetsSourcePack());
        vaos = new VAOManager();
        shaders = new ShaderManager(provider);
        textures = new TextureManager(provider);
        sounds = new SoundManager(provider);
        fonts = new FontManager(provider);
        framebuffers = new FramebufferManager();

        events.post(new RegisterCoreEvent());
    }

    /**
     * Register foundation services
     */
    public static void registerFoundation()
    {
        configs = new SettingsManager();
        models = new ModelManager(provider);
        l10n = new L10n();
        structures = new StructureManager(BBS.getConfigPath("structures"));

        setupForms(forms);
        setupL10n(l10n);
        setupConfigs(configFolder);
    }

    private static void setupConfigs(File destination)
    {
        destination.mkdirs();

        KeybindSettings.registerClasses();

        setupConfig(Icons.PROCESSOR, "bbs", new File(destination, "bbs.json"), BBSSettings::register);
        setupConfig(Icons.KEY, "keybinds", new File(destination, "keybinds.json"), KeybindSettings::register);

        events.post(new RegisterSettingsEvent());
        configs.reload();
    }

    public static void setupConfig(Icon icon, String id, File destination, Consumer<SettingsBuilder> registerer)
    {
        SettingsBuilder builder = new SettingsBuilder(icon, id, destination);
        Settings settings = builder.getConfig();

        registerer.accept(builder);

        configs.modules.put(settings.getId(), settings);
    }

    private static void setupForms(FormArchitect forms)
    {
        FormCategory extra = new FormCategory(UIKeys.FORMS_CATEGORIES_EXTRA);
        BillboardForm billboard = new BillboardForm();
        LabelForm label = new LabelForm();
        BlockForm block = new BlockForm();
        StructureForm structure = new StructureForm();
        LightForm light = new LightForm();
        ExtrudedForm extruded = new ExtrudedForm();

        billboard.texture.set(Link.assets("textures/error.png"));
        extruded.texture.set(Link.assets("textures/error.png"));

        extra.forms.add(billboard);
        extra.forms.add(label);
        extra.forms.add(block);
        extra.forms.add(structure);
        extra.forms.add(light);
        extra.forms.add(extruded);

        forms.categories.add(new RecentFormCategory());
        forms.readUserCategories();
        forms.categories.add(new ModelFormCategory());
        forms.categories.add(new ParticleFormCategory());
        forms.categories.add(extra);

        events.post(new RegisterFormsEvent(forms));
    }

    private static void setupL10n(L10n l10n)
    {
        l10n.registerOne((lang) -> Link.assets("strings/" + lang + ".json"));

        events.post(new RegisterL10nEvent(l10n));
    }

    /**
     * Register factories
     */
    public static void registerFactories()
    {
        /* Register world objects */
        factoryWorldObjects = new MapFactory<WorldObject, Class<? extends UIWorldObject>>()
            .register(Link.bbs("prop"), PropObject.class, UIPropWorldObject.class)
            .register(Link.bbs("camera"), CameraObject.class, UICameraWorldObject.class);

        /* Register camera clips */
        factoryClips = new MapFactory<Clip, ClipFactoryData>()
            .register(Link.bbs("idle"), IdleClip.class, new ClipFactoryData(Icons.FRUSTUM, 0x159e64, UIIdleClip.class)
                .withConverter(Link.bbs("dolly"), new IdleToDollyConverter())
                .withConverter(Link.bbs("path"), new IdleToPathConverter())
                .withConverter(Link.bbs("keyframe"), new IdleToKeyframeConverter()))
            .register(Link.bbs("dolly"), DollyClip.class, new ClipFactoryData(Icons.CAMERA, 0xffa500, UIDollyClip.class)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER)
                .withConverter(Link.bbs("path"), new DollyToPathConverter())
                .withConverter(Link.bbs("keyframe"), new DollyToKeyframeConverter())
                .withConverter(Link.bbs("dolly"), new PathToDollyConverter()))
            .register(Link.bbs("circular"), CircularClip.class, new ClipFactoryData(Icons.OUTLINE_SPHERE, 0x4ba03e, UICircularClip.class)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER))
            .register(Link.bbs("path"), PathClip.class, new ClipFactoryData(Icons.GALLERY, 0x6820ad, UIPathClip.class)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER))
            .register(Link.bbs("keyframe"), KeyframeClip.class, new ClipFactoryData(Icons.CURVES, 0xde2e9f, UIKeyframeClip.class)
                .withConverter(Link.bbs("idle"), IdleConverter.CONVERTER)
                .withConverter(Link.bbs("keyframe"), new PathToKeyframeConverter()))

            .register(Link.bbs("translate"), TranslateClip.class, new ClipFactoryData(Icons.UPLOAD, 0x4ba03e, UITranslateClip.class))
            .register(Link.bbs("angle"), AngleClip.class, new ClipFactoryData(Icons.ARC, 0xd77a0a, UIAngleClip.class))
            .register(Link.bbs("drag"), DragClip.class, new ClipFactoryData(Icons.FADING, 0x4baff7, UIDragClip.class))
            .register(Link.bbs("shake"), ShakeClip.class, new ClipFactoryData(Icons.EXCHANGE, 0x159e64, UIShakeClip.class))
            .register(Link.bbs("math"), MathClip.class, new ClipFactoryData(Icons.GRAPH, 0x6820ad, UIMathClip.class))
            .register(Link.bbs("look"), LookClip.class, new ClipFactoryData(Icons.VISIBLE, 0x197fff, UILookClip.class))
            .register(Link.bbs("orbit"), OrbitClip.class, new ClipFactoryData(Icons.GLOBE, 0xd82253, UIOrbitClip.class))
            .register(Link.bbs("remapper"), RemapperClip.class, new ClipFactoryData(Icons.TIME, 0x222222, UIRemapperClip.class))
            .register(Link.bbs("audio"), AudioClip.class, new ClipFactoryData(Icons.SOUND, 0xffc825, UIAudioClip.class))
            .register(Link.bbs("subtitle"), SubtitleClip.class, new ClipFactoryData(Icons.FONT, 0x888899, UISubtitleClip.class));

        /* Register actions */
        factoryActions = new MapFactory<Clip, ClipFactoryData>()
            .register(Link.bbs("form"), FormActionClip.class, new ClipFactoryData(Icons.POSE, 0xde2e9f, UIFormAction.class));

        /* Register forms */
        forms = new FormArchitect();
        forms
            .registerEditor(Link.bbs("billboard"), (f) -> new UIBillboardForm())
            .registerEditor(Link.bbs("label"), (f) -> new UILabelForm())
            .registerEditor(Link.bbs("model"), (f) -> new UIModelForm())
            .registerEditor(Link.bbs("particle"), (f) -> new UIParticleForm())
            .registerEditor(Link.bbs("block"), (f) -> new UIBlockForm())
            .registerEditor(Link.bbs("structure"), (f) -> new UIStructureForm())
            .registerEditor(Link.bbs("light"), (f) -> new UILightForm())
            .registerEditor(Link.bbs("extruded"), (f) -> new UIExtrudedForm())
            .register(Link.bbs("billboard"), BillboardForm.class)
            .register(Link.bbs("label"), LabelForm.class)
            .register(Link.bbs("model"), ModelForm.class)
            .register(Link.bbs("particle"), ParticleForm.class)
            .register(Link.bbs("block"), BlockForm.class)
            .register(Link.bbs("structure"), StructureForm.class)
            .register(Link.bbs("light"), LightForm.class)
            .register(Link.bbs("extruded"), ExtrudedForm.class);

        /* Register block models */
        factoryBlockModels = new MapFactory<BlockModelFactory, BlockModelFactoryData>()
            .register(Link.bbs("full"), BlockModelAll.class, new BlockModelFactoryData(Icons.BLOCK, UIModelBlockWithCollision::new))
            .register(Link.bbs("vertical"), BlockModelVertical.class, new BlockModelFactoryData(Icons.BLOCK, UIModelBlockVertical::new))
            .register(Link.bbs("each"), BlockModelEach.class, new BlockModelFactoryData(Icons.BLOCK, UIModelBlockEach::new))
            .register(Link.bbs("plant"), BlockModelPlant.class, new BlockModelFactoryData(Icons.TREE, UIModelBlockWithCollision::new))
            .register(Link.bbs("crop"), BlockModelCrop.class, new BlockModelFactoryData(Icons.CROPS, UIModelBlockWithCollision::new))
            .register(Link.bbs("slab"), BlockModelSlab.class, new BlockModelFactoryData(Icons.SLAB, UIModelBlockFactory::new))
            .register(Link.bbs("stair"), BlockModelStair.class, new BlockModelFactoryData(Icons.STAIR, UIModelBlockFactory::new))
            .register(Link.bbs("combined"), BlockModelCombined.class, new BlockModelFactoryData(Icons.MINIMIZE, UIModelBlockCombined::new));

        /* Register world generators */
        factoryGenerators = new MapFactory<Generator, Void>()
            .register(Link.bbs("default"), GeneratorDefault.class)
            .register(Link.bbs("flat"), GeneratorFlat.class)
            .register(Link.bbs("void"), GeneratorVoid.class);

        /* Register entity components */
        factoryEntityComponents = new MapFactory<Component, Class<? extends UIEntityComponent>>()
            .register(Link.bbs("basic"), BasicComponent.class, UIBasicEntityComponent.class)
            .register(Link.bbs("collision"), CollisionComponent.class, null)
            .register(Link.bbs("form"), FormComponent.class, UIFormEntityComponent.class)
            .register(Link.bbs("record"), RecordComponent.class, null);

        /* Register entity components */
        factoryFontFormats = new MapFactory<IFontFormat, Class<? extends UIBaseFontFormat>>()
            .register(Link.bbs("color"), ColorFontFormat.class, UIColorFontFormat.class)
            .register(Link.bbs("italic"), ItalicFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("bold"), BoldFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("reset"), ResetFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("rainbow"), RainbowFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("shake"), ShakeFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("wave"), WaveFontFormat.class, UIBaseFontFormat.class);

        events.post(new RegisterFactoriesEvent());
    }

    public static void initialize() throws Exception
    {
        l10n.reload();

        sounds.init();
    }

    public static void terminate()
    {
        forms.writeUserCategories();

        vaos.delete();
        shaders.delete();
        textures.delete();
        sounds.delete();
        framebuffers.delete();

        models.delete();
        structures.delete();
    }
}