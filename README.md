# BBS

**BBS** is a voxel game engine and a sandbox written in Java. At the moment, it's not finished, yet it still has a ton of features. To find out more about **BBS**, check out the [wiki](https://github.com/BBS-Engine/bbs/wiki).

## Structure

**BBS** is organized into two projects: engine (the core components, libraries, etc.) and app (the sandbox itself). You can build the engine (`gradle :engine:build`), and use it in other projects (but you'll have to specify the dependencies though, see Core Survivor's [build.gradle](https://github.com/BBS-Engine/core-survivor/blob/main/build.gradle)). You'll need to write a lot of bootstrap code though. See `app/`'s source code.

## Building

To build, you need Java **8+** and Gradle **7.5.1**.

As for IDE, BBS was developed in IntelliJ **2022.3.1** (Community Edition). Build is as easy as executing `./buil.sh`, that should compile **BBS** to `release/`, and you should be able to run it by double-clicking `launcher.jar`.

## Developing

To launch Core Survivor in IntelliJ, you need to create an Application run configuration with following options:

* Module: `BBS.app.main`
* JVM arguments: `-Dfile.encoding=UTF-8 -Dfabric.development=true`
* Prorgram arguments: `--gameDirectory $ProjectFileDir$\game\ --development --width 1280 --height 720`
* Main class: `net.fabricmc.loader.impl.launch.knot.KnotClient`

You'll need to create folder `game/` in the root of the project.

## Credits

The code is written entirely by me. While localization files were made by:

| Language           | Author          |
|--------------------|-----------------|
| English `en_us`    | McHorse         |
| Portuguese `pt_BR` | Draacoun, Aloan |
| Russian `ru_RU`    | Kirkus, McHorse |
| Ukrainian `uk_UA`  | Kirkus          |

## License

The source code (`engine/`, `sandbox/`) is licensed under MIT. See [LICENSE.txt](./LICENSE.txt). 