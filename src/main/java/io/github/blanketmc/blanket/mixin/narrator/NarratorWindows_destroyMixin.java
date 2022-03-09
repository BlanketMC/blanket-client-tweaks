package io.github.blanketmc.blanket.mixin.narrator;

import com.mojang.text2speech.NarratorWindows;
import io.github.blanketmc.blanket.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NarratorWindows.class, remap = false)
public class NarratorWindows_destroyMixin {


    @Inject(
            method = "destroy()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/text2speech/NarratorWindows$SAPIWrapperSolutionDLL;uninit(J)V",
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void beforeUninit(CallbackInfo ci) {
        if (Config.isNarratorOptionChange.get()) {
            Config.isNarratorOptionChange.set(false);
            ci.cancel();
        }
    }
}
