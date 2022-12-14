package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.quiltmc.loader.api.minecraft.ClientOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ClientOnly
@Mixin(Entity.class)
public class Entity_lavaDamageMixin {

    @Shadow public World world;


    @Inject(
            method = "isFireImmune()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isFireImmuneAndServerSide(CallbackInfoReturnable<Boolean> cir) {
        if (Config.lavaDamageDesyncFix && this.world.isClient()) cir.setReturnValue(true);
    }
}
