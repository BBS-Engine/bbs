## BBS 0.1.4

This update features bug fixes, QoL tweaks and two awesome features: anchor form property and second set of rotation fields to transforms UI.

* Added opaque option to subtitle clip.
* Added feature to teleport cursor to opposite edge of the screen when editing transformation with `G`, `R`, `S` keys.
* Added different orbit modes to orbit camera in film editor (`P` key to change between orbit, first person, third person and third person frontal).
* Added pre and post interpolation to camera clips.
* Added screenshot button to film editor.
* Added options to edit skybox' gradient in world settings.
* Added anchor form property to attach actor to another actor's form.
* Added `Ctrl + G` in film editor that allows to move current replay's position to cursor.
* Added `Escape` key to stop video recording during video recording in film editor.
* Added anchor preview (axes renderer) to form editor.
* Added a second set of rotation fields to transforms UI (FunkyFight).
* Added particle form option to change the texture.
* Changed `X`, `Y` and `Z` keys to not reset the angle when switching between axes during `G`, `R`, `S` transform editing (FunkyFight).
* Changed default key for toggling camera clips from `H` to `J`.
* Changed right click to left click to pick form/body part in form editor.
* Changed `.schematic` loading from the load structure overlay panel to drag and dropping a `.schematic` file when world editor panel is opened.
* Fixed audio clip starts playback after finishing playing (FunkyFight).
* Fixed a couple of NPE crashes (FunkyFight).
* Fixed undo/redo not reselecting keyframes properly.
* Fixed texture issues related to pixel alignment (FunkyFight).
* Fixed crash with extruded form for really big images (FunkyFight).

## BBS 0.1.3

This update features bug fixes and QoL features to film editor.

* Added context menu to film editor viewport which allows to: add a new replay at the position of the cursor, and move (insert keyframes at current tick) the replay to the cursor.
* Added editing some properties of a clip, the same properties of currently selected clips will get edited too.
* Added instant option (toggle) to generic keyframes (FunkyFight).
* Added display time label to audio player in screenplay editor.
* Added two extra keyframes groups (4 channels) to animate extra variables within the model: `extra1.x`, `extra1.y`, `extra2.x` and `extra2.y` (KIKOMNEW).
* Added `G`, `S` and `R` hotkeys to transformation UI element in form keyframe editor to edit transformation with a mouse. `X`, `Y` and `Z` keys can be used to change which axis to edit. `Enter` (or **LMB**) to accept the value or `Escape` (or **RMB**) to cancel editing. **IMPORTANT**: this is a simple implementation! There is no special accounting to current limb or camera's position/orientation, yet...
* Changed the rendering of world and object editor tools to be hidden when the UI is hidden with `F1`.
* Change the audio rendering to show ending more apparent (FunkyFight).
* Fixed negative pause incorrectly calculates timing for color coding audio.
* Fixed clips editor's viewport not resetting when switching to another film.
* Fixed gradient editor's context menu in particle editor.
* Fixed shaders related bugs with camera world object.
* Fixed orbit camera mode not working during playback.

## BBS 0.1.2

Another patch update that mostly fixes film editor crashes (shout out to FunkyFight for finding most of those bugs).

* Added display of native keyboard character names in keybinds (`F9`) and key combo input fields.
* Added engine setting **Force QWERTY** under Appearance category (disabled by default, it allows to force QWERTY key name display instead of current keyboard layout).
* Added paused particle form property to control the emission of the particle effect.
* Added forced duration to form property keyframes (FunkyFight).
* Added a feature to prevent launching multiple BBS Studio instances (FunkyFight).
* Added `Ctrl + C` and `Ctrl + V` to keyframe editors.
* Fixed overlay panels can be moved outside of window's bounds (FunkyFight).
* Fixed form properties getting locked due to incorrectly implemented property fetching (FunkyFight).
* Fixed crashing when pasting keyframes of incompatible types (FunkyFight).
* Fixed crashing when picked model in film editor is empty (FunkyFight).
* Fixed crashing when cutting clips (FunkyFight).
* Fixed translate and angle clips being applied with no prior clips (FunkyFight).
* Fixed point management in path clip editor (FunkyFight).
* Fixed crash when converting a clip (FunkyFight).
* Fixed screenplay editor cutoff feature.
* Fixed incorrect application of keyframes toward the last tick (FunkyFight).
* Fixed model not loading due to math expressions not being parsed correctly (if math expression parsing fails, it will use `0`).
* Fixed crashing when a form body part doesn't have a form (FunkyFight).
* Updated Super Supporter list in welcome menu! 💖

## BBS 0.1.1

Small BBS Studio update mostly done to film editor.

* Added new screenplay editor that allows to generate audio voice lines (via ElevenLabs.io) more flexibly. There are ways to add pauses, or cutoff parts of the audio, as well as save all of the voice lines as a single audio file, and generate subtitle clips out of the script.
* Added a feature to insert form property keyframes when holding `Shift + Ctrl` and picking a form property in the preview.
* Added a feature to dope sheet editor to edit a single keyframe channel at the same time by left clicking on hover pencil icon in the left part of the keyframe editor.
* Changed replay editor layout: removed channel list and replaced it with two keyframe modes (entity keyframes, form properties) and moved replay list to the left side.