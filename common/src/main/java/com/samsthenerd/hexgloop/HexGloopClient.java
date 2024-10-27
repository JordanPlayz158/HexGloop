package com.samsthenerd.hexgloop;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.mojang.datafixers.util.Pair;
import com.samsthenerd.hexgloop.blockentities.BERConjuredRedstone;
import com.samsthenerd.hexgloop.blockentities.BERHexChest;
import com.samsthenerd.hexgloop.blockentities.BlockEntityDial;
import com.samsthenerd.hexgloop.blockentities.BlockEntityGloopEnergizer;
import com.samsthenerd.hexgloop.blockentities.BlockEntityPedestal;
import com.samsthenerd.hexgloop.blockentities.HexGloopBEs;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.items.HexGloopItems;
import com.samsthenerd.hexgloop.items.ItemCastersCoin;
import com.samsthenerd.hexgloop.items.ItemCastingFrog;
import com.samsthenerd.hexgloop.items.ItemEssenceStone;
import com.samsthenerd.hexgloop.items.ItemFidget;
import com.samsthenerd.hexgloop.items.ItemGloopDye;
import com.samsthenerd.hexgloop.items.ItemGloopifact;
import com.samsthenerd.hexgloop.items.ItemHandMirror;
import com.samsthenerd.hexgloop.items.ItemHexSword;
import com.samsthenerd.hexgloop.items.ItemHexTool;
import com.samsthenerd.hexgloop.items.ItemLibraryCard;
import com.samsthenerd.hexgloop.items.ItemSlateLoader;
import com.samsthenerd.hexgloop.keybinds.HexGloopKeybinds;
import com.samsthenerd.hexgloop.misc.StaffColorLoader;
import com.samsthenerd.hexgloop.misc.clientgreatbook.GreatBook;
import com.samsthenerd.hexgloop.network.HexGloopNetwork;
import com.samsthenerd.hexgloop.network.ServerSideCheckClient;
import com.samsthenerd.hexgloop.utils.GloopUtils;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.storage.ItemFocus;
import at.petrak.hexcasting.common.items.storage.ItemSpellbook;
import at.petrak.hexcasting.common.items.magic.ItemCreativeUnlocker;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import dev.architectury.event.events.client.ClientTextureStitchEvent;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.item.ItemPropertiesRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

public class HexGloopClient {
    public static Random random = new Random();

    public static boolean isServerSide = false;

    public static Tessellator newTess;

    public static void onInitializeClient() {
        HexGloop.logPrint("Initializing HexGloopClient");
        addToTextureAtlas();
        HexGloopNetwork.registerClientSideOnly();
        registerModelPredicates();
        registerColorProviders();
        registerScryingDisplayers();
        StaffColorLoader.init();
        HexGloopKeybinds.registerKeybinds();

        ServerSideCheckClient.registerDisconnectUpdate();

        GreatBook.registerLoadEvent();
        registerRenderers();

        newTess = new Tessellator();
    }

    private static void addToTextureAtlas(){
        ClientTextureStitchEvent.PRE.register((SpriteAtlasTexture atlas, Consumer<Identifier> spriteAdder) -> {
            if(atlas.getId().equals(TexturedRenderLayers.CHEST_ATLAS_TEXTURE)){
                spriteAdder.accept(new Identifier(HexGloop.MOD_ID, "entity/chest/gloopy_slate_chest"));
                spriteAdder.accept(new Identifier(HexGloop.MOD_ID, "entity/chest/slate_chest"));
            }
            if(atlas.getId().equals(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)){
                // load patchi lectern book textures if they exist
                if(!BookRegistry.INSTANCE.isLoaded()) HexGloop.logPrint("patchi books not loaded");
                for(Book book : BookRegistry.INSTANCE.books.values()){
                    Identifier textureId = new Identifier(book.id.getNamespace(), "entity/lectern_" + book.id.getPath());
                    spriteAdder.accept(textureId);
                }
            }
        });
    }

    private static void registerRenderers(){
        BlockEntityRendererRegistry.register(HexGloopBEs.CONJURED_REDSTONE_BE.get(), BERConjuredRedstone::new);
        BlockEntityRendererRegistry.register(HexGloopBEs.SLATE_CHEST_BE.get(), BERHexChest::new);
        RenderTypeRegistry.register(RenderLayer.getTranslucent(), HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get(), HexGloopBlocks.HEXXED_GLASS_BLOCK.get(),
            HexGloopBlocks.ENLIGHTENMENT_BRIDGE_BLOCK.get(), HexGloopBlocks.ENLIGHTENMENT_GATE_BLOCK.get(), HexGloopBlocks.GLOOP_BLOCK.get());

        RenderTypeRegistry.register(RenderLayer.getCutout(), HexGloopBlocks.IOTIC_DIAL_BLOCK.get());
    }

    private static void registerColorProviders(){
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                NbtCompound iotaNbt = HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack);
                if(iotaNbt == null){
                    return 0xFFFFFF; //white
                }
                return HexIotaTypes.getColor(iotaNbt);
            }
            return 0xFFFFFF; //white
		}, HexGloopItems.MULTI_FOCUS_ITEM.get());

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            FrozenPigment colorizer = HexGloopItems.CASTING_POTION_ITEM.get().getColorizer(stack);
            if(tintIndex == 0 || tintIndex >= 5 || colorizer == null){
                return 0xFFFFFF; //white
            }
            return tintsFromColorizer(colorizer, tintIndex-1, 4);
        }, HexGloopItems.CASTING_POTION_ITEM.get());

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1 || tintIndex > 4){ // base
                return 0xFFFFFF; //white
            }
            int color = ItemGloopDye.getDyeColor(stack);
            if(tintIndex == 0){
                return color;
            }
            if(tintIndex == 2){ // red
                return color & 0xFF0000;
            }
            if(tintIndex == 3){ // green
                return color & 0x00FF00;
            }
            if(tintIndex == 4){ // blue
                return color & 0x0000FF;
            }
            return 0xFFFFFF; //white
        }, HexGloopItems.GLOOP_DYE_ITEM.get());

        ItemConvertible[] NEW_FOCII = {HexGloopItems.FOCAL_PENDANT.get(), HexGloopItems.FOCAL_RING.get(), HexGloopItems.CASTERS_COIN.get(), 
            HexGloopItems.GLOOPIFACT_ITEM.get()};
        
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                return GloopUtils.getIotaColor(stack);
            }
            return 0xFF_FFFFFF;
        }, NEW_FOCII);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                return HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get().getColor(stack);
            }
            if(tintIndex == 2){
                return GloopUtils.getIotaColor(stack);
            }
            return 0xFF_FFFFFF;
        }, HexGloopItems.DYEABLE_SPELLBOOK_ITEM);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 0){
                return HexGloopItems.DREIDEL_FIDGET.get().getColor(stack);
            }
            return 0xFF_FFFFFF;
        }, HexGloopItems.DREIDEL_FIDGET);

        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if(tintIndex == 1){
                return HexGloopItems.ESSENCE_STONE_ITEM.get().getColors(stack).getLeft();
            }
            if(tintIndex == 2){
                return HexGloopItems.ESSENCE_STONE_ITEM.get().getColors(stack).getRight();
            }
            return 0xFF_FFFFFF;
        }, HexGloopItems.ESSENCE_STONE_ITEM);

        int emptyColor = 0x382f40;

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
            if(!(world.getBlockEntity(pos) instanceof BlockEntityDial dialBE)){
                return emptyColor;
            }
            ItemStack stack = dialBE.getInnerMultiFocus().copy();
            if(stack.isEmpty()){
                return emptyColor;
            }
            if(tintIndex == 0){
                NbtCompound tag = HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack);
                if(tag == null){
                    return emptyColor;
                }
                return HexIotaTypes.getColor(tag);
            }
            int selIndex = tintIndex + (tintIndex >= dialBE.getSelection() ? 1 : 0);
            NbtCompound tag = HexGloopItems.MULTI_FOCUS_ITEM.get().readSlotIotaTag(stack, selIndex);
            if(tag == null){
                return emptyColor;
            }
            return HexIotaTypes.getColor(tag);
        }, HexGloopBlocks.IOTIC_DIAL_BLOCK);
    }

    public static int tintsFromColorizer(FrozenPigment colorizer, int tintIndex, int sections){
        float time = MinecraftClient.getInstance().world.getTime();
        double section = 5.0 * tintIndex;
        return colorizer.getColor(time, new Vec3d(section, 0, 0));
    }

    private static void registerModelPredicates(){
        ItemPropertiesRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("selected"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            int slot = ItemSpellbook.getPage(itemStack, -1);
            if(slot < 1 || HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(itemStack) == null){
                return 0.0F;
            }
            return (float)((1/6.0)*(slot));
        });

        ItemPropertiesRegistry.register(HexGloopItems.MULTI_FOCUS_ITEM.get(), new Identifier("sealed"), 
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
            return ItemSpellbook.isSealed(itemStack) ? 1.0F : 0.0F;
        });

        ItemPropertiesRegistry.register(HexGloopItems.CASTING_POTION_ITEM.get(), new Identifier("colorized"),
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                FrozenPigment colorizer = HexGloopItems.CASTING_POTION_ITEM.get().getColorizer(itemStack);
                return (colorizer == null) ? 0.0F : 1.0F;
            }
        );

        ItemPropertiesRegistry.register(HexGloopItems.CASTING_POTION_ITEM.get(), new Identifier("hashex"),
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                return (HexGloopItems.CASTING_POTION_ITEM.get().hasHex(itemStack)) ? 1.0F : 0.0F;
            }
        );

        UnclampedModelPredicateProvider focusModelProvider = (stack, level, holder, holderID) -> {
            if(!(stack.getItem() instanceof IotaHolderItem iotaHolder)) return 0;
            if (iotaHolder.readIotaTag(stack) == null && !NBTHelper.hasString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY)) {
                return 0;
            }
            // so it works for dyebook too
            if(stack.getItem() instanceof ItemSpellbook && ItemSpellbook.isSealed(stack)){
                return 1;
            }
            if (stack.getNbt() != null && stack.getNbt().contains(ItemFocus.TAG_SEALED) && stack.getNbt().getBoolean(ItemFocus.TAG_SEALED)) {
                return 1;
            }
            return 0.5f;
        };

        ItemPropertiesRegistry.register(HexGloopItems.FOCAL_PENDANT.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);
        ItemPropertiesRegistry.register(HexGloopItems.FOCAL_RING.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);
        ItemPropertiesRegistry.register(HexGloopItems.DYEABLE_SPELLBOOK_ITEM.get(), ItemFocus.OVERLAY_PRED, focusModelProvider);

        ItemPropertiesRegistry.register(HexGloopItems.CASTERS_COIN.get(), ItemCastersCoin.OVERLAY_PRED,
            (ItemStack itemStack, ClientWorld clientWorld, LivingEntity livingEntity, int i) -> {
                return (HexGloopItems.CASTERS_COIN.get().isBound(itemStack)) ? 1.0F : 0.0F;
            }
        );

        ItemPropertiesRegistry.register(HexGloopItems.GLOOPIFACT_ITEM.get(), ItemFocus.OVERLAY_PRED, (stack, level, holder, holderID) -> {
            ItemGloopifact gloopifactItem = HexGloopItems.GLOOPIFACT_ITEM.get();
            if(!gloopifactItem.hasHex(stack)){
                return 0;
            }
            if(gloopifactItem.readIotaTag(stack) == null){
                return 0.5f;
            }
            return 1f;
        });

        ItemPropertiesRegistry.register(HexGloopItems.HAND_MIRROR_ITEM.get(), ItemHandMirror.MIRROR_ACTIVATED_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.HAND_MIRROR_ITEM.get().isMirrorActivated(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.SLATE_LOADER_ITEM.get(), ItemSlateLoader.ACTIVATED_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.SLATE_LOADER_ITEM.get().hasPatterns(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.SLATE_CANVAS_ITEM.get(), ItemSlateLoader.ACTIVATED_PRED, (stack, level, holder, holderID) -> {
            return FilledMapItem.getMapId(stack) != null ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.INVENTORTY_ITEM.get(), ItemPackagedHex.HAS_PATTERNS_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.INVENTORTY_ITEM.get().hasHex(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.CASTING_FROG_ITEM.get(), ItemPackagedHex.HAS_PATTERNS_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.CASTING_FROG_ITEM.get().hasHex(stack) ? 1 : 0;
        });
        ItemPropertiesRegistry.register(HexGloopItems.CASTING_FROG_ITEM.get(), ItemCastingFrog.FROG_VARIANT_PREDICATE, (stack, level, holder, holderID) -> {
            FrogVariant variant = HexGloopItems.CASTING_FROG_ITEM.get().getFrogVariant(stack);
            if(variant == null){
                World world = level != null ? level : MinecraftClient.getInstance().world;
                BlockPos pos = (holder != null ? holder : MinecraftClient.getInstance().player).getBlockPos();
                variant = ItemCastingFrog.getFrogVariantFromWorld(world, pos);
            }
            // i *guess* it makes sense to just have this be an if statement like this but it's a bit silly 
            if(variant == FrogVariant.COLD){
                return 0; // green
            }
            if(variant == FrogVariant.TEMPERATE){
                return 0.5f; // orange
            }
            if(variant == FrogVariant.WARM){
                return 1; // white
            }
            return 0.5f; // idk default 
        });

        ItemPropertiesRegistry.register(HexGloopItems.ESSENCE_STONE_ITEM.get(), ItemEssenceStone.ESSENCE_PREDICATE, (stack, level, holder, holderID) -> {
            return HexGloopItems.ESSENCE_STONE_ITEM.get().hasEssence(stack) ? 1 : 0;
        });

        ItemPropertiesRegistry.register(HexGloopItems.SCRIPT_ITEM.get(), ItemFocus.OVERLAY_PRED, (stack, level, holder, holderID) -> {
            return HexGloopItems.SCRIPT_ITEM.get().readIotaTag(stack) == null ? 0 : 1;
        });

        ItemPropertiesRegistry.register(HexGloopItems.LIBRARY_CARD_ITEM.get(), ItemLibraryCard.DIMENSION_PREDICATE, (stack, level, holder, holderID) -> {
            return HexGloopItems.LIBRARY_CARD_ITEM.get().getPredicateValue(HexGloopItems.LIBRARY_CARD_ITEM.get().getDimension(stack));
        });

        UnclampedModelPredicateProvider HEX_TOOL_PROVIDER = (stack, level, holder, holderID) -> {
            if(!(stack.getItem() instanceof ItemHexTool hexTool)) return 0;
            if(!hexTool.hasHex(stack)){
                return 0;
            } else {
                return hexTool.hasMediaToUse(stack) ? 1 : 0.5f;
            }
        };

        ItemPropertiesRegistry.register(HexGloopItems.HEX_BLADE_ITEM.get(), ItemHexSword.TOOL_STATUS_PREDICATE, HEX_TOOL_PROVIDER);
        ItemPropertiesRegistry.register(HexGloopItems.HEX_PICKAXE_ITEM.get(), ItemHexSword.TOOL_STATUS_PREDICATE, HEX_TOOL_PROVIDER);
        
        UnclampedModelPredicateProvider FIDGET_INDEX_PROVIDER = (stack, level, holder, holderID) -> {
            if(!(stack.getItem() instanceof ItemFidget fidget)) return 0;
            
            int index = ItemFidget.getPage(stack)-1;
            return ((float)index) / (float) fidget.fidgetSettings.slots;
        };
        ItemPropertiesRegistry.register(HexGloopItems.COPPER_PEN_FIDGET.get(), ItemFidget.INDEX_PREDICATE, FIDGET_INDEX_PROVIDER);
        ItemPropertiesRegistry.register(HexGloopItems.DREIDEL_FIDGET.get(), ItemFidget.INDEX_PREDICATE, FIDGET_INDEX_PROVIDER);
        ItemPropertiesRegistry.register(HexGloopItems.RAINBOW_AMOGUS_FIDGET.get(), ItemFidget.INDEX_PREDICATE, FIDGET_INDEX_PROVIDER);
    }

    
    public static DecimalFormat DUST_FORMAT = new DecimalFormat("###,###.##");

    private static void pedestalDisplay(List<Pair<ItemStack, Text>> lines,
        BlockState state, BlockPos pos, PlayerEntity observer,
        World world,
        Direction hitFace){
            
        BlockEntityPedestal be = world.getBlockEntity(pos, HexGloopBEs.PEDESTAL_BE.get()).orElse(null);
        if(be == null) return;
        ItemStack stack = be.getStack(0);
        if(stack.isEmpty()){
            MutableText text = Text.literal("Empty");
            Style stlye = Style.EMPTY.withColor(Formatting.GRAY).withItalic(true);
            text.setStyle(stlye);
            lines.add(new Pair<>(ItemStack.EMPTY, text));
        } else {
            lines.add(new Pair<>(stack, stack.getName()));
            ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
            if(iotaHolder == null) return;
            NbtCompound nbt = iotaHolder.readIotaTag();
            if(nbt == null) return;
            if(HexIotaTypes.getTypeFromTag(nbt) != HexIotaTypes.PATTERN && state.getBlock() != HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get()){
                return;
            }
            if(nbt != null){
                Text iotaDesc = HexIotaTypes.getDisplay(iotaHolder.readIotaTag());
                ItemStack slateIcon = new ItemStack(HexItems.SLATE);
                HexItems.SLATE.writeDatum(slateIcon, new PatternIota(HexPattern.fromAngles("", HexDir.EAST)));
                lines.add(new Pair<>(slateIcon, iotaDesc));
            }
        }
    }

    private static void registerScryingDisplayers(){
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.GLOOP_ENERGIZER_BLOCK.get(), 
        (lines, state, pos, observer, world, direction) -> {
            if(world.getBlockEntity(pos) instanceof BlockEntityGloopEnergizer energizer){
                if (energizer.getMedia() < 0) {
                    lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), ItemCreativeUnlocker.infiniteMedia(world)));
                } else {
                    var dustCount = (float) energizer.getMedia() / (float) MediaConstants.DUST_UNIT;
                    var dustCmp = Text.translatable("hexcasting.tooltip.media",
                    DUST_FORMAT.format(dustCount));
                    lines.add(new Pair<>(new ItemStack(HexItems.AMETHYST_DUST), dustCmp));
                }
                lines.add(new Pair<>(new ItemStack(Items.WATER_BUCKET), Text.literal(energizer.getNumConnected() + " blocks")));
                // lines.add(new Pair<>(energizer.getLatestResult(), Text.literal("Latest result")));
            }
        });

        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.PEDESTAL_BLOCK.get(), HexGloopClient::pedestalDisplay);
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.MIRROR_PEDESTAL_BLOCK.get(), HexGloopClient::pedestalDisplay);
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.MIND_PEDESTAL_BLOCK.get(), HexGloopClient::pedestalDisplay);
        
        ScryingLensOverlayRegistry.addDisplayer(HexGloopBlocks.IOTIC_DIAL_BLOCK.get(), 
        (lines, state, pos, observer, world, direction) -> {
            if(world.getBlockEntity(pos) instanceof BlockEntityDial dialBE){
                ItemStack stack = dialBE.getInnerMultiFocus();
                if(stack.isEmpty()) return;
                NbtCompound tag = HexGloopItems.MULTI_FOCUS_ITEM.get().readIotaTag(stack);
                if(tag == null) return;
                Text iotaDesc = HexIotaTypes.getDisplay(tag);
                lines.add(new Pair<>(stack, iotaDesc));
            }
        });
    }
}
