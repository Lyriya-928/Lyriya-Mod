# Upstream source record

This file records sources consulted while implementing Lyriya Mod. Upstream repositories are kept outside this project tree. No upstream source files have been copied into this project.

## Meteor Client

- Repository: https://github.com/MeteorDevelopment/meteor-client
- Default branch: `master`; GitHub advertised HEAD `2385f7b4bb9de1a698b4503254bd32f30fd7a87d` when checked on 2026-10-14.
- 26.3 feature reference checkout: an external local checkout; its machine-specific path is intentionally not published.
- Feature reference branch: `pr6679`
- Feature reference commit: `9a9600699248bb4f34be5f05b93e42ff5c695e22`
- License: GNU General Public License v3.0, `LICENSE`
- Features researched: `AutoTotem`, `Portals`, `NoRender`, `Fullbright`, `Freecam`, and `NoGhostBlocks`.
- This round's pinned NoRender and Fullbright/Freecam comparison commit: `9a9600699248bb4f34be5f05b93e42ff5c695e22` (the local `pr6679` checkout resolves to this exact commit).
- Source paths:
  - Auto Totem: `src/main/java/meteordevelopment/meteorclient/systems/modules/combat/AutoTotem.java`
  - Portals: `src/main/java/meteordevelopment/meteorclient/systems/modules/player/Portals.java`
  - NoRender: `src/main/java/meteordevelopment/meteorclient/systems/modules/render/NoRender.java`
  - Fullbright: `src/main/java/meteordevelopment/meteorclient/systems/modules/render/Fullbright.java`
  - Freecam: `src/main/java/meteordevelopment/meteorclient/systems/modules/render/Freecam.java`
  - No Ghost Blocks: `src/main/java/meteordevelopment/meteorclient/systems/modules/world/NoGhostBlocks.java`
  - 26.3 portal screen behavior: `src/main/java/meteordevelopment/meteorclient/mixin/LocalPlayerMixin.java`, method `modifyPortalTransitionEffect`.
- Scope: default-branch HEAD was recorded; target-version behavior and APIs were inspected in the available 26.3 `pr6679` checkout. Lyriya does not use Meteor runtime APIs. Auto Totem, SafeWalk+, Auto Crafting, Fullbright, NoRender, Freecam, GUI Move, and the pre-existing features are standalone implementations. The two No Ghost Blocks Mixin hooks adapt the limited client-prediction behavior from Meteor's `NoGhostBlocks` and `BlockItemMixin`; they carry Meteor Development's copyright/license attribution and a modification notice.
- Adapted No Ghost Blocks behavior: `src/main/java/com/lyriya/mod/mixin/MultiPlayerGameModeNoGhostMixin.java` and `BlockItemNoGhostMixin.java`. The port is limited to client-side break/place prediction paths in Minecraft 26.3; it does not claim universal block-state repair.
- Freecam review in this round compared `systems/modules/render/Freecam.java` and the corresponding camera/input mixins. Lyriya's implementation is independent: keyboard input is captured in `KeyboardHandlerMixin`, player input suppression remains in `KeyboardInputFreecamMixin`, camera state is interpolated in `CameraFreecamMixin`, and optional first-person hand rendering is controlled by `FreecamHandsMixin`. No Meteor source file was copied.
- Fullbright review compared `systems/modules/render/Fullbright.java`, `BrightnessGetterMixin.java`, `EntityRendererMixin.java`, and `LightmapMixin.java`. Lyriya independently implements a lightmap white override for Gamma, locally owned Night Vision, and Luminance hooks in `LightmapGammaFullbrightMixin`, `BrightnessGetterFullbrightMixin`, and `EntityRendererFullbrightMixin`. The Gamma path does not alter the user's saved Gamma option and resumes the native renderer when disabled. No Meteor source was copied.
- NoRender comparison pinned to `9a9600699248bb4f34be5f05b93e42ff5c695e22`, source `systems/modules/render/NoRender.java`, plus the actual related Meteor mixins. Lyriya's implemented subset is independent and uses target-version hooks in `HudNoRenderMixin`, `ScreenEffectNoRenderMixin`, `BossBarNoRenderMixin`, `ScreenBackgroundNoRenderMixin`, `TotemAnimationNoRenderMixin`, `EntitySelectionNoRenderMixin`, `DropEntitySpawnNoRenderMixin`, `LevelExtractorNoRenderMixin`, `BeaconBeamNoRenderMixin`, `EnchantTableBookNoRenderMixin`, and `SignTextNoRenderMixin`. Full per-setting status is in `docs/NORENDER_COMPATIBILITY.md`; no unused reference settings were copied into Lyriya's UI.
- This round extended the NoRender adapter with independently authored hooks in `EatingParticlesNoRenderMixin`, `ObfuscationNoRenderMixin`, `BarrierVisibilityNoRenderMixin`, `TextureRotationsNoRenderMixin`, `EnchantmentGlintNoRenderMixin`, `MessageSignatureIndicatorNoRenderMixin`, `BlockEntityListNoRenderMixin`, `ParticleListNoRenderMixin`, and `CaveCullingNoRenderMixin`. The Banner `Pillar` path in `BannerRenderModeNoRenderMixin` adapts Meteor's small render strategy from its `BannerRendererMixin`; that file retains a Meteor Development copyright reference, GPL notice, and explicit modification note. It does not reuse Meteor's module/settings APIs.

### GUI Move

- User-specified pinned references:
  - `GUIMove.java`: https://github.com/MeteorDevelopment/meteor-client/blob/2385f7b4bb9de1a698b4503254bd32f30fd7a87d/src/main/java/meteordevelopment/meteorclient/systems/modules/movement/GUIMove.java
  - `GuiMixin.java`: https://github.com/MeteorDevelopment/meteor-client/blob/2385f7b4bb9de1a698b4503254bd32f30fd7a87d/src/main/java/meteordevelopment/meteorclient/mixin/GuiMixin.java
  - `ScreenMixin.java`: https://github.com/MeteorDevelopment/meteor-client/blob/2385f7b4bb9de1a698b4503254bd32f30fd7a87d/src/main/java/meteordevelopment/meteorclient/mixin/ScreenMixin.java
- Requested reference commit: `2385f7b4bb9de1a698b4503254bd32f30fd7a87d`. That object was not present in the available local Meteor checkout. The related 26.3 source paths were inspected in local branch `pr6679`, commit `9a9600699248bb4f34be5f05b93e42ff5c695e22`; this difference is recorded rather than treating the two revisions as identical.
- License: GNU General Public License v3.0, repository `LICENSE`.
- Migration scope: independent implementation in `GuiMoveFeature`, `GuiMoveMixin`, `KeyboardHandlerMixin`, and `KeyMappingAccessor`. No Meteor Java source or license header was copied. The implementation narrows activation to inventory/container screens by default, skips text entry and MaLiLib/Lyriya config screens, and uses 26.3 `Gui.setScreen`, `KeyboardHandler.keyPress`, and `KeyMapping.ALL` APIs verified against the local 26.3 source/reference and compile classpath.
- License handling: the project itself is GPL-3.0-only, compatible with the referenced upstream license. If later edits copy or adapt the upstream files rather than continuing the independent implementation, preserve the upstream copyright and license notice, identify modifications, and meet GPL source and redistribution obligations.

## Meteor Lotus

- Repository: https://github.com/xiaohe66/xiaohe66-meteor-lotus
- Branch: `1.21.11-1.16`
- Commit: `4ca773b8198b5bb8a4ceb7c0d15c18c8516a4f7c`
- Reference checkout: task scratch area, not part of this project.
- Minecraft version in the branch: 1.21.11; it is not treated as directly compatible with 26.3.
- License: GNU General Public License v3.0, `LICENSE`.
- Features researched: `SafeWalkPlus`, `EntityList`, `AutoCrafting`, and `AutoHelmet`.
- Source paths:
  - SafeWalk+: `src/main/java/com/xiaohe66/mc/meteor/lotus/modules/SafeWalkPlus.java`
  - Entity List: `src/main/java/com/xiaohe66/mc/meteor/lotus/modules/EntityList.java`
  - Auto Crafting: `src/main/java/com/xiaohe66/mc/meteor/lotus/modules/AutoCrafting.java`
  - Auto Helmet: `src/main/java/com/xiaohe66/mc/meteor/lotus/modules/AutoHelmet.java`
- The 2026-10-18 workflow review re-read the pinned `AutoCrafting.java` above. Lyriya follows its current-result priority, craftable recipe-book selection, one-target task, C/keep and V/drop actions, and delayed repeat pattern, but uses Minecraft 26.3 `RecipeDisplayEntry`/`RecipeCollection` and `ContainerInput` APIs. No Lotus source was copied; this is a behavioral adaptation.
- Meteor API dependencies found: module registration/settings and Orbit events; inventory utilities for Auto Helmet; Meteor's text renderer, settings, and toast APIs for Entity List. These are implementation dependencies to replace, not runtime dependencies to retain.
- Auto Helmet imports and calls Meteor's `BaseModule`, `TickEvent`, settings, `Utils.getEnchantments`, `InvUtils`, and Orbit; its entity scan uses the old version's `PiglinEntity` and player/world APIs.
- Entity List imports Lotus `ItemWarp`, `LogEvent`, `DisplaySide`, and `Const`, plus Meteor's module/events/settings/text renderer, name and toast utilities, and Orbit event bus. The copied implementation therefore depended on both Lotus helpers and Meteor's systems; the Lyriya version uses the client level and Fabric HUD extraction instead.
- Scope: behavior and dependency research only; no Lotus source has been copied or modified in Lyriya Mod.

## Tweakeroo Free Camera reference

- Repository: https://github.com/maruohon/tweakeroo
- License: LGPL-3.0 as declared by the upstream repository/license page (`LICENSE.txt`). No Tweakeroo source or binary is included in Lyriya Mod.
- Research scope for this change: Free Camera's separation of camera controls from optional player inputs/movement and its independent camera state. Candidate upstream paths named by the project source browser are `src/main/java/tweakeroo/util/CameraEntity.java`, `CameraUtils.java`, and the related entity/HUD mixins.
- Revision limitation: GitHub's source listing in the available browser exposed the `ornithe/1.12.2` branch and a short visible head `241a2a4`; direct clone/API retrieval failed in the local terminal (TLS credential acquisition failure, including a request through the session-only local proxy). That branch is not Minecraft 26.3 and a complete commit SHA or current Fabric source could not be independently confirmed in this delivery. Accordingly, no source lines are claimed to have been ported; implementation decisions were checked against the actual local Minecraft 26.3 `MouseHandler.turnPlayer` and `LocalPlayer.turn` bytecode instead.
- Lyriya implementation scope: `FreecamFeature`, `MouseHandlerFreecamMixin`, `KeyboardInputFreecamMixin`, and `FreecamHudMixin` remain independently authored. The mouse mixin routes vanilla's already sensitivity-scaled, inverted, and optionally smoothed deltas to the free camera. No Tweakeroo runtime API or keybind system is used.

### NoRender, Fullbright, and Freecam follow-up audit

- Reference commit: `9a9600699248bb4f34be5f05b93e42ff5c695e22` (Meteor Client, GPL-3.0; source files `systems/modules/render/NoRender.java`, `Fullbright.java`, and `Freecam.java`).
- `NoRender.java` was enumerated directly: Overlay, HUD, World, and Entity groups; 48 Setting declarations including the `BannerRenderMode` enum and particle/block/entity lists. The complete status is in `docs/NORENDER_COMPATIBILITY.md`.
- Scope: the new NoRender hooks are independent Mixin implementations against Minecraft 26.3. They use the reference to identify intended behavior and target methods, but do not import Meteor APIs or copy a complete Meteor source file. New hook files cover eating particles, font obfuscation, barrier display ticks, model seed/offset stabilization, item foil submission, chat signature markers, block entity render-state filtering, and client particle admission.
- Unimplemented reference settings remain absent from the UI: Cave Culling and Banner Render Mode. The former needs a safe compiled chunk-culling rebuild callback; the latter requires a separately audited model submission path for all enum states.
- These changes are independent implementations informed by GPL-3.0 upstream behavior. No Meteor code is bundled, and no additional upstream copyright header is claimed for independently authored files. If future changes copy or adapt source expressions/code beyond API-level behavior, preserve its notice and record the exact file and modifications here before redistribution.

## MaLiLib

- Original upstream: https://github.com/maruohon/malilib (LGPL-3.0; `LICENSE.txt`).
- Adopted compatible fork: https://github.com/sakura-ryoko/malilib, branch `26.3`.
- Inspected branch checkout: `b043d04c21009a9ac1d19078e56c501d63d56ec8`; its `gradle.properties` declares MaLiLib `0.30.2`, Minecraft `26.3`, Fabric Loader `0.19.5`, and Fabric API `0.161.0+26.3`; its build declares Java release 25 and Fabric Loom `1.18.+`.
- GitHub tag `26.3-0.30.1` is a separate earlier commit: `f189e3cd94cf494f95ebf337efd56cf291047d30`. It is not the adopted version.
- Published binary: `malilib-fabric-26.3-0.30.2.jar`, available from [Modrinth](https://modrinth.com/mod/malilib/version/0.30.2) and [CurseForge](https://www.curseforge.com/minecraft/mc-mods/malilib/files/9100218). Lyriya's Gradle build resolves `maven.modrinth:malilib:0.30.2` from Modrinth Maven; dependency resolution and compilation succeeded.
- License at inspected fork checkout: LGPLv3 (`LICENSE.txt`); metadata identifies the library as LGPLv3. The dependency remains separate and is not shaded or copied into Lyriya Mod.
- API verified in that checkout: `ConfigBooleanHotkeyed`, `ConfigInteger`, `ConfigHotkey`, `IConfigHandler`, `ConfigManager`, `ConfigUtils`, `IKeybindProvider`, `InputEventHandler`, `GuiConfigsBase`, and `Registry.CONFIG_SCREEN`. It supports registered options, persistent config files, editable multi-key bindings, and MaLiLib-style configuration screens.
- The inspected 0.30.2 sources also provide `ConfigColor`, `ConfigDouble`, `ConfigStringList`, `GuiStringListSelection`, and `IStringListConsumer`; these are used for Entity List colors/ranges and localized item/entity selection dialogs. MaLiLib has no Minecraft Item/EntityType registry picker, so Lyriya builds the candidate labels from the target game's registries and uses MaLiLib's own selection UI.
- Scope: required runtime dependency for configuration UI, settings, persistence, and keybinds. Lyriya feature behavior remains independent implementation code.

## Target build versions

- Minecraft: `26.3`
- Java runtime found on this machine: Oracle JDK `26.0.1`; Minecraft target bytecode release: `25`.
- Fabric Loader: `0.19.5`
- Fabric API: `0.161.0+26.3`
- MaLiLib Fabric: `0.30.2` (Sakura-Ryoko 26.3 fork)
- Fabric Loom: `1.17.21` (resolved from the `1.17-SNAPSHOT` plugin line)
- Gradle Wrapper: `9.6.1`
- The 26.3 toolchain uses Mojang's published, unobfuscated names; dependencies use `implementation` with the new `net.fabricmc.fabric-loom` plugin. No Yarn mappings dependency is configured.

## Lyriya Mod 0.1.2 follow-up

### Auto Crafting

- Lotus source inspected at branch `1.21.11-1.16`, commit `4ca773b8198b5bb8a4ceb7c0d15c18c8516a4f7c`: `src/main/java/com/xiaohe66/mc/meteor/lotus/modules/AutoCrafting.java`.
- The source declares author `He`; the checked-out Lotus project is GPL-3.0. The adaptation notice and source link are retained in `AutoCraftingFeature.java`; Lyriya remains GPL-3.0-only.
- Adapted Lotus behavior: choose the visible crafting output first (and add it to the target whitelist), otherwise choose one craftable whitelist target; keep output with `QUICK_MOVE` for C, throw the result stack for V, and delay/recheck synchronized result and input state before another recipe request.
- Minecraft 26.3 adaptation: use `RecipeCollection`/`RecipeDisplayEntry`, `handlePlaceRecipe`, `handleContainerInput`, and the current `CraftingMenu` slots in place of Lotus's 1.21.11 recipe-book and Meteor inventory APIs. No Meteor or Lotus runtime classes are used.
- Capacity bug cause: 26.3 `Inventory.getSlotWithRemainingSpace(ItemStack)` scans only the selected/offhand and existing non-full matching stacks; it returns `-1` when an empty slot is the available destination. The old check treated that as a full inventory. C now follows Lotus's initial `getFreeSlot` requirement, which scans the 36 ordinary player inventory slots; V skips this capacity requirement. The same empty-slot check is retained before C takes a result, so a full inventory cannot silently lose a result.

### MaLiLib item selector

- `LyriyaConfigGui.SearchableItemSelection` subclasses MaLiLib 0.30.2's actual `GuiStringListSelection`. Its `SearchableItemList` is created inside that popup; it opens and focuses the popup list's own `WidgetSearchBar`.
- MaLiLib's list widget forwards key/character input to that field and refreshes its visible entries through `getEntryStringsForFilter`/`matchesFilter`. Lyriya supplies both the localized display label and registry ID as filter terms, normalizes case with `Locale.ROOT`, and displays a no-results message in the same popup. The selection set is held by MaLiLib's list widget, so filtering does not clear selected entries; the add callback merges selected IDs into the saved whitelist.

### Freecam and Sodium compatibility review

- The separate Freecam X descend key has been removed. Freecam observes `Minecraft.options.keyShift.isDown()` for camera descent while leaving the configured vanilla key event and `KeyboardInputFreecamMixin`'s `Input.shift()` value intact for player sneak.
- The local `.minecraft/mods` directory did not contain Sodium and the available `latest.log` is dated 2026-09-30, so the Sodium version used for the user's white-chunk report could not be identified from this workspace.
- For a concrete 26.3 comparison, Sodium tag `mc26.3-0.9.2` (source file `common/src/main/java/net/caffeinemc/mods/sodium/mixin/core/render/world/LevelExtractorMixin.java`, release commit short SHA `03198cb`) was inspected. It redirects terrain setup in `LevelExtractor.extract` and separately replaces visible block-entity extraction; it does not overwrite `extractVisibleEntities`. Lyriya's `FreecamPlayerBodyMixin` redirects only `Camera.isDetached()` inside `extractVisibleEntities`, the local camera-entity inclusion condition. That is an entity-extraction hook, not a terrain/frustum or chunk-build hook, and the inspected Sodium source does not target that method. This narrows the suspected interaction but cannot establish the user's actual Sodium compatibility or resolve the visual symptom without the installed version and gameplay test.
- Minecraft 26.3 is compiled and shipped with Mojang names in this Loom configuration; explicit Yarn/Mojang remapping is not configured. The Minecraft merged jar verifies the named target method. The project's attempted Mixin AP setup failed because no obfuscation mapping set exists in this non-obfuscated environment, so no refmap is generated or referenced. This build arrangement is retained; it does not itself prove runtime Mixin application.

## Lyriya Mod 0.1.3 follow-up

- Freecam comparison references: Meteor Client's `Freecam.java` and `LevelExtractorMixin.java`, plus Tweakeroo's `pre-rewrite/fabric/1.21.1-masa` camera utilities and world-renderer mixin. These were consulted for behavior only; no source code was copied. Meteor's Freecam invalidates `levelExtractor` data on activation/deactivation. Lyriya now calls the 26.3 `LevelExtractor.allChanged()` at those transitions. `FreecamPlayerBodyMixin` remains limited to the local-player inclusion check in `extractVisibleEntities`; it does not modify terrain extraction, frustum checks, or chunk building. The Sodium version in the user's test environment is unknown, so compatibility and the white-chunk symptom remain unconfirmed.
- `LyriyaConfigGui.SearchableItemSelection` continues to use MaLiLib 0.30.2's popup and embedded `WidgetSearchBar`. The selector list now creates `ItemIconStringEntry` rows, rendering the item resolved from the stable registry ID and the localized item label in the same filtered, multi-select list. The existing selection callback still merges IDs into the saved whitelist.
- C/V handling inside a crafting table is a narrow `KeyboardHandler.keyPress` hook gated to `CraftingScreen` and the configured MaLiLib `IKeybind` values. It leaves events to an active `EditBox`; other screens are untouched. This adapts the current Minecraft screen input path and adds no Meteor/Lotus input code.
- Automatic-on-open result handling is a new persisted MaLiLib setting, default false. Manual C/V handling is separate. Entity List scale labels no longer contain the literal percent sign that caused formatted translation errors; the numeric setting and HUD rendering are unchanged.

## Licensing and redistribution

No complete upstream Java source file has been copied into Lyriya Mod. GUI Move and most features are independent implementations against the target API; the two No Ghost Blocks hooks are limited adaptations noted above. The project uses GPL-3.0-only as its project license; the complete license text is in the root `LICENSE` file. Any further upstream adaptations must be recorded here with the affected file notices before redistribution.
