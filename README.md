# Vanilla Flavors

A Fabric mod for Minecraft 26.3 that brings back classic flavors of vanilla: things broken in 1.3.1 (12w18a) that Mojang never fixed, plus old looks and animations from Beta and 1.x. Every feature can be turned on or off from Mod Menu (optional, needs Cloth Config).

## Features

**Graphics**
- Beta distance fog (b1.7.3)
- Lightmap style: Beta grayscale or modern colorized, switchable live with F7 (as in Better Than Adventure)
- Beta smooth lighting
- Void particles near the bottom of the world (1.7)
- Dimension loading titles ("Entering the Nether", "Respawning"...)
- Golem build particles

**Animations**
- Beta arm swing, no arm sway
- Sideways backward walking and classic head turn (pre-1.12)
- Fall tilt (1.12.2 camera pitch)
- Random damage tilt
- Twisted arm (bug MC-1349)

**Entities**
- Ghasts swell before firing
- Minecarts push the player
- Lag-free minecarts

**Gameplay**
- Instant teleport (no sliding endermen)
- Creative mode blocks in the hotbar (1.2.5)
- Notch's apple
- Smooth map marker

## Download
Get the `.jar` from [Releases](../../releases). Requires Fabric Loader 0.20+.

## Building
```bash
./build.sh
```
Compiles with `javac` against the 26.3 game jar and the libraries installed by the Modrinth App (see the variables at the top of `build.sh`). The jar ends up in `build/libs/`.

## License
[LGPL-3.0](COPYING.LESSER) (it builds on the [GPL-3.0](COPYING)).
