package io.github.blanketmc.blanket.mixin.fixes;

import io.github.blanketmc.blanket.Config;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AmethystBlock.class)
public abstract class AmethystBlock_AmethystSoundFix extends Block {
    public AmethystBlock_AmethystSoundFix(Settings settings) {
        super(settings);
    }

    @Override
    public BlockSoundGroup getSoundGroup(BlockState state) {
        if (Config.fixSwappedAmethystSound) {
            if (state.getBlock() == Blocks.MEDIUM_AMETHYST_BUD || state.getBlock() == Blocks.LARGE_AMETHYST_BUD) {
                return state.getBlock() == Blocks.MEDIUM_AMETHYST_BUD ? BlockSoundGroup.MEDIUM_AMETHYST_BUD: BlockSoundGroup.LARGE_AMETHYST_BUD;
            }
        }
        return super.getSoundGroup(state);
    }
}
