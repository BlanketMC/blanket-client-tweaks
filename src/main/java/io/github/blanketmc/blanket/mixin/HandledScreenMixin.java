package io.github.blanketmc.blanket.mixin;

import io.github.blanketmc.blanket.Config;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {

    @Shadow public abstract void onClose();

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void listenToCloseAction(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir){
        if (Config.mouseCloseNotHandledFix) {
            if (MinecraftClient.getInstance().options.keyInventory.matchesMouse(button)) {
                this.onClose();
                cir.setReturnValue(true);
            }
        }
    }
}
