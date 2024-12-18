package com.samsthenerd.hexgloop.datagen;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class HexGloopDataGenerator implements DataGeneratorEntrypoint {

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    pack.addProvider(TagGenerator::new);
  }

  private static class TagGenerator extends FabricTagProvider.BlockTagProvider {
    public TagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
      super(output, completableFuture);
    }

    // Gonna need to check convention, can't find any clear links though for converting
    //   Material.METAL into tags
    private static final TagKey<Block> METAL_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:metal"));

    private static final TagKey<Block> STONE_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:stone"));

    private static final TagKey<Block> AMETHYST_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:amethyst"));

    private static final TagKey<Block> GLASS_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:glass"));

    private static final TagKey<Block> ORGANIC_PRODUCT_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:organic_product"));

    private static final TagKey<Block> CARPET_BLOCKS = TagKey.of(RegistryKeys.BLOCK,
        new Identifier("c:carpet"));

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
      // Metal Blocks
      addRegistrySuppliers(METAL_BLOCKS,
          HexGloopBlocks.GLOOP_ENERGIZER_BLOCK, HexGloopBlocks.ACCELERATOR_BLOCK);

      // Stone Blocks
      addRegistrySuppliers(STONE_BLOCKS,
          HexGloopBlocks.PEDESTAL_BLOCK, HexGloopBlocks.MIRROR_PEDESTAL_BLOCK,
          HexGloopBlocks.MIND_PEDESTAL_BLOCK, HexGloopBlocks.SLATE_CHEST_BLOCK,
          HexGloopBlocks.GLOOPY_SLATE_CHEST_BLOCK, HexGloopBlocks.REDSTONE_WAVE_LOCUS_BLOCK,
          HexGloopBlocks.SYNCETRIX_BLOCK, HexGloopBlocks.SLATE_LAMP_BLOCK,
          HexGloopBlocks.ENLIGHTENMENT_BRIDGE_BLOCK);

      // Amethyst Blocks
      addRegistrySuppliers(AMETHYST_BLOCKS,
          HexGloopBlocks.IOTIC_DIAL_BLOCK, HexGloopBlocks.CONJURED_REDSTONE_BLOCK,
          HexGloopBlocks.SENTINEL_BED_BLOCK, HexGloopBlocks.CHARGED_AMETHYST_BLOCK,
          HexGloopBlocks.ENLIGHTENMENT_GATE_BLOCK);

      // Glass Blocks
      addRegistrySuppliers(GLASS_BLOCKS,
          HexGloopBlocks.HEXXED_GLASS_BLOCK);

      // Organic Product Blocks
      addRegistrySuppliers(ORGANIC_PRODUCT_BLOCKS,
          HexGloopBlocks.GLOOP_BLOCK);

      // Carpet Blocks
      addRegistrySuppliers(CARPET_BLOCKS,
          HexGloopBlocks.THINKING_CARPET_BLOCK);
    }

    private void addRegistrySuppliers(TagKey<Block> tag, RegistrySupplier<? extends Block>... suppliers) {
      getOrCreateTagBuilder(tag).add((RegistryKey<Block>[]) Arrays.stream(suppliers).map(Supplier::get).toArray());
    }
  }
}
