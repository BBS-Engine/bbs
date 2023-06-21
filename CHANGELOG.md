## BBS 0.7.2

This big update that features lots of small tweaks, but nothing groundbreaking. There was also some code base clean up was done, and the [source code was released](https://github.com/BBS-Engine/bbs/). 

* Added player recording timeline keybinds to jump to next (`]`) and previous (`[`) actions
* Added camera editor jump forward and backward keybinds to move cursor more than one tick (jump is configurable in settings)
* Added support for Blockbench's UV rotations (Aloan)
* Added a feature to click on actors (when a scene clip is selected) to open player recording editor for that actor
* Added body parts merging when tweening forms
* Added context menu to world setting's shading direction to set it up from camera
* Added ID to world objects and `IScriptWorld.getObject(String)`, `IScriptWorld.getObjects(String)`, `IScriptWorld.getAllObject()` to query world objects from ID
* Added a feature to offset block placement in world editor by scrolling mouse wheel while holding `Ctrl`
* Added `overlap` property to model's `config.json` to allow rendering models on top of other models without Z-fighting
* Added entity speed property to entity's basic component
* Added adding a keyframe clip generated from player recording in camera editor panel (`Add clip... > From player recording...`)
* Added preview of tween duration to player recording editor timeline
* Added custom user form categories
* Added deferred rendering
* Added camera clip panels and icons in add context menu
* Added saving of overlay panel positions during session
* Changed F3 keybind to be global (previously it worked only in the world)
* Changed default form action values: duration `10` (was `0`) and tween enabled (was `disabled`)
* Changed not selected tiles much brighter (Aloan)
* Changed font renderer to use color multiplication only for shadows
* Changed fade in and fade out fields to be in a single row in camera clip's envelopes
* Changed some keybinds (next and previous frame, next and previous action, next and previous clip) to trigger more than once when the key is held
* Changed trackpad fields to always evaluate the content of the textbox on unfocus
* Changed world object clicking in REPL panel to generate new ID based code
* Changed shift duration keybind/context menu action to shift offset to the cursor while keeping duration in the same place
* Changed overlay panels design
* Changed video recording to use direct recording with JavaCV
* Improved form action editing by reflecting values in the world and making form palette open only within timeline's frame
* Improved shift to cursor keybind/context menu action to relatively shift all selected clips
* Improved UI rendering by introducing rendering batching
* Fixed UV inconsistencies with Blockbench models
* Fixed camera walk mode messes up camera editing by disabling walk mode when entering camera editor
* Fixed body parts not syncing correctly with form action
* Fixed resize bar in camera editor appearing in wrong order
* Fixed a bug with binary data not being correctly read due to overflow of keys
* Fixed first-person form crash (Toray)
* Fixed some grammar issues in English translation by Aloan
* Fixed idle clip to dolly clip converter not taking in account yaw and pitch
* Fixed player recording editor's teleport tool
* Fixed multiple line form category
* Fixed body parts canceling animations
* Fixed extruded items' texture mapping (gaps)
* Fixed crash with particle effect form editor
* Fixed X alignment to round when adding or moving keyframes
* Removed Manual (`bbs:manual`) clip
* Removed speed options from Path (`bbs:path`) clip

## BBS 0.7.1

This little update features a couple of fixes, Portuguese 🇧🇷 translation, and better machinima recording tools.

Special thanks to Draacoun and Aloan for Portuguese translation, and Kirkus for Ukrainian translation!

* Added Portuguese 🇧🇷 translation by Draacoun and Aloan
* Added a feature in world editor whenever a block can't be ray traced, it will pick a block in the air in the direction of mouse (Ctrl + Mouse wheel to control distance)
* Added removing blocks with paste brush in the world editor
* Added video width and video height options to BBS' video recording settings
* Added preview to camera editor
* Added idle to dolly, path and keyframe converters (Right click > Convert clip... on camera timeline)
* Added body part rendering within UI for **Model** (`bbs:model`) form
* Added `step` interpolation support for Blockbench models (Aloan)
* Added form updating in the form editor
* Added **Form** (`bbs:form`) action to player recordings
* Added player recording editing within camera editor when scene clip is selected
* Added offset (allows to shift the model) and rotations (generates 3 extra block models rotated  90, 180 and 270 degrees horizontally) options to combined block model (Aloan)
* Changed partial player recording to only overwrite the data that was recorded and not frames entirely
* Fixed startup crash due to some graphics drivers being stricter (reserved keywords were used) (Aloan)
* Fixed `query.yaw_speed` model variable was fixed
* Fixed model meshes not respecting pivot point for translation
* Fixed pressing Enter REPL breaks down the line
* Fixed particle effect identifier tooltip (Draacoun)
* Fixed replay not being editable when closing and opening scene menu
* Fixed scene actors remaining in the world if BBS was closed during the scene
* Fixed scene actors remaining in the world if camera editor started editing another camera work
* Fixed crash when loading BBS with no sound devices enabled (Draacoun)
* Removed letterbox and overlay from BBS' camera editor settings

## BBS 0.7

This update is all over the place. The biggest feature in this update, in my opinion is basic voxel lighting. It's still buggy and unfinished (because there is no sky light, only torch light). Other cool features are joystick model control, world settings presets, and world editor's masks and improvements to spray and smooth brushes.

Special thanks to Kirkus for updating language strings!

* Added `IScriptPlayer.canControl()` and `IScriptPlayer.setControl(boolean)`, which allows you to lock and unlock player controls via scripts
* Added color filter option to **Model** (`bbs:model`) form
* Added voxel block light propagation (aka torch light)
* Added joystick support:
    * Added variables `joystick.l_x`, `joystick.l_y`, `joystick.r_x`, `joystick.r_y`, `joystick.l_trigger`, `joystick.r_trigger` to Blockbench models
    * Added actions `joystick_up`, `joystick_left`, `joystick_down` and `joystick_right` to Blockbench models, which get triggered when joystick's Dpad buttons are pressed
    * Added character control to third person game controller (left bumper toggles control, right bumper changes between front and back views, A is for jumping)
* Added world settings presets
* Added lightmap option to world settings
* Added support to load multiple `.bbs.json` files in the folder (it doesn't merge models, but if there is one model, and multiple files with just animations, it would compile those animations to the model)
* Added `config.json` file to Blockbench models folder that supports following properties:
    * `animations` (list of strings) allows you to specify path to other `.bbs.json` that contain animations to merge with this model's animations
    * `normals` (boolean) allows you to toggle normals (`false` (disabled), under the hood makes all of model's normals `(0, 1, 0)`)
    * `culling` (boolean) allows you to disable face culling, so both sides of the model are visible (Aloan)
* Added mask to world editor (which allows you to pick which blocks are affected by tool and brushes during editing)
* Added snap sound when taking screenshot (TheGreatFelix)
* Added beep sound when starting/stopping video recording
* Added scene tick offset option to **Scene** (`bbs:scene`) clip
* Added back audio preview
* Added play modes to **Sound** (`bbs:sound`) trigger block
* Added chunks to world settings (which allows to change "render distance" in the world)
* Added force all chunk loading during video recording
* Added vertical and horizontal flip of currently copied structure to world editor
* Added a mechanism to load custom mipmaps for blocks atlas (`%NAME_OF_ATLAS%_mipmap_1.png` and `%NAME_OF_ATLAS%_mipmap_2.png`)
* Added reload terrain button to utility panel
* Added ability to record separate groups (for example, just the position, or just the rotation) in scene panel
* Added keybind to open World editor in the dashboard (Ctrl + Shift + B)
* Added saving camera's position and rotation upon exiting the world (Aloan)
* Added skybox form option to world settings
* Added input delay to textbox fields in texture picker and camera world object so that it wouldn't spam errors in the log
* Changed spray brush's chance range from `0..1` (normalized percentage) to `0..100` (percentage)
* Changed maximum memory limit for uploading models to GPU from `4mb` to `8mb`
* Improved smooth brush by changing the algorithm to height map blurring
* Fixed flood fill tool's flood fill algorithm
* Fixed crash when removing last particle effect (Centryfuga)
* Fixed crash with RGB textures (xxxSgyshenka)
* Fixed rotation and flipping of non opaque blocks (Centryfuga)
* Fixed `jump` animation not working during scene playback
* Fixed camera panel keybinds getting triggered during flight mode
* Fixed sound list overlay not getting updated
* Fixed scene is being stopped before finishing recording
* Fixed path clip's interpolation can't be changed
* Fixed pasting body parts was not possible on the root form
* Fixed a couple of annoying UX problems in animation editor: mouse scrolling in quick are results in camera zooming in/out, previous tick keybind not working, and when navigating in keyframe editor camera would move as well
* Fixed save button not changing when toggling collision and collision box options (Aloan)
* Fixed pasting entities didn't work (Aloan)
* Improved player recording code and removed unnecessary feature that were present in Blockbuster (teleport back)
* Moved sky and fog options from Settings > App to World settings
* Removed world time trigger block
* Removed replace mode in world editor (because it's basically replaced by masks)
* Removed teleport back option in scene replays
* Removed skybox image world settings option

## BBS 0.6.1

This patch update provides only minor fixes. It also features new section in welcome menu with list of Patreon subscribers. If you'd like to see your name there too, feel free to pledge on [Patreon](https://www.patreon.com/McHorse) ($10 tier).

* Added scripting features:
    * Added `bbs.isDevelopment()` checks whether BBS was launched in development or play mode
    * Added `ModelForm.playAnimation(String)` for `bbs:model` forms allows to play a Blockbench animation once
    * Added `IScriptEntity.getFirstPersonForm()` and `IScript.setFirstPersonForm(Form)` to manipulate entity's (player's) first-person form
    * Added `Form.tween(Form, int, String)`, where `int` is duration (in ticks), and `String` is interpolation's key (which can be looked up [here](https://github.com/mchorse/mclib/blob/1.12/src/main/java/mchorse/mclib/utils/Interpolation.java#L8))
* Added Patreon section to Welcome menu
* Changed lower limit for chunk size from `16` to `2`
* Changed icon graphic to always render in the middle of the graphic's area
* Changed trigger object location in side scroller game controller (by proximity, 2 blocks radius) instead of directly looking at it
* Fixed copy language strings JSON crashing (Draacoun)
* Fixed textbox's selection method with Shift + click
* Fixed context menu wouldn't appear in script editor
* Fixed lists element could be dragged with right or middle mouse buttons
* Fixed URL opening not working on Windows
* Improved key cap sprite for key strokes
* Improved utility panel's window resizing feature

## BBS 0.6

This update brings new UI editor, which allows creating custom UI menus, which was only possible through scripts before, with a GUI editor. Beside UI editor, `0.6` allows to customize pause, inventory, and dialogue menus with the new UI editor, world and dashboard menus were merged, and game controllers were introduced.

* Added shading direction option to world settings
* Added holding `Space` to vector fields (3 trackpad/number fields marked red, green and blue) to change all fields at once
* Added a feature when pressing `Escape` when dashboard is hidden, it will show the dashboard first before exiting the dashboard
* Added UI editor, which allows creating custom menus using simple UI components (buttons, icons, graphics, textbox, text, labels, etc.), which are also scriptable
* Added User interface (`bbs:ui`) trigger block which allows opening custom UI menus and also close any UI menu
* Added `IScriptUI.createFromData(String)` (and its overrides) and `IScriptUIBuilder.get(String)`
* Added game controllers feature, which allow you to change the gameplay in the play mode:
    * First-/third-person controller, allows the player to go around from 1st-person or 3rd-person POVs
    * Top down controller, allows the player to control WASD cardinally without mouse with a camera looking down
    * Side scroller controller, allows the player to control horizontally (on X axis)
* Added game settings options:
    * Pause UI, it allows you to pick a custom UI that will replace pause menu
    * Inventory UI, it allows you to pick a UI that will serve as a template for inventory menu (see **Inventory UI** requirements below) (Kirkus)
    * Dialogue UI, it allows you to pick a UI that will serve as a template for dialogue menu (see **Dialogue UI** requirements below) (Centryfuga)
* Changed mouse and keystrokes rendering
* Changed world manager to be an overlay panel
* Fixed `,` is used in certain locales instead of `.` for trackpad (Centryfuga)
* Merged world menu together with dashboard, so now there is only one creative panel

### Inventory UI requirements

Following UI components (with corresponding IDs) must be present for the custom inventory to work:

* ID `form`, type Form (`bbs:form`), it will display player's form.
* ID `quest`, type Layout (`bbs:layout`) with Layout type `None`, it will display quests, if the player has any.
* ID `slots`, type Layout (`bbs:layout`) with Layout type any but not `None`, it will display player's equipment slots (of which there are currently 2).
* ID `inventory`, type Layout (`bbs:layout`) with Layout type `None` and preferably `100` width and `160` height, it will display player's inventory, which is hardcoded to have 5 items per row and in total 40 slots.

All of these elements can be attached to root, or nested into other UI components. You can download [this example](https://github.com/mchorse/bbs/blob/master/uis/inventory.json) and move it to `game/data/uis/`.

### Dialogue UI requirements

Following UI components (with corresponding IDs) must be present for the custom inventory to work:

* ID `form`, type Form (`bbs:form`), it will display current dialogue reaction's form.
* ID `quest`, type Layout (`bbs:layout`) with Layout type `None`, it will display current dialogue's quests, if any are present.
* ID `crafting`, type Layout (`bbs:layout`) with Layout type `None`, it will display current dialogue's crafting table, if one is present.
* ID `replies`, type Layout (`bbs:layout`) with Layout type `Column`, it will display current dialogue's replies (reply nodes connected to current reaction node).
* ID `reaction`, type Layout (`bbs:layout`) with Layout type `None`, it will display current dialogue reaction node's text.
* ID `back`, type Button (`bbs:button`), it will be displayed when quests or crafting table is displayed.
* ID `accept`, type Button (`bbs:button`), it will be displayed when quests are present, which allows accepting/completing a selected quest.

All of these elements can be attached to root, or nested into other UI components. You can download [this example](https://github.com/mchorse/bbs/blob/master/uis/dialogue.json) and move it to `game/data/uis/`.

## BBS 0.5.1

This patch update adds UI keybinds configuration section, which allows to change keybinds within BBS' UIs, and improves some UI features. I also accidentally overwrote Ukrainian language strings with Russian strings, so I had to release this update prematurely. I appologize for any inconvenience! 🤦‍♂️

Special thanks to Kirkus for updating language strings!

* Added HSV picker option (which is enabled by default) which changes color picker to use HSV (Hue, Saturation, Value) instead of RGB (Red, Green, Blue)
* Added configuration category to remap UI keybinds within BBS' menus
* Added ⇧, ⊞, ⌘ and ⌥ symbols to BBS (Round) and BBS (Square) fonts
* Changed `Tab` key for textarea (bigger text field that supports wordwrapping and multiple lines) to focus next/previous element instead of inserting 4 spaces
* Changed display text of modifier keys from Ctrl, Shift, Alt, Cmd/Win, to ^, ⇧, ⌥, ⌘/⊞ respectively
* Changed Z axis's color in transformation and vector elements toward light-blue (Chryfi)
* Fixed user added languages are getting reset before launching (Draacoun)
* Fixed items (within item forms) not appearing in player menu
* Improved jump to next/previous focusable element with `Tab` key by:
    * Limiting scope of focusable elements to first parent container
    * Scrolling to focused element if next/previous focusable element is in a scrolling view
* Improved a lot of GUI code (internal changes)
* Moved `camera.gui.` language keys to `bbs.ui.camera.`
* Moved flight keybinds to UI keybinds
* Removed F1 key in play mode

## BBS 0.5

This update features improvements to items, adds a couple of skybox options, camera world object and macOS fixes.

Special thanks to Kirkus for updating language strings!

* Added camera world object, which allows rendering scene to a texture (which can be displayed with billboard form)
* Added skybox options to world menu's world settings panel
* Added `bbs:item` form, which allows displaying items (and entity's items from an equipment slot)
* Added Use target option to body parts which passes the original entity from parent form
* Added item options to the items editor:
    * Form, allows you to substitute an item rendering from items atlas with a custom form
    * Extruded, allows you to change item rendering as a voxelized (extruded) model instead of as a plain billboard (quad that always faces the camera)
    * Frame color, allows you to specify color for new item tooltips (TorayLife)
    * Description, allows you to add description/lore of the item (TorayLife)
* Added Move to cursor (Ctrl + G) keybind in entity and world object editors panels to move entity/world object to place where cursor hovers
* Added Toggle axes (Ctrl + T) keybind to world menu that allows to toggle visibility of +X+Y+Z-X-Y-Z orbit pivot/axes preview
* Fixed crash with in-game scripting documentation (TheBendy)
* Fixed camera keeps going when switching to another menu (TheBendy)
* Fixed multiple macOS related issues:
    * Fixed mouse pointer incorrectly calculate mouse coordinates
    * Fixed screenshot and texture saving hanging
    * Fixed open URL buttons
* Improved item tooltips (TorayLife)

## BBS 0.4

This update is mostly focused around removing asset packs from BBS. The reason they were removed is due to their limitations of distributing BBS. If you were to make a game with BBS, with those asset packs present, you would have to purchase licenses to these packs, or otherwise you wouldn't be able to legally distribute your game made with BBS.

This update features new font editor, MagicaVoxel (`.vox`) model loading, Blockbench mesh (update BBS export plugin) model loading, first-person options and other nice tweaks.

Special thanks to Kirkus for updating language strings!

### List of BREAKING changes

* Items made with default iconset `assets:textures/iconset.png` would have no texture due to removal of the iconset asset pack

### List of changes

* Added font editor panel to dashboard, which allows you to edit existing bitmap fonts, or create new ones. Font editor offers following features:
    * Manage glyphs (characters) and edit their options (size, offset, horizontal width, kerning, whether it's an emoji) and appearance (by drawing on a canvas)
    * Manage font's formatting codes, which allows to add new colors if needed
    * Change name and font base height
* Added label form option to change the BBS font it uses to render the string with
* Added line drawing from previous point by holding Shift in pixel editor
* Added new fonts: `BBS (round)`, `BBS (round, mono)`, and `BBS (square)`
* Added icon button to change items atlas (iconset)
* Added context menu option to place the UV to the place where cursor is (Centryfuga, Joziah3)
* Added collisions and eye position preview for morphs (TheBendy)
* Added vertical wrapping of block models to tile set editor (Centryfuga)
* Added a keybind to save world in world menu Ctrl + Shift + S (Joziah3)
* Added first-person form options (form and offset) (TheBendy)
* Added Blockbench meshes support to BBS model loading
* Added MagicaVoxel (.vox) model support to BBS model loading
* Changed orbit camera flight to be handled by delegated key events instead of during render
* Fixed custom hitbox raytracing messes with the next ray traced position (creating three blocks in a row)
* Fixed formatting doesn't get correctly applied when splitting into multiple lines (TheBendy)
* Removed remaining asset packs from BBS: [PixelFonts](https://chevyray.itch.io/pixel-fonts), [zpix](https://github.com/SolidZORO/zpix-pixel-font) and [RPG iconset](https://itch.io/c/1059809/16x16px-iconsets-by-cyangmou)

## BBS 0.3.1

This quick patch update just fixes some bugs.

* Added remove selection key (Ctrl + D) in world menu
* Changed feature panel to be taller in the welcome menu
* Fixed extra languages not loading at launch (Draacoun)
* Fixed keys marked as completed in language editor get exported as well
* Fixed completion label in langage editor don't account completed keys
* Fixed texture picker in tile set editor and form picker in HUD scenes appear in wrong order (TheBendy)
* Fixed HUD form rotation is being converted to rads twice (Joziah3)
* Fixed crash with insert frames... and paste tools in player recording editor (TheBendy)
* Improved arc tool which now produces a cleaner block path

## BBS 0.3

This update features refactored quest chains, schematic importer (with an editor), new forms, multiple UX features and tweaks. **This update contains breaking changes!**

### List of BREAKING changes

* Keyframe paths in animation panel were changed, so some (if not all) keyframes that worked in 0.2 would stop working!
* Trigger world object's hitbox anchor was changed to middle XZ and bottom Y, so any trigger objects would need to be shifted by (0.5, 0, 0.5)!

### List of changes

* Added schematic importer (Centryfuga): here is how it works, if you would place a .schematic file into `game/config/structures/`, it would appear in the load structures list with `.schematic` suffix, load it, and it will open an overlay where you can adjust the blocks. Once you're done configuring, close the overlay panel, and the structure would appear. Make sure to save it!
* Added new forms:
    * `bbs:block` a form that renders a single block variant
    * `bbs:structure` a form that renders saved structure (schematic structures have to be saved first)
* Added basic crash report saving mechanism: if the engine crashes, a window should popup that would offering opening crash logs folder, copy crash log to the copy-paste buffer, and just close
* Added new global triggers in Game Settings:
    * `Player: clicked mouse` gets triggered when player presses or releases a mouse button
    * `UI: open menu` gets triggered when app opens a UI menu (TheBendy)
    * `UI: open menu` gets triggered when a UI menu gets closed (TheBendy)
* Added scripting methods:
    * `IScriptEntity.getForm()` returns a direct reference to entity's form (or `null` if it doesn't have one)
    * `IScriptWorld.getEntityByUUID(String)` returns an entity found in the world by given UUID
    * `IScriptWorlds.loadAt(String, double, double, double, float, float)` to spawn player at desired coordinates
* Added arc tool to the world editor (Centryfuga)
* Added open blocks panel keybind to world editor (`O` is default key) (Centryfuga)
* Added basic quest HUD renderer
* Added basic quest tracker in player's inventory menu
* Added `launch.bat`/`launch.sh` (depending on the OS) scripts to distribution
* Added space + left click to move camera as an alternative to middle mouse button (FreakZillA8)
* Added hitbox size option to trigger world object
* Added change tile sets button to world add and edit overlays
* Added support for multiple quest chains and quest nodes to be attached to the same reaction node
* Changed logging system: logs will no longer be save near `launcher.jar` but rather in `game/logs/`. `launcher.log` is the latest log, while the logs with date filenames are previous logs.
* Changed font line word wrapping threshold to 12 symbols
* Changed quest chains: a separate chain is a list of quests that needed to be completed in order, unnecessary options like allow retake and auto accept were removed
* Changed right clicking an entity in REPL world panel pasting `.getEntityByUUID()` snippet instead of direct reference
* Fixed animated models rendered rotated groups incorrectly (Hrymka)
* Fixed form body parts are not considered for equality resulting in selecting incorrect form in the form list menu
* Fixed crash when clicking on non model morphs in animation panel (Kirkus)
* Fixed exponential interpolations having a rough end (because the range of original formulas were 0.001..1 instead of 0..1)
* Fixed mouse clicking in REPL panel will insert code bits even when it shouldn't (Kirkus)
* Fixed a couple of scripting docs documentation issues
* Fixed a crash when there are extra non .jar files in dependencies (Joziah3)
* Fixed memory leak with chalkboard upon window resize (Just Jory)
* Fixed GUI flickering when changing GUI scale by dragging (Just Jory)
* Fixed picking a form with body parts won't add its body parts to the tree list
* Fixed edit metadata and convert world overlays wrongly display warnings about empty world ID
* Fixed non-existent entity/object still being present in the list after it was removed
* Fixed trigger objects hotkey HUD tooltips are visible during camera playback (Kirkus)
* Improved animation panel:
    * Added ability to keyframe color and transform properties
    * Changed the paths for body parts
    * Moved keyframe list was moved to an overlay panel
    * Removed pick/edit keybinds for form picking/editing (to avoid triggering them when vertically navigating by `Q` and `E`)
* Improved langauge string editor:
    * Added context menu option to keys to allow mark keys as completed (in case translation isn't needed)
    * Added feature to add supported langauges without hardcode: create a file `game/assets/lang_editor/languages.json`, and the object's key is the language ID and the value is display label, so adding `{"es_ES": "Español (es_ES)"}` to the file will add Spanish to the list of languages
* Improved crafting table panel:
    * Added solid black background and right click animation to the recipe list
    * Moved title and craft button labels under settings (gear icon)
* Improved tile set editor:
    * Added context menu to UV editor to jumpt to currently selected UV region (Joziah3)
    * Added block tint option (Joziah3)
    * Added replace block model button to the list overlay (Joziah3)
    * Added copy/paste block model context menu items to the list overlay
* Removed camera sync mode, now camera clips are edited by just enabling flight mode

## BBS 0.2

This update features support for localization of some European languages, bug fixes, and some nice tweaks. Special thanks goes to Draacoun, Kirkus, Noozy and Rebane!

**IMPORTANT**: new default tile set atlas (`assets:textures/default_atlas.png`) was added, so in the future, the old default atlas texture (`assets:textures/atlas.png`) will be removed!

* Added font rendering support of letters from: Portuguese, Russian, Ukrainian, French, German, Spanish, Italian, Swedish, Norwegian, Dutch, Danish and Chinese (thanks to [zpix-pixel-font](https://github.com/SolidZORO/zpix-pixel-font) and Chunk7 for introducing it to me)
* Added Russian (by Kirkus) and Ukrainian (by Kirkus) languages
* Added font rendering formatting codes wave (§w, stacking increases vertical amplitude), shake (§s, stacking increases distance of shake) and rainbow (§n, stacking changes per letter rainbow colors or per entire text fragment) (Centryfuga)
* Added language editor in utility panel (F6) that allows to edit current language's strings (it's userful mostly for translators)
* Added walking camera mode in world menu (Ctrl + B) (Centryfuga)
* Added application icons (Windows and Linux) (Kirkus)
* Added v-sync and frame rate options to App configurations (Protoxy)
* Added feature to connect two nodes by dragging on connection line (Пивовар)
* Added atlas texture picker to tile set editor
* Added tab keybind to toggle between panels in world menu (Kirkus)
* Added `bbs.camera.lock()`, `bbs.camera.isLocked()`, `bbs.camera.set(x, y, z, yaw, pitch, roll, fov)` and `bbs.camera.unlock()` scripting methods to lock and unlock camera
* Added `bbs.animations.play(id)`, `bbs.animations.isPlaying(id)` and `bbs.animations.stop(id)` scripting methods to play and stop animations
* Added FOV options to player data and BBS' engine options
* Added **Animation** and **HUD scene** trigger blocks
* Added form hitbox options
* Added new texture atlas and default tile set (upon first world creation)
* Changed list icon in HUD scenes panel to an appropriate icon (TheBendy)
* Changed buttons, toggles and labels text get shortened with ... if it's too long
* Fixed title in block model factory panel
* Fixed crash when using inventory during scene recording (Kirkus)
* Fixed morph menu in the scenes panel is covered by icon bar (TheBendy and Maysvoch)
* Fixed JSON parser not treating correctly backslashes at the end of the string
* Fixed crashing when a model is empty or has incorrect data format
* Fixed trigger objects could be accessed from anywhere
* Fixed default model texture isn't being picked when picking a texture
* Fixed multi-link based textures appear black with block atlas (due to mipmapping)
* Fixed dialogue labels don't get correctly when they have new line(s) (Пивовар)
* Fixed objectives not being created (Kirkus)
* Fixed actors falling when switching to flight mode
* Fixed blocks placement isn't prioritized (when other chunks are loaded, they are not being updated first)
* Fixed plant block model's texture being stretched
* Fixed form and texture picker panels could've been opened multiple times atop each other (Kirkus)
* Improved forms by rewriting their properties to be separate objects (which also support tweening/transitioning)
* Improved baked AO which elimited dark shading from the sides of blocks (thanks to [0fps article](https://0fps.net/2013/07/03/ambient-occlusion-for-minecraft-like-worlds/))
* Moved REPL panel to world menu

## BBS 0.1.1

Quick patch fix to make it a little bit more stable.

* Added JSON options to specify texture atlas for tile sets
* Added warning when World ID is empty in create world overlay panel (Janetyqua)
* Added warning when generator options are not filled in (meepstertron)
* Added warning when removing a block model in the tile set editor
* Added `RegisterTriggersEvent` for modders to register custom global triggers (TorayLife)
* Added framebuffer toggle in App's settings to disable rendering with framebuffer (it may help with Intel graphics cards 🤞)
* Added first person player data option
* Added jump player data options (toggle jump altogether or limit to jumping only when being on the ground)
* Changed region shapes' volumes rendered as thick lines
* Fixed `min-games` to `mini-games` in the welcome menu (Kyttu)
* Fixed video recording not working when `ffmpeg` is setup (meepstertron)
* Fixed extra blank context menu items in trigger and condition menus
* Fixed a pixel gets drawn in top left corner in the texture editor due to division by `0`
* Fixed missing UI strings (resume button)
* Fixed Max stacks field had wrong label (item's display name) (TheBendy)
* Fixed player data not updating
* Fixed chalkboard passing mouse wheel scroll events
* Fixed letter box and center lines not correctly aligned when letter box is enabled
* Fixed empty UI keys (now all missing UI keys will display as the corresponding UI key)
* Fixed crashing when removing a body part from form editor
* Fixed items not being updated after editing
* Fixed extrude tool not having a keybind (its keybind now is `Shift + 0`)
* Fixed backspace, delete and enter/return are not repeating in the textarea/script editor
* Fixed world objects' debug not rendering (i.e. the proper size of hitboxes and region's shape areas)
* Fixed actors not disappearing after camera was played
* Fixed buttons in data panels crashing BBS due to not selected data entry (TheBendy)
* Fixed chunks not being updated when moving the camera (Janetyqua)
* Fixed folders appearing in the pick data lists (Centryfuga)
* Moved HUD scene's input fields into options :gear: (TheBendy)
* Removed buttons under `Configuration > BBS > Data` (TheBendy)
* Removed flat scrollbar option (TheBendy)
