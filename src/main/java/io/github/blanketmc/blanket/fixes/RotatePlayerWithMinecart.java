package io.github.blanketmc.blanket.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

/**
 * This is an older code from another project
 * should be cleaned and refactored.
 * by - KosmX
 */
public class RotatePlayerWithMinecart {
    public static boolean smoothMode = true;

    private static float yaw = 0f;
    private static boolean doCorrection;

    //posVelocity is from position's change -- real, big delay
    //gotVelocity is from Minecart.getVelocity() -- represents rail's direction, immediate
    @Nullable
    private static Vec3d gotVelocity = null;
    @Nullable
    private static Vec3d posVelocity = null;

    @Nullable
    private static Vec3d lastCoord = null;
    private static float lastYaw = 0f;
    private static Vec3d lastVelocity;
    private static float rawLastYaw;
    private static float rawYaw;
    private static int tickAfterLastFollow = 0;
    private static int tickAfterPistonRail;
    private static int pistorRailTick = 0;
    private static float difference;
    private static int lastSlowdown = 0;


    //-----------------CORE METHOD-----------------------

    public static void update(MinecartEntity minecart) {
        lastYaw = yaw;
        boolean update = calculateNewDirection(minecart);
        if (tickAfterLastFollow++ > Config.rotatePlayerWithMinecart_threshold) lastYaw = yaw;
        else if (doCorrection) lastYaw = normalize(lastYaw + 180f);
        doCorrection = false;
        if (update) tickAfterLastFollow = 0;
        difference = normalize(yaw - lastYaw);
    }

    public static boolean calculateNewDirection(MinecartEntity minecart) {
        boolean update = false;
        float yawF = rawYaw;
        boolean correction = false;
        boolean successUpdate = updateSmartCorrection(minecart);
        if (minecart.getVelocity().lengthSquared() > 0.000002f) {
            if (tickAfterPistonRail != Config.rotatePlayerWithMinecart_threshold) tickAfterPistonRail++;
            if (pistorRailTick != 0) pistorRailTick--;
            yawF = sphericalFromVec3d(minecart.getVelocity());
            update = true;
            correction = true;
        } else if (minecart.getVelocity().lengthSquared() == 0f && successUpdate && posVelocity.lengthSquared() > 0.02f) {
            if (pistorRailTick != Config.rotatePlayerWithMinecart_threshold) pistorRailTick++;
            else tickAfterPistonRail = 0;
            yawF = getEighthDirection(sphericalFromVec3d(posVelocity));
            update = true;
        } else {
            if (pistorRailTick != 0) pistorRailTick--;
        }
        rawLastYaw = rawYaw;
        rawYaw = yawF;
        if (correction) checkSmartCorrection(successUpdate);
        calculateNewDirection(yawF);
        return update;
    }
    //-------------methods-----------------------------

    private static float sphericalFromVec3d(Vec3d vec3d) {
        return (float) (MathHelper.atan2(-vec3d.x, vec3d.z) * 57.2957763671875D);
    }

    public static void onStartRiding() {
        lastCoord = null;
        tickAfterLastFollow = 100;
        lastVelocity = Vec3d.ZERO;
        lastSlowdown = 100;
        tickAfterPistonRail = Config.rotatePlayerWithMinecart_threshold;
        pistorRailTick = 0;
    }

    private static void calculateNewDirection(float yawF) {
        if (RotatePlayerWithMinecart.smoothMode) {
            if (!Config.rotatePlayerWithMinecart_alwaysLookForward && tickAfterLastFollow > Config.rotatePlayerWithMinecart_threshold) {
                yaw = yawF;
            } else if (doCorrection) {
                yaw = normalize(yaw + 180f);
            }
            if (Math.abs(yawF - yaw) < 180f) {
                yaw = yaw / 2 + yawF / 2;
            } else {
                float tmp = yaw / 2 + yawF / 2;
                yaw = (tmp >= 0) ? tmp - 180f : tmp + 180f;
            }
        } else {
            yaw = yawF;
        }
    }


    private static void checkSmartCorrection(boolean successUpdate) {
        boolean correction = false;
        if (Config.rotatePlayerWithMinecart_smartMode) {
            float ang = 60f;
            if (tickAfterPistonRail == Config.rotatePlayerWithMinecart_threshold && floatCircleDistance(rawLastYaw, rawYaw, 360) > 180f - ang && floatCircleDistance(rawLastYaw, rawYaw, 360) < 180 + ang) {
                correction = true;
                /*-------------------Explain, what does the following complicated code------------------------
                 *The Smart correction's aim is to make difference between a U-turn and a collision, what isn't an easy task
                 * The speed vector always rotate in 180* so I need data from somewhere else:Position->real speed(with a little delay)
                 * I observed 2 things:
                 * 1:On collision, the speed decreases (gotVelocity)
                 * 2:On taking U-turn, the real velocity vector and the minecart.getVelocity() are ~perpendicular
                 *
                 * fix to piston rail-
                 * while travelling on piston-rail
                 * gotVelocity = zero
                 * posVelocity always real (while euclidean space isn't broken)
                 *
                 */
                if (successUpdate) {
                    boolean bl1 = posVelocity.lengthSquared() > 0.00004f && Math.abs(posVelocity.normalize().dotProduct(gotVelocity.normalize())) < 0.8f;//vectors dot product ~0, if vectors are ~perpendicular to each other
                    boolean bl2 = (!bl1) || lastSlowdown < Config.rotatePlayerWithMinecart_threshold && Math.abs(posVelocity.normalize().dotProduct(gotVelocity.normalize())) < 0.866f && gotVelocity.lengthSquared() < 0.32;
                    if (bl1 && !bl2) {
                        correction = false;
                    }
                }
            }
        }
        doCorrection = correction;
    }

    private static boolean updateSmartCorrection(MinecartEntity minecart) {
        boolean success = lastCoord != null;
        Vec3d pos = minecart.getPos();
        if (success) {
            posVelocity = new Vec3d(pos.x - lastCoord.x, 0, pos.z - lastCoord.z);
            lastVelocity = (gotVelocity == null) ? new Vec3d(0, 0, 0) : gotVelocity;
            gotVelocity = new Vec3d(minecart.getVelocity().getX(), 0, minecart.getVelocity().getZ());
            if (gotVelocity.length() != 0 && lastVelocity.length() / gotVelocity.length() > 2.4d) lastSlowdown = 0;
            ++lastSlowdown;
        }
        lastCoord = pos;
        return success;
    }

    public static float calcYaw(float entityYaw) {
        return (Config.rotatePlayerWithMinecart_alwaysLookForward) ? (entityYaw + normalize(yaw - entityYaw)) : (entityYaw + difference);
    }


    private static float normalize(Float f) {
        return (Math.abs(f) > 180) ? (f < 0) ? f + 360f : f - 360f : f;
    }

    private static float getEighthDirection(Float f) {
        if (floatCircleDistance(f % 90, 0, 90) < 20) return ((float) Math.round(f / 90)) * 90;
        return f;
    }

    private static float floatCircleDistance(float i1, float i2, float size) {
        float dist1 = Math.abs(i1 - i2);
        return Math.min(dist1, Math.abs(dist1 - size));
    }
}