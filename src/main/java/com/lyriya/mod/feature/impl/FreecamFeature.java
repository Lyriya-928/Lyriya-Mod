package com.lyriya.mod.feature.impl;

import com.lyriya.mod.config.LyriyaConfig;
import com.lyriya.mod.feature.Feature;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.client.KeyMapping;
import com.lyriya.mod.mixin.KeyMappingAccessor;

public final class FreecamFeature extends Feature {
    public static final FreecamFeature INSTANCE = new FreecamFeature();
    private Vec3 cameraPosition;
    private Vec3 previousCameraPosition;
    private CameraType previousPerspective;
    private ClientLevel activeLevel;
    private float yaw;
    private float pitch;
    private float previousYaw;
    private float previousPitch;
    private boolean forward, backward, left, right, up, down;

    private FreecamFeature() { super("freecam", LyriyaConfig.FREECAM_ENABLED); }

    @Override
    protected void onEnable() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        initializeCamera(client);
    }

    private void initializeCamera(Minecraft client) {
        activeLevel = client.level;
        previousPerspective = client.options.getCameraType();
        client.options.setCameraType(CameraType.FIRST_PERSON);
        cameraPosition = client.player.getEyePosition();
        previousCameraPosition = cameraPosition;
        yaw = client.player.getYRot();
        pitch = client.player.getXRot();
        previousYaw = yaw;
        previousPitch = pitch;
        forward = backward = left = right = up = down = false;
        releaseVanillaMovementKeys();
        // The detached camera changes the relevant view region. Invalidate the
        // 26.3 extractor's compiled visibility data on activation and restore.
        client.levelExtractor.allChanged();
    }

    @Override
    protected void onDisable() {
        Minecraft client = Minecraft.getInstance();
        if (previousPerspective != null) client.options.setCameraType(previousPerspective);
        if (activeLevel != null) client.levelExtractor.allChanged();
        releaseVanillaMovementKeys();
        previousPerspective = null;
        activeLevel = null;
        cameraPosition = null;
        previousCameraPosition = null;
        forward = backward = left = right = up = down = false;
    }

    @Override
    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) {
            if (previousPerspective != null) {
                onDisable();
                LyriyaConfig.FREECAM_ENABLED.setBooleanValue(false);
                LyriyaConfig.INSTANCE.save();
            }
            return;
        }
        if (activeLevel != null && client.level != activeLevel) {
            onDisable();
            LyriyaConfig.FREECAM_ENABLED.setBooleanValue(false);
            LyriyaConfig.INSTANCE.save();
            return;
        }
        if (cameraPosition == null) initializeCamera(client);
        if (client.options.getCameraType() != CameraType.FIRST_PERSON) {
            client.options.setCameraType(CameraType.FIRST_PERSON);
        }
        if (client.gui.screen() != null) {
            forward = backward = left = right = up = down = false;
            return;
        }
        // The configured vanilla sneak key remains a real player input. Freecam
        // observes the same mapping for vertical camera movement without capturing it.
        down = client.options.keyShift.isDown();
        double radians = Math.toRadians(yaw);
        double forwardX = -Math.sin(radians);
        double forwardZ = Math.cos(radians);
        // KeyboardInput encodes left as +1 and right as -1. Use that same local
        // strafe convention before rotating the vector by the camera yaw.
        double strafe = (left ? 1 : 0) - (right ? 1 : 0);
        double dx = 0, dy = 0, dz = 0;
        if (forward) { dx += forwardX; dz += forwardZ; }
        if (backward) { dx -= forwardX; dz -= forwardZ; }
        dx += Math.cos(radians) * strafe;
        dz += Math.sin(radians) * strafe;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length > 0) {
            double speed = LyriyaConfig.FREECAM_SPEED.getDoubleValue() * 0.5;
            dx = dx / length * speed;
            dz = dz / length * speed;
        }
        if (up) dy += LyriyaConfig.FREECAM_SPEED.getDoubleValue() * 0.5;
        if (down) dy -= LyriyaConfig.FREECAM_SPEED.getDoubleValue() * 0.5;
        if (cameraPosition == null) cameraPosition = client.player.getEyePosition();
        previousCameraPosition = cameraPosition;
        previousYaw = yaw;
        previousPitch = pitch;
        cameraPosition = cameraPosition.add(dx, dy, dz);
    }

    public boolean activePosition() { return isEnabled() && cameraPosition != null; }
    public Vec3 cameraPosition() { return cameraPosition; }
    public float cameraYaw() { return yaw; }
    public float cameraPitch() { return pitch; }

    public Vec3 cameraPosition(float partialTick) {
        if (cameraPosition == null || previousCameraPosition == null) return cameraPosition;
        return previousCameraPosition.lerp(cameraPosition, partialTick);
    }
    public float cameraYaw(float partialTick) { return previousYaw + (yaw - previousYaw) * partialTick; }
    public float cameraPitch(float partialTick) { return previousPitch + (pitch - previousPitch) * partialTick; }

    public boolean captureKey(int key, int action) {
        Minecraft client = Minecraft.getInstance();
        if (!isEnabled() || client.gui.screen() != null) {
            forward = backward = left = right = up = down = false;
            return false;
        }
        if (!LyriyaConfig.FREECAM_CONTROL_KEYS.getBooleanValue()) {
            forward = backward = left = right = up = down = false;
            return false;
        }
        if (matches(client.options.keyUp, key)) forward = action != 0;
        else if (matches(client.options.keyDown, key)) backward = action != 0;
        else if (matches(client.options.keyLeft, key)) left = action != 0;
        else if (matches(client.options.keyRight, key)) right = action != 0;
        else if (matches(client.options.keyJump, key)) up = action != 0;
        else return false;
        return true;
    }

    private static boolean matches(KeyMapping mapping, int key) {
        return ((KeyMappingAccessor) (Object) mapping).lyriya$getKey().getValue() == key;
    }

    private static void releaseVanillaMovementKeys() {
        Minecraft client = Minecraft.getInstance();
        if (client.options == null) return;
        client.options.keyUp.setDown(false);
        client.options.keyDown.setDown(false);
        client.options.keyLeft.setDown(false);
        client.options.keyRight.setDown(false);
        client.options.keyJump.setDown(false);
    }

    /** Receives vanilla's already sensitivity-scaled, inverted and optionally smoothed deltas. */
    public void turnProcessed(double deltaX, double deltaY) {
        previousYaw = yaw;
        previousPitch = pitch;
        yaw += (float) deltaX * 0.15f;
        pitch = Math.clamp(pitch + (float) deltaY * 0.15f, -90.0f, 90.0f);
    }
}
