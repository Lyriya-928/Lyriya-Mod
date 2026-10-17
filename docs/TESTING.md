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
