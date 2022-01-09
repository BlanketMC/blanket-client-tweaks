package io.github.blanketmc.blanket.mixin;

import io.github.blanketmc.blanket.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class Entity_clientDamageMixin {

    private final Entity self = (Entity) (Object) this;


    @Inject(
            method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isInvulnerableToAndClient(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (Config.entityDamageDesyncFix && (self instanceof ItemEntity || self instanceof ExperienceOrbEntity)) {
            cir.setReturnValue(true);
        }
    }
}
