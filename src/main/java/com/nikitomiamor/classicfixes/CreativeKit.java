package com.nikitomiamor.classicfixes;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/** Los bloques que 1.2.5 ponia en la barra al entrar en un mundo creativo de un jugador. */
public final class CreativeKit {
    private static final Block[] HOTBAR = {
        Blocks.STONE, Blocks.COBBLESTONE, Blocks.BRICKS, Blocks.DIRT, Blocks.OAK_PLANKS,
        Blocks.OAK_LOG, Blocks.OAK_LEAVES, Blocks.TORCH, Blocks.SMOOTH_STONE_SLAB
    };

    private CreativeKit() {}

    public static void give(ServerPlayer player) {
        if (!ClassicFixesConfig.get().creativeKit || !player.isCreative() || player.level().getServer().isDedicatedServer()) return;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < HOTBAR.length; i++) {
            if (inventory.getItem(i).isEmpty()) inventory.setItem(i, new ItemStack(HOTBAR[i]));
        }
    }
}
