package com.lyriya.mod.config;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Resolves whitelist editor input and expands persisted item-tag rules for crafting. */
public final class AutoCraftingWhitelist {
    private static long tagGeneration;
    private static long cachedGeneration = -1;
    private static ClientLevel cachedLevel;
    private static List<String> cachedRules = List.of();
    private static List<Item> cachedTargets = List.of();

    private AutoCraftingWhitelist() { }

    public enum Error { NONE, EMPTY, BAD_FILTER, INVALID_ID, UNKNOWN_NAME, UNKNOWN_TAG, EMPTY_TAG, DUPLICATE }

    public record Resolution(List<String> rules, Error error) {
        public boolean successful() { return error == Error.NONE && !rules.isEmpty(); }
    }

    public static Resolution resolveInput(String input, ClientLevel level) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty()) return new Resolution(List.of(), Error.EMPTY);

        String filter = null;
        int comma = value.lastIndexOf(',');
        if (comma >= 0) {
            if (value.indexOf(',') != comma || !value.substring(comma + 1).trim().equalsIgnoreCase("c")) {
                return new Resolution(List.of(), Error.BAD_FILTER);
            }
            filter = "c";
            value = value.substring(0, comma).trim();
            if (value.isEmpty()) return new Resolution(List.of(), Error.EMPTY);
        }

        if (value.startsWith("#")) {
            if (filter != null) return new Resolution(List.of(), Error.BAD_FILTER);
            String tagText = value.substring(1).trim();
            try {
                Identifier id = Identifier.parse(tagText);
                TagKey<Item> tag = TagKey.create(Registries.ITEM, id);
                Optional<HolderSet.Named<Item>> members = itemLookup(level).get(tag);
                if (members.isEmpty()) return new Resolution(List.of(), Error.UNKNOWN_TAG);
                boolean hasItems = members.get().stream().anyMatch(holder -> holder.value() != Items.AIR);
                if (!hasItems) return new Resolution(List.of(), Error.EMPTY_TAG);
                return new Resolution(List.of("#" + id), Error.NONE);
            } catch (RuntimeException ignored) {
                return new Resolution(List.of(), Error.UNKNOWN_TAG);
            }
        }

        if (filter == null && value.contains(":")) {
            try {
                Identifier id = Identifier.parse(value);
                Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
                if (item.isEmpty() || item.get() == Items.AIR) return new Resolution(List.of(), Error.INVALID_ID);
                return new Resolution(List.of(id.toString()), Error.NONE);
            } catch (RuntimeException ignored) {
                return new Resolution(List.of(), Error.INVALID_ID);
            }
        }

        String needle = value.toLowerCase(Locale.ROOT);
        List<String> matches = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item == Items.AIR) continue;
            String name = new ItemStack(item).getHoverName().getString();
            String id = BuiltInRegistries.ITEM.getKey(item).toString();
            boolean match = filter == null
                ? name.equalsIgnoreCase(value)
                : name.toLowerCase(Locale.ROOT).contains(needle) || id.toLowerCase(Locale.ROOT).contains(needle);
            if (match) matches.add(id);
        }
        if (matches.isEmpty()) return new Resolution(List.of(), Error.UNKNOWN_NAME);
        return new Resolution(List.copyOf(matches), Error.NONE);
    }

    public static List<Item> targets(ClientLevel level) {
        List<String> rules = List.copyOf(LyriyaConfig.AUTO_CRAFTING_WHITELIST.getStrings());
        if (cachedLevel == level && cachedGeneration == tagGeneration && cachedRules.equals(rules)) return cachedTargets;

        LinkedHashSet<Item> items = new LinkedHashSet<>();
        HolderLookup.RegistryLookup<Item> lookup = itemLookup(level);
        for (String raw : rules) {
            if (raw.startsWith("#")) {
                try {
                    TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(raw.substring(1)));
                    lookup.get(tag).ifPresent(holders -> holders.stream()
                        .map(Holder::value).filter(item -> item != Items.AIR).forEach(items::add));
                } catch (RuntimeException ignored) { }
                continue;
            }
            try {
                BuiltInRegistries.ITEM.getOptional(Identifier.parse(raw))
                    .filter(item -> item != Items.AIR).ifPresent(items::add);
            } catch (RuntimeException ignored) { }
        }
        cachedLevel = level;
        cachedGeneration = tagGeneration;
        cachedRules = rules;
        cachedTargets = List.copyOf(items);
        return cachedTargets;
    }

    public static void invalidateTags() {
        tagGeneration++;
        cachedLevel = null;
        cachedTargets = List.of();
    }

    private static HolderLookup.RegistryLookup<Item> itemLookup(ClientLevel level) {
        if (level != null) return level.registryAccess().lookupOrThrow(Registries.ITEM);
        return BuiltInRegistries.ITEM;
    }
}
