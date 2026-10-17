# NoRender reference compatibility audit

Reference: Meteor Client `NoRender.java`, commit [`9a9600699248bb4f34be5f05b93e42ff5c695e22`](https://github.com/MeteorDevelopment/meteor-client/blob/9a9600699248bb4f34be5f05b93e42ff5c695e22/src/main/java/meteordevelopment/meteorclient/systems/modules/render/NoRender.java). The checkout is GPL-3.0 (`LICENSE`). This audit records the actual settings declared by that file, including its enum and list settings. Lyriya does not link Meteor runtime classes.

## Status meanings

- **Implemented/build checked** means a Lyriya option is registered and connected to a 26.3 Mixin or client behavior hook; Java compilation succeeds. It does not mean Mixin application or visual behavior was tested in a running client.
- **Not exposed** means no Lyriya setting is shown, so the UI does not imply that the behavior exists.

## Overlay

| Meteor setting | Lyriya status | Target behavior / note |
|---|---|---|
| Nether Portal Overlay | Implemented/build checked | `Hud.extractPortalOverlay` cancellation |
| Spyglass Overlay | Implemented/build checked | `Hud.extractSpyglassOverlay` cancellation |
| Nausea | Implemented/build checked | `Hud.extractConfusionOverlay` cancellation |
| Pumpkin Overlay | Implemented/build checked | Pumpkin texture alpha is set to zero in `Hud.extractCameraOverlays` |
| Powdered Snow Overlay | Implemented/build checked | Powdered-snow texture alpha is set to zero in `Hud.extractCameraOverlays` |
| Fire Overlay | Implemented/build checked | `ScreenEffectRenderer.submitFire` cancellation |
| Liquid Overlay | Implemented/build checked | `ScreenEffectRenderer.submitWater` cancellation |
| In-wall Overlay | Implemented/build checked | `ScreenEffectRenderer.submitBlockSprite` cancellation; this target draws the in-wall effect |
| Vignette | Implemented/build checked | `Hud.extractVignette` cancellation |
| GUI Background | Implemented/build checked | `Screen.extractBackground` cancellation |
| Totem Animation | Implemented/build checked | Cancels `LocalPlayer.displayItemActivation` only for a Totem of Undying |
| Eating Particles | Implemented/build checked | Cancels `LivingEntity.spawnItemParticles` only for stacks carrying the Food component. |
| Enchantment Glint | Implemented/build checked | Replaces only the item render submit's foil type with `NONE`; other item rendering continues. |

## HUD

| Meteor setting | Lyriya status | Target behavior / note |
|---|---|---|
| Boss Bar | Implemented/build checked | `BossHealthOverlay.extractRenderState` cancellation |
| Scoreboard | Implemented/build checked | `Hud.extractScoreboardSidebar` and its display helper cancellation |
| Crosshair | Implemented/build checked | `Hud.extractCrosshair` cancellation |
| Title | Implemented/build checked | `Hud.extractTitle` cancellation |
| Held Item Name | Implemented/build checked | `Hud.extractSelectedItemName` cancellation |
| Obfuscation | Implemented/build checked | Changes the `Style.isObfuscated()` expression in `Font.getGlyph`; text remains visible without obfuscation. |
| Potion Icons | Implemented/build checked | `Hud.extractEffects` cancellation |
| Message Signature Indicator | Implemented/build checked | Changes only `GuiMessage.Line.tag()` consumed by the chat line renderer to `null`. |

## World

| Meteor setting | Lyriya status | Target behavior / note |
|---|---|---|
| Weather | Implemented/build checked | Wraps `LevelExtractor.extract` weather extraction and clears extracted weather intensity |
| World Border | Implemented/build checked | Wraps world-border extraction and sets its render-state alpha to zero |
| Blindness | Implemented/build checked | `MobEffectFogEnvironment.isApplicable` filters only blindness fog; effect gameplay remains enabled. |
| Darkness | Implemented/build checked | `MobEffectFogEnvironment.isApplicable` filters only darkness fog; effect gameplay remains enabled. |
| Fog | Implemented/build checked | `FogRenderer.getBuffer` disables the general fog buffer; dimensions/shaders may look different while enabled. |
| Enchantment Table Book | Implemented/build checked | Wraps only the book model submission in `EnchantTableRenderer.submit` |
| Sign Text | Implemented/build checked | Cancels only `AbstractSignRenderer.submitSignText(SignRenderState, PoseStack, SubmitNodeCollector, SignText)` at method entry; sign boards and their other rendering remain untouched. Can obscure player-authored information. |
| Block Break Overlay | Implemented/build checked | Cancels `LevelExtractor.extractBlockDestroyAnimation` |
| Beacon Beams | Implemented/build checked | Cancels `BeaconRenderer.submitBeaconBeam` |
| Falling Blocks | Implemented/build checked | Filters `FallingBlockEntity` in `EntityRenderer.shouldRender` without changing entity simulation |
| Cave Culling | Implemented/build checked | Cancels `VisGraph.setOpaque`; the setting and master enable toggle invalidate chunk render data, including after config load. The option warns of performance cost. |
| Map Markers | Implemented/build checked | `MapRenderer.render` receives an empty decoration list |
| Map Contents | Implemented/build checked | Cancels `MapRenderer.render` |
| Banner Render Mode (`Everything`, `Pillar`, `None`) | Implemented/build checked | `BannerRenderer.submit` preserves vanilla rendering, cancels it, or submits the plain base/pole model according to the selected mode. |
| Firework Explosions | Implemented/build checked | Cancels extraction for the firework spark and overlay particle classes |
| Particle Type List | Implemented/build checked | World-tab Add/Remove dialogs show localized particle names and persist registry keys; both `ClientLevel.addParticle` overloads drop selected client particles. This affects client particle creation, not server state. |
| Barrier Invisibility | Implemented/build checked | `ClientLevel.animateTick` substitutes Barrier into the target block's client display-tick call, exposing nearby barrier particles through the vanilla held-barrier behavior. |
| Texture Rotations | Implemented/build checked | `BlockBehaviour.getSeed` and `BlockStateBase.getOffset` return stable values. This changes model randomization and offsets; existing chunk render data may need rebuilding to fully reflect the setting. |
| Block Entity Render List | Implemented/build checked | World-tab Add/Remove dialogs use localized names for block types; the dispatcher filters matching `BlockEntityRenderState.blockState` before submission. |

## Entity

| Meteor setting | Lyriya status | Target behavior / note |
|---|---|---|
| Selected Entity List | Implemented/build checked | Localized entity-type picker; `EntityRenderer.shouldRender` filters selected types |
| Drop Spawn Packets | Implemented/build checked, risky | Default off. When enabled, selected spawn packets are cancelled before client entity creation. This affects client entity availability and interactions; it is not merely visual. |
| Armor | Implemented/build checked | Cancels `HumanoidArmorLayer.submit` for player avatar render states |
| Invisibility | Implemented/build checked | Changes `Entity.isInvisibleTo` for rendering queries only; does not change effect/gameplay state. |
| Glowing | Implemented/build checked | Returns false from `Entity.isCurrentlyGlowing` while selected; removes the rendering outline. |
| Spawner Entities | Implemented/build checked | Cancels `SpawnerRenderer.submitEntityInSpawner` |
| Dead Entities | Implemented/build checked | Cancels `EntityRenderer.extractRenderState` for living entities in their death animation |
| Nametags | Implemented/build checked | `EntityRenderer.getNameTag` returns no label while enabled |

The entity, block entity, and particle list pickers store registry keys for persistence but present localized/readable names; duplicate labels include the registry key. `Drop Spawn Packets` is explicitly warned and defaults to false.

## Minecraft 26.3 sign-text hook audit

The old `@ModifyConstant(intValue = 4, ordinal = 1)` did not match the 26.3 target: the actual private method has descriptor `(SignRenderState, PoseStack, SubmitNodeCollector, SignText)void` and contains exactly one `ICONST_4` (`ordinal = 0`). Bytecode shows that constant is used in `4 * textLineHeight / 2` to vertically center four sign lines; changing it does not disable text. The replacement uses a cancellable `@Inject` at `HEAD` with the full method descriptor, cancelling only text submission for the front or back side currently being rendered. `defaultRequire` remains `1`.

The project uses Fabric Loom `1.17.21` with Minecraft `26.3`. Loom's legacy Mixin AP is disabled (the default), so this build does not generate a refmap; `lyriya.mixins.json` intentionally has no `refmap` field and the JAR has no refmap entry. This is expected for the unobfuscated Minecraft versions beginning at 26.1: current Loom remaps Mixin targets in place when remapping is needed. Therefore `No refMap loaded` is expected diagnostic context here, while the actual zero-target cause was the nonexistent second matching constant. See the [Fabric Loom options](https://docs.fabricmc.net/develop/loom/options) and [Fabric Mixin obfuscation notes](https://wiki.fabricmc.net/drafts:mixin_obfuscation).

The reference for the NoRender option list is Meteor Client commit `9a9600699248bb4f34be5f05b93e42ff5c695e22`; it is a behavior/setting comparison, not the source of this sign-text injector. The previous constant hook was Lyriya's local implementation and had no separately pinned Minecraft target version. This replacement is checked against the actual 26.3 compile dependency; no Meteor code or runtime API is used. Static inspection and compilation do not confirm runtime Mixin application; client startup remains untested by design.

All 48 audited reference settings now have corresponding visible controls and behavior, including list editors. Compilation does not verify Mixin application or the visual correctness of the Banner Pillar mode. No in-game behavior was tested for this delivery.
