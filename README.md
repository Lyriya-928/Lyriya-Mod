# Lyriya Mod

English | [简体中文](README.zh-CN.md)

Lyriya Mod is a collection of Fabric client-side utilities for technical survival, redstone engineering, and technical Minecraft gameplay. Many features are inspired by or adapted from other open-source projects. Lyriya Mod does not claim original authorship of every included feature and does not require Meteor Client or Meteor Lotus.

> **Early Demo:** This project is still in an early stage of development. Some features have not been fully verified in-game by the project maintainer. Auto Helmet may fail to trigger as expected, while NoRender, Freecam, Auto Crafting, and other features require further testing in real gameplay scenarios. Implementation status and known limitations are documented below and in `docs/`.

## Installation

Requirements:

- Minecraft 26.3
- Fabric Loader 0.19.5 or later
- Fabric API 0.161.0+26.3 or a compatible version
- Java 25 or later

Place the following mod JARs in your instance's `mods` directory:

- **MaLiLib Fabric 26.3-0.30.2 (required):** Obtain `malilib-fabric-26.3-0.30.2.jar` from [Modrinth](https://modrinth.com/mod/malilib/version/0.30.2) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib/files/9100218). MaLiLib is a separate LGPL-3.0-only dependency and is not bundled with the Lyriya JAR.
- **Lyriya Mod 0.1.4:** The build artifact is `build/libs/lyriya-mod-0.1.4.jar`.

Fabric metadata declares `malilib >= 0.30.2` as a required runtime dependency. Do not install Meteor Client or Meteor Lotus to satisfy Lyriya Mod's dependencies.

## Configuration

Lyriya Mod uses MaLiLib for its configuration panel, option components, keybindings, and JSON persistence.

The default keybinding for opening the configuration panel is **Backslash (`\`)**. You can change it under **General → Open configuration**.

Configuration is stored in `config/lyriya-mod.json`. Existing settings are preserved. The legacy `L + Y` configuration-panel shortcut is migrated to Backslash, while other user-defined keybindings remain unchanged.

**Toggle Notifications** is enabled by default. Notifications appear in the HUD overlay message area when a feature is manually toggled and are not sent to chat. Loading configuration does not generate individual notifications for every module.

**Debug Logging** and **Debug Messages** are disabled by default. Auto Crafting debug messages are rate-limited to one notification when no whitelist target is available or no recipe has sufficient materials.

## Feature Status

The descriptions below indicate that corresponding implementations exist in the code. They do not imply that every feature has been verified in-game during the current development cycle. Entity List and Portals were previously tested by the user and reported to work in earlier builds. The client was not launched for testing during the current cycle.

- **Auto Helmet** (`H`): Retains the original behavior of equipping a golden helmet when piglins are nearby. This module is currently frozen. Previous user feedback indicated that it may fail to trigger. Its detection, activation, inventory interaction, and equipment logic have not been modified.
- **Entity List** (`J`): Displays entities tracked by the client's current entity data, with category filters. Configuration includes horizontal and vertical anchors, margins, X/Y offsets, scaling, maximum line count, line spacing, shadows, and separate colors for dropped items, players, hostile/passive mobs, and other entities. The list does not include entities in unloaded chunks or every entity on the server.
- **Portals** (`P`): Preserves the ability to interact with supported interfaces while portal visual effects are active.
- **GUI Move** (`G`): By default, allows movement only in inventory and container interfaces. Other eligible interfaces, jumping, sneaking, and sprinting can be enabled through configuration. Text input and Lyriya/MaLiLib configuration interfaces are excluded.
- **Auto Totem** (`O`): Moves a totem of undying from the inventory to the offhand according to the selected health-based, Elytra-related, or always-active mode.
- **SafeWalk+** (`K`): Offers Soft Sneak, which retains the previous lighter edge assistance and is not a fall-proof guarantee, and Edge Auto Sneak, which checks support beneath the leading foot strip and adds normal sneak input. Fixed and stable-per-approach random distance modes are available. It never locks player coordinates or edits movement speed/velocity.
- **Auto Crafting**: The **Edit Item Whitelist** page shows existing entries and accepts item IDs (for example `minecraft:obsidian`), exact localized item names, item tags prefixed with `#`, or contains matching with `,c` (for example `深板岩,c`). Tag rules are saved as tags and expanded against the current item registry when crafting targets are resolved. The editor can add, remove selected entries, and reports invalid input. Modes include automatic start at a crafting table and keybind-controlled execution. C keeps results; V drops task results. Automatic start keeps results by default and has its own drop-results setting. Existing ID-only 0.1.3 whitelist entries remain readable.
- **Fullbright** (`F7`): Provides three modes: Gamma, Potion, and Luminance. Gamma overrides the lightmap without changing the player's Gamma setting. Potion applies a local night vision effect with a default target duration of 120 seconds. Luminance sets minimum block and sky light levels. Potion mode does not actively replace other detected sources of night vision.
- **NoRender** (`N`): Implements selected overlays, HUD elements, fog, weather, world borders, block-breaking progress, beacons, enchanted books, signs, falling blocks, maps, fireworks, spawners, armor, entity visibility, particles, enchantment glint, obfuscated text, signature indicators, barrier visibility, random block rotations, cave culling, banner modes, and configurable entity, block-entity, and particle lists. **Drop Spawn Packets** is disabled by default; when enabled it discards matching packets so those entities do not enter the client world. This is not a visual-only change. See the [NoRender Compatibility Matrix](docs/NORENDER_COMPATIBILITY.md) for implemented, unimplemented, and limited options.
- **Freecam** (`F8`): Camera movement and look are separated from player movement; Minecraft's processed mouse sensitivity, inversion, and smoothing feed camera turning. WASD and jump control the camera; the configured vanilla sneak key remains unconsumed for the player's normal sneak input and also moves the camera down. Settings include optional player-body rotation/movement, Render Hands, Render Player Body (off by default), hotbar, and status bars. Camera position and direction are interpolated and diagonal movement is normalized. Freecam changes only the client-side camera and does not move the server-side player.
- **No Ghost Blocks** (`F9`): Attempts to reduce client-side prediction issues during supported block-breaking and block-placement operations. It does not guarantee a fix for every server synchronization issue.

NoRender options are organized into Overlay, HUD, World, and Entity categories. Child settings are checked live, so disabling one restores its vanilla render path; cave-culling and texture-rotation settings ask Minecraft to rebuild compiled chunk geometry. Spawn packets already discarded cannot be restored retroactively. Compatibility details and remaining unimplemented reference settings are documented in the [NoRender Compatibility Matrix](docs/NORENDER_COMPATIBILITY.md).

**Known issue:** Fullbright's Luminance mode may update only some already-loaded chunks after its minimum light level changes. This is documented as unfixed in [Testing and known issues](docs/TESTING.md); Gamma and Potion modes are not disabled by this issue.

The feature list describes implementation status in the code, not successful in-game verification of every feature by the Lyriya Mod project.

## Upstream Projects and Licensing

Lyriya Mod is licensed under **GPL-3.0-only**. See the [LICENSE](LICENSE) file for the full license text.

The project studies, references, or adapts features from the following open-source projects. Detailed commit references, affected files, and license records are documented in [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md).

- [Meteor Client](https://github.com/MeteorDevelopment/meteor-client) — GPL-3.0. Some features use its behavior as a reference. Portions of the No Ghost Blocks and banner-rendering logic have been adapted from its source code. Copyright notices and modification notes are retained in the relevant source files.
- [Meteor Lotus](https://github.com/xiaohe66/xiaohe66-meteor-lotus) — GPL-3.0. Used to research several features. The current source records indicate that no source code was copied from this project.
- [MaLiLib](https://github.com/maruohon/malilib) and its [community build source for Minecraft 26.3](https://github.com/sakura-ryoko/malilib) — LGPL-3.0. MaLiLib is a separately installed runtime dependency and is not bundled with the Lyriya JAR.

Any future porting of upstream code must retain the applicable copyright and license notices and document the scope of modifications in the source records.

## Building

Use JDK 25 or later.

```powershell
.\gradlew.bat build --offline
```

Offline builds are supported after dependencies have been resolved and cached. The initial dependency resolution for MaLiLib requires an internet connection.

Build artifacts are generated in `build/libs/`.

Build and static analysis results are documented in [docs/TESTING.md](docs/TESTING.md). Upstream sources and licensing details are documented in [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md).

## License

Lyriya Mod is licensed under **GPL-3.0-only**. MaLiLib is a separate **LGPL-3.0-only** dependency.

See [LICENSE](LICENSE) and [docs/UPSTREAM_SOURCES.md](docs/UPSTREAM_SOURCES.md) for license information and records of adapted upstream code.
