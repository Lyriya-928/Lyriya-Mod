package com.lyriya.mod.mixin;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.impl.FreecamFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftFreecamPickMixin {
    @Shadow public LocalPlayer player;
    @Shadow public ClientLevel level;
    @Shadow public HitResult hitResult;
    @Shadow public Entity crosshairPickEntity;

    @Inject(method = "pick(F)V", at = @At("HEAD"), cancellable = true)
    private void lyriya$pickFromFreecam(float partialTick, CallbackInfo ci) {
        FreecamFeature freecam = FreecamFeature.INSTANCE;
        if (!freecam.activePosition() || player == null || level == null) return;

        // Minecraft's per-tick target selection is the shared input for its outline,
        // block breaking and targeted-use paths. Keep server-side reach checks intact.
        Vec3 from = freecam.cameraPosition(partialTick);
        float yaw = freecam.cameraYaw(partialTick);
        float pitch = freecam.cameraPitch(partialTick);
        Vec3 direction = viewVector(yaw, pitch);
        double blockReach = player.blockInteractionRange();
        ItemStack held = player.getActiveItem();
        AttackRange configuredRange = held.get(net.minecraft.core.component.DataComponents.ATTACK_RANGE);
        double entityReach = configuredRange == null ? player.entityInteractionRange()
            : configuredRange.effectiveMaxRange(player);
        double reach = Math.max(blockReach, entityReach);
        Vec3 to = from.add(direction.scale(reach));

        BlockHitResult blockHit = level.clip(new ClipContext(from, from.add(direction.scale(blockReach)),
            ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        HitResult selected = blockHit;
        double nearest = blockHit.getType() == HitResult.Type.MISS ? reach * reach
            : blockHit.getLocation().distanceToSqr(from);

        AABB searchBox = new AABB(from, to).inflate(1.0);
        for (Entity entity : level.getEntities(player, searchBox, EntitySelector.CAN_BE_PICKED)) {
            double margin = entity.getPickRadius();
            AABB box = entity.getBoundingBox().inflate(margin);
            var intersection = box.clip(from, to);
            if (intersection.isEmpty()) continue;
            double distance = intersection.get().distanceToSqr(from);
            if (distance < nearest && distance <= entityReach * entityReach) {
                nearest = distance;
                selected = new EntityHitResult(entity, intersection.get());
            }
        }

        hitResult = selected;
        crosshairPickEntity = selected instanceof EntityHitResult entityHit ? entityHit.getEntity() : null;
        ci.cancel();
    }

    private static Vec3 viewVector(float yaw, float pitch) {
        float yawRadians = yaw * ((float) Math.PI / 180.0F);
        float pitchRadians = pitch * ((float) Math.PI / 180.0F);
        float horizontal = (float) Math.cos(pitchRadians);
        return new Vec3(-Math.sin(yawRadians) * horizontal, -Math.sin(pitchRadians),
            Math.cos(yawRadians) * horizontal);
    }
}
