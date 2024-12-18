package com.samsthenerd.hexgloop.blockentities;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class HexGloopBEs {
    public static DeferredRegister<BlockEntityType<?>> blockEntities = DeferredRegister.create(HexGloop.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    public static RegistrySupplier<BlockEntityType<BlockEntityGloopEnergizer>> GLOOP_ENERGIZER_BE 
        = blockEntities.register(new Identifier(HexGloop.MOD_ID, "gloop_energizer_tile"), 
        () -> BlockEntityType.Builder.create(BlockEntityGloopEnergizer::new, HexGloopBlocks.GLOOP_ENERGIZER_BLOCK.get()).build(null));

    public static RegistrySupplier<BlockEntityType<BlockEntityPedestal>> PEDESTAL_BE
        = blockEntities.register(
            new Identifier(HexGloop.MOD_ID, "pedestal_tile"),
            () -> BlockEntityType.Builder.create(BlockEntityPedestal::new, HexGloopBlocks.PEDESTAL_BLOCK.get(), HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get(), HexGloopBlocks.MIND_PEDESTAL_BLOCK.get()).build(null)
        );

    public static RegistrySupplier<BlockEntityType<BlockEntitySlateChest>> SLATE_CHEST_BE
        = blockEntities.register(
            new Identifier(HexGloop.MOD_ID, "slate_chest_tile"),
            () -> BlockEntityType.Builder.create(BlockEntitySlateChest::new, HexGloopBlocks.SLATE_CHEST_BLOCK.get(), HexGloopBlocks.GLOOPY_SLATE_CHEST_BLOCK.get()).build(null)
        );

    public static RegistrySupplier<BlockEntityType<BlockEntityConjuredRedstone>> CONJURED_REDSTONE_BE 
        = blockEntities.register(new Identifier(HexGloop.MOD_ID, "conjured_redstone_tile"), 
        () -> BlockEntityType.Builder.create(BlockEntityConjuredRedstone::new, HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get()).build(null));

    public static RegistrySupplier<BlockEntityType<BlockEntityDial>> DIAL_BE 
        = blockEntities.register(new Identifier(HexGloop.MOD_ID, "iotic_dial_tile"), 
        () -> BlockEntityType.Builder.create(BlockEntityDial::new, HexGloopBlocks.IOTIC_DIAL_BLOCK.get()).build(null));

    public static void register(){
        blockEntities.register();
    }
}
