# BBS

**BBS** is a voxel machinima studio written in Java ([Blockbuster mod](https://github.com/mchorse/blockbuster) successor).

This is a private branch, in a sense that I'm not building it for other people to use, but if you want to try it out, feel free to build it yourself. 

This version of BBS is fully focused on making machinimas/animations, and hence all of the gameplay features in the main branch **were removed**, and extra machinima/animation features **were added**.

## Structure

**BBS** is organized into two projects: engine (the core components, libraries, etc.) and studio (the app itself). You can build the engine (`gradle :engine:build`), and use it in other projects (but you'll have to specify the dependencies though, see Core Survivor's [build.gradle](https://github.com/BBS-Engine/core-survivor/blob/main/build.gradle)). You'll need to write a lot of bootstrap code though. See `studio/`'s source code.

## Building

To build, you need Java **8+** and Gradle **7.5.1**.

As for IDE, BBS was developed in IntelliJ **2022.3.1** (Community Edition). Build is as easy as executing `./build.sh`, that should compile **BBS** to `release/`, and you should be able to run it by double-clicking `launcher.jar`.

## Developing

To launch BBS in IntelliJ, you need to create an Application run configuration with following options:

* Module: `BBS.studio.main`
* JVM arguments: `-Dfile.encoding=UTF-8` (add `-XstartOnFirstThread` if you're on macOS)
* Prorgram arguments: `--gameDirectory $ProjectFileDir$\game\ --width 1280 --height 720`
* Main class: `mchorse.studio.Studio`

You'll need to create folder `game/` in the root of the project.

## License

The source code (`engine/`, `studio/`) is licensed under MIT. See [LICENSE.txt](./LICENSE.txt). 
