package io.github.blanketmc.blanket.mixin.narrator;

import com.mojang.text2speech.Narrator;
import io.github.blanketmc.blanket.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NarratorManager.class)
public class NarratorManager_toggleMixin {

    @Mutable
    @Shadow
    @Final
    private Narrator narrator;


    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"
            )
    )
    private Narrator onGetNarrator() {
        return Config.disableNarratorThreadWhenUnused &&
                MinecraftClient.getInstance().options.narrator == NarratorMode.OFF ? null : Narrator.getNarrator();
    }


    @Inject(
            method = "addToast(Lnet/minecraft/client/option/NarratorMode;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onNarratorOptionChange(NarratorMode option, CallbackInfo ci) {
        if (option != NarratorMode.OFF && narrator == null) {
            narrator = Narrator.getNarrator();
        }
        if (narrator == null) {
            ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
            SystemToast.show(
                    toastManager,
                    SystemToast.Type.NARRATOR_TOGGLE,
                    new TranslatableText("narrator.toast.disabled"),
                    new TranslatableText("options.narrator.notavailable")
            );
            ci.cancel();
        }
    }


    @Inject(
            method = "isActive()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    public void isActive(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.narrator == null || this.narrator.active());
    }


    @Inject(
            method = "destroy()V",
            at = @At("HEAD"),
            cancellable = true
    )
    public void onDestroy(CallbackInfo ci) {
        if (this.narrator != null) {
            this.narrator.destroy();
            this.narrator = null;
        }
        ci.cancel();
    }
}
