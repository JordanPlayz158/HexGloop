package com.samsthenerd.hexgloop.items;

import java.util.Set;
import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.items.ItemFidget.FidgetSettings;
import com.samsthenerd.hexgloop.misc.GloopBanners;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel;
import com.samsthenerd.hexgloop.misc.wnboi.LabelTypes.PatternLabel.PatternOptions;
import com.samsthenerd.wnboi.utils.RenderUtils;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.items.ItemStaff;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.BannerPatternItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class HexGloopItems{
    public static DeferredRegister<Item> items = DeferredRegister.create(HexGloop.MOD_ID, RegistryKeys.ITEM);

    public static final RegistrySupplier<Item> GLOOP_ITEM = item("gloop", 
        () -> new ItemSimpleMediaProvider(defaultSettings(), MediaConstants.CRYSTAL_UNIT * 2, 950));
    public static final RegistrySupplier<Item> SYNCHRONOUS_GLOOP_ITEM = item("synchronous_gloop", 
        () -> new Item(defaultSettings()));
    public static final RegistrySupplier<ItemGloopifact> GLOOPIFACT_ITEM = item("gloopifact",
        () -> new ItemGloopifact(defaultSettings().maxCount(1).fireproof()));
    public static final RegistrySupplier<ItemMultiFocus> MULTI_FOCUS_ITEM = item("multi_focus", 
        () -> new ItemMultiFocus(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingRing> CASTING_RING_ITEM = item("casting_ring", 
        () -> new ItemCastingRing(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemCastingPotion> CASTING_POTION_ITEM = item("casting_potion", 
        () -> new ItemCastingPotion(defaultSettings().maxCount(16)));
    public static final RegistrySupplier<ItemGloopDye> GLOOP_DYE_ITEM = item("gloop_dye", 
        () -> new ItemGloopDye(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemDyeableSpellbook> DYEABLE_SPELLBOOK_ITEM = item("covered_spellbook",
        () -> new ItemDyeableSpellbook(defaultSettings().maxCount(1).fireproof()));
    
    // just so i can show a spellbook in rei/patchoulli without actually worrying about it being valid
    public static final RegistrySupplier<Item> FAKE_SPELLBOOK_PLACEHOLDER = item("fake_spellbook_for_rei",
        () -> new Item(new Item.Settings()));


    public static final RegistrySupplier<ItemFocus> FOCAL_PENDANT = item("focal_pendant",
        () -> new ItemFocus(defaultSettings().maxCount(1)));
    public static final RegistrySupplier<ItemFocus> FOCAL_RING = item("focal_ring",
        () -> new ItemFocus(defaultSettings().maxCount(1).fireproof()));
    public static final RegistrySupplier<ItemHandMirror> HAND_MIRROR_ITEM = item("hand_mirror",
        () -> new ItemHandMirror(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemCastersCoin> CASTERS_COIN = item("casters_coin",
        () -> new ItemCastersCoin(defaultSettings()));

    public static final RegistrySupplier<ItemHeartPigment> HEART_PIGMENT = item("heart_pigment",
        () -> new ItemHeartPigment(defaultSettings()));


    public static final RegistrySupplier<Item> SLATE_BOWL = item("slate_bowl", 
        () -> new Item(defaultSettings()));

    public static final int HEX_SNACK_MEDIA = MediaConstants.DUST_UNIT / 2;

    public static final RegistrySupplier<Item> HEX_SNACK = item("hex_snack",
        () -> new ItemSimpleMediaProvider(defaultSettings().food((new FoodComponent.Builder()).hunger(2).saturationModifier(0.5f).snack().build()),
            HEX_SNACK_MEDIA, 1));

    public static final RegistrySupplier<Item> HEXXY_OS = item("hexxyos",
        () -> new ItemHexxyOs(defaultSettings().food((new FoodComponent.Builder()).hunger(8).saturationModifier(0.8f).build())));

    public static final RegistrySupplier<Item> HEXXY_PLUSH = item("hexxy_plush", () -> new BannerPatternItem(GloopBanners.HERMES_PATTERN_ITEM_KEY, defaultSettings().maxCount(1)));


    public static final RegistrySupplier<ItemHexSword> HEX_BLADE_ITEM = item("hex_blade", 
        () -> new ItemHexSword(defaultSettings().maxCount(1).fireproof()));
    public static final RegistrySupplier<ItemHexMiningTool> HEX_PICKAXE_ITEM = item("hex_pickaxe", 
        () -> new ItemHexMiningTool(defaultSettings().maxCount(1).fireproof(), 2.0F, -2.4f, Set.of(BlockTags.PICKAXE_MINEABLE), false)); 

    public static final RegistrySupplier<ItemSlateLoader> SLATE_LOADER_ITEM = item("slate_loader",
        () -> new ItemSlateLoader(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<Item> MEPT_LOG_ITEM = item("mept_log",
        () -> new Item(defaultSettings()));

    public static final RegistrySupplier<ItemInventorty> INVENTORTY_ITEM = item("inventorty",
        () -> new ItemInventorty(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemCastingFrog> CASTING_FROG_ITEM = item("casting_frog",
        () -> new ItemCastingFrog(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemEssenceStone> ESSENCE_STONE_ITEM = item("essence_stone",
        () -> new ItemEssenceStone(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemPatternScript> SCRIPT_ITEM = item("script",
        () -> new ItemPatternScript(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemCopingSaw> COPING_SAW_ITEM = item("coping_saw",
        () -> new ItemCopingSaw(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemLibraryCard> LIBRARY_CARD_ITEM = item("library_card",
        () -> new ItemLibraryCard(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<Item> EMPTY_JAR_ITEM = item("empty_jar",
        () -> new Item(defaultSettings().maxCount(16)));

    public static final RegistrySupplier<ItemMindJar> MIND_JAR_ITEM = item("mind_jar",
        () -> new ItemMindJar(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemCosmeticEnergizer> COSMETIC_ENERGIZER_ITEM = item("cosmetic_energizer",
        () -> new ItemCosmeticEnergizer(defaultSettings().maxCount(1)));

    public static final RegistrySupplier<ItemSlateCanvas> SLATE_CANVAS_ITEM = item("slate_canvas",
        () -> new ItemSlateCanvas(defaultSettings()));

    // fidgets -- todo: maybe move these out of here and into their own file

    public static final RegistrySupplier<ItemFidget> COPPER_PEN_FIDGET = item("copper_pen_fidget", 
        () -> new ItemFidget(defaultSettings().maxCount(1), 
        new FidgetSettings(2, Math.PI*0.75, 0xC0_C15A36, 0xC0_CFA0F2, 0xFF_6D3422, 0xFF_54398A){
            @Override
            public Pair<Integer, Integer> getCurveOptions(int index, boolean isCurrent, boolean isSelected){
                if(isCurrent || isSelected)
                    return new Pair<Integer, Integer>(RenderUtils.buildCurveOptions(1, 1, false, false), 2);
                return new Pair<Integer, Integer>(RenderUtils.buildCurveOptions(0, 0, false, false), 10);
            }
        }));

    public static final RegistrySupplier<ItemDyeableFidget> DREIDEL_FIDGET = item("dreidel_fidget", 
        () -> new ItemDyeableFidget(defaultSettings().maxCount(1), 
        new FidgetSettings(4, Math.PI*0.75, 0xC0_49acff, 0xC0_ffe44a, 0xFF_3254ab, 0xFF_f2a600)
        ));

    public static final RegistrySupplier<ItemFidget> RAINBOW_AMOGUS_FIDGET = item("rainbow_amogus_fidget", 
        () -> new ItemFidget(defaultSettings().maxCount(1), new FidgetSettings(6){
            @Override
            public Label getDefaultLabel(int i, boolean isCurrent, boolean isSelected){
                if(i == 0){
                    return new PatternLabel(HexPattern.fromAngles("dewdeqawwqwwedwewdweqaqedaqw", HexDir.EAST), 
                        new PatternOptions(0xFF_FFFFFF,//(isCurrent ? DARK_AMOGUS[0] : MID_AMOGUS[0]), 
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]) & 0x00_FFFFFF | 0x40_000000, 
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]) & 0x00_FFFFFF | 0x60_000000, 
                        // , 0x40_BFBFBF, 0x60_BFBFBF,
                        (isCurrent ? MID_AMOGUS[0] : LIGHT_AMOGUS[0]), 0f, 0f, false));
                }
                return super.getDefaultLabel(i, isCurrent, isSelected);
            }

            // picked from the amogus texture
            public static int[] DARK_AMOGUS = {0xFF_990001, 0xFF_8C3D00, 0xFF_8A8C00, 0xFF_218C00, 0xFF_00678B, 0xFF_4D008C};
            public static int[] MID_AMOGUS = {0xFF_CC1413, 0xFF_E56F17, 0xFF_E2E518, 0xFF_47E519, 0xFF_15AEE5, 0xFF_8916E6};
            public static int[] LIGHT_AMOGUS = {0xFF_FF6666, 0xFF_FFA865, 0xFF_FCFF65, 0xFF_8AFF66, 0xFF_66D6FF, 0xFF_BA66FF};

            @Override
            public int getColorFill(int index, int vI, int numOuter, int numInner, boolean isInner, boolean isCurrent, boolean isSelected){
                return (isCurrent ? MID_AMOGUS[index] : LIGHT_AMOGUS[index]) & 0x00_FFFFFF | 0xC0_000000;
            }
    
            @Override
            public int getColorOutline(int index, int vI, boolean isCurrent, boolean isSelected){
                return isCurrent ? DARK_AMOGUS[index] : MID_AMOGUS[index];
            }
        }));

    public static final String[] NEW_STAFF_IDS = {"bone_staff", "quartz_staff", "carrot_staff", "bee_staff", "rod_staff",
        "ice_staff", "blaze_staff", "wither_staff", "pumpkin_staff", "candy_cane_staff", "menorah_staff", "christmas_tree_staff", "lovely_staff", "longinus_staff", "owl_staff", "ghost_staff", "celestial_staff"};
    static {
        for(String id : NEW_STAFF_IDS){
            item(id, () -> new ItemStaff(defaultSettings().maxCount(1)));
        }
    }
    

    public static <T extends Item> RegistrySupplier<T> item(String name, Supplier<T> item) {
		return items.register(new Identifier(HexGloop.MOD_ID, name), item);
	}

    public static Item.Settings defaultSettings(){
        return new Item.Settings().group(HEX_GLOOP_GROUP);
    }

    public static final ItemGroup HEX_GLOOP_GROUP = CreativeTabRegistry.create(
		new Identifier(HexGloop.MOD_ID, "general"),
		() -> GLOOP_ITEM.get().getDefaultStack());

    public static void register(){
        items.register();
    }
}
