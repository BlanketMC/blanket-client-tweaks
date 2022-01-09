package io.github.blanketmc.blanket.mixin.clientCommands;

import com.mojang.brigadier.StringReader;
import io.github.blanketmc.blanket.utils.ClientCommands;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Pattern;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntity_clientCommandsMixin {
    @Inject(at = @At("HEAD"), method = "sendChatMessage", cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("/")) {
            StringReader reader = new StringReader(message);
            reader.skip();
            int cursor = reader.getCursor();
            reader.setCursor(cursor);
            if (ClientCommands.isClientSideCommand(message.substring(1).split(Pattern.quote(" ")))) {
                ClientCommands.executeCommand(reader);
                ci.cancel();
            }
        }
    }
}
