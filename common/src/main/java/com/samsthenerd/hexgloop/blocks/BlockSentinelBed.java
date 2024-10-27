package com.samsthenerd.hexgloop.blocks;

import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class BlockSentinelBed extends Block implements ICatPost{
    public BlockSentinelBed (Settings settings){
        super(settings);
    }

    public static Modification ambitModifier(CastingEnvironment ctx, Vec3d pos, Boolean original){
        LivingEntity entity = ctx.getCastingEntity();
        if(!original && entity instanceof ServerPlayerEntity caster){
            Vec3i vec3i = new Vec3i((int) pos.x, (int) pos.y, (int) pos.z);
            BlockState state = caster.getWorld().getBlockState(new BlockPos(vec3i));
            if(state.getBlock() == HexGloopBlocks.SENTINEL_BED_BLOCK.get())
                return Modification.ENABLE;
        }
        return Modification.NONE;
    }
}
