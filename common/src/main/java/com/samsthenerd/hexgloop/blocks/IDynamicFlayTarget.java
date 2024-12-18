package com.samsthenerd.hexgloop.blocks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;

public interface IDynamicFlayTarget {
    // politely ask you to not modify the sacrifice
    public void absorbVillagerMind(VillagerEntity sacrifice, BlockPos flayPos, CastingEnvironment ctx);
    
    // return true if it can be accepted
    public boolean canAcceptMind(VillagerEntity sacrifice, BlockPos flayPos, CastingEnvironment ctx);
}
