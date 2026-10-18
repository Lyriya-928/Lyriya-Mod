package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.config.SafeWalkMode;
import com.lyriya.mod.config.SafeWalkBehavior;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;

import java.util.concurrent.ThreadLocalRandom;

public final class SafeWalkFeature extends Feature {
    public static final SafeWalkFeature INSTANCE = new SafeWalkFeature();
    private double selectedRandomDistance = -1;
    private ClientLevel observedLevel;

    private SafeWalkFeature() { super("safe_walk", LyriyaConfig.SAFE_WALK_ENABLED); }

    @Override protected void onEnable() { observedLevel = Minecraft.getInstance().level; resetApproach(); }
    @Override protected void onDisable() { observedLevel = null; resetApproach(); }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null) {
            observedLevel = null;
            resetApproach();
        } else if (observedLevel != client.level) {
            observedLevel = client.level;
            resetApproach();
        }
    }

    public boolean shouldAutoSneak(Input input) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (!isEnabled() || player == null || client.level == null || input == null
            || input.shift() || input.jump() || player.onGround() == false) {
            if (player == null || client.level == null) resetApproach();
            return false;
        }

        // Match KeyboardInput.tick: left is the positive local strafe component.
        Vec2 movement = new Vec2((input.left() ? 1 : 0) - (input.right() ? 1 : 0),
            (input.forward() ? 1 : 0) - (input.backward() ? 1 : 0));
        if (movement.lengthSquared() < 0.01f) return false;
        movement = movement.normalized();
        float yaw = player.getYRot() * ((float) Math.PI / 180.0f);
        double dx = -Math.sin(yaw) * movement.y + Math.cos(yaw) * movement.x;
        double dz = Math.cos(yaw) * movement.y + Math.sin(yaw) * movement.x;

        double trigger = triggerDistance();
        double maxDistance = currentMode() == SafeWalkMode.FIXED ? trigger
            : Math.max(LyriyaConfig.SAFE_WALK_RANDOM_MIN.getDoubleValue(), LyriyaConfig.SAFE_WALK_RANDOM_MAX.getDoubleValue());
        if (currentBehavior() == SafeWalkBehavior.SOFT_SNEAK) {
            if (unsupportedDistance(player, dx, dz, maxDistance + 0.2) < 0) {
                resetApproach();
                return false;
            }
            // Preserve the previously shipped soft assistance path and its threshold behavior.
            double edgeDistance = unsupportedDistance(player, dx, dz, trigger + 0.15);
            return edgeDistance >= 0 && edgeDistance <= trigger + 0.025;
        }
        double edgeDistance = leadingFootGapDistance(player, dx, dz, maxDistance + 0.2);
        if (edgeDistance < 0) {
            resetApproach();
            return false;
        }
        return edgeDistance <= trigger + 0.025;
    }

    private double triggerDistance() {
        if (currentMode() != SafeWalkMode.RANDOM_RANGE) {
            selectedRandomDistance = -1;
            return LyriyaConfig.SAFE_WALK_DISTANCE.getDoubleValue();
        }
        if (selectedRandomDistance < 0) {
            double min = Math.min(LyriyaConfig.SAFE_WALK_RANDOM_MIN.getDoubleValue(), LyriyaConfig.SAFE_WALK_RANDOM_MAX.getDoubleValue());
            double max = Math.max(LyriyaConfig.SAFE_WALK_RANDOM_MIN.getDoubleValue(), LyriyaConfig.SAFE_WALK_RANDOM_MAX.getDoubleValue());
            selectedRandomDistance = min == max ? min : ThreadLocalRandom.current().nextDouble(min, Math.nextUp(max));
        }
        return selectedRandomDistance;
    }

    private double leadingFootGapDistance(Player player, double dx, double dz, double limit) {
        AABB box = player.getBoundingBox();
        AABB feet = new AABB(box.minX + 0.015, box.minY - 0.08, box.minZ + 0.015,
            box.maxX - 0.015, box.minY + 0.015, box.maxZ - 0.015);
        double inset = 0.035;
        AABB frontX = null;
        AABB frontZ = null;
        if (Math.abs(dx) > 1.0e-4) {
            frontX = dx > 0 ? new AABB(feet.maxX - inset, feet.minY, feet.minZ, feet.maxX, feet.maxY, feet.maxZ)
                : new AABB(feet.minX, feet.minY, feet.minZ, feet.minX + inset, feet.maxY, feet.maxZ);
        }
        if (Math.abs(dz) > 1.0e-4) {
            frontZ = dz > 0 ? new AABB(feet.minX, feet.minY, feet.maxZ - inset, feet.maxX, feet.maxY, feet.maxZ)
                : new AABB(feet.minX, feet.minY, feet.minZ, feet.maxX, feet.maxY, feet.minZ + inset);
        }
        if (frontX == null && frontZ == null) return -1;
        for (double distance = 0; distance <= limit + 1.0e-6; distance += 0.025) {
            if (frontX != null && !clientLevelHasSupport(player, frontX.move(dx * distance, 0, dz * distance))) return distance;
            if (frontZ != null && !clientLevelHasSupport(player, frontZ.move(dx * distance, 0, dz * distance))) return distance;
        }
        return -1;
    }

    private double unsupportedDistance(Player player, double dx, double dz, double limit) {
        AABB bounds = player.getBoundingBox();
        AABB feet = new AABB(bounds.minX + 0.015, bounds.minY - 0.08, bounds.minZ + 0.015,
            bounds.maxX - 0.015, bounds.minY + 0.015, bounds.maxZ - 0.015);
        if (!clientLevelHasSupport(player, feet)) {
            // If already partly over an edge, only assist when support is behind the player and
            // the requested movement continues outward. Moving back toward support is safe.
            boolean supportBehind = false;
            boolean supportAhead = false;
            for (double distance = 0.05; distance <= 0.5; distance += 0.05) {
                supportBehind |= clientLevelHasSupport(player, feet.move(-dx * distance, 0, -dz * distance));
                supportAhead |= clientLevelHasSupport(player, feet.move(dx * distance, 0, dz * distance));
            }
            return supportBehind && !supportAhead ? 0 : -1;
        }
        for (double distance = 0.05; distance <= limit + 1e-6; distance += 0.025) {
            if (!clientLevelHasSupport(player, feet.move(dx * distance, 0, dz * distance))) return distance;
        }
        return -1;
    }

    private static boolean clientLevelHasSupport(Player player, AABB feet) {
        return player.level().getBlockCollisions(player, feet).iterator().hasNext();
    }

    private void resetApproach() { selectedRandomDistance = -1; }

    private static SafeWalkMode currentMode() {
        return (SafeWalkMode) LyriyaConfig.SAFE_WALK_MODE.getOptionListValue();
    }

    private static SafeWalkBehavior currentBehavior() {
        return (SafeWalkBehavior) LyriyaConfig.SAFE_WALK_BEHAVIOR.getOptionListValue();
    }
}
