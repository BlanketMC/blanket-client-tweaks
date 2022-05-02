package io.github.blanketmc.blanket.mixin.fixes;

import com.google.common.collect.Multimap;
import io.github.blanketmc.blanket.Config;
import io.github.blanketmc.blanket.mixin.accessors.EntityAttributeModifierAccessor;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStack_tooltipMixin {


    @Inject(
            method = "getTooltip(Lnet/minecraft/entity/player/PlayerEntity;" +
                    "Lnet/minecraft/client/item/TooltipContext;)Ljava/util/List;",
            locals = LocalCapture.CAPTURE_FAILSOFT,
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/item/ItemStack;getAttributeModifiers(" +
                                    "Lnet/minecraft/entity/EquipmentSlot;)Lcom/google/common/collect/Multimap;",
                            ordinal = 0,
                            shift = At.Shift.AFTER
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getOperation()" +
                                    "Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;",
                            ordinal = 0,
                            shift = At.Shift.BEFORE
                    )
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getId()Ljava/util/UUID;",
                    ordinal = 0
            )
    )
    private void getTooltip(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir,
                            List<Text> list, MutableText mutableText, int i, EquipmentSlot[] var6, int var7, int var8,
                            EquipmentSlot equipmentSlot, Multimap<EntityAttribute, EntityAttributeModifier> multimap,
                            Iterator<Map.Entry<EntityAttribute, EntityAttributeModifier>> var11,
                            Map.Entry<EntityAttribute, EntityAttributeModifier> entry,
                            EntityAttributeModifier entityAttributeModifier, double d, boolean bl) {
        if (Config.itemStackTooltipUUIDFix && entityAttributeModifier.getId().equals(Item.ATTACK_DAMAGE_MODIFIER_ID))
            ((EntityAttributeModifierAccessor)entityAttributeModifier).setUuid(Item.ATTACK_DAMAGE_MODIFIER_ID);
    }
}
