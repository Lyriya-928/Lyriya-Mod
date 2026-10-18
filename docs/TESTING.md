# Build and verification record

## Environment

- Minecraft target: 26.3
- Java found: Oracle JDK 26.0.1; compilation target release 25
- Fabric Loader: 0.19.5
- Fabric API: 0.161.0+26.3
- MaLiLib: 0.30.2 from Modrinth Maven
- Loom resolved: 1.17.21
- Gradle: 9.6.1

## Earlier standalone phase (before MaLiLib integration)

| Stage | Command | Result |
|---|---|---|
| Standalone project baseline | `gradle build --offline` | Passed before feature implementation. |
| First feature compilation pass | `gradle build --offline` | Failed on version-specific API assumptions (`InputConstants.Type.KEYSYM`, `Minecraft.screen`) and Mixin annotation remapping; corrected using 26.3 APIs and no remapping annotation processor. |
| Feature implementation build | `gradlew.bat build --offline` | Passed after corrections. Features compile. |
| Runtime dependency graph | `gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed. No Meteor Client or Meteor Lotus coordinate was present. This check predates the MaLiLib dependency. |
| Mod metadata | Inspected `fabric.mod.json` in the earlier JAR | Valid at the time; it predated the MaLiLib dependency. |
| Client startup | `gradlew.bat runClient --offline` | Historical first-stage launch only. No world or feature interaction was tested. Routine deliveries must not launch a client. |

No client or world was started for this MaLiLib integration delivery. Build and static metadata checks do not establish in-game behavior.

## MaLiLib integration verification (2026-10-15)

| Check | Command or inspection | Result |
|---|---|---|
| MaLiLib resolution and compile | `.\gradlew.bat build` (through the local proxy for the first resolution) | Passed with `maven.modrinth:malilib:0.30.2`; all production sources compiled. |
| Required offline build | `.\gradlew.bat build --offline` | Passed after dependency resolution. |
| Runtime dependencies | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed; includes `maven.modrinth:malilib:0.30.2`, Loader/API, and no Meteor Client or Meteor Lotus. |
| Mod metadata and JAR contents | Read `fabric.mod.json` and entries in `build/libs/lyriya-mod-0.1.0.jar` | Mod ID/version are `lyriya_mod`/`0.1.0`; dependency `malilib >= 0.30.2` is declared; the JAR does not bundle MaLiLib classes. |
| Gameplay | Not run | No Minecraft client or world was started, as requested. |

## Continued development (2026-10-15)

| Check | Command or inspection | Result |
|---|---|---|
| GUI Move and translation stage | `.\gradlew.bat build --offline` | Passed after resolving a 26.3 HUD API name against MaLiLib source. |
| Auto Totem stage | `.\gradlew.bat build --offline` | Passed after adapting inventory interaction to 26.3 `ContainerInput.SWAP` and `handleContainerInput`. |
| SafeWalk+ stage | `.\gradlew.bat build --offline` | Passed; Player ledge hook target matches the inspected 26.3 Meteor source and compiled. |
| Auto Crafting stage | `.\gradlew.bat build --offline` | Passed after using 26.3 recipe display context and recipe-book types. |
| NoRender, Fullbright, Freecam and No Ghost Blocks stages | `.\gradlew.bat build --offline --rerun-tasks` | Passed; production Java and resources were rebuilt from source. |
| Game testing | Not run | No client, world, or `runClient` task was started. Prior user-reported tests for Entity List and Portals remain the only gameplay reports. |

## Final verification (2026-10-15)

| Check | Command or inspection | Result |
|---|---|---|
| Full offline build | `.\gradlew.bat build --offline --rerun-tasks` | Passed; recompiles production sources and resources. `test` was `NO-SOURCE`. |
| Runtime dependency resolution | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed; includes MaLiLib `0.30.2`, Fabric Loader/API, and no Meteor Client or Lotus coordinates. |
| Translation coverage | Static check of all `.apply(...)` options and packaged `en_us`/`zh_cn` JSON | Passed; all 40 registered config options have name, pretty name, and comment keys; 155 locale keys match between files. |
| JAR metadata and Mixin manifest | Parsed `fabric.mod.json` and `lyriya.mixins.json` from final JAR; checked each declared Mixin class entry | Passed; ID/version `lyriya_mod`/`0.1.0`, required dependency `malilib >= 0.30.2`, all 13 declared client Mixins are present. |
| Independence check | Inspected final JAR entries and resolved runtime graph | No Meteor Client, Lotus, or MaLiLib implementation classes are bundled; no Meteor/Lotus runtime dependency is present. |
| Game testing | Not run | No `runClient`, Minecraft launch, or world entry was performed. Gameplay behavior remains for the user to verify. |

Final artifact: `build/libs/lyriya-mod-0.1.0.jar` (63,496 bytes). Passing compilation verifies source compatibility only; it does not verify Mixin application or in-game behavior.

## User feedback improvements (2026-10-15)

| Check | Command or inspection | Result |
|---|---|---|
| Freecam / Fullbright / notification stage | `.\gradlew.bat build --offline` | Passed. Freecam input/camera, all three Fullbright modes, and shared feature toggle messages compile. |
| Entity List / SafeWalk+ stage | `.\gradlew.bat build --offline` | Passed after adding layout/color controls and replacing ledge clipping with synthetic sneak input. |
| Auto Crafting / NoRender stage | `.\gradlew.bat build --offline` | Passed with localized registry selectors, two crafting modes, and the implemented NoRender subset. |
| Final source rebuild | `.\gradlew.bat build --offline --rerun-tasks` | Passed; recompiles Java and processes resources. `test` is `NO-SOURCE`. |
| Runtime dependency resolution | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed; includes MaLiLib `0.30.2`, Fabric Loader/API and Minecraft. No Meteor Client or Meteor Lotus coordinates occur in the runtime graph. |
| Final JAR metadata | Parsed `fabric.mod.json` and `lyriya.mixins.json` inside `build/libs/lyriya-mod-0.1.0.jar` | Passed static parse: `lyriya_mod` version `0.1.0`, required `malilib >= 0.30.2`, 21 declared Mixin classes present. |
| Bundled dependency check | Inspected JAR entries | No Meteor, Lotus, or MaLiLib implementation classes are bundled. |
| Locale JSON and key parity | Parsed packaged `en_us.json` and `zh_cn.json` | Passed JSON parse; 304 keys in each file with no key missing from either locale. All literal `.apply(...)` option keys are covered; the General open-config option uses the existing `GENERAL_KEY` constant and retains its previous translations. |
| Critical Mixin target audit | Cross-checked target methods against the local 26.3 merged JAR and Meteor commit `9a9600699248bb4f34be5f05b93e42ff5c695e22` | The critical Lightmap, brightness getter, entity renderer, camera, keyboard input, Hud, screen-effect, and client spawn-handler targets were inspected. Compilation does not prove Mixin application. |
| Gameplay | Not run | No `runClient`, Minecraft launch, world entry, or manual game test was performed. User testing remains necessary. |

Current artifact: `build/libs/lyriya-mod-0.1.0.jar` (105,190 bytes at this build). This is a build and static-check result, not a gameplay validation. The NoRender implementation remains partial; see `docs/NORENDER_COMPATIBILITY.md` for per-setting status and the unexposed remainder.

## Second-round user feedback delivery (2026-10-15)

| Check | Command or inspection | Result |
|---|---|---|
| Full offline rebuild | `.\gradlew.bat build --offline --rerun-tasks` | Passed; Java and resources rebuilt. `test` is `NO-SOURCE`. |
| Runtime dependency graph | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed; contains Loader 0.19.5, Fabric API 0.161.0+26.3, and MaLiLib 0.30.2; no Meteor Client or Lotus coordinates. |
| Packaged metadata | Parsed `fabric.mod.json` and Mixin manifest from the final JAR | Valid `lyriya_mod` 0.1.0 metadata; Minecraft 26.3, Java >=25, Fabric Loader/API minimums and `malilib >= 0.30.2` declared. All 45 declared Mixin class entries exist in the JAR. |
| Locale resources | Parsed packaged `en_us.json` and `zh_cn.json` | Both valid JSON, 396 keys each, identical key sets; all statically registered setting name/pretty-name/comment keys are covered. |
| NoRender source audit | Counted `Setting` declarations in the pinned upstream file and rows in `docs/NORENDER_COMPATIBILITY.md` | 48 source settings and 48 compatibility rows. NoRender list controls persist registry keys while exposing localized/readable selection labels. |
| Target signature review | Inspected Minecraft 26.3 merged classes with `javap` and compared new hooks with the pinned Meteor source | Target classes/methods/fields for food particles, font obfuscation, item glint, chat indicator, block model randomness, cave culling, banners, block entity rendering, and client particle admission exist. This does not confirm Mixin application. |
| Independence | Inspected final JAR entries and runtime dependency report | No Meteor, Lotus, or MaLiLib implementation classes bundled; no Meteor/Lotus runtime dependency. |
| Gameplay | Not run | No `runClient`, Minecraft launch, world entry, or manual test was performed. This was explicitly requested. |

Current artifact: `build/libs/lyriya-mod-0.1.0.jar` (144,806 bytes). Build success verifies compilation and packaging only. All feature behavior, especially Freecam input isolation, Fullbright render output, SafeWalk's edge timing, Auto Crafting interactions, and NoRender visual hooks, remains for user gameplay verification. Auto Helmet source logic remains frozen due the reported trigger issue.

## Follow-up correction round (2026-10-18)

### Known issue: Fullbright Luminance chunk refresh

**Known, not fixed.** After changing Luminance's minimum queried light level, some already-loaded chunks may update while others retain their prior brightness until Minecraft rebuilds or reloads their lighting/render data. This affects Luminance mode's visual consistency only. Gamma and Potion modes remain independently selectable. No large lighting-system rewrite was attempted in this round.

### Static implementation review

- NoRender's render hooks were audited against the registered child settings. Immediate hooks read the master and matching child value at each render/extraction call; selected entity/block/particle lists check the current list and current object. Banner mode passes through the vanilla path when set to `Everything`. Disabling one option does not clear unrelated renderer state.
- `Cave Culling` and `Texture Rotations`/offset suppression invalidate the 26.3 `LevelExtractor` compiled geometry on value changes and on NoRender master changes (`allChanged()` sets the renderer's compiled-geometry invalidation flag). Weather intensity is reassigned by vanilla extraction on the next frame after the setting is disabled; world-border extraction likewise resumes its vanilla path.
- Two limitations are inherently non-retroactive: particles rejected at creation cannot be recreated after unselecting their type, and entity spawn packets already discarded by **Drop Spawn Packets** cannot be recovered until the server sends another spawn/update. The latter remains disabled by default and is explicitly warned in the option description.
- Freecam's source targets were checked against local Minecraft 26.3 bytecode: `MouseHandler.turnPlayer(double)` applies sensitivity, smooth-camera, scope, and invert settings before calling `LocalPlayer.turn(double,double)`. The new redirect routes those processed values to the camera and only invokes the body turn when configured. HUD targets (`extractItemHotbar`, health/vehicle/air, contextual bars) were checked against the same compiled class.
- Auto Crafting selection uses MaLiLib's `GuiStringListSelection` and its built-in search widget; the subclass now supplies current localized labels and stable registry IDs as filter text. C keeps result stacks; V issues a full-stack `ContainerInput.THROW`. Tasks stop on an unavailable screen, no valid target, or no remaining craftable matching recipe. This was source/build reviewed only.
- SafeWalk's random trigger is retained until the approach clears its configured look-ahead area. Soft Sneak preserves the former support-distance path; Edge Auto Sneak checks a leading-foot strip and adds the normal sneak field to keyboard input. Freecam and SafeWalk do not both write movement state.

### Build and static checks for this round

| Check | Command/inspection | Result |
|---|---|---|
| Intermediate feature builds | `.\gradlew.bat build --offline` (twice) | Passed after the Freecam/SafeWalk/Auto Crafting logic batch and again after adding the Freecam HUD controls and searchable selector. |
| Final full rebuild | `.\gradlew.bat build --offline --rerun-tasks` | Passed; Java and resources were rebuilt from source. `test` is `NO-SOURCE`. |
| GUI target API review | `javap` on the local Minecraft 26.3 merged jar | Confirmed the mouse processing path and HUD extraction target methods exist with the expected parameters. |
| NoRender cache review | `javap` on local `LevelExtractor.allChanged()` and renderer/extractor paths | Confirmed the compiled-geometry invalidation flag is raised; visual restoration was not tested in a client. |
| Packaged metadata and dependencies | Parsed final `fabric.mod.json`/Mixin list; `dependencies --configuration runtimeClasspath --offline` | Valid `lyriya_mod` 0.1.0 metadata for Minecraft 26.3, Loader 0.19.5, Fabric API 0.161.0+26.3 and MaLiLib 0.30.2; all 46 listed Mixin classes are packaged. Runtime graph resolves and has no Meteor or Lotus dependency. |
| Languages | Parsed final packaged `en_us.json` and `zh_cn.json`; checked registered option keys | 423 matching valid keys per language; all 114 literal registered config option name/pretty-name/comment groups are covered. |
| Artifact | `build/libs/lyriya-mod-0.1.0.jar` | 843,456 bytes; generated 2026-10-18 06:09:24 UTC. |
| In-game testing | Not run | No `runClient`, client launch, or world entry was performed. All changes remain subject to user gameplay validation. |
# Lyriya Mod 0.1.3 — user gameplay checks

These checks are for a Minecraft 26.3 client with the project's configured Fabric Loader, Fabric API, and MaLiLib versions. They are manual gameplay checks; the build described below does not replace them.

## Auto Crafting: spruce logs to spruce planks

1. Enable Auto Crafting and add spruce planks from **Auto Crafting → Add items**. Search must be visibly inside the popup listing candidate items, and each row should show a vanilla item icon beside its localized name. Verify localized-name and `minecraft:spruce_planks` search, case-insensitive live filtering, no-results feedback, clearing, and multi-selection. Adding must preserve earlier whitelist entries.
2. Set **Open Screen** mode. Put a stack of spruce logs in the player inventory, leave at least one genuinely empty player inventory slot, and open a normal crafting table. No C/V key is needed to start this mode. Verify **Drop Results When Auto-Started** defaults off and separately test it on and off.
3. Verify the recipe book's spruce-planks recipe is requested, the result appears and is shift-clicked into inventory, then the workflow waits for slot synchronization before continuing. Confirm inputs are consumed and unrelated inventory stacks remain untouched. The prior false “no inventory space” condition used `getSlotWithRemainingSpace` alone; 26.3 returns -1 when no matching partial stack exists even if an empty slot is available.
4. Set **Hotkey** mode, open the crafting table, and press C. C keeps results and follows Lotus's rule that at least one player inventory slot is empty. Press C again to stop; restart with V, which throws only the crafting result stack and does not require inventory capacity. Verify continuous crafting and no duplicate recipe request during synchronization. Focus the recipe-book search box and confirm C/V type normally there.
5. Place a valid result in the output slot before starting; it should be handled before choosing a recipe-book target. Close the table, exhaust materials, and test a full inventory. Each unable-to-continue state should give a specific localized reason, and V must never throw unrelated inventory items.

## Freecam

1. Enable Freecam and **Control Keys**. Move the camera with WASD and jump. Press the currently configured vanilla sneak key and verify the player receives normal sneak input while the camera also descends; there must be no independent camera-descend binding.
2. With **Render Player Body** off, confirm the body stays hidden. Turn it on, move the camera away, and confirm the local player model is rendered at its original position. Verify **Render Hands** separately.
3. Test terrain from multiple directions with Freecam both with and without Sodium (record the exact Sodium version). Check turning around, camera motion, player-body rendering, and chunk visibility. A static build does not establish that white-chunk rendering is resolved.
4. Open a GUI, release held keys, disable Freecam, leave the world, and disconnect. Confirm camera input and normal player input restore; verify ray picking and block interaction from the camera.

## Configuration selector and category layout

1. In both English and Simplified Chinese, open **Auto Crafting → Add items**. Search inside the item-selection window by localized name and registry ID, verify live filtering, case-insensitive text, no-result feedback, clearing, multi-selection, and adding to the existing whitelist.
2. Resize to a normal wide window and a narrow window. Verify categories are all directly reachable in a compact wrapping layout with no page controls, overlaps, clipped click targets, or list content covered by the navigation rows.

## Freecam terrain check

1. In the actual Sodium setup, record the installed Sodium version. Compare Freecam with **Render Player Body** off and on.
2. Move continuously and look in several directions, including backwards. Note whether white terrain regions appear and whether they clear after disabling Freecam.
3. If practical, repeat without Sodium to separate the local-player entity inclusion hook from terrain renderer behavior. This build does not establish that the white-chunk symptom is fixed.

## Build and static checks for 0.1.3

The source build is run with `.\gradlew.bat build --offline`. Inspect the generated `build/libs/lyriya-mod-0.1.3.jar` for `fabric.mod.json`, the 0.1.3 version, configured dependencies, `FreecamPlayerBodyMixin`, the selector row class, both locale JSON resources, and new config translations. Do not use `runClient` for these checks.

No game client or world is started as part of the automated delivery. Record any observed behavior and the exact test conditions above when reporting gameplay results.


## 0.1.1 delivery checks (2026-10-18)

| Check | Command/inspection | Result |
|---|---|---|
| Final source build | `.\gradlew.bat build --offline --rerun-tasks` | Passed. Gradle compiled the current sources; `test` is `NO-SOURCE`. No Minecraft client was launched. |
| Runtime dependency graph | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed. Uses Fabric Loader 0.19.5, Fabric API 0.161.0+26.3, and MaLiLib 0.30.2. No Meteor Client or Meteor Lotus dependency appears. |
| Final JAR | `build/libs/lyriya-mod-0.1.1.jar` | 853,499 bytes; file timestamp 2026-10-18 15:47:34 +08:00. Metadata parses as `lyriya_mod` 0.1.1 for Minecraft 26.3 and declares MaLiLib `>= 0.30.2`. |
| Mixin manifest | Parsed `lyriya.mixins.json` and JAR entries | All 48 declared client Mixin classes are present, including `FreecamPlayerBodyMixin`. Static `javap` inspection confirms 26.3 `LevelExtractor.extractVisibleEntities(Camera, Frustum, DeltaTracker, LevelRenderState)` and `Camera.isDetached()` exist; the injection applies only at the local camera-entity exclusion check. This is not an application/startup test. |
| Refmap/AP | Inspected JAR and tried Loom 1.17 Mixin AP configuration | No refmap is packaged. Loom reports this 26.3 environment as non-obfuscated; explicit Mojang mappings are rejected, while the legacy Mixin AP cannot find `mappingsFinal`. The experiment was reverted, preserving the existing build setup. The `No refMap loaded` warning and actual Mixin application remain unverified in-game. |
| Locale and bundled code | Parsed packaged `en_us.json` and `zh_cn.json`; searched JAR entries | Both are valid JSON with 437 matching keys. New Freecam and Auto Crafting entries exist in both locales. No Meteor, Lotus, or MaLiLib implementation classes are bundled. |

## Lyriya Mod 0.1.4 — user gameplay checks

Do not use `runClient`; these checks are for a manually launched Minecraft 26.3 client. The build and static inspection do not establish runtime behavior.

### Freecam terrain visibility

1. Enable Freecam and look forward, then rotate to look behind the player. Repeat while the camera is moving, including across chunk boundaries. Check whether the formerly white terrain regions appear.
2. Repeat with Render Player Body off and on. The camera/frustum change is independent of the player body extraction option.
3. Disable Freecam and confirm ordinary terrain rendering and the player's camera state return to normal.

### Auto Crafting whitelist editor

1. Open Lyriya configuration → Auto Crafting → **Edit Item Whitelist**. Confirm the editor shows existing saved entries and preserves them when adding a new item.
2. Add `minecraft:obsidian`; confirm the entry appears with a localized display name and registry ID. Add `黑曜石` in Chinese or `Obsidian` in English; confirm it resolves to the same stable ID and duplicate addition is reported without clearing the list.
3. Add `深板岩,c` in Chinese; confirm all current-language item names containing the text are added. Try an unsupported suffix such as `,x` and confirm the editor explains the accepted `,c` filter.
4. Try an empty input, a missing item ID, and `#minecraft:dragon_immune`. Confirm each is rejected with the corresponding localized message. Vanilla 26.3 packages `dragon_immune` as a **block** tag, not an item tag; a datapack may separately define an item tag by that name.
5. Add an item tag known to exist in the current world, then start Auto Crafting and verify recipes with items from that tag can be selected. Remove selected entries and check the updated whitelist persists after relaunch.

### 0.1.4 static/build checks

`gradle.properties` sets the Mod version to `0.1.4`; run ` .\gradlew.bat build --offline` and inspect `build/libs/lyriya-mod-0.1.4.jar`. Verify `fabric.mod.json`, the new editor/resolver and tag update Mixin are packaged, both language JSON files parse and contain the same new keys, and no Meteor/Lotus classes are bundled. No Minecraft client was launched for this release.
| Game behavior | Not run | No `runClient`, Minecraft launch, or world entry was performed. Follow the manual tests above. |

## 0.1.2 build and static inspection (2026-10-18)

| Check | Command or inspection | Result |
|---|---|---|
| Offline build | `$env:GRADLE_USER_HOME = '.gradle-home'; .\gradlew.bat build --offline` | Passed with Loom 1.17.21; `compileJava`, resources, jar, and `build` succeeded. The initial Wrapper attempt using the default `C:\Users\kerin\.gradle` could not open its lock file due sandbox access, so the command used the existing repository-local Gradle cache. No client task was run. |
| Runtime dependencies | `.\gradlew.bat dependencies --configuration runtimeClasspath --offline` | Passed; includes Loader 0.19.5, Fabric API 0.161.0+26.3 and MaLiLib 0.30.2. No Meteor Client or Lotus runtime coordinates. |
| Final JAR | `build/libs/lyriya-mod-0.1.2.jar` | 853,313 bytes; generated 2026-10-18 16:48:13 +08:00. Packaged metadata is `lyriya_mod` 0.1.2, Minecraft 26.3, with MaLiLib `>= 0.30.2`. |
| Mixin manifest | Parsed JAR's `lyriya.mixins.json` and entries | All 48 declared client Mixin classes are present; no missing entries. No refmap is packaged or named in the Mixin config. 26.3 uses the configured Mojang named classes in this non-obfuscated Loom environment; this does not confirm runtime application. |
| Bundled implementation check | Inspected JAR entries | No Meteor/Lotus classes or runtime dependencies are included. Both English and Simplified Chinese locale JSON resources are packaged. |
| Sodium scope | Local installation inspection and pinned source review | This machine's `.minecraft/mods` directory has no Sodium JAR and the available log predates this test; the user's Sodium version is unknown. The pinned 0.9.2 source review shows its `LevelExtractor` mixin does not target `extractVisibleEntities`, where Lyriya changes only the local camera-entity exclusion. Chunk rendering still requires the user's in-game comparison. |
| In-game test | Not run | No `runClient`, client launch, world entry, or manual gameplay testing was performed. See the 0.1.2 steps above. |
