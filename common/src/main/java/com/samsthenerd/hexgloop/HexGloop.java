package com.samsthenerd.hexgloop;

import dev.architectury.registry.registries.RegistrarManager;
import java.util.List;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Suppliers;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.blocks.iotic.HexGloopIoticProviders;
import com.samsthenerd.hexgloop.casting.ContextModificationHandlers;
import com.samsthenerd.hexgloop.casting.HexGloopDiscoverers;
import com.samsthenerd.hexgloop.casting.HexGloopRegisterPatterns;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.misc.GloopBanners;
import com.samsthenerd.hexgloop.misc.HexGloopGameEvents;
import com.samsthenerd.hexgloop.misc.ITrinkety;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes;
import com.samsthenerd.hexgloop.network.HexGloopNetwork;
import com.samsthenerd.hexgloop.recipes.HexGloopRecipes;
import com.samsthenerd.hexgloop.utils.GloopXPlat;
import com.samsthenerd.hexgloop.utils.StringsToDirMap;

import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;

public class HexGloop {
    public static final String MOD_ID = "hexgloop";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ITrinkety TRINKETY_INSTANCE;
    public static GloopXPlat GLOOPXPLAT;

    public static final Supplier<RegistrarManager> REGISTRIES = Suppliers.memoize(() -> RegistrarManager.get(MOD_ID));

	public static final void logPrint(String message){
        if(Platform.isDevelopmentEnvironment()){
            LOGGER.info(message);
        }
	}

    public static List<RegistrySupplier<? extends Item>> FOCUS_ITEMS = List.of(HexGloopItems.MULTI_FOCUS_ITEM, 
        HexGloopItems.FOCAL_PENDANT, HexGloopItems.FOCAL_RING);

    public static void onInitialize() {
        HexGloopBlocks.register();
        HexGloopItems.register();
        HexGloopBEs.register();
        HexGloopRecipes.register();
        HexGloopNetwork.register();
        HexGloopRegisterPatterns.registerPatterns();
        HexGloopGameEvents.register();
        StringsToDirMap.init();
        LabelTypes.registerIotaLabelFunctions();
        HexGloopDiscoverers.init();
        ContextModificationHandlers.init();
        HexGloopIoticProviders.register();
        GloopBanners.registerBannerPatterns();
    }
}
