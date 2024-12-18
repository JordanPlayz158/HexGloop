package com.samsthenerd.hexgloop.mixins.dyeablestaffs;

import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.misc.StaffColorLoader;

import at.petrak.hexcasting.common.items.ItemStaff;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

@Mixin(ItemStaff.class)
public class MixinInjectDyeableStaff implements DyeableItem{

    public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubNbt(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(COLOR_KEY, NbtElement.NUMBER_TYPE)) {
            return nbtCompound.getInt(COLOR_KEY);
        }
        Identifier stackId = Registries.ITEM.getId(stack.getItem());
        Integer color = StaffColorLoader.STAFF_COLORS.get(stackId);
        if(color != null){
            return color;
        }
        return 0xff7e4f;
    }
}