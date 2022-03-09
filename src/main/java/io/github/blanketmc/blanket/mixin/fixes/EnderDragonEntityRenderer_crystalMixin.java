package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnderDragonEntityRenderer.class)
public class EnderDragonEntityRenderer_crystalMixin {


    @Redirect(
            method = "render(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;" +
                    "FFLnet/minecraft/client/util/math/MatrixStack;" +
                    "Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;" +
                            "connectedCrystal:Lnet/minecraft/entity/decoration/EndCrystalEntity;",
                    ordinal = 0
            )
    )
    public EndCrystalEntity connectedCrystals(EnderDragonEntity instance) {
        return Config.crystalsTargetDeadDragonFix && instance.ticksSinceDeath > 0 ? null : instance.connectedCrystal;
    }
}
