package io.github.blanketmc.blanket.mixin;

import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import io.github.blanketmc.blanket.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(value = YggdrasilUserApiService.class, remap = false)
public abstract class YggdrasilUserApiService_syncBlockListMixin {

    @Shadow @Nullable protected abstract Set<UUID> fetchBlockList();

    @Shadow @Nullable private Set<UUID> blockList;

    //Non-blocking API request.
    @Redirect(method = "isBlockedPlayer", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/YggdrasilUserApiService;fetchBlockList()Ljava/util/Set;"))
    private Set<UUID> isBlockedPlayerFetchAsync(YggdrasilUserApiService instance) {
        if (Config.chatLagFix) {
            CompletableFuture.runAsync(() -> this.blockList = fetchBlockList());
            return null;
        }
        return this.fetchBlockList();
    }

    @Redirect(method = "forceFetchBlockList", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/response/BlockListResponse;getBlockedProfiles()Ljava/util/Set;"))
    private Set<UUID> forceFetchDontReturnNull(BlockListResponse instance) {
        if (Config.chatLagFix) {
            Set<UUID> uuids = instance.getBlockedProfiles();
            return uuids == null ? new HashSet<>() : uuids;
        }
        return instance.getBlockedProfiles();
    }
}
