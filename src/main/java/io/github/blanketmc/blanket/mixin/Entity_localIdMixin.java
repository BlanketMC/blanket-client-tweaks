package io.github.blanketmc.blanket.mixin;

import io.github.blanketmc.blanket.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.atomic.AtomicInteger;

@Environment(EnvType.CLIENT)
@Mixin(Entity.class)
public class Entity_localIdMixin {
    private static final AtomicInteger LocalClientId = new AtomicInteger();


    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/concurrent/atomic/AtomicInteger;incrementAndGet()I"
            )
    )
    private int incrementAndGetCustom(AtomicInteger atomicInteger, EntityType<?> entityType, World world) {
        return Config.sharedEntityIdFix ? LocalClientId.incrementAndGet() : atomicInteger.incrementAndGet();
    }
}
