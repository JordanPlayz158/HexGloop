package com.samsthenerd.hexgloop.blocks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import com.samsthenerd.hexgloop.HexGloop;

import net.minecraft.block.Block;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;

public class BlockTestFlayTarget extends Block implements IDynamicFlayTarget {
    public BlockTestFlayTarget(Settings settings) {
        super(settings);
    }

    @Override
    public void absorbVillagerMind(VillagerEntity sacrifice, BlockPos flayPos, CastingEnvironment ctx){
        HexGloop.logPrint("absorbed villager mind !");
    }
    
    @Override
    public boolean canAcceptMind(VillagerEntity sacrifice, BlockPos flayPos, CastingEnvironment ctx){
        HexGloop.logPrint("can accept villager mind !");
        return true;
    }
}
