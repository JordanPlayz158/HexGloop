package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;

// an item that can act as a substitute for a villager
// see also: IMindTargetItem for items that can *receive* a mind
public interface IFlayableItem {
    // doesn't need to be an actually spawned in-world villager, you can just make the entity and return it.
    // return null if there's no villager to flay
    @Nullable
    public VillagerEntity getFlayableVillager(ItemStack stack, @Nullable ItemEntity itemEnt, CastingEnvironment ctx);

    // do whatever, probably just decrement the stack count or clear some nbt 
    // result consumer is used as a way to return whatever results you want
    public void handleBrainsweep(ItemStack stack, @Nullable ItemEntity itemEnt, CastingEnvironment ctx, Consumer<ItemStack> resultConsumer);

    // if it counts as murder - if so it plays the death sound and alerts villagers
    public default boolean wasMurderous(ItemStack stack, @Nullable ItemEntity itemEnt, CastingEnvironment ctx){
        return false;
    }
}
