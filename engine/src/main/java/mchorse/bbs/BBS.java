package mchorse.bbs;

import mchorse.bbs.animation.clip.AnimationClip;
import mchorse.bbs.animation.clip.UIAnimationClip;
import mchorse.bbs.audio.SoundManager;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.clips.Clip;
import mchorse.bbs.camera.clips.ClipFactoryData;
import mchorse.bbs.camera.clips.converters.DollyToKeyframeConverter;
import mchorse.bbs.camera.clips.converters.DollyToPathConverter;
import mchorse.bbs.camera.clips.converters.IdleConverter;
import mchorse.bbs.camera.clips.converters.IdleToDollyConverter;
import mchorse.bbs.camera.clips.converters.IdleToKeyframeConverter;
import mchorse.bbs.camera.clips.converters.IdleToPathConverter;
import mchorse.bbs.camera.clips.converters.PathToDollyConverter;
import mchorse.bbs.camera.clips.converters.PathToKeyframeConverter;
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
import mchorse.bbs.events.register.RegisterItemsEvent;
import mchorse.bbs.events.register.RegisterL10nEvent;
import mchorse.bbs.events.register.RegisterSettingsEvent;
import mchorse.bbs.forms.FormArchitect;
import mchorse.bbs.forms.categories.FormCategory;
import mchorse.bbs.forms.categories.ModelFormCategory;
import mchorse.bbs.forms.categories.ParticleFormCategory;
import mchorse.bbs.forms.categories.RecentFormCategory;
import mchorse.bbs.forms.forms.BillboardForm;
import mchorse.bbs.forms.forms.BlockForm;
import mchorse.bbs.forms.forms.ItemForm;
import mchorse.bbs.forms.forms.LabelForm;
import mchorse.bbs.forms.forms.LightForm;
import mchorse.bbs.forms.forms.ModelForm;
import mchorse.bbs.forms.forms.ParticleForm;
import mchorse.bbs.forms.forms.StructureForm;
import mchorse.bbs.game.conditions.ConditionFactoryData;
import mchorse.bbs.game.conditions.blocks.ConditionBlock;
import mchorse.bbs.game.conditions.blocks.ConditionConditionBlock;
import mchorse.bbs.game.conditions.blocks.DialogueConditionBlock;
import mchorse.bbs.game.conditions.blocks.EntityConditionBlock;
import mchorse.bbs.game.conditions.blocks.FormConditionBlock;
import mchorse.bbs.game.conditions.blocks.ItemConditionBlock;
import mchorse.bbs.game.conditions.blocks.QuestConditionBlock;
import mchorse.bbs.game.conditions.blocks.ScriptConditionBlock;
import mchorse.bbs.game.conditions.blocks.StateConditionBlock;
import mchorse.bbs.game.controllers.IGameController;
import mchorse.bbs.game.controllers.SideScrollerGameController;
import mchorse.bbs.game.controllers.ThirdPersonGameController;
import mchorse.bbs.game.controllers.TopDownGameController;
import mchorse.bbs.game.dialogues.DialogueFactoryData;
import mchorse.bbs.game.dialogues.nodes.CraftingNode;
import mchorse.bbs.game.dialogues.nodes.QuestChainNode;
import mchorse.bbs.game.dialogues.nodes.QuestNode;
import mchorse.bbs.game.dialogues.nodes.ReactionNode;
import mchorse.bbs.game.dialogues.nodes.ReplyNode;
import mchorse.bbs.game.entities.components.NpcComponent;
import mchorse.bbs.game.entities.components.PlayerComponent;
import mchorse.bbs.game.events.nodes.CancelNode;
import mchorse.bbs.game.events.nodes.ConditionNode;
import mchorse.bbs.game.events.nodes.EventBaseNode;
import mchorse.bbs.game.events.nodes.SwitchNode;
import mchorse.bbs.game.events.nodes.TriggerNode;
import mchorse.bbs.game.items.Item;
import mchorse.bbs.game.items.ItemManager;
import mchorse.bbs.game.items.ItemTrigger;
import mchorse.bbs.game.quests.objectives.CollectObjective;
import mchorse.bbs.game.quests.objectives.KillObjective;
import mchorse.bbs.game.quests.objectives.Objective;
import mchorse.bbs.game.quests.objectives.StateObjective;
import mchorse.bbs.game.quests.rewards.ItemStackReward;
import mchorse.bbs.game.quests.rewards.Reward;
import mchorse.bbs.game.regions.shapes.BoxShape;
import mchorse.bbs.game.regions.shapes.CylinderShape;
import mchorse.bbs.game.regions.shapes.Shape;
import mchorse.bbs.game.regions.shapes.SphereShape;
import mchorse.bbs.game.scripts.ui.UserInterfaceTriggerBlock;
import mchorse.bbs.game.scripts.ui.components.UIButtonComponent;
import mchorse.bbs.game.scripts.ui.components.UIClickComponent;
import mchorse.bbs.game.scripts.ui.components.UIComponent;
import mchorse.bbs.game.scripts.ui.components.UIFormComponent;
import mchorse.bbs.game.scripts.ui.components.UIGraphicsComponent;
import mchorse.bbs.game.scripts.ui.components.UIIconButtonComponent;
import mchorse.bbs.game.scripts.ui.components.UILabelComponent;
import mchorse.bbs.game.scripts.ui.components.UILayoutComponent;
import mchorse.bbs.game.scripts.ui.components.UISlotComponent;
import mchorse.bbs.game.scripts.ui.components.UIStringListComponent;
import mchorse.bbs.game.scripts.ui.components.UITextComponent;
import mchorse.bbs.game.scripts.ui.components.UITextareaComponent;
import mchorse.bbs.game.scripts.ui.components.UITextboxComponent;
import mchorse.bbs.game.scripts.ui.components.UIToggleComponent;
import mchorse.bbs.game.scripts.ui.components.UITrackpadComponent;
import mchorse.bbs.game.scripts.ui.graphics.GradientGraphic;
import mchorse.bbs.game.scripts.ui.graphics.Graphic;
import mchorse.bbs.game.scripts.ui.graphics.IconGraphic;
import mchorse.bbs.game.scripts.ui.graphics.ImageGraphic;
import mchorse.bbs.game.scripts.ui.graphics.RectGraphic;
import mchorse.bbs.game.scripts.ui.graphics.ShadowGraphic;
import mchorse.bbs.game.scripts.ui.graphics.TextGraphic;
import mchorse.bbs.game.triggers.TriggerFactoryData;
import mchorse.bbs.game.triggers.blocks.AnimationTriggerBlock;
import mchorse.bbs.game.triggers.blocks.CameraTriggerBlock;
import mchorse.bbs.game.triggers.blocks.DialogueTriggerBlock;
import mchorse.bbs.game.triggers.blocks.FormTriggerBlock;
import mchorse.bbs.game.triggers.blocks.HUDSceneTriggerBlock;
import mchorse.bbs.game.triggers.blocks.ItemTriggerBlock;
import mchorse.bbs.game.triggers.blocks.ScriptTriggerBlock;
import mchorse.bbs.game.triggers.blocks.SoundTriggerBlock;
import mchorse.bbs.game.triggers.blocks.StateTriggerBlock;
import mchorse.bbs.game.triggers.blocks.TriggerBlock;
import mchorse.bbs.game.utils.factory.MapFactory;
import mchorse.bbs.graphics.FramebufferManager;
import mchorse.bbs.graphics.RenderingContext;
import mchorse.bbs.graphics.shaders.ShaderManager;
import mchorse.bbs.graphics.text.FontManager;
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
import mchorse.bbs.recording.actions.Action;
import mchorse.bbs.recording.actions.ActionFactoryData;
import mchorse.bbs.recording.actions.FormAction;
import mchorse.bbs.recording.scene.AudioClip;
import mchorse.bbs.recording.scene.SceneClip;
import mchorse.bbs.resources.AssetProvider;
import mchorse.bbs.resources.Link;
import mchorse.bbs.resources.packs.ExternalAssetsSourcePack;
import mchorse.bbs.resources.packs.InternalAssetsSourcePack;
import mchorse.bbs.settings.Settings;
import mchorse.bbs.settings.SettingsBuilder;
import mchorse.bbs.settings.SettingsManager;
import mchorse.bbs.ui.UIKeys;
import mchorse.bbs.ui.camera.clips.UIAngleClip;
import mchorse.bbs.ui.camera.clips.UICircularClip;
import mchorse.bbs.ui.camera.clips.UIDollyClip;
import mchorse.bbs.ui.camera.clips.UIDragClip;
import mchorse.bbs.ui.camera.clips.UIIdleClip;
import mchorse.bbs.ui.camera.clips.UIKeyframeClip;
import mchorse.bbs.ui.camera.clips.UILookClip;
import mchorse.bbs.ui.camera.clips.UIMathClip;
import mchorse.bbs.ui.camera.clips.UIOrbitClip;
import mchorse.bbs.ui.camera.clips.UIPathClip;
import mchorse.bbs.ui.camera.clips.UIRemapperClip;
import mchorse.bbs.ui.camera.clips.UIShakeClip;
import mchorse.bbs.ui.camera.clips.UITranslateClip;
import mchorse.bbs.ui.font.format.UIBaseFontFormat;
import mchorse.bbs.ui.font.format.UIColorFontFormat;
import mchorse.bbs.ui.forms.editors.forms.UIBillboardForm;
import mchorse.bbs.ui.forms.editors.forms.UIBlockForm;
import mchorse.bbs.ui.forms.editors.forms.UIItemForm;
import mchorse.bbs.ui.forms.editors.forms.UILabelForm;
import mchorse.bbs.ui.forms.editors.forms.UILightForm;
import mchorse.bbs.ui.forms.editors.forms.UIModelForm;
import mchorse.bbs.ui.forms.editors.forms.UIParticleForm;
import mchorse.bbs.ui.forms.editors.forms.UIStructureForm;
import mchorse.bbs.ui.game.conditions.blocks.UIConditionConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIDialogueConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIEntityConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIFormConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIItemConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIQuestConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIScriptConditionBlockPanel;
import mchorse.bbs.ui.game.conditions.blocks.UIStateConditionBlockPanel;
import mchorse.bbs.ui.game.controllers.UIBaseGameControllerPanel;
import mchorse.bbs.ui.game.controllers.UIThirdPersonGameControllerPanel;
import mchorse.bbs.ui.game.controllers.UITopDownGameControllerPanel;
import mchorse.bbs.ui.game.items.UIItemEditor;
import mchorse.bbs.ui.game.items.UIItemTriggerEditor;
import mchorse.bbs.ui.game.nodes.dialogues.UICraftingNodePanel;
import mchorse.bbs.ui.game.nodes.dialogues.UIDialogueNodePanel;
import mchorse.bbs.ui.game.nodes.dialogues.UIQuestChainNodePanel;
import mchorse.bbs.ui.game.nodes.dialogues.UIQuestDialogueNodePanel;
import mchorse.bbs.ui.game.nodes.dialogues.UIReactionNodePanel;
import mchorse.bbs.ui.game.nodes.events.UICancelNodePanel;
import mchorse.bbs.ui.game.nodes.events.UIConditionNodePanel;
import mchorse.bbs.ui.game.nodes.events.UISwitchNodePanel;
import mchorse.bbs.ui.game.nodes.events.UITriggerNodePanel;
import mchorse.bbs.ui.game.quests.objectives.UICollectObjective;
import mchorse.bbs.ui.game.quests.objectives.UIKillObjective;
import mchorse.bbs.ui.game.quests.objectives.UIObjective;
import mchorse.bbs.ui.game.quests.objectives.UIStateObjective;
import mchorse.bbs.ui.game.quests.rewards.UIItemStackReward;
import mchorse.bbs.ui.game.quests.rewards.UIReward;
import mchorse.bbs.ui.game.scripts.themes.Themes;
import mchorse.bbs.ui.game.triggers.panels.UIAnimationTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UICameraTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIDialogueTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIFormTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIHUDSceneTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIItemTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIScriptTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UISoundTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIStateTriggerBlockPanel;
import mchorse.bbs.ui.game.triggers.panels.UIUserInterfaceTriggerBlockPanel;
import mchorse.bbs.ui.recording.editor.actions.UIFormActionPanel;
import mchorse.bbs.ui.recording.scene.UIAudioClip;
import mchorse.bbs.ui.recording.scene.UISceneClip;
import mchorse.bbs.ui.tileset.panels.UIModelBlockCombined;
import mchorse.bbs.ui.tileset.panels.UIModelBlockEach;
import mchorse.bbs.ui.tileset.panels.UIModelBlockFactory;
import mchorse.bbs.ui.tileset.panels.UIModelBlockVertical;
import mchorse.bbs.ui.tileset.panels.UIModelBlockWithCollision;
import mchorse.bbs.ui.ui.components.UIButtonComponentPanel;
import mchorse.bbs.ui.ui.components.UIComponentPanel;
import mchorse.bbs.ui.ui.components.UIFormComponentPanel;
import mchorse.bbs.ui.ui.components.UIGraphicsComponentPanel;
import mchorse.bbs.ui.ui.components.UIIconButtonComponentPanel;
import mchorse.bbs.ui.ui.components.UILabelBaseComponentPanel;
import mchorse.bbs.ui.ui.components.UILabelComponentPanel;
import mchorse.bbs.ui.ui.components.UILayoutComponentPanel;
import mchorse.bbs.ui.ui.components.UISlotComponentPanel;
import mchorse.bbs.ui.ui.components.UIStringListComponentPanel;
import mchorse.bbs.ui.ui.components.UITextComponentPanel;
import mchorse.bbs.ui.ui.components.UITextboxComponentPanel;
import mchorse.bbs.ui.ui.components.UIToggleComponentPanel;
import mchorse.bbs.ui.ui.components.UITrackpadComponentPanel;
import mchorse.bbs.ui.ui.graphics.UIGradientGraphicPanel;
import mchorse.bbs.ui.ui.graphics.UIGraphicPanel;
import mchorse.bbs.ui.ui.graphics.UIIconGraphicPanel;
import mchorse.bbs.ui.ui.graphics.UIImageGraphicPanel;
import mchorse.bbs.ui.ui.graphics.UIShadowGraphicPanel;
import mchorse.bbs.ui.ui.graphics.UITextGraphicPanel;
import mchorse.bbs.ui.utils.icons.Icons;
import mchorse.bbs.ui.utils.keys.KeybindSettings;
import mchorse.bbs.ui.world.entities.components.UIBasicEntityComponent;
import mchorse.bbs.ui.world.entities.components.UIEntityComponent;
import mchorse.bbs.ui.world.entities.components.UIFormEntityComponent;
import mchorse.bbs.ui.world.entities.components.UIItemEntityComponent;
import mchorse.bbs.ui.world.objects.objects.UICameraWorldObject;
import mchorse.bbs.ui.world.objects.objects.UIPropWorldObject;
import mchorse.bbs.ui.world.objects.objects.UIRegionWorldObject;
import mchorse.bbs.ui.world.objects.objects.UITriggerWorldObject;
import mchorse.bbs.ui.world.objects.objects.UIWorldObject;
import mchorse.bbs.utils.colors.Colors;
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
import mchorse.bbs.world.entities.components.ItemComponent;
import mchorse.bbs.world.objects.CameraObject;
import mchorse.bbs.world.objects.PropObject;
import mchorse.bbs.world.objects.RegionObject;
import mchorse.bbs.world.objects.TriggerObject;
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
    private static ItemManager items;
    private static RenderingContext render = new RenderingContext();
    private static L10n l10n;
    private static StructureManager structures;

    /* Data factories */
    private static MapFactory<EventBaseNode, DialogueFactoryData> factoryDialogues;
    private static MapFactory<ConditionBlock, ConditionFactoryData> factoryConditions;
    private static MapFactory<TriggerBlock, TriggerFactoryData> factoryTriggers;
    private static MapFactory<UIComponent, Class<? extends UIComponentPanel>> factoryUIComponents;
    private static MapFactory<Shape, Void> factoryShapes;
    private static MapFactory<Graphic, Class<? extends UIGraphicPanel>> factoryGraphics;
    private static MapFactory<Objective, Class<? extends UIObjective>> factoryObjectives;
    private static MapFactory<Reward, Class<? extends UIReward>> factoryRewards;
    private static MapFactory<WorldObject, Class<? extends UIWorldObject>> factoryWorldObjects;
    private static MapFactory<Clip, ClipFactoryData> factoryClips;
    private static MapFactory<Action, ActionFactoryData> factoryActions;
    private static MapFactory<Item, Class<? extends UIItemEditor>> factoryItems;
    private static MapFactory<BlockModelFactory, BlockModelFactoryData> factoryBlockModels;
    private static MapFactory<Generator, Void> factoryGenerators;
    private static MapFactory<Component, Class<? extends UIEntityComponent>> factoryEntityComponents;
    private static MapFactory<IFontFormat, Class<? extends UIBaseFontFormat>> factoryFontFormats;
    private static MapFactory<IGameController, Class<? extends UIBaseGameControllerPanel>> factoryGameControllers;

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

    public static ItemManager getItems()
    {
        return items;
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

    public static MapFactory<EventBaseNode, DialogueFactoryData> getFactoryDialogues()
    {
        return factoryDialogues;
    }

    public static MapFactory<ConditionBlock, ConditionFactoryData> getFactoryConditions()
    {
        return factoryConditions;
    }

    public static MapFactory<TriggerBlock, TriggerFactoryData> getFactoryTriggers()
    {
        return factoryTriggers;
    }

    public static MapFactory<UIComponent, Class<? extends UIComponentPanel>> getFactoryUIComponents()
    {
        return factoryUIComponents;
    }

    public static MapFactory<Shape, Void> getFactoryShapes()
    {
        return factoryShapes;
    }

    public static MapFactory<Graphic, Class<? extends UIGraphicPanel>> getFactoryGraphics()
    {
        return factoryGraphics;
    }

    public static MapFactory<Objective, Class<? extends UIObjective>> getFactoryObjectives()
    {
        return factoryObjectives;
    }

    public static MapFactory<Reward, Class<? extends UIReward>> getFactoryRewards()
    {
        return factoryRewards;
    }

    public static MapFactory<WorldObject, Class<? extends UIWorldObject>> getFactoryWorldObjects()
    {
        return factoryWorldObjects;
    }

    public static MapFactory<Clip, ClipFactoryData> getFactoryClips()
    {
        return factoryClips;
    }

    public static MapFactory<Action, ActionFactoryData> getFactoryActions()
    {
        return factoryActions;
    }

    public static MapFactory<Item, Class<? extends UIItemEditor>> getFactoryItems()
    {
        return factoryItems;
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

    public static MapFactory<IGameController, Class<? extends UIBaseGameControllerPanel>> getFactoryGameControllers()
    {
        return factoryGameControllers;
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
        items = new ItemManager();
        l10n = new L10n();
        structures = new StructureManager(BBS.getConfigPath("structures"));

        setupForms(forms);
        setupItems(items);
        setupL10n(l10n);
        setupConfigs(configFolder);
    }

    private static void setupConfigs(File destination)
    {
        destination.mkdirs();

        Themes.initiate(BBS.configFolder);
        KeybindSettings.registerClasses();

        setupConfig("bbs", new File(destination, "bbs.json"), BBSSettings::register);
        setupConfig("keybinds", new File(destination, "keybinds.json"), KeybindSettings::register);

        events.post(new RegisterSettingsEvent());
        configs.reload();
    }

    public static void setupConfig(String id, File destination, Consumer<SettingsBuilder> registerer)
    {
        SettingsBuilder builder = new SettingsBuilder(id, destination);
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
        ItemForm item = new ItemForm();
        LightForm light = new LightForm();

        billboard.texture.set(Link.assets("textures/error.png"));

        extra.forms.add(billboard);
        extra.forms.add(label);
        extra.forms.add(block);
        extra.forms.add(structure);
        extra.forms.add(item);
        extra.forms.add(light);

        forms.categories.add(new RecentFormCategory());
        forms.readUserCategories();
        forms.categories.add(new ModelFormCategory());
        forms.categories.add(new ParticleFormCategory());
        forms.categories.add(extra);

        events.post(new RegisterFormsEvent(forms));
    }

    private static void setupItems(ItemManager items)
    {
        events.post(new RegisterItemsEvent(items));
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
        /* Register dialogue nodes */
        factoryDialogues = new MapFactory<EventBaseNode, DialogueFactoryData>()
            .register(Link.bbs("condition"), ConditionNode.class, new DialogueFactoryData(Colors.CONDITION, UIConditionNodePanel.class))
            .register(Link.bbs("switch"), SwitchNode.class, new DialogueFactoryData(Colors.FACTION, UISwitchNodePanel.class))
            .register(Link.bbs("trigger"), TriggerNode.class, new DialogueFactoryData(Colors.STATE, UITriggerNodePanel.class))
            .register(Link.bbs("cancel"), CancelNode.class, new DialogueFactoryData(Colors.CANCEL, UICancelNodePanel.class))
            .register(Link.bbs("reply"), ReplyNode.class, new DialogueFactoryData(Colors.REPLY, UIDialogueNodePanel.class))
            .register(Link.bbs("reaction"), ReactionNode.class, new DialogueFactoryData(Colors.STATE, UIReactionNodePanel.class))
            .register(Link.bbs("crafting"), CraftingNode.class, new DialogueFactoryData(Colors.CRAFTING, UICraftingNodePanel.class))
            .register(Link.bbs("quest_chain"), QuestChainNode.class, new DialogueFactoryData(Colors.QUEST, UIQuestChainNodePanel.class))
            .register(Link.bbs("quest"), QuestNode.class, new DialogueFactoryData(Colors.QUEST, UIQuestDialogueNodePanel.class));

        /* Register condition blocks */
        factoryConditions = new MapFactory<ConditionBlock, ConditionFactoryData>()
            .register(Link.bbs("quest"), QuestConditionBlock.class, new ConditionFactoryData(Colors.QUEST, UIQuestConditionBlockPanel.class))
            .register(Link.bbs("state"), StateConditionBlock.class, new ConditionFactoryData(Colors.STATE, UIStateConditionBlockPanel.class))
            .register(Link.bbs("dialogue"), DialogueConditionBlock.class, new ConditionFactoryData(Colors.DIALOGUE, UIDialogueConditionBlockPanel.class))
            .register(Link.bbs("item"), ItemConditionBlock.class, new ConditionFactoryData(Colors.CRAFTING, UIItemConditionBlockPanel.class))
            .register(Link.bbs("entity"), EntityConditionBlock.class, new ConditionFactoryData(Colors.ENTITY, UIEntityConditionBlockPanel.class))
            .register(Link.bbs("condition"), ConditionConditionBlock.class, new ConditionFactoryData(Colors.CONDITION, UIConditionConditionBlockPanel.class))
            .register(Link.bbs("form"), FormConditionBlock.class, new ConditionFactoryData(Colors.FORM, UIFormConditionBlockPanel.class))
            .register(Link.bbs("script"), ScriptConditionBlock.class, new ConditionFactoryData(Colors.REPLY, UIScriptConditionBlockPanel.class));

        /* Register condition blocks */
        factoryTriggers = new MapFactory<TriggerBlock, TriggerFactoryData>()
            .register(Link.bbs("sound"), SoundTriggerBlock.class, new TriggerFactoryData(Colors.REPLY, UISoundTriggerBlockPanel.class))
            .register(Link.bbs("dialogue"), DialogueTriggerBlock.class, new TriggerFactoryData(Colors.DIALOGUE, UIDialogueTriggerBlockPanel.class))
            .register(Link.bbs("script"), ScriptTriggerBlock.class, new TriggerFactoryData(Colors.ENTITY, UIScriptTriggerBlockPanel.class))
            .register(Link.bbs("item"), ItemTriggerBlock.class, new TriggerFactoryData(Colors.CRAFTING, UIItemTriggerBlockPanel.class))
            .register(Link.bbs("state"), StateTriggerBlock.class, new TriggerFactoryData(Colors.STATE, UIStateTriggerBlockPanel.class))
            .register(Link.bbs("form"), FormTriggerBlock.class, new TriggerFactoryData(Colors.FORM, UIFormTriggerBlockPanel.class))
            .register(Link.bbs("camera"), CameraTriggerBlock.class, new TriggerFactoryData(0x159e64, UICameraTriggerBlockPanel.class))
            .register(Link.bbs("animation"), AnimationTriggerBlock.class, new TriggerFactoryData(0x99e65f, UIAnimationTriggerBlockPanel.class))
            .register(Link.bbs("hud"), HUDSceneTriggerBlock.class, new TriggerFactoryData(0xc42430, UIHUDSceneTriggerBlockPanel.class))
            .register(Link.bbs("ui"), UserInterfaceTriggerBlock.class, new TriggerFactoryData(0xfff540, UIUserInterfaceTriggerBlockPanel.class));

        /* Register UI components */
        factoryUIComponents = new MapFactory<UIComponent, Class<? extends UIComponentPanel>>()
            .register(Link.bbs("graphics"), UIGraphicsComponent.class, UIGraphicsComponentPanel.class)
            .register(Link.bbs("button"), UIButtonComponent.class, UIButtonComponentPanel.class)
            .register(Link.bbs("icon"), UIIconButtonComponent.class, UIIconButtonComponentPanel.class)
            .register(Link.bbs("label"), UILabelComponent.class, UILabelComponentPanel.class)
            .register(Link.bbs("text"), UITextComponent.class, UITextComponentPanel.class)
            .register(Link.bbs("textbox"), UITextboxComponent.class, UITextboxComponentPanel.class)
            .register(Link.bbs("textarea"), UITextareaComponent.class, UILabelBaseComponentPanel.class)
            .register(Link.bbs("toggle"), UIToggleComponent.class, UIToggleComponentPanel.class)
            .register(Link.bbs("trackpad"), UITrackpadComponent.class, UITrackpadComponentPanel.class)
            .register(Link.bbs("strings"), UIStringListComponent.class, UIStringListComponentPanel.class)
            .register(Link.bbs("slot"), UISlotComponent.class, UISlotComponentPanel.class)
            .register(Link.bbs("layout"), UILayoutComponent.class, UILayoutComponentPanel.class)
            .register(Link.bbs("form"), UIFormComponent.class, UIFormComponentPanel.class)
            .register(Link.bbs("clickarea"), UIClickComponent.class, UIComponentPanel.class);

        /* Register region shapes */
        factoryShapes = new MapFactory<Shape, Void>()
            .register(Link.bbs("box"), BoxShape.class)
            .register(Link.bbs("sphere"), SphereShape.class)
            .register(Link.bbs("cylinder"), CylinderShape.class);

        /* Register UI graphics */
        factoryGraphics = new MapFactory<Graphic, Class<? extends UIGraphicPanel>>()
            .register(Link.bbs("rect"), RectGraphic.class, UIGraphicPanel.class)
            .register(Link.bbs("gradient"), GradientGraphic.class, UIGradientGraphicPanel.class)
            .register(Link.bbs("image"), ImageGraphic.class, UIImageGraphicPanel.class)
            .register(Link.bbs("text"), TextGraphic.class, UITextGraphicPanel.class)
            .register(Link.bbs("icon"), IconGraphic.class, UIIconGraphicPanel.class)
            .register(Link.bbs("shadow"), ShadowGraphic.class, UIShadowGraphicPanel.class);

        /* Register quest objectives */
        factoryObjectives = new MapFactory<Objective, Class<? extends UIObjective>>()
            .register(Link.bbs("collect"), CollectObjective.class, UICollectObjective.class)
            .register(Link.bbs("kill"), KillObjective.class, UIKillObjective.class)
            .register(Link.bbs("state"), StateObjective.class, UIStateObjective.class);

        /* Register quest rewards */
        factoryRewards = new MapFactory<Reward, Class<? extends UIReward>>()
            .register(Link.bbs("items"), ItemStackReward.class, UIItemStackReward.class);

        /* Register world objects */
        factoryWorldObjects = new MapFactory<WorldObject, Class<? extends UIWorldObject>>()
            .register(Link.bbs("prop"), PropObject.class, UIPropWorldObject.class)
            .register(Link.bbs("region"), RegionObject.class, UIRegionWorldObject.class)
            .register(Link.bbs("trigger"), TriggerObject.class, UITriggerWorldObject.class)
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

            .register(Link.bbs("scene"), SceneClip.class, new ClipFactoryData(Icons.SCENE, 0xff1493, UISceneClip.class))
            .register(Link.bbs("audio"), AudioClip.class, new ClipFactoryData(Icons.SOUND, 0xffffc825, UIAudioClip.class))
            .register(Link.bbs("animation"), AnimationClip.class, new ClipFactoryData(Icons.CURVES, 0xeeeeee, UIAnimationClip.class));

        /* Register actions */
        factoryActions = new MapFactory<Action, ActionFactoryData>()
            .register(Link.bbs("form"), FormAction.class, new ActionFactoryData(0xde2e9f, UIFormActionPanel.class));

        /* Register items */
        factoryItems = new MapFactory<Item, Class<? extends UIItemEditor>>()
            .register(Link.bbs("trigger"), ItemTrigger.class, UIItemTriggerEditor.class);

        /* Register forms */
        forms = new FormArchitect();
        forms
            .registerEditor(Link.bbs("billboard"), (f) -> new UIBillboardForm())
            .registerEditor(Link.bbs("label"), (f) -> new UILabelForm())
            .registerEditor(Link.bbs("model"), (f) -> new UIModelForm())
            .registerEditor(Link.bbs("particle"), (f) -> new UIParticleForm())
            .registerEditor(Link.bbs("block"), (f) -> new UIBlockForm())
            .registerEditor(Link.bbs("structure"), (f) -> new UIStructureForm())
            .registerEditor(Link.bbs("item"), (f) -> new UIItemForm())
            .registerEditor(Link.bbs("light"), (f) -> new UILightForm())
            .register(Link.bbs("billboard"), BillboardForm.class)
            .register(Link.bbs("label"), LabelForm.class)
            .register(Link.bbs("model"), ModelForm.class)
            .register(Link.bbs("particle"), ParticleForm.class)
            .register(Link.bbs("block"), BlockForm.class)
            .register(Link.bbs("structure"), StructureForm.class)
            .register(Link.bbs("item"), ItemForm.class)
            .register(Link.bbs("light"), LightForm.class);

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
            .register(Link.bbs("item"), ItemComponent.class, UIItemEntityComponent.class)
            .register(Link.bbs("npc"), NpcComponent.class, null)
            .register(Link.bbs("player"), PlayerComponent.class, null)
            .register(Link.bbs("record"), RecordComponent.class, null);

        /* Register entity components */
        factoryFontFormats = new MapFactory<IFontFormat, Class<? extends UIBaseFontFormat>>()
            .register(Link.bbs("color"), ColorFontFormat.class, UIColorFontFormat.class)
            .register(Link.bbs("italic"), ItalicFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("reset"), ResetFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("rainbow"), RainbowFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("shake"), ShakeFontFormat.class, UIBaseFontFormat.class)
            .register(Link.bbs("wave"), WaveFontFormat.class, UIBaseFontFormat.class);

        /* Register game controllers */
        factoryGameControllers = new MapFactory<IGameController, Class<? extends UIBaseGameControllerPanel>>()
            .register(Link.bbs("third_person"), ThirdPersonGameController.class, UIThirdPersonGameControllerPanel.class)
            .register(Link.bbs("side_scroller"), SideScrollerGameController.class, UIBaseGameControllerPanel.class)
            .register(Link.bbs("top_down"), TopDownGameController.class, UITopDownGameControllerPanel.class);

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
        items.delete();
        framebuffers.delete();

        models.delete();
        structures.delete();
    }
}