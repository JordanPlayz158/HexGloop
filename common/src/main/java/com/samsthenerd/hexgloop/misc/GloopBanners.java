package com.samsthenerd.hexgloop.misc;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;

public class GloopBanners {
    public static final TagKey<BannerPattern> HERMES_PATTERN_ITEM_KEY = TagKey.of(RegistryKeys.BANNER_PATTERN, new Identifier(HexGloop.MOD_ID, "pattern_item/hermes"));
    public static final Registrar<BannerPattern> BANNER_PATTERNS = HexGloop.REGISTRIES.get().get(RegistryKeys.BANNER_PATTERN);

    public static void registerBannerPatterns(){
        BANNER_PATTERNS.register(new Identifier(HexGloop.MOD_ID, "hermes"), () -> new BannerPattern("hermes"));
    }
}
