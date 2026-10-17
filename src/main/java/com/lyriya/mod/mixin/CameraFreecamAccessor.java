package com.lyriya.mod.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraFreecamAccessor {
    @Invoker("setPosition") void lyriya$setPosition(Vec3 position);
    @Invoker("setRotation") void lyriya$setRotation(float yaw, float pitch);
}
