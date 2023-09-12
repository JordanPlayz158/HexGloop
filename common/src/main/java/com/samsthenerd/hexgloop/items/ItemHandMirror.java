package com.samsthenerd.hexgloop.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.items.ItemFocus;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ItemHandMirror extends Item implements IotaHolderItem {

    public static final Identifier MIRROR_ACTIVATED_PRED = new Identifier(HexGloop.MOD_ID, "is_mirror_activated");
    public static final String ITEM_DISPLAY_TAG = "item_in_mirror_desc";
    public static final String ITEM_UUID_TAG = "item_in_mirror_uuid";

    public ItemHandMirror(Settings settings){
        super(settings);
    }

    @Nullable
    public NbtCompound readIotaTag(ItemStack stack){
        if(stack.getNbt() != null && stack.getNbt().contains(ItemFocus.TAG_DATA))
            return NBTHelper.getCompound(stack, ItemFocus.TAG_DATA);
        return null;
    }

    public boolean canWrite(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            return true; // ? i suppose? unless it's already empty
        }
        if(iota instanceof EntityIota entityIota && entityIota.getEntity() instanceof ItemEntity itemEnt){
            return true;
        }
        return false;
    }

    public void writeDatum(ItemStack stack, @Nullable Iota iota){
        if(iota == null){
            stack.removeSubNbt(ItemFocus.TAG_DATA);
            stack.removeSubNbt(ITEM_DISPLAY_TAG);
            stack.removeSubNbt(ITEM_UUID_TAG);
        }
        if(iota instanceof EntityIota entityIota && entityIota.getEntity() instanceof ItemEntity itemEnt){
            NBTHelper.put(stack, ItemFocus.TAG_DATA, HexIotaTypes.serialize(iota));
            NBTHelper.putUUID(stack, ITEM_UUID_TAG, itemEnt.getUuid());
            if(itemEnt.getStack() != null){
                NBTHelper.put(stack, ITEM_DISPLAY_TAG, itemEnt.getStack().writeNbt(new NbtCompound()));
            } else {
                stack.removeSubNbt(ITEM_DISPLAY_TAG);
            }
        }
    }

    public boolean isMirrorActivated(ItemStack stack){
        ItemStack descStack = getMirroredItemStack(stack);
        if(descStack != null && !descStack.isEmpty()){
            return true;
        }
        return false;
    }

    public ItemStack getMirroredItemStack(ItemStack stack){
        if(stack.getNbt() != null && stack.getNbt().contains(ITEM_DISPLAY_TAG)){
            return ItemStack.fromNbt(stack.getNbt().getCompound(ITEM_DISPLAY_TAG));
        }
        return ItemStack.EMPTY;
    }

    // to update the thingy
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(world instanceof ServerWorld sWorld){
            NbtCompound nbt = stack.getNbt();
            if(nbt == null) return;
            if(nbt.containsUuid(ITEM_UUID_TAG)){
                UUID uuid = nbt.getUuid(ITEM_UUID_TAG);
                Entity ent = sWorld.getEntity(uuid);
                if(ent instanceof ItemEntity itemEnt){
                    if(itemEnt.getStack() != null){
                        NBTHelper.put(stack, ITEM_DISPLAY_TAG, itemEnt.getStack().writeNbt(new NbtCompound()));
                    } else {
                        stack.removeSubNbt(ITEM_DISPLAY_TAG);
                    }
                } else {
                    stack.removeSubNbt(ITEM_DISPLAY_TAG);
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ItemStack descStack = getMirroredItemStack(stack);
        if(descStack.isEmpty()) return;
        MutableText itemDesc = descStack.getName().copy();
        MutableText tipText = Text.translatable("item.hexgloop.hand_mirror.tooltip", itemDesc);
        Style style = tipText.getStyle();
        style = style.withItalic(true).withColor(Formatting.GRAY);
        tipText.setStyle(style);
        tooltip.add(tipText);
    }
}