package com.samsthenerd.hexgloop.casting.orchard;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class OpReadOrchard implements ConstMediaAction {
    private boolean asList;

    public OpReadOrchard(boolean asList){
        this.asList = asList;
    }

    @Override
    public int getArgc(){ return 0;}

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
        ServerPlayerEntity player = (ServerPlayerEntity) context.getCastingEntity();
        if(player == null) return new ArrayList<Iota>();
        if(asList){
            List<Double> orchardList = ((IOrchard)player).getOrchardList();
            List<Iota> iotadList = orchardList.stream().map(val -> new DoubleIota(val)).collect(Collectors.toList());
            ListIota orchardIotaList = new ListIota(iotadList);
            return List.of(orchardIotaList);
        } else {
            return List.of(new DoubleIota(((IOrchard)player).getOrchardValue()));
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}
