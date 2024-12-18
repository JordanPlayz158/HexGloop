package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.casting.canvas.OpGetBlockColor;
import com.samsthenerd.hexgloop.casting.canvas.OpPutColor;
import com.samsthenerd.hexgloop.casting.dimensions.OpIsInDimension;
import com.samsthenerd.hexgloop.casting.gloopifact.OpReadGloopifact;
import com.samsthenerd.hexgloop.casting.gloopifact.OpSyncRavenmindGloopifact;
import com.samsthenerd.hexgloop.casting.gloopifact.OpWriteGloopifact;
import com.samsthenerd.hexgloop.casting.inventorty.OpGetInventoryLore;
import com.samsthenerd.hexgloop.casting.inventorty.OpGetTypeInSlot;
import com.samsthenerd.hexgloop.casting.inventorty.OpSlotCount;
import com.samsthenerd.hexgloop.casting.inventorty.OpStackTransfer;
import com.samsthenerd.hexgloop.casting.ioticblocks.OpReadBlock;
import com.samsthenerd.hexgloop.casting.ioticblocks.OpWriteBlock;
import com.samsthenerd.hexgloop.casting.mirror.OpBindMirror;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpEvalCatchMishap;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpHahaFunnyAssertQEDGetItLikeTheMathProofLol;
import com.samsthenerd.hexgloop.casting.mishapprotection.OpRevealLastMishap;
import com.samsthenerd.hexgloop.casting.orchard.OpReadOrchard;
import com.samsthenerd.hexgloop.casting.redstone.OpConjureRedstone;
import com.samsthenerd.hexgloop.casting.redstone.OpGetComparator;
import com.samsthenerd.hexgloop.casting.tags.OpCheckTag;
import com.samsthenerd.hexgloop.casting.tags.OpGetTags;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyReadIota;
import com.samsthenerd.hexgloop.casting.trinketyfocii.OpTrinketyWriteIota;
import com.samsthenerd.hexgloop.casting.truenameclassaction.OpAgreeTruenameEULA;
import com.samsthenerd.hexgloop.casting.truenameclassaction.OpGetCoinBinder;
import com.samsthenerd.hexgloop.casting.truenameclassaction.OpRefreshTruename;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.casting.actions.spells.OpMakePackagedSpell;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class HexGloopRegisterPatterns {

    public static void registerPatterns(){
        registerItemDependentPatterns();
        HexGloopItems.FOCAL_RING.listen(event -> registerTrinketyFociiPatterns());
        HexGloopBlocks.CONJURED_REDSTONE_BLOCK.listen(event -> registerRedstonePatterns());
        maybeRegisterHexal();
        maybeRegisterMoreIotas();
        // non item dependent stuff: 
        try{
            // orchard patterns
            HexActions.make("read_orchard", new ActionRegistryEntry(
                HexPattern.fromAngles("dqqqqq", HexDir.SOUTH_EAST),
                new OpReadOrchard(false)));

            HexActions.make("read_orchard_list", new ActionRegistryEntry(
                HexPattern.fromAngles("dqqqqqdeeeqdqeee", HexDir.SOUTH_EAST),
                new OpReadOrchard(true)));
            
            // dimension checks
            HexActions.make("in_overworld", new ActionRegistryEntry(
                HexPattern.fromAngles("aqawqadaqdeeweweweew", HexDir.SOUTH_EAST),
                new OpIsInDimension(World.OVERWORLD)));
            HexActions.make("in_nether", new ActionRegistryEntry(
                HexPattern.fromAngles("eaqawqadaqdeewewewe", HexDir.EAST),
                new OpIsInDimension(World.NETHER)));

            HexActions.make("opnop_useless", new ActionRegistryEntry(
                HexPattern.fromAngles("ddeaqq", HexDir.NORTH_EAST),
                new OpNop()));

            HexActions.make("check_ambit", new ActionRegistryEntry(
                HexPattern.fromAngles("wawaw", HexDir.EAST),
                new OpCheckAmbit()));

            // catchy eval
            HexActions.make("catchy_eval", new ActionRegistryEntry(
                HexPattern.fromAngles("dweaqqw", HexDir.SOUTH_EAST),
                OpEvalCatchMishap.INSTANCE));
            HexActions.make("reveal_mishap", new ActionRegistryEntry(
                HexPattern.fromAngles("dweaqqqqa", HexDir.SOUTH_EAST),
                new OpRevealLastMishap(true)));

            HexActions.make("assert", new ActionRegistryEntry(
                HexPattern.fromAngles("qed", HexDir.NORTH_EAST),
                new OpHahaFunnyAssertQEDGetItLikeTheMathProofLol()));

            HexActions.make("gloopimind_upload", new ActionRegistryEntry(
                HexPattern.fromAngles("qewdweeddw", HexDir.NORTH_EAST),
                new OpSyncRavenmindGloopifact(true)));
            HexActions.make("gloopimind_download", new ActionRegistryEntry(
                HexPattern.fromAngles("qedadewdde", HexDir.NORTH_EAST),
                new OpSyncRavenmindGloopifact(false)));
            
            // bound mirror pedestal stuff:
            // normal bind: deeeedwwdwdw, north east
            HexActions.make("bind_mirror", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeedwwdwdw", HexDir.NORTH_EAST),
                new OpBindMirror(false)));
            // temp bind  : aqqqqawwawaw, north west
            HexActions.make("temp_bind_mirror", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqawwawaw", HexDir.NORTH_WEST),
                new OpBindMirror(true)));

            // hotbar patterns
            HexActions.make("set_hotbar_slot", new ActionRegistryEntry(
                HexPattern.fromAngles("dwewdwe", HexDir.WEST),
                new OpHotbar(false, false)));

            HexActions.make("get_hotbar_slot", new ActionRegistryEntry(
                HexPattern.fromAngles("qwawqwa", HexDir.EAST),
                new OpHotbar(false, true)));

            HexActions.make("swap_hotbar_slot", new ActionRegistryEntry(
                HexPattern.fromAngles("qwawqwadawqwa", HexDir.EAST),
                new OpHotbar(true, false)));
            
            // inventorty patterns
            HexActions.make("torty_transfer", new ActionRegistryEntry(
                HexPattern.fromAngles("wawqwaqewdwwdade", HexDir.SOUTH_WEST),
                new OpStackTransfer()));
            HexActions.make("torty_count", new ActionRegistryEntry(
                HexPattern.fromAngles("awewdqdwewaaw", HexDir.EAST),
                new OpSlotCount(true)));
            HexActions.make("torty_max_count", new ActionRegistryEntry(
                HexPattern.fromAngles("ewdqdwewaqae", HexDir.NORTH_WEST),
                new OpSlotCount(false)));

            HexActions.make("torty_inv_sizes", new ActionRegistryEntry(
                HexPattern.fromAngles("qaeaqeaq", HexDir.EAST),
                new OpGetInventoryLore(true)));

            // gloopifact patterns
            HexActions.make("gloopifact_read", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqeqadaqw", HexDir.NORTH_EAST),
                new OpReadGloopifact(false)));
            HexActions.make("gloopifact_write", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeeqqadaqw", HexDir.NORTH_EAST),
                new OpWriteGloopifact(false)));
            HexActions.make("gloopifact_check_read", new ActionRegistryEntry(
                HexPattern.fromAngles("wwqadaqwwaqqqqq", HexDir.NORTH_EAST),
                new OpReadGloopifact(true)));
            HexActions.make("gloopifact_check_write", new ActionRegistryEntry(
                HexPattern.fromAngles("wwqadaqwwdeeeee", HexDir.NORTH_EAST),
                new OpWriteGloopifact(true)));
            

            // qwawqwadawqwqwqwqwqw <- simpler sign write with hexagon
            HexActions.make("set_label", new ActionRegistryEntry(
                HexPattern.fromAngles("wwedwewdweqawqwqwqwqwqw", HexDir.SOUTH_WEST),
                new OpSetLabel()));

            // snack
            HexActions.make("conjure_tasty_treat", new ActionRegistryEntry(
                HexPattern.fromAngles("eeewdw", HexDir.SOUTH_WEST),
                new OpConjureTastyTreat()));
            
            // for coins really, i mean i guess could be expanded to other stuff later though ?
            HexActions.make("get_item_bound_caster", new ActionRegistryEntry(
                HexPattern.fromAngles("qaqqaqqqqq", HexDir.NORTH_EAST),
                new OpGetCoinBinder(false)));
            HexActions.make("compare_item_bound_caster", new ActionRegistryEntry(
                HexPattern.fromAngles("qaqqaqqqqqead", HexDir.NORTH_EAST),
                new OpGetCoinBinder(true)));
            HexActions.make("cooler_get_item_bound_caster", new ActionRegistryEntry(
                HexPattern.fromAngles("qaqqwawqwqwqwqwqw", HexDir.NORTH_EAST),
                new OpGetCoinBinder(false, true)));
            HexActions.make("cooler_compare_item_bound_caster", new ActionRegistryEntry(
                HexPattern.fromAngles("qaqqwawqwqwqwqwqwead", HexDir.NORTH_EAST),
                new OpGetCoinBinder(true, true)));

            HexActions.make("truename_agree_eula", new ActionRegistryEntry(
                HexPattern.fromAngles("awwqaq", HexDir.SOUTH_EAST),
                new OpAgreeTruenameEULA()));

            // op dispense
            HexActions.make("dispense", new ActionRegistryEntry(
                HexPattern.fromAngles("wqwaeqqqeddqeqd", HexDir.SOUTH_WEST),
                new OpDispense()));
                
            // op frog eat
            HexActions.make("frog_eat", new ActionRegistryEntry(
                HexPattern.fromAngles("wawadawaewqaw", HexDir.EAST),
                new OpFrogEat()));

            // iotic block read and writes
            HexActions.make("block_read", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqeawqwaw", HexDir.EAST),
                new OpReadBlock(false)));
            HexActions.make("block_write", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeeqdwewewewdw", HexDir.EAST),
                new OpWriteBlock(false)));
            HexActions.make("can_block_read", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqeawqwawe", HexDir.EAST),
                new OpReadBlock(true)));
            HexActions.make("can_block_write", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeeqdwewewewdwe", HexDir.EAST),
                new OpWriteBlock(true)));

            HexActions.make("stonecut", new ActionRegistryEntry(
                HexPattern.fromAngles("edwdqeeeeaaeaeaeaea", HexDir.EAST),
                new OpStoneCut()));

            HexActions.make("put_canvas_color", new ActionRegistryEntry(
                HexPattern.fromAngles("edeaeeeweee", HexDir.SOUTH_EAST),
                new OpPutColor()));
            HexActions.make("get_block_color", new ActionRegistryEntry(
                HexPattern.fromAngles("qqqqeqwawqwa", HexDir.NORTH_WEST),
                new OpGetBlockColor()));

        } catch (IllegalArgumentException exn) {
            exn.printStackTrace();
        }
    }

    private static Map<RegistrySupplier<? extends Item>, UncheckedPatternRegister> itemDependentPatternRegisterers = new HashMap<>();

    static {
        itemDependentPatternRegisterers.put(HexGloopItems.CASTING_POTION_ITEM, () -> {
            HexActions.make("craft/potion", new ActionRegistryEntry(
                HexPattern.fromAngles("wawwedewwqqq", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.CASTING_POTION_ITEM.get(),
                    MediaConstants.SHARD_UNIT)));
        });

        itemDependentPatternRegisterers.put(HexGloopItems.GLOOPIFACT_ITEM, () -> {
            HexActions.make("craft/gloopifact", new ActionRegistryEntry(
                HexPattern.fromAngles("wwaadaqwaweqqwaweewawqwwwwadeeeeeqww", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.GLOOPIFACT_ITEM.get(),
                    MediaConstants.CRYSTAL_UNIT * 16)));
        });

        itemDependentPatternRegisterers.put(HexGloopItems.INVENTORTY_ITEM, () -> {
            HexActions.make("craft/inventorty", new ActionRegistryEntry(
                HexPattern.fromAngles("waqqqqqwqawqedeqwaq", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.INVENTORTY_ITEM.get(),
                    MediaConstants.CRYSTAL_UNIT * 4)));
        });

        itemDependentPatternRegisterers.put(HexGloopItems.HEX_BLADE_ITEM, () -> {
            HexActions.make("craft/hex_blade", new ActionRegistryEntry(
                HexPattern.fromAngles("waqqqqqwwwaqwwwwaq", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.HEX_BLADE_ITEM.get(),
                    MediaConstants.CRYSTAL_UNIT * 4)));
        });

        itemDependentPatternRegisterers.put(HexGloopItems.HEX_PICKAXE_ITEM, () -> {
            HexActions.make("craft/hex_pickaxe", new ActionRegistryEntry(
                HexPattern.fromAngles("wwaqqqqqeaqdewaqweaewqawedqqqeaeq", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.HEX_PICKAXE_ITEM.get(),
                    MediaConstants.CRYSTAL_UNIT * 4)));
        });

        itemDependentPatternRegisterers.put(HexGloopItems.CASTING_FROG_ITEM, () -> {
            HexActions.make("craft/casting_frog", new ActionRegistryEntry(
                HexPattern.fromAngles("wwaqqqqqeaqdqaqedeqaqdqqeaqwdwqae", HexDir.EAST),
                new OpMakePackagedSpell<>(HexGloopItems.CASTING_FROG_ITEM.get(),
                    MediaConstants.CRYSTAL_UNIT * 4)));
        });

        // craft shoe things: waqqqqqwwaqwdwqaw        
    }

    private static void registerItemDependentPatterns(){
        for(Entry<RegistrySupplier<? extends Item>, UncheckedPatternRegister> entry : itemDependentPatternRegisterers.entrySet()){
            entry.getKey().listen(item -> {
                try{
                    entry.getValue().register();
                } catch (IllegalArgumentException exn) {
                    exn.printStackTrace();
                }
            });
        }
    }

    private static void registerRedstonePatterns(){
        try {
            // redstone stuff
            HexActions.make("read_comparator", new ActionRegistryEntry(
                HexPattern.fromAngles("ddwwdwwdd", HexDir.SOUTH_EAST),
                new OpGetComparator(false)));
            HexActions.make("read_redstone", new ActionRegistryEntry(
                HexPattern.fromAngles("aawwawwaa", HexDir.SOUTH_WEST),
                new OpGetComparator(true)));

            HexActions.make("conjure_redstone", new ActionRegistryEntry(
                HexPattern.fromAngles("qqadd", HexDir.NORTH_EAST),
                new OpConjureRedstone()));
        } catch (IllegalArgumentException exn) {
            exn.printStackTrace();
        }
    }

    private static void registerTrinketyFociiPatterns(){
        try {
            // pendant read/write
            HexActions.make("read_pendant", new ActionRegistryEntry(
                HexPattern.fromAngles("waaqqqqqe", HexDir.SOUTH_EAST),
                new OpTrinketyReadIota((ctx) -> List.of("necklace"), false)));
            HexActions.make("write_pendant", new ActionRegistryEntry(
                HexPattern.fromAngles("wadeeeeeq", HexDir.SOUTH_EAST),
                new OpTrinketyWriteIota((ctx) -> List.of("necklace"), false)));
            // pendant checks
            HexActions.make("check_read_pendant", new ActionRegistryEntry(
                HexPattern.fromAngles("waaqqqqqee", HexDir.SOUTH_EAST),
                new OpTrinketyReadIota((ctx) -> List.of("necklace"), true)));
            HexActions.make("check_write_pendant", new ActionRegistryEntry(
                HexPattern.fromAngles("wadeeeeeqe", HexDir.SOUTH_EAST),
                new OpTrinketyWriteIota((ctx) -> List.of("necklace"), true)));

            // ring basics
            Function<CastingEnvironment, List<String>> standardRingFunc = (ctx) -> {
                if(ctx.getCastingHand() == Hand.MAIN_HAND) {
                    return List.of("offhandring", "mainhandring");
                }
                return List.of("mainhandring", "offhandring");
            };
            HexActions.make("read_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqeawqwqwqwqwqw", HexDir.EAST),
                new OpTrinketyReadIota(standardRingFunc, false)));
            HexActions.make("write_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeeqdwewewewewew", HexDir.EAST),
                new OpTrinketyWriteIota(standardRingFunc, false)));
            
            // left hand ring
            HexActions.make("read_left_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqweeeeewqq", HexDir.EAST),
                new OpTrinketyReadIota((ctx) -> List.of("offhandring", "mainhandring"), false)));
            HexActions.make("write_left_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("deewqqqqqwee", HexDir.EAST),
                new OpTrinketyWriteIota((ctx) -> List.of("offhandring", "mainhandring"), false)));
            
            // right hand ring
            HexActions.make("read_right_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqweeeee", HexDir.EAST),
                new OpTrinketyReadIota((ctx) -> List.of("mainhandring", "offhandring"), false)));
            HexActions.make("write_right_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeewqqqqq", HexDir.EAST),
                new OpTrinketyWriteIota((ctx) -> List.of("mainhandring", "offhandring"), false)));
            
            // ring checks
            HexActions.make("check_read_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("aqqqqqeawqwqwqwqwqwe", HexDir.EAST),
                new OpTrinketyReadIota(standardRingFunc, true)));
            HexActions.make("check_write_ring", new ActionRegistryEntry(
                HexPattern.fromAngles("deeeeeqdwewewewewewq", HexDir.EAST),
                new OpTrinketyWriteIota(standardRingFunc, true)));

            HexActions.make("clear_truename", new ActionRegistryEntry(
                HexPattern.fromAngles("wwwdwwwdwqqaqwedeewawwwawww", HexDir.SOUTH_WEST),
                new OpRefreshTruename()));
        } catch (IllegalArgumentException exn) {
            exn.printStackTrace();
        }
    }

    private static void maybeRegisterHexal(){
        if(!Platform.isModLoaded("hexal")) return;
        try {
            HexActions.make("torty_get_type", new ActionRegistryEntry(
                HexPattern.fromAngles("qwaeawqaqded", HexDir.NORTH_EAST),
                new OpGetTypeInSlot()));
            
        } catch (IllegalArgumentException exn) {
            exn.printStackTrace();
        }
    }

    private static void maybeRegisterMoreIotas(){
        if(!Platform.isModLoaded("moreiotas")) return;
        try {
            HexActions.make("stringify_mishap", new ActionRegistryEntry(
                HexPattern.fromAngles("dweaqqqqd", HexDir.SOUTH_EAST),
                new OpRevealLastMishap(false)));

            HexActions.make("torty_inv_names", new ActionRegistryEntry(
                HexPattern.fromAngles("waeaeaeawa", HexDir.EAST),
                new OpGetInventoryLore(false)));
            // tag stuff
            HexActions.make("get_tags", new ActionRegistryEntry(
                HexPattern.fromAngles("wwedwe", HexDir.NORTH_EAST),
                new OpGetTags()));
            HexActions.make("check_tag", new ActionRegistryEntry(
                HexPattern.fromAngles("wwedwew", HexDir.NORTH_EAST),
                new OpCheckTag()));
            
        } catch (IllegalArgumentException exn) {
            exn.printStackTrace();
        }
    }

    @FunctionalInterface
    public static interface UncheckedPatternRegister{
        public void register() throws IllegalArgumentException;
    }
}
