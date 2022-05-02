package io.github.blanketmc.blanket.mixin.accessors;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.UUID;

@Mixin(EntityAttributeModifier.class)
public interface EntityAttributeModifierAccessor {
    @Accessor("uuid") @Mutable
    void setUuid(UUID id);
}
