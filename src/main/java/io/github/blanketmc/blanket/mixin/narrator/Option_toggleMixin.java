package io.github.blanketmc.blanket.mixin.narrator;

import io.github.blanketmc.blanket.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.NarratorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Option.class)
public class Option_toggleMixin {


    @Inject(
            method = "method_32553(Lnet/minecraft/client/option/GameOptions;Lnet/minecraft/client/option/Option;" +
                    "Lnet/minecraft/client/option/NarratorMode;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onNarratorSettingChange(GameOptions options, Option option,
                                                NarratorMode mode, CallbackInfo ci) {
        if (Config.disableNarratorThreadWhenUnused) {
            MinecraftClient MC = MinecraftClient.getInstance();
            if (MC.options.narrator != mode) {
                MC.options.narrator = mode;
                if (mode == NarratorMode.OFF) {
                    Config.isNarratorOptionChange.set(true);
                    NarratorManager.INSTANCE.destroy();
                    ci.cancel();
                } else {
                    NarratorManager.INSTANCE.addToast(mode);
                    ci.cancel();
                }
            }
        }
    }
}
