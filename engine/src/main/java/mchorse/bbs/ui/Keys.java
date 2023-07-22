package mchorse.bbs.ui;

import mchorse.bbs.ui.utils.keys.KeyCombo;
import org.lwjgl.glfw.GLFW;

public class Keys
{
    /* General */
    public static final KeyCombo DESELECT = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_CLIPS_DESELECT, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo KEYBINDS = new KeyCombo(UIKeys.KEYS_LIST, GLFW.GLFW_KEY_F9);
    public static final KeyCombo NEXT = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_NEXT, GLFW.GLFW_KEY_RIGHT).repeatable();
    public static final KeyCombo PLAUSE = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PLAUSE, GLFW.GLFW_KEY_SPACE);
    public static final KeyCombo PREV = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PREV, GLFW.GLFW_KEY_LEFT).repeatable();
    public static final KeyCombo REDO = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_REDO, GLFW.GLFW_KEY_Y, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo UNDO = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_UNDO, GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo COPY = new KeyCombo(UIKeys.COPY, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_CONTROL);
    public static final KeyCombo PASTE = new KeyCombo(UIKeys.PASTE, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_CONTROL);

    /* Camera editor */
    public static final KeyCombo ADD_AT_CURSOR = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_AT_CURSOR, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo ADD_AT_TICK = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_AT_TICK, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo ADD_ON_TOP = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_ADD_ON_TOP, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo CLIP_CUT = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_CUT, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("camera");
    public static final KeyCombo CLIP_DURATION = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_SHIFT_DURATION, GLFW.GLFW_KEY_M).categoryKey("camera");
    public static final KeyCombo CLIP_ENABLE = new KeyCombo(UIKeys.CAMERA_TIMELINE_KEYS_ENABLED, GLFW.GLFW_KEY_H).categoryKey("camera");
    public static final KeyCombo CLIP_REMOVE = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_REMOVE_CLIPS, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo CLIP_SHIFT = new KeyCombo(UIKeys.CAMERA_TIMELINE_CONTEXT_SHIFT, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("camera");
    public static final KeyCombo FLIGHT = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_MODES_FLIGHT, GLFW.GLFW_KEY_F).categoryKey("camera");
    public static final KeyCombo LOOPING = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_MODES_LOOPING, GLFW.GLFW_KEY_L).categoryKey("camera");
    public static final KeyCombo LOOPING_SET_MAX = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_LOOPING_SET_MAX, GLFW.GLFW_KEY_RIGHT_BRACKET).categoryKey("camera");
    public static final KeyCombo LOOPING_SET_MIN = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_LOOPING_SET_MIN, GLFW.GLFW_KEY_LEFT_BRACKET).categoryKey("camera");
    public static final KeyCombo NEXT_CLIP = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_NEXT_CLIP, GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_LEFT_SHIFT).repeatable().categoryKey("camera");
    public static final KeyCombo PREV_CLIP = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_PREV_CLIP, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_LEFT_SHIFT).repeatable().categoryKey("camera");
    public static final KeyCombo JUMP_FORWARD = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_JUMP_FORWARD, GLFW.GLFW_KEY_UP).repeatable().categoryKey("camera");
    public static final KeyCombo JUMP_BACKWARD = new KeyCombo(UIKeys.CAMERA_EDITOR_KEYS_EDITOR_JUMP_BACKWARD, GLFW.GLFW_KEY_DOWN).repeatable().categoryKey("camera");

    /* Flight mode keybinds */
    public static final KeyCombo FLIGHT_FORWARD = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_FORWARD, GLFW.GLFW_KEY_W).categoryKey("flight");
    public static final KeyCombo FLIGHT_BACKWARD = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_BACKWARD, GLFW.GLFW_KEY_S).categoryKey("flight");
    public static final KeyCombo FLIGHT_LEFT = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_LEFT, GLFW.GLFW_KEY_A).categoryKey("flight");
    public static final KeyCombo FLIGHT_RIGHT = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_RIGHT, GLFW.GLFW_KEY_D).categoryKey("flight");
    public static final KeyCombo FLIGHT_UP = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_UP, GLFW.GLFW_KEY_E).categoryKey("flight");
    public static final KeyCombo FLIGHT_DOWN = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_DOWN, GLFW.GLFW_KEY_Q).categoryKey("flight");
    public static final KeyCombo FLIGHT_TILT_UP = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_TILT_UP, GLFW.GLFW_KEY_UP).categoryKey("flight");
    public static final KeyCombo FLIGHT_TILT_DOWN = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_TILT_DOWN, GLFW.GLFW_KEY_DOWN).categoryKey("flight");
    public static final KeyCombo FLIGHT_PAN_LEFT = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_PAN_LEFT, GLFW.GLFW_KEY_LEFT).categoryKey("flight");
    public static final KeyCombo FLIGHT_PAN_RIGHT = new KeyCombo(UIKeys.CAMERA_FLIGHT_FLIGHT_PAN_RIGHT, GLFW.GLFW_KEY_RIGHT).categoryKey("flight");

    /* Dashboard */
    public static final KeyCombo OPEN_DATA_MANAGER = new KeyCombo(UIKeys.PANELS_KEYS_OPEN_DATA_MANAGER, GLFW.GLFW_KEY_N).categoryKey("dashboard");
    public static final KeyCombo TOGGLE_VISIBILITY = new KeyCombo(UIKeys.DASHBOARD_CONTEXT_TOGGLE_VISIBILITY, GLFW.GLFW_KEY_F1).categoryKey("dashboard");
    public static final KeyCombo DASHBOARD_WORLD_EDITOR = new KeyCombo(UIKeys.PANELS_KEYS_WORLD_EDITOR, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("dashboard");

    /* Forms */
    public static final KeyCombo FORMS_FOCUS = new KeyCombo(UIKeys.FORMS_LIST_CONTEXT_FOCUS, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("forms");
    public static final KeyCombo FORMS_PICK = new KeyCombo(UIKeys.PICK, GLFW.GLFW_KEY_P).categoryKey("forms");
    public static final KeyCombo FORMS_EDIT = new KeyCombo(UIKeys.EDIT, GLFW.GLFW_KEY_E).categoryKey("forms");
    public static final KeyCombo FORMS_PICK_ALT = new KeyCombo(UIKeys.PICK, GLFW.GLFW_KEY_P, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("forms");
    public static final KeyCombo FORMS_EDIT_ALT = new KeyCombo(UIKeys.EDIT, GLFW.GLFW_KEY_E, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("forms");

    /* Nodes */
    public static final KeyCombo NODES_TIE = new KeyCombo(UIKeys.NODES_CONTEXT_TIE, GLFW.GLFW_KEY_F).categoryKey("nodes");
    public static final KeyCombo NODES_UNTIE = new KeyCombo(UIKeys.NODES_CONTEXT_UNTIE, GLFW.GLFW_KEY_U).categoryKey("nodes");
    public static final KeyCombo NODES_MAIN = new KeyCombo(UIKeys.NODES_CONTEXT_MAIN, GLFW.GLFW_KEY_M).categoryKey("nodes");
    public static final KeyCombo NODES_SORT = new KeyCombo(UIKeys.NODES_CONTEXT_SORT, GLFW.GLFW_KEY_C).categoryKey("nodes");

    /* Pixel editor */
    public static final KeyCombo PIXEL_COPY = new KeyCombo(UIKeys.TEXTURES_VIEWER_CONTEXT_COPY_HEX, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("pixels");
    public static final KeyCombo PIXEL_SWAP = new KeyCombo(UIKeys.TEXTURES_KEYS_SWAP, GLFW.GLFW_KEY_X).categoryKey("pixels");
    public static final KeyCombo PIXEL_PICK = new KeyCombo(UIKeys.TEXTURES_KEYS_PICK, GLFW.GLFW_KEY_R).categoryKey("pixels");

    /* Keyframes */
    public static final KeyCombo KEYFRAMES_MAXIMIZE = new KeyCombo(UIKeys.KEYFRAMES_CONTEXT_MAXIMIZE, GLFW.GLFW_KEY_HOME).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_SELECT_ALL = new KeyCombo(UIKeys.KEYFRAMES_CONTEXT_SELECT_ALL, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_INTERP = new KeyCombo(UIKeys.KEYFRAMES_KEYS_TOGGLE_INTERP, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("keyframes");
    public static final KeyCombo KEYFRAMES_EASING = new KeyCombo(UIKeys.KEYFRAMES_KEYS_TOGGLE_EASING, GLFW.GLFW_KEY_E, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("keyframes");

    /* Record editor */
    public static final KeyCombo RECORD_SELECT_ALL = new KeyCombo(UIKeys.RECORD_EDITOR_CONTEXT_SELECT_ALL, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_JUMP_NEXT_ACTION = new KeyCombo(UIKeys.RECORD_EDITOR_JUMP_NEXT_ACTION, GLFW.GLFW_KEY_RIGHT_BRACKET).categoryKey("record");
    public static final KeyCombo RECORD_JUMP_PREV_ACTION = new KeyCombo(UIKeys.RECORD_EDITOR_JUMP_PREV_ACTION, GLFW.GLFW_KEY_LEFT_BRACKET).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_ADD = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_ADD_TITLE, GLFW.GLFW_KEY_N, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_APPLY = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_APPLY_TITLE, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_CAMERA = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_CAMERA_TITLE, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_CAPTURE = new KeyCombo(UIKeys.RECORD_EDITOR_CAPTURE, GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_COPY = new KeyCombo(UIKeys.RECORD_EDITOR_COPY, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_CUT = new KeyCombo(UIKeys.RECORD_EDITOR_CUT, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_INSERT = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_INSERT_TITLE, GLFW.GLFW_KEY_I).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_LERP = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_LERP_TITLE, GLFW.GLFW_KEY_L).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_PASTE = new KeyCombo(UIKeys.RECORD_EDITOR_PASTE, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_PROCESS = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_PROCESS_TITLE, GLFW.GLFW_KEY_M).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_REMOVE = new KeyCombo(UIKeys.REMOVE, GLFW.GLFW_KEY_DELETE).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_REVERSE = new KeyCombo(UIKeys.RECORD_EDITOR_TOOLS_PROCESS_REVERSE_TITLE, GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");
    public static final KeyCombo RECORD_TOOL_TELEPORT = new KeyCombo(UIKeys.RECORD_EDITOR_TELEPORT, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("record");

    /* Scripts */
    public static final KeyCombo SCRIPT_WORD_WRAP = new KeyCombo(UIKeys.SCRIPTS_KEYS_WORD_WRAP, GLFW.GLFW_KEY_P, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("scripts");

    /* Chalkboard */
    public static final KeyCombo CHALKBOARD_CLEAR = new KeyCombo(UIKeys.CHALKBOARD_KEYS_CLEAR, GLFW.GLFW_KEY_DELETE).categoryKey("chalkboard");

    /* World menu */
    public static final KeyCombo WORLD_MOVE_CENTER = new KeyCombo(UIKeys.WORLD_CONTEXT_MOVE_CENTER, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world");
    public static final KeyCombo WORLD_MOVE_TO_CURSOR = new KeyCombo(UIKeys.WORLD_CONTEXT_MOVE_TO_CURSOR, GLFW.GLFW_KEY_G, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");
    public static final KeyCombo WORLD_RECORD_REPLAY = new KeyCombo(UIKeys.WORLD_KEYS_RECORD_REPLAY, GLFW.GLFW_KEY_RIGHT_ALT).categoryKey("world");
    public static final KeyCombo WORLD_PLAYBACK_SCENE = new KeyCombo(UIKeys.WORLD_KEYS_PLAYBACK_SCENE, GLFW.GLFW_KEY_RIGHT_CONTROL).categoryKey("world");
    public static final KeyCombo WORLD_TOGGLE_WALK = new KeyCombo(UIKeys.WORLD_KEYS_TOGGLE_WALK, GLFW.GLFW_KEY_B, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");
    public static final KeyCombo WORLD_CYCLE_PANELS = new KeyCombo(UIKeys.WORLD_KEYS_CYCLE_PANELS, GLFW.GLFW_KEY_TAB).categoryKey("world");
    public static final KeyCombo WORLD_TOGGLE_PLAYER = new KeyCombo(UIKeys.WORLD_KEYS_TOGGLE_PLAYER, GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world");
    public static final KeyCombo WORLD_SAVE = new KeyCombo(UIKeys.WORLD_KEYS_SAVE, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world");
    public static final KeyCombo WORLD_TOGGLE_AXES = new KeyCombo(UIKeys.WORLD_KEYS_TOGGLE_AXES, GLFW.GLFW_KEY_T, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world");

    /* World editor */
    public static final KeyCombo WE_DESELECT = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_DESELECT, GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_MASK = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_MASK, GLFW.GLFW_KEY_M).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_CUBE = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_FILL_CUBE, GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_SPHERE = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_FILL_SPHERE, GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_FILL_CYLINDER = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_FILL_CYLINDER, GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_CLEAR = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_CLEAR, GLFW.GLFW_KEY_DELETE).categoryKey("world_editor");
    public static final KeyCombo WE_COPY = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_COPY, GLFW.GLFW_KEY_C, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_CUT = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_CUT, GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_PASTE = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_PASTE, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_ROTATE = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_ROTATE, GLFW.GLFW_KEY_PERIOD).categoryKey("world_editor");
    public static final KeyCombo WE_ROTATE_CC = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_ROTATE_CC, GLFW.GLFW_KEY_COMMA).categoryKey("world_editor");
    public static final KeyCombo WE_FLIP_H = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_FLIP_H, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world_editor");
    public static final KeyCombo WE_FLIP_V = new KeyCombo(UIKeys.WORLD_EDITOR_ACTIONS_FLIP_V, GLFW.GLFW_KEY_V, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("world_editor");
    public static final KeyCombo WE_MOVE_TO_SELECTION = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_MOVE_TO_SELECTION, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT).categoryKey("world_editor");
    public static final KeyCombo WE_MOVE_TO_CENTER = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_MOVE_TO_CENTER, GLFW.GLFW_KEY_F, GLFW.GLFW_KEY_LEFT_CONTROL).categoryKey("world_editor");
    public static final KeyCombo WE_PICK = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_PICK_BLOCK, GLFW.GLFW_KEY_R).categoryKey("world_editor");
    public static final KeyCombo WE_BLOCKS = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_OPEN_BLOCKS, GLFW.GLFW_KEY_O).categoryKey("world_editor");
    public static final KeyCombo WE_SELECT_FLOATING = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_SELECTION_SELECT_FLOATING, GLFW.GLFW_KEY_U).categoryKey("world_editor");
    public static final KeyCombo WE_RELOAD_CHUNKS = new KeyCombo(UIKeys.WORLD_EDITOR_CONTEXT_UTILITY_RELOAD_CHUNKS, GLFW.GLFW_KEY_R, GLFW.GLFW_KEY_LEFT_ALT).categoryKey("world_editor");
}