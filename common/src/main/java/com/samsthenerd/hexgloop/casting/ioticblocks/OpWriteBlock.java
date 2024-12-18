package com.samsthenerd.hexgloop.casting.ioticblocks;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.blocks.iotic.IoticHandler;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class OpWriteBlock implements ConstMediaAction {
    private boolean simulate;

    public OpWriteBlock(boolean simulate){
        this.simulate = simulate;
    }

    @Override
    public int getArgc(){ return 2;}

    @Override
    public int getMediaCost(){
        return 0;
    }

    @Override
    public boolean isGreat(){ return false;}

    @Override
    public boolean getCausesBlindDiversion(){ return false;}

    @Override 
    public boolean getAlwaysProcessGreatSpell(){ return false;}

    @Override
    public Text getDisplayName(){ 
        return DefaultImpls.getDisplayName(this);
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment context){

        BlockPos blockPos = OperatorUtils.getBlockPos(args, 0, getArgc());

        ADIotaHolder holder = IoticHandler.findIotaHolder(context.getWorld(), blockPos);
        if(holder == null){
            if(simulate){
                return List.of(new BooleanIota(false));
            } else {
                MishapThrowerWrapper.throwMishap(MishapBadBlock.of(blockPos, "iota.write"));
                return new ArrayList<>();
            }
        }
        Iota iota = args.get(1);
        
        boolean canWrite = holder.writeIota(iota, true);
        if(simulate) return List.of(new BooleanIota(canWrite));
        if(!canWrite){
            MishapThrowerWrapper.throwMishap(MishapBadBlock.of(blockPos, "iota.write"));
            return new ArrayList<>();
        }
        holder.writeIota(iota, false);
        return new ArrayList<>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }   
}
