package io.github.blanketmc.blanket.mixin;

import io.github.blanketmc.blanket.Config;
import net.minecraft.client.render.entity.GuardianEntityRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GuardianEntityRenderer.class)
public abstract class GuardianEntityRenderer_beamMixin {


    @Redirect(
            method = "render(Lnet/minecraft/entity/mob/GuardianEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getTime()J"
            )
    )
    public long fixBeamRender(World world) {
        return (Config.guardianBeamRenderFix) ? world.getTime() % 192000 : world.getTime();
    }
}
