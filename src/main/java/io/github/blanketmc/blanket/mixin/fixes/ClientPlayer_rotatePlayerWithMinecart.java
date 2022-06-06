package io.github.blanketmc.blanket.mixin.fixes;

import com.mojang.authlib.GameProfile;

import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.fixes.RotatePlayerWithMinecart;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayer_rotatePlayerWithMinecart extends AbstractClientPlayerEntity {

    public ClientPlayer_rotatePlayerWithMinecart(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Shadow public abstract float getYaw(float tickDelta);


    @Inject(
            method = "tickRiding",
            at = @At("TAIL")
    )
    private void ridingTickTail(CallbackInfo info){
        Entity vehicle = this.getVehicle();
        if(Config.rotatePlayerWithMinecart && vehicle instanceof MinecartEntity){
            /*Using MinecartEntity.getYaw() is unusable, because it's not the minecart's yaw...
             *There is NO method in mc to get the minecart's real yaw...
             *I need to create my own identifier method (from the speed)
             */
            RotatePlayerWithMinecart.update((MinecartEntity)vehicle);
            this.setYaw(RotatePlayerWithMinecart.calcYaw(this.getYaw()));
            this.bodyYaw = RotatePlayerWithMinecart.calcYaw(this.bodyYaw);
        }
    }


    @Inject(method = "startRiding", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/MinecraftClient;getSoundManager()Lnet/minecraft/client/sound/SoundManager;"))
    private void startRidingInject(Entity entity, boolean force, CallbackInfoReturnable<Object> info){
        RotatePlayerWithMinecart.onStartRiding();
    }
}