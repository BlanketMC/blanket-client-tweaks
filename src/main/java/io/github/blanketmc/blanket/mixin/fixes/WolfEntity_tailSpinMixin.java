package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(WolfEntity.class)
public abstract class WolfEntity_tailSpinMixin extends TameableEntity implements Angerable {

    protected WolfEntity_tailSpinMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }


    /**
     * @author FX - PR0CESS
     * @reason Fix for the wolf's tail spinning
     */
    @Overwrite
    public float getTailAngle() {
        if (this.hasAngerTime()) {
            return 1.5393804F;
        } else {
            if (this.isTamed()) {
                if (Config.wolfTailSpinFix) return (0.15F + 0.4F * (this.getHealth() / this.getMaxHealth())) * 3.1415927F;
                return (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * 3.1415927F;
            }
            return 0.62831855F;
        }
    }

}
