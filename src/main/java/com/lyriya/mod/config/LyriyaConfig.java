package com.lyriya.mod.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.config.ConfigUtils;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.IConfigHandler;
import fi.dy.masa.malilib.config.options.ConfigBooleanHotkeyed;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigDouble;
import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.config.options.ConfigInteger;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.config.options.ConfigStringList;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import fi.dy.masa.malilib.registry.Registry;
import fi.dy.masa.malilib.util.FileUtils;
import fi.dy.masa.malilib.util.data.ModInfo;
import fi.dy.masa.malilib.util.data.json.JsonUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class LyriyaConfig implements IConfigHandler, IInitializationHandler, IKeybindProvider {
    private static final String MOD_ID = "lyriya_mod";
    private static final String GENERAL_KEY = "lyriya_mod.config.general";
    private static final String AUTO_HELMET_KEY = "lyriya_mod.config.auto_helmet";
    private static final String ENTITY_LIST_KEY = "lyriya_mod.config.entity_list";
    private static final String PORTALS_KEY = "lyriya_mod.config.portals";
    private static final String CONFIG_FILE_NAME = "lyriya-mod.json";
    private static final String HOTKEY_DEFAULTS_MIGRATED_KEY = "lyriyaHotkeyDefaultsMigrated";

    public static final ConfigHotkey OPEN_CONFIG = new ConfigHotkey(
        "openConfig", "BACKSLASH", "Open Lyriya Mod configuration"
    ).apply(GENERAL_KEY);
    public static final ConfigBoolean DEBUG_LOGGING = new ConfigBoolean(
        "debugLogging", false, "Log feature state changes for diagnosis"
    ).apply(GENERAL_KEY);
    public static final ConfigBoolean DEBUG_MESSAGES = new ConfigBoolean(
        "debugMessages", false, "Show feature state changes on screen for diagnosis"
    ).apply(GENERAL_KEY);
    public static final ConfigBoolean TOGGLE_NOTIFICATIONS = new ConfigBoolean(
        "toggleNotifications", true, "Show a brief HUD message when a feature is manually enabled or disabled"
    ).apply(GENERAL_KEY);

    public static final ConfigBooleanHotkeyed AUTO_HELMET_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Enable automatic golden helmet equipment"
    ).apply(AUTO_HELMET_KEY);
    public static final ConfigInteger AUTO_HELMET_RANGE = new ConfigInteger(
        "range", 10, 1, 40, "Maximum distance to scan for living Piglins"
    ).apply(AUTO_HELMET_KEY);
    public static final ConfigInteger AUTO_HELMET_DELAY = new ConfigInteger(
        "delayTicks", 5, 0, 64, "Ticks a Piglin must remain nearby before equipping"
    ).apply(AUTO_HELMET_KEY);

    public static final ConfigBooleanHotkeyed ENTITY_LIST_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Show the loaded client entity list"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_INCLUDE_DROPS = new ConfigBoolean(
        "includeDrops", true, "Include dropped item entities in the displayed groups"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_INCLUDE_PLAYERS = new ConfigBoolean(
        "includePlayers", true, "Include player entities in the displayed groups"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_INCLUDE_LIVING = new ConfigBoolean(
        "includeLiving", true, "Include living entities other than players"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_INCLUDE_OTHER = new ConfigBoolean(
        "includeOther", true, "Include entities outside the other categories"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_RIGHT_ALIGNED = new ConfigBoolean(
        "rightAligned", false, "Align the entity list to the right screen edge"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_MARGIN = new ConfigInteger(
        "margin", 8, 0, 100, "Distance from the screen edge in GUI pixels"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_MAX_ROWS = new ConfigInteger(
        "maxRows", 8, 1, 30, "Maximum number of entity groups shown in the HUD"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_BOTTOM_ALIGNED = new ConfigBoolean(
        "bottomAligned", false, "Anchor the list to the bottom instead of the top edge"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_OFFSET_X = new ConfigInteger(
        "offsetX", 0, -500, 500, "Horizontal offset from the selected screen edge in GUI pixels"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_OFFSET_Y = new ConfigInteger(
        "offsetY", 0, -500, 500, "Vertical offset from the selected screen edge in GUI pixels"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_SCALE = new ConfigInteger(
        "scalePercent", 100, 50, 200, "HUD text scale as a percentage"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigInteger ENTITY_ROW_SPACING = new ConfigInteger(
        "rowSpacing", 1, 0, 8, "Extra GUI pixels between entity list rows"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigBoolean ENTITY_TEXT_SHADOW = new ConfigBoolean(
        "textShadow", true, "Draw a shadow behind entity list text"
    ).apply(ENTITY_LIST_KEY);
    public static final ConfigColor ENTITY_COLOR_DROPS = new ConfigColor("colorDrops", "#FFFFD166", "Color for dropped item rows").apply(ENTITY_LIST_KEY);
    public static final ConfigColor ENTITY_COLOR_PLAYERS = new ConfigColor("colorPlayers", "#FF72D6FF", "Color for player rows").apply(ENTITY_LIST_KEY);
    public static final ConfigColor ENTITY_COLOR_HOSTILE = new ConfigColor("colorHostile", "#FFFF6B6B", "Color for hostile mob rows").apply(ENTITY_LIST_KEY);
    public static final ConfigColor ENTITY_COLOR_PASSIVE = new ConfigColor("colorPassive", "#FF8CE99A", "Color for passive mob rows").apply(ENTITY_LIST_KEY);
    public static final ConfigColor ENTITY_COLOR_OTHER = new ConfigColor("colorOther", "#FFE5E5E5", "Color for other entity rows").apply(ENTITY_LIST_KEY);

    public static final ConfigBooleanHotkeyed PORTALS_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Keep screens open during portal effects"
    ).apply(PORTALS_KEY);

    public static final ConfigBooleanHotkeyed GUI_MOVE_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Enable movement in selected screens"
    ).apply("lyriya_mod.config.gui_move");
    public static final ConfigOptionList GUI_MOVE_SCOPE = new ConfigOptionList(
        "screenScope", GuiMoveScope.INVENTORY, "Which screens allow movement"
    ).apply("lyriya_mod.config.gui_move");
    public static final ConfigBoolean GUI_MOVE_JUMP = new ConfigBoolean(
        "allowJump", false, "Allow jumping while movement in screens is active"
    ).apply("lyriya_mod.config.gui_move");
    public static final ConfigBoolean GUI_MOVE_SNEAK = new ConfigBoolean(
        "allowSneak", false, "Allow sneaking while movement in screens is active"
    ).apply("lyriya_mod.config.gui_move");
    public static final ConfigBoolean GUI_MOVE_SPRINT = new ConfigBoolean(
        "allowSprint", false, "Allow sprinting while movement in screens is active"
    ).apply("lyriya_mod.config.gui_move");

    public static final ConfigBooleanHotkeyed AUTO_TOTEM_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Automatically equip a totem in the offhand when conditions require it"
    ).apply("lyriya_mod.config.auto_totem");
    public static final ConfigOptionList AUTO_TOTEM_MODE = new ConfigOptionList(
        "mode", AutoTotemMode.SMART, "Select when the offhand should hold a totem"
    ).apply("lyriya_mod.config.auto_totem");
    public static final ConfigInteger AUTO_TOTEM_HEALTH = new ConfigInteger(
        "healthThreshold", 10, 1, 36, "Hold a totem at or below this health including absorption"
    ).apply("lyriya_mod.config.auto_totem");
    public static final ConfigBoolean AUTO_TOTEM_ELYTRA = new ConfigBoolean(
        "elytraFlight", true, "Hold a totem while flying with an Elytra"
    ).apply("lyriya_mod.config.auto_totem");
    public static final ConfigInteger AUTO_TOTEM_DELAY = new ConfigInteger(
        "swapDelayTicks", 0, 0, 20, "Ticks between inventory swap attempts"
    ).apply("lyriya_mod.config.auto_totem");
    public static final ConfigBooleanHotkeyed SAFE_WALK_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Automatically sneak as you approach unsupported block edges"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigOptionList SAFE_WALK_BEHAVIOR = new ConfigOptionList(
        "behavior", SafeWalkBehavior.SOFT_SNEAK, "Choose the legacy soft edge assistance or normal edge auto-sneaking"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigOptionList SAFE_WALK_MODE = new ConfigOptionList(
        "distanceMode", SafeWalkMode.FIXED, "Choose a fixed trigger distance or a random distance range"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigDouble SAFE_WALK_DISTANCE = new ConfigDouble(
        "triggerDistance", 0.35, 0.05, 1.5, true, "Distance from an unsupported edge that starts automatic sneaking"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigDouble SAFE_WALK_RANDOM_MIN = new ConfigDouble(
        "randomMinDistance", 0.25, 0.05, 1.5, true, "Minimum random edge trigger distance"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigDouble SAFE_WALK_RANDOM_MAX = new ConfigDouble(
        "randomMaxDistance", 0.5, 0.05, 1.5, true, "Maximum random edge trigger distance"
    ).apply("lyriya_mod.config.safe_walk");
    public static final ConfigBooleanHotkeyed AUTO_CRAFTING_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Enable crafting from the configured item whitelist at a crafting table"
    ).apply("lyriya_mod.config.auto_crafting");
    public static final ConfigOptionList AUTO_CRAFTING_MODE = new ConfigOptionList(
        "mode", AutoCraftingMode.OPEN_SCREEN, "Choose automatic start on opening a crafting table or manual hotkey start"
    ).apply("lyriya_mod.config.auto_crafting");
    public static final ConfigBoolean AUTO_CRAFTING_DROP_ON_OPEN_SCREEN = new ConfigBoolean(
        "dropResultsWhenAutoStarted", false, "Drop task results only when crafting starts automatically on opening a table"
    ).apply("lyriya_mod.config.auto_crafting");
    public static final ConfigStringList AUTO_CRAFTING_WHITELIST = new ConfigStringList(
        "itemWhitelist", com.google.common.collect.ImmutableList.of(), "Internal item registry keys for the selected whitelist"
    ).apply("lyriya_mod.config.auto_crafting");
    // Kept hidden so old configs can migrate the previous single target without losing user data.
    public static final ConfigString AUTO_CRAFTING_LEGACY_TARGET = new ConfigString(
        "targetItem", "", "Legacy single target migrated into the item whitelist"
    ).apply("lyriya_mod.config.auto_crafting");
    public static final ConfigInteger AUTO_CRAFTING_DELAY = new ConfigInteger(
        "craftDelayTicks", 4, 1, 40, "Minimum ticks between craft requests"
    ).apply("lyriya_mod.config.auto_crafting");
    public static final fi.dy.masa.malilib.config.options.ConfigHotkey AUTO_CRAFTING_TOGGLE =
        new fi.dy.masa.malilib.config.options.ConfigHotkey("toggleCrafting", "", "Start or continue crafting and keep the output")
            .apply("lyriya_mod.config.auto_crafting");
    public static final fi.dy.masa.malilib.config.options.ConfigHotkey AUTO_CRAFTING_DROP =
        new fi.dy.masa.malilib.config.options.ConfigHotkey("craftDrop", "", "Start or continue crafting and drop the output")
            .apply("lyriya_mod.config.auto_crafting");
    public static final ConfigBooleanHotkeyed NO_RENDER_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Disable selected visual effects"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_PORTAL_OVERLAY = new ConfigBoolean(
        "portalOverlay", false, "Hide the portal transition overlay"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_FIRE_OVERLAY = new ConfigBoolean(
        "fireOverlay", false, "Hide the first person fire overlay"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_SPYGLASS_OVERLAY = new ConfigBoolean("spyglassOverlay", false, "Hide the spyglass overlay").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_PUMPKIN_OVERLAY = new ConfigBoolean("pumpkinOverlay", false, "Hide the pumpkin helmet overlay").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_POWDERED_SNOW_OVERLAY = new ConfigBoolean("powderedSnowOverlay", false, "Hide the powdered snow overlay").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_NAUSEA = new ConfigBoolean("nausea", false, "Hide the nausea overlay").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_LIQUID_OVERLAY = new ConfigBoolean("liquidOverlay", false, "Hide the underwater overlay").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_IN_WALL_OVERLAY = new ConfigBoolean("inWallOverlay", false, "Hide the overlay shown when inside a block").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_VIGNETTE = new ConfigBoolean("vignette", false, "Hide the screen edge vignette").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_BOSS_BAR = new ConfigBoolean("bossBar", false, "Hide boss bars").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_SCOREBOARD = new ConfigBoolean("scoreboard", false, "Hide the scoreboard sidebar").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_CROSSHAIR = new ConfigBoolean("crosshair", false, "Hide the crosshair").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_TITLE = new ConfigBoolean("title", false, "Hide titles and subtitles").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_HELD_ITEM_NAME = new ConfigBoolean("heldItemName", false, "Hide the held item name").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_POTION_ICONS = new ConfigBoolean("potionIcons", false, "Hide status effect icons").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_TOTEM_ANIMATION = new ConfigBoolean("totemAnimation", false, "Hide the item activation animation such as a totem pop").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_GUI_BACKGROUND = new ConfigBoolean("guiBackground", false, "Hide backgrounds drawn behind open screens").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_EATING_PARTICLES = new ConfigBoolean("eatingParticles", false, "Hide particles emitted when eating food").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_OBFUSCATION = new ConfigBoolean("obfuscation", false, "Disable obfuscated text glyphs").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_BARRIER_INVISIBILITY = new ConfigBoolean("barrierInvisibility", false, "Show barrier blocks when holding a barrier item").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_TEXTURE_ROTATIONS = new ConfigBoolean("textureRotations", false, "Disable random block model rotations and offsets").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_CAVE_CULLING = new ConfigBoolean("caveCulling", false, "Disable solid-face chunk occlusion culling").apply("lyriya_mod.config.no_render");
    public static final ConfigOptionList NO_RENDER_BANNER_MODE = new ConfigOptionList(
        "bannerRenderMode", BannerRenderMode.EVERYTHING, "Choose full banner rendering, a plain pole, or no banner model"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_ENCHANTMENT_GLINT = new ConfigBoolean("enchantmentGlint", false, "Hide enchantment glint from item rendering").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_SIGNATURE_INDICATOR = new ConfigBoolean("messageSignatureIndicator", false, "Hide chat message signature indicators").apply("lyriya_mod.config.no_render");
    public static final ConfigStringList NO_RENDER_BLOCK_ENTITY_TYPES = new ConfigStringList(
        "blockEntityTypes", com.google.common.collect.ImmutableList.of(), "Block types whose block entity renderers are hidden"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigStringList NO_RENDER_PARTICLE_TYPES = new ConfigStringList(
        "particleTypes", com.google.common.collect.ImmutableList.of(), "Particle types excluded from client particle creation"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigStringList NO_RENDER_ENTITY_TYPES = new ConfigStringList(
        "entityTypes", com.google.common.collect.ImmutableList.of(), "Entity types excluded from rendering or spawn processing"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_DROP_SPAWN_PACKETS = new ConfigBoolean(
        "dropSpawnPackets", false, "WARNING: discard selected entity spawn packets so those entities are absent from the client world"
    ).apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_NAMETAGS = new ConfigBoolean("nametags", false, "Hide entity name labels").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_WEATHER = new ConfigBoolean("weather", false, "Hide extracted weather visuals").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_WORLD_BORDER = new ConfigBoolean("worldBorder", false, "Hide the client-rendered world border").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_BLOCK_BREAK = new ConfigBoolean("blockBreakOverlay", false, "Hide block breaking progress overlays").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_BEACON_BEAMS = new ConfigBoolean("beaconBeams", false, "Hide beacon beam rendering").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_ENCHANT_BOOK = new ConfigBoolean("enchantmentTableBook", false, "Hide the animated book above enchanting tables").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_SIGN_TEXT = new ConfigBoolean("signText", false, "Hide text rendered on signs").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_FALLING_BLOCKS = new ConfigBoolean("fallingBlocks", false, "Hide falling block entity rendering").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_DEAD_ENTITIES = new ConfigBoolean("deadEntities", false, "Hide entities during their death animation").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_MAP_MARKERS = new ConfigBoolean("mapMarkers", false, "Hide map decorations and markers").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_MAP_CONTENTS = new ConfigBoolean("mapContents", false, "Hide map contents").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_FIREWORKS = new ConfigBoolean("fireworkExplosions", false, "Hide firework explosion particles").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_SPAWNER_ENTITIES = new ConfigBoolean("spawnerEntities", false, "Hide the animated entity inside spawners").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_ARMOR = new ConfigBoolean("armor", false, "Hide player armor rendering").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_BLINDNESS = new ConfigBoolean("blindness", false, "Disable blindness fog while keeping the effect itself").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_DARKNESS = new ConfigBoolean("darkness", false, "Disable darkness fog while keeping the effect itself").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_FOG = new ConfigBoolean("fog", false, "Disable the general world fog buffer").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_INVISIBILITY = new ConfigBoolean("invisibility", false, "Render entities that are invisible to the local player").apply("lyriya_mod.config.no_render");
    public static final ConfigBoolean NO_RENDER_GLOWING = new ConfigBoolean("glowing", false, "Hide entity glowing outlines").apply("lyriya_mod.config.no_render");
    public static final ConfigBooleanHotkeyed FULLBRIGHT_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Improve visibility using the selected brightness mode"
    ).apply("lyriya_mod.config.fullbright");
    public static final ConfigOptionList FULLBRIGHT_MODE = new ConfigOptionList(
        "mode", FullbrightMode.GAMMA, "Select Gamma, Potion, or Luminance brightness"
    ).apply("lyriya_mod.config.fullbright");
    public static final ConfigInteger FULLBRIGHT_POTION_DURATION = new ConfigInteger(
        "potionDurationSeconds", 120, 60, 600, "Target duration of the locally applied Night Vision effect"
    ).apply("lyriya_mod.config.fullbright");
    public static final ConfigOptionList FULLBRIGHT_LIGHT_LAYER = new ConfigOptionList(
        "lightType", LightLayerOption.BLOCK, "Light layer affected by Luminance mode"
    ).apply("lyriya_mod.config.fullbright");
    public static final ConfigInteger FULLBRIGHT_MIN_LIGHT = new ConfigInteger(
        "minimumLightLevel", 8, 0, 15, "Minimum queried light level in Luminance mode"
    ).apply("lyriya_mod.config.fullbright");
    public static final ConfigBooleanHotkeyed FREECAM_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Move the camera independently from the player position"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigDouble FREECAM_SPEED = new ConfigDouble(
        "speed", 1.0, 0.1, 5.0, true, "Camera movement speed in blocks per tick"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_SHOW_HANDS = new ConfigBoolean(
        "showHands", true, "Render first-person hands while freecam is active"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_RENDER_PLAYER_BODY = new ConfigBoolean(
        "renderPlayerBody", false, "Render the local player body at its real position while freecam is active"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_CONTROL_KEYS = new ConfigBoolean(
        "controlKeys", true, "Use movement keys to control the free camera; when enabled, camera input takes priority over player movement"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_PLAYER_BODY_ROTATION = new ConfigBoolean(
        "playerBodyRotation", false, "Also apply native mouse turn input to the stationary player body"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_PLAYER_BODY_MOVEMENT = new ConfigBoolean(
        "playerBodyMovement", false, "Allow movement keys to control the player only when Control Keys is disabled"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_RENDER_HOTBAR = new ConfigBoolean(
        "renderHotbar", true, "Show the hotbar while free camera is active"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBoolean FREECAM_RENDER_STATUS_BARS = new ConfigBoolean(
        "renderStatusBars", true, "Show health, armor, hunger and related status bars while free camera is active"
    ).apply("lyriya_mod.config.freecam");
    public static final ConfigBooleanHotkeyed NO_GHOST_ENABLED = new ConfigBooleanHotkeyed(
        "enabled", false, "", "Reduce client prediction for block break and placement actions"
    ).apply("lyriya_mod.config.no_ghost_blocks");
    public static final ConfigBoolean NO_GHOST_BREAKING = new ConfigBoolean(
        "breaking", true, "Apply reduced client prediction while breaking blocks"
    ).apply("lyriya_mod.config.no_ghost_blocks");
    public static final ConfigBoolean NO_GHOST_PLACING = new ConfigBoolean(
        "placing", true, "Apply reduced client prediction while placing blocks"
    ).apply("lyriya_mod.config.no_ghost_blocks");

    public static final List<IConfigBase> GENERAL_OPTIONS = List.of(OPEN_CONFIG, DEBUG_LOGGING, DEBUG_MESSAGES, TOGGLE_NOTIFICATIONS);
    public static final List<IConfigBase> AUTO_HELMET_OPTIONS = List.of(
        AUTO_HELMET_ENABLED, AUTO_HELMET_RANGE, AUTO_HELMET_DELAY
    );
    public static final List<IConfigBase> ENTITY_LIST_OPTIONS = List.of(
        ENTITY_LIST_ENABLED, ENTITY_INCLUDE_DROPS, ENTITY_INCLUDE_PLAYERS,
        ENTITY_INCLUDE_LIVING, ENTITY_INCLUDE_OTHER, ENTITY_RIGHT_ALIGNED,
        ENTITY_MARGIN, ENTITY_MAX_ROWS, ENTITY_BOTTOM_ALIGNED, ENTITY_OFFSET_X, ENTITY_OFFSET_Y,
        ENTITY_SCALE, ENTITY_ROW_SPACING, ENTITY_TEXT_SHADOW, ENTITY_COLOR_DROPS,
        ENTITY_COLOR_PLAYERS, ENTITY_COLOR_HOSTILE, ENTITY_COLOR_PASSIVE, ENTITY_COLOR_OTHER
    );
    public static final List<IConfigBase> PORTALS_OPTIONS = List.of(PORTALS_ENABLED);
    public static final List<IConfigBase> GUI_MOVE_OPTIONS = List.of(
        GUI_MOVE_ENABLED, GUI_MOVE_SCOPE, GUI_MOVE_JUMP, GUI_MOVE_SNEAK, GUI_MOVE_SPRINT
    );
    public static final List<IConfigBase> AUTO_TOTEM_OPTIONS = List.of(
        AUTO_TOTEM_ENABLED, AUTO_TOTEM_MODE, AUTO_TOTEM_HEALTH, AUTO_TOTEM_ELYTRA, AUTO_TOTEM_DELAY
    );
    public static final List<IConfigBase> SAFE_WALK_OPTIONS = List.of(
        SAFE_WALK_ENABLED, SAFE_WALK_BEHAVIOR, SAFE_WALK_MODE, SAFE_WALK_DISTANCE, SAFE_WALK_RANDOM_MIN, SAFE_WALK_RANDOM_MAX
    );
    public static final List<IConfigBase> AUTO_CRAFTING_OPTIONS = List.of(
        AUTO_CRAFTING_ENABLED, AUTO_CRAFTING_MODE, AUTO_CRAFTING_DROP_ON_OPEN_SCREEN,
        AUTO_CRAFTING_DELAY, AUTO_CRAFTING_TOGGLE, AUTO_CRAFTING_DROP
    );
    private static final List<IConfigBase> AUTO_CRAFTING_PERSISTED_OPTIONS = List.of(
        AUTO_CRAFTING_ENABLED, AUTO_CRAFTING_MODE, AUTO_CRAFTING_DROP_ON_OPEN_SCREEN, AUTO_CRAFTING_WHITELIST,
        AUTO_CRAFTING_LEGACY_TARGET, AUTO_CRAFTING_DELAY
    );
    public static final List<IConfigBase> NO_RENDER_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_PORTAL_OVERLAY, NO_RENDER_FIRE_OVERLAY,
        NO_RENDER_SPYGLASS_OVERLAY, NO_RENDER_PUMPKIN_OVERLAY, NO_RENDER_POWDERED_SNOW_OVERLAY,
        NO_RENDER_NAUSEA, NO_RENDER_LIQUID_OVERLAY,
        NO_RENDER_IN_WALL_OVERLAY, NO_RENDER_VIGNETTE, NO_RENDER_BOSS_BAR,
        NO_RENDER_SCOREBOARD, NO_RENDER_CROSSHAIR, NO_RENDER_TITLE,
        NO_RENDER_HELD_ITEM_NAME, NO_RENDER_POTION_ICONS, NO_RENDER_TOTEM_ANIMATION, NO_RENDER_GUI_BACKGROUND,
        NO_RENDER_DROP_SPAWN_PACKETS, NO_RENDER_NAMETAGS, NO_RENDER_ARMOR, NO_RENDER_WEATHER,
        NO_RENDER_WORLD_BORDER, NO_RENDER_BLOCK_BREAK, NO_RENDER_BEACON_BEAMS,
        NO_RENDER_ENCHANT_BOOK, NO_RENDER_SIGN_TEXT, NO_RENDER_FALLING_BLOCKS, NO_RENDER_DEAD_ENTITIES,
        NO_RENDER_BLINDNESS, NO_RENDER_DARKNESS, NO_RENDER_FOG,
        NO_RENDER_MAP_MARKERS, NO_RENDER_MAP_CONTENTS, NO_RENDER_FIREWORKS, NO_RENDER_SPAWNER_ENTITIES,
        NO_RENDER_EATING_PARTICLES, NO_RENDER_OBFUSCATION, NO_RENDER_BARRIER_INVISIBILITY, NO_RENDER_TEXTURE_ROTATIONS,
        NO_RENDER_CAVE_CULLING, NO_RENDER_BANNER_MODE,
        NO_RENDER_ENCHANTMENT_GLINT, NO_RENDER_SIGNATURE_INDICATOR
    );
    public static final List<IConfigBase> NO_RENDER_WORLD_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_WEATHER, NO_RENDER_WORLD_BORDER, NO_RENDER_BLOCK_BREAK,
        NO_RENDER_BLINDNESS, NO_RENDER_DARKNESS, NO_RENDER_FOG,
        NO_RENDER_BEACON_BEAMS, NO_RENDER_ENCHANT_BOOK, NO_RENDER_SIGN_TEXT,
        NO_RENDER_FALLING_BLOCKS, NO_RENDER_MAP_MARKERS,
        NO_RENDER_MAP_CONTENTS, NO_RENDER_FIREWORKS,
        NO_RENDER_BARRIER_INVISIBILITY, NO_RENDER_TEXTURE_ROTATIONS, NO_RENDER_CAVE_CULLING,
        NO_RENDER_BANNER_MODE
    );
    public static final List<IConfigBase> NO_RENDER_OVERLAY_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_PORTAL_OVERLAY, NO_RENDER_FIRE_OVERLAY, NO_RENDER_SPYGLASS_OVERLAY,
        NO_RENDER_PUMPKIN_OVERLAY, NO_RENDER_POWDERED_SNOW_OVERLAY, NO_RENDER_NAUSEA,
        NO_RENDER_LIQUID_OVERLAY, NO_RENDER_IN_WALL_OVERLAY, NO_RENDER_VIGNETTE,
        NO_RENDER_GUI_BACKGROUND, NO_RENDER_TOTEM_ANIMATION, NO_RENDER_EATING_PARTICLES, NO_RENDER_ENCHANTMENT_GLINT
    );
    public static final List<IConfigBase> NO_RENDER_HUD_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_BOSS_BAR, NO_RENDER_SCOREBOARD, NO_RENDER_CROSSHAIR,
        NO_RENDER_TITLE, NO_RENDER_HELD_ITEM_NAME, NO_RENDER_POTION_ICONS, NO_RENDER_OBFUSCATION, NO_RENDER_SIGNATURE_INDICATOR
    );
    public static final List<IConfigBase> NO_RENDER_ENTITY_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_NAMETAGS, NO_RENDER_DROP_SPAWN_PACKETS, NO_RENDER_ARMOR,
        NO_RENDER_INVISIBILITY, NO_RENDER_GLOWING, NO_RENDER_SPAWNER_ENTITIES, NO_RENDER_DEAD_ENTITIES
    );
    private static final List<IConfigBase> NO_RENDER_PERSISTED_OPTIONS = List.of(
        NO_RENDER_ENABLED, NO_RENDER_PORTAL_OVERLAY, NO_RENDER_FIRE_OVERLAY, NO_RENDER_SPYGLASS_OVERLAY,
        NO_RENDER_PUMPKIN_OVERLAY, NO_RENDER_POWDERED_SNOW_OVERLAY, NO_RENDER_NAUSEA,
        NO_RENDER_LIQUID_OVERLAY, NO_RENDER_IN_WALL_OVERLAY, NO_RENDER_VIGNETTE, NO_RENDER_BOSS_BAR,
        NO_RENDER_SCOREBOARD, NO_RENDER_CROSSHAIR, NO_RENDER_TITLE, NO_RENDER_HELD_ITEM_NAME,
        NO_RENDER_POTION_ICONS, NO_RENDER_TOTEM_ANIMATION, NO_RENDER_GUI_BACKGROUND,
        NO_RENDER_ENTITY_TYPES, NO_RENDER_DROP_SPAWN_PACKETS, NO_RENDER_NAMETAGS,
        NO_RENDER_WEATHER, NO_RENDER_WORLD_BORDER, NO_RENDER_BLOCK_BREAK, NO_RENDER_BEACON_BEAMS,
        NO_RENDER_ENCHANT_BOOK, NO_RENDER_SIGN_TEXT, NO_RENDER_FALLING_BLOCKS, NO_RENDER_DEAD_ENTITIES,
        NO_RENDER_BLINDNESS, NO_RENDER_DARKNESS, NO_RENDER_FOG,
        NO_RENDER_MAP_MARKERS, NO_RENDER_MAP_CONTENTS, NO_RENDER_FIREWORKS, NO_RENDER_SPAWNER_ENTITIES, NO_RENDER_ARMOR,
        NO_RENDER_INVISIBILITY, NO_RENDER_GLOWING,
        NO_RENDER_EATING_PARTICLES, NO_RENDER_OBFUSCATION, NO_RENDER_BARRIER_INVISIBILITY, NO_RENDER_TEXTURE_ROTATIONS,
        NO_RENDER_CAVE_CULLING, NO_RENDER_BANNER_MODE,
        NO_RENDER_ENCHANTMENT_GLINT, NO_RENDER_SIGNATURE_INDICATOR, NO_RENDER_BLOCK_ENTITY_TYPES, NO_RENDER_PARTICLE_TYPES
    );
    public static final List<IConfigBase> FULLBRIGHT_OPTIONS = List.of(
        FULLBRIGHT_ENABLED, FULLBRIGHT_MODE, FULLBRIGHT_POTION_DURATION, FULLBRIGHT_LIGHT_LAYER, FULLBRIGHT_MIN_LIGHT
    );
    public static final List<IConfigBase> FREECAM_OPTIONS = List.of(FREECAM_ENABLED, FREECAM_SPEED,
        FREECAM_CONTROL_KEYS, FREECAM_PLAYER_BODY_ROTATION, FREECAM_PLAYER_BODY_MOVEMENT,
        FREECAM_SHOW_HANDS, FREECAM_RENDER_PLAYER_BODY,
        FREECAM_RENDER_HOTBAR, FREECAM_RENDER_STATUS_BARS);
    public static final List<IConfigBase> NO_GHOST_OPTIONS = List.of(
        NO_GHOST_ENABLED, NO_GHOST_BREAKING, NO_GHOST_PLACING
    );

    private static final List<IHotkey> HOTKEYS = List.of(
        OPEN_CONFIG, AUTO_HELMET_ENABLED, ENTITY_LIST_ENABLED, PORTALS_ENABLED, GUI_MOVE_ENABLED,
        AUTO_TOTEM_ENABLED, SAFE_WALK_ENABLED, AUTO_CRAFTING_ENABLED, AUTO_CRAFTING_TOGGLE, AUTO_CRAFTING_DROP, NO_RENDER_ENABLED,
        FULLBRIGHT_ENABLED, FREECAM_ENABLED, NO_GHOST_ENABLED
    );

    public static final LyriyaConfig INSTANCE = new LyriyaConfig();

    private LyriyaConfig() {
    }

    public static void register() {
        InitializationHandler.getInstance().registerInitializationHandler(INSTANCE);
    }

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(MOD_ID, this);
        Registry.CONFIG_SCREEN.registerConfigScreenFactory(
            new ModInfo(MOD_ID, "Lyriya Mod", LyriyaConfigGui::new)
        );
        InputEventHandler.getKeybindManager().registerKeybindProvider(this);
        OPEN_CONFIG.getKeybind().setCallback((action, key) -> {
            if (action == KeyAction.PRESS) GuiBase.openGui(new LyriyaConfigGui());
            return true;
        });
        setToggleCallback(AUTO_HELMET_ENABLED);
        setToggleCallback(ENTITY_LIST_ENABLED);
        setToggleCallback(PORTALS_ENABLED);
        setToggleCallback(GUI_MOVE_ENABLED);
        setToggleCallback(AUTO_TOTEM_ENABLED);
        setToggleCallback(SAFE_WALK_ENABLED);
        setToggleCallback(AUTO_CRAFTING_ENABLED);
        AUTO_CRAFTING_TOGGLE.getKeybind().setCallback((action, key) -> {
            if (action == KeyAction.PRESS) com.lyriya.mod.feature.impl.AutoCraftingFeature.toggleTask(false);
            return true;
        });
        AUTO_CRAFTING_DROP.getKeybind().setCallback((action, key) -> {
            if (action == KeyAction.PRESS) com.lyriya.mod.feature.impl.AutoCraftingFeature.toggleTask(true);
            return true;
        });
        setToggleCallback(NO_RENDER_ENABLED);
        NO_RENDER_ENABLED.setValueChangeCallback(ignored -> invalidateNoRenderChunkData());
        NO_RENDER_CAVE_CULLING.setValueChangeCallback(ignored -> invalidateNoRenderChunkData());
        NO_RENDER_TEXTURE_ROTATIONS.setValueChangeCallback(ignored -> invalidateNoRenderChunkData());
        setToggleCallback(FULLBRIGHT_ENABLED);
        setToggleCallback(FREECAM_ENABLED);
        setToggleCallback(NO_GHOST_ENABLED);
    }

    private void setToggleCallback(ConfigBooleanHotkeyed option) {
        option.getKeybind().setCallback(new IHotkeyCallback() {
            @Override
            public boolean onKeyAction(KeyAction action, IKeybind key) {
                if (action == KeyAction.PRESS) {
                    option.toggleBooleanValue();
                    save();
                }
                return true;
            }
        });
    }

    private static void invalidateNoRenderChunkData() {
        net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        if (client.levelRenderer != null) client.levelExtractor.allChanged();
    }

    @Override
    public void addKeysToMap(IKeybindManager manager) {
        for (IHotkey hotkey : HOTKEYS) manager.addKeybindToMap(hotkey.getKeybind());
    }

    @Override
    public void addHotkeys(IKeybindManager manager) {
        manager.addHotkeysForCategory("Lyriya Mod", "lyriya_mod.hotkeys.general", List.of(OPEN_CONFIG));
        manager.addHotkeysForCategory("Lyriya Mod", "lyriya_mod.hotkeys.features", List.of(
            AUTO_HELMET_ENABLED, ENTITY_LIST_ENABLED, PORTALS_ENABLED, GUI_MOVE_ENABLED,
            AUTO_TOTEM_ENABLED, SAFE_WALK_ENABLED, AUTO_CRAFTING_ENABLED, NO_RENDER_ENABLED,
            FULLBRIGHT_ENABLED, FREECAM_ENABLED, NO_GHOST_ENABLED
        ));
        manager.addHotkeysForCategory("Lyriya Mod", "lyriya_mod.hotkeys.auto_crafting", List.of(AUTO_CRAFTING_TOGGLE, AUTO_CRAFTING_DROP));
    }

    @Override
    public void load() {
        Path file = configFile();
        if (!Files.exists(file) || !Files.isReadable(file)) return;

        JsonElement element = JsonUtils.parseJsonFile(file);
        if (element == null || !element.isJsonObject()) return;
        JsonObject root = element.getAsJsonObject();

        if (root.has("enabled") || root.has("options")) {
            ModConfig.migrateLegacy(root);
            save();
            return;
        }

        ConfigUtils.readConfigBase(root, "General", GENERAL_OPTIONS);
        ConfigUtils.readConfigBase(root, "AutoHelmet", AUTO_HELMET_OPTIONS);
        ConfigUtils.readConfigBase(root, "EntityList", ENTITY_LIST_OPTIONS);
        ConfigUtils.readConfigBase(root, "Portals", PORTALS_OPTIONS);
        ConfigUtils.readConfigBase(root, "GuiMove", GUI_MOVE_OPTIONS);
        ConfigUtils.readConfigBase(root, "AutoTotem", AUTO_TOTEM_OPTIONS);
        ConfigUtils.readConfigBase(root, "SafeWalk", SAFE_WALK_OPTIONS);
        ConfigUtils.readConfigBase(root, "AutoCrafting", AUTO_CRAFTING_PERSISTED_OPTIONS);
        ConfigUtils.readHotkeys(root, "Hotkeys", List.of(AUTO_CRAFTING_TOGGLE, AUTO_CRAFTING_DROP));
        ConfigUtils.readConfigBase(root, "NoRender", NO_RENDER_PERSISTED_OPTIONS);
        invalidateNoRenderChunkData();
        ConfigUtils.readConfigBase(root, "Fullbright", FULLBRIGHT_OPTIONS);
        ConfigUtils.readConfigBase(root, "Freecam", FREECAM_OPTIONS);
        ConfigUtils.readConfigBase(root, "NoGhostBlocks", NO_GHOST_OPTIONS);
        boolean changed = migrateLegacyOpenKey(root);
        changed |= migrateLegacyModuleHotkeys(root);
        JsonElement legacyTarget = root.has("AutoCrafting") && root.get("AutoCrafting").isJsonObject()
            ? root.getAsJsonObject("AutoCrafting").get("targetItem") : null;
        if (legacyTarget != null && legacyTarget.isJsonPrimitive()) {
            String id = legacyTarget.getAsString().trim();
            if (isValidItemId(id) && !AUTO_CRAFTING_WHITELIST.getStrings().contains(id)) {
                java.util.ArrayList<String> migrated = new java.util.ArrayList<>(AUTO_CRAFTING_WHITELIST.getStrings());
                migrated.add(id);
                AUTO_CRAFTING_WHITELIST.setStrings(migrated);
                changed = true;
            }
            if (!id.isEmpty()) {
                AUTO_CRAFTING_LEGACY_TARGET.setValueFromString("");
                changed = true;
            }
        }
        if (changed) save();
    }

    private static boolean migrateLegacyOpenKey(JsonObject root) {
        JsonElement generalElement = root.get("General");
        if (generalElement == null || !generalElement.isJsonObject()) return false;
        JsonElement keyElement = generalElement.getAsJsonObject().get("openConfig");
        if (keyElement == null) return false;
        String stored = null;
        if (keyElement.isJsonPrimitive() && keyElement.getAsJsonPrimitive().isString()) {
            stored = keyElement.getAsString();
        } else if (keyElement.isJsonObject()) {
            JsonElement keys = keyElement.getAsJsonObject().get("keys");
            if (keys != null && keys.isJsonPrimitive() && keys.getAsJsonPrimitive().isString()) {
                stored = keys.getAsString();
            }
        }
        if ("L,Y".equals(stored)) {
            OPEN_CONFIG.getKeybind().setValueFromString("BACKSLASH");
            return true;
        }
        return false;
    }

    /** Clears only values matching the old built-in defaults once; all custom keybinds survive. */
    private static boolean migrateLegacyModuleHotkeys(JsonObject root) {
        JsonElement marker = root.get(HOTKEY_DEFAULTS_MIGRATED_KEY);
        if (marker != null && marker.isJsonPrimitive() && marker.getAsJsonPrimitive().isBoolean()
            && marker.getAsBoolean()) return false;

        boolean changed = false;
        changed |= clearFormerDefault(root, "AutoHelmet", "enabled", "H", AUTO_HELMET_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "EntityList", "enabled", "J", ENTITY_LIST_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "Portals", "enabled", "P", PORTALS_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "AutoTotem", "enabled", "O", AUTO_TOTEM_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "SafeWalk", "enabled", "K", SAFE_WALK_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "NoRender", "enabled", "N", NO_RENDER_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "Fullbright", "enabled", "F7", FULLBRIGHT_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "Freecam", "enabled", "F8", FREECAM_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "NoGhostBlocks", "enabled", "F9", NO_GHOST_ENABLED.getKeybind());
        changed |= clearFormerDefault(root, "Hotkeys", "toggleCrafting", "C", AUTO_CRAFTING_TOGGLE.getKeybind());
        changed |= clearFormerDefault(root, "Hotkeys", "craftDrop", "V", AUTO_CRAFTING_DROP.getKeybind());
        root.addProperty(HOTKEY_DEFAULTS_MIGRATED_KEY, true);
        return true;
    }

    private static boolean clearFormerDefault(JsonObject root, String section, String option, String oldDefault, IKeybind keybind) {
        JsonElement sectionElement = root.get(section);
        if (sectionElement == null || !sectionElement.isJsonObject()) return false;
        JsonElement optionElement = sectionElement.getAsJsonObject().get(option);
        if (optionElement == null || !optionElement.isJsonObject()) return false;

        JsonElement bindElement = optionElement.getAsJsonObject().get("hotkey");
        if (bindElement == null) bindElement = optionElement;
        String stored = readStoredKeys(bindElement);
        if (!matchesFormerDefault(stored, oldDefault)) return false;

        keybind.setValueFromString("");
        return true;
    }

    private static String readStoredKeys(JsonElement element) {
        if (element == null) return null;
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) return element.getAsString();
        if (element.isJsonObject()) {
            JsonElement keys = element.getAsJsonObject().get("keys");
            if (keys != null && keys.isJsonPrimitive() && keys.getAsJsonPrimitive().isString()) return keys.getAsString();
        }
        return null;
    }

    private static boolean matchesFormerDefault(String stored, String oldDefault) {
        if (stored == null) return false;
        String normalized = stored.trim().toUpperCase(java.util.Locale.ROOT);
        return normalized.equals(oldDefault)
            || normalized.equals("KEY.KEYBOARD." + oldDefault)
            || normalized.equals("KEYBOARD." + oldDefault);
    }

    @Override
    public void save() {
        JsonObject root = new JsonObject();
        root.addProperty(HOTKEY_DEFAULTS_MIGRATED_KEY, true);
        ConfigUtils.writeConfigBase(root, "General", GENERAL_OPTIONS);
        ConfigUtils.writeConfigBase(root, "AutoHelmet", AUTO_HELMET_OPTIONS);
        ConfigUtils.writeConfigBase(root, "EntityList", ENTITY_LIST_OPTIONS);
        ConfigUtils.writeConfigBase(root, "Portals", PORTALS_OPTIONS);
        ConfigUtils.writeConfigBase(root, "GuiMove", GUI_MOVE_OPTIONS);
        ConfigUtils.writeConfigBase(root, "AutoTotem", AUTO_TOTEM_OPTIONS);
        ConfigUtils.writeConfigBase(root, "SafeWalk", SAFE_WALK_OPTIONS);
        ConfigUtils.writeConfigBase(root, "AutoCrafting", AUTO_CRAFTING_PERSISTED_OPTIONS);
        ConfigUtils.writeHotkeys(root, "Hotkeys", List.of(AUTO_CRAFTING_TOGGLE, AUTO_CRAFTING_DROP));
        ConfigUtils.writeConfigBase(root, "NoRender", NO_RENDER_PERSISTED_OPTIONS);
        ConfigUtils.writeConfigBase(root, "Fullbright", FULLBRIGHT_OPTIONS);
        ConfigUtils.writeConfigBase(root, "Freecam", FREECAM_OPTIONS);
        ConfigUtils.writeConfigBase(root, "NoGhostBlocks", NO_GHOST_OPTIONS);

        Path file = configFile();
        try {
            Files.createDirectories(file.getParent());
            if (!JsonUtils.writeJsonToFile(root, file)) {
                com.lyriya.mod.Lyriya.LOG.error("Could not save MaLiLib configuration to {}", file);
            }
        } catch (IOException e) {
            com.lyriya.mod.Lyriya.LOG.error("Could not create configuration directory for {}", file, e);
        }
    }

    @Override
    public void onConfigsChanged() {
        save();
    }

    private static Path configFile() {
        return FileUtils.getConfigDirectory().resolve(CONFIG_FILE_NAME);
    }

    private static boolean isValidItemId(String value) {
        try {
            net.minecraft.resources.Identifier id = net.minecraft.resources.Identifier.parse(value);
            return net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(id);
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
