package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WolfEntity.class)
public abstract class WolfEntity_tailSpinMixin extends TameableEntity implements Angerable {

    protected WolfEntity_tailSpinMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * @author FX - PR0CESS
     * @author KosmX
     * @reason Fix for the wolf's tail spinning
     */
    @Inject(method = "getTailAngle", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    public void getTailAngle(CallbackInfoReturnable<Float> cir) {
        if (Config.wolfTailSpinFix) {
            cir.setReturnValue ((0.15F + 0.4F * (this.getHealth() / this.getMaxHealth())) * 3.1415927F);
        }
    }

}
