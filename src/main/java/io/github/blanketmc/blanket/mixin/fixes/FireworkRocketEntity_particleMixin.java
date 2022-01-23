package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntity_particleMixin {

    @Shadow private LivingEntity shooter;


    @ModifyArg(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"
            ),
            index = 2
    )
    public double lowerParticleIfObstructive(double y) {
        if (Config.flashingFireworkParticlesFix && this.shooter != null && this.shooter.getPitch() <= -50) y -= 0.8D;
        return y;
    }
}
