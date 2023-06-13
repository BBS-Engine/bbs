package mchorse.bbs;

import mchorse.bbs.animation.AnimationManager;
import mchorse.bbs.bridge.IBridge;
import mchorse.bbs.camera.CameraManager;
import mchorse.bbs.game.crafting.CraftingManager;
import mchorse.bbs.game.dialogues.DialogueManager;
import mchorse.bbs.game.huds.HUDManager;
import mchorse.bbs.game.misc.GameSettings;
import mchorse.bbs.game.quests.QuestManager;
import mchorse.bbs.game.quests.chains.QuestChainManager;
import mchorse.bbs.game.scripts.ScriptManager;
import mchorse.bbs.game.scripts.ui.UserInterfaceManager;
import mchorse.bbs.game.states.States;
import mchorse.bbs.particles.ParticleManager;
import mchorse.bbs.recording.RecordManager;
import mchorse.bbs.recording.scene.SceneManager;

import java.io.File;

public class BBSData
{
    /* Data managers */
    private static GameSettings settings;
    private static States states;
    private static QuestManager quests;
    private static CraftingManager crafting;
    private static DialogueManager dialogues;
    private static QuestChainManager chains;
    private static ScriptManager scripts;
    private static HUDManager huds;
    private static CameraManager cameras;
    private static SceneManager scenes;
    private static RecordManager records;
    private static AnimationManager animations;
    private static UserInterfaceManager uis;
    private static ParticleManager particles;

    public static GameSettings getSettings()
    {
        return settings;
    }

    public static States getStates()
    {
        return states;
    }

    public static QuestManager getQuests()
    {
        return quests;
    }

    public static CraftingManager getCrafting()
    {
        return crafting;
    }

    public static DialogueManager getDialogues()
    {
        return dialogues;
    }

    public static QuestChainManager getChains()
    {
        return chains;
    }

    public static ScriptManager getScripts()
    {
        return scripts;
    }

    public static HUDManager getHUDs()
    {
        return huds;
    }

    public static CameraManager getCameras()
    {
        return cameras;
    }

    public static SceneManager getScenes()
    {
        return scenes;
    }

    public static RecordManager getRecords()
    {
        return records;
    }

    public static AnimationManager getAnimations()
    {
        return animations;
    }

    public static UserInterfaceManager getUIs()
    {
        return uis;
    }

    public static ParticleManager getParticles()
    {
        return particles;
    }

    public static void load(File folder, IBridge bridge)
    {
        settings = new GameSettings(new File(folder, "settings.json"));
        settings.load();
        states = new States(new File(folder, "states.json"));
        states.load();

        quests = new QuestManager(new File(folder, "quests"));
        crafting = new CraftingManager(new File(folder, "crafting"));
        dialogues = new DialogueManager(new File(folder, "dialogues"), bridge);
        chains = new QuestChainManager(new File(folder, "chains"));
        scripts = new ScriptManager(new File(folder, "scripts"), bridge);
        huds = new HUDManager(new File(folder, "huds"));
        cameras = new CameraManager(new File(folder, "cameras"));
        scenes = new SceneManager(new File(folder, "scenes"));
        records = new RecordManager(new File(folder, "records"), bridge);
        animations = new AnimationManager(new File(folder, "animations"));
        uis = new UserInterfaceManager(new File(folder, "uis"));
        particles = new ParticleManager(new File(folder, "particles"));
    }

    public static void delete()
    {
        settings.save();
        states.save();

        settings = null;
        states = null;
        quests = null;
        crafting = null;
        dialogues = null;
        chains = null;
        scripts = null;
        huds = null;
        cameras = null;
        scenes = null;
        records = null;
        particles = null;
    }
}