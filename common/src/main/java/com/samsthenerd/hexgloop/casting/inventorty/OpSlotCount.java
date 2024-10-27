package com.samsthenerd.hexgloop.casting.inventorty;

import java.util.List;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.GrabbableStack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.text.Text;

public class OpSlotCount implements ConstMediaAction{

    boolean getActual = true;

    public OpSlotCount(boolean getActual){
        this.getActual = getActual;
    }

    @Override
    public int getArgc(){ return 1; }

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
    public List<Iota> execute(List<? extends Iota> args, CastingContext context){
        InventortyUtils.assertKittyCasting(context);
        GrabbableStack grabStack = InventortyUtils.getStackFromGrabbable(args.get(0), context, 0, getArgc());
        int maxCount = grabStack.getMaxCount();
        if(grabStack.getStack() != null && !grabStack.getStack().isEmpty())
            maxCount = Math.min(maxCount, grabStack.getStack().getMaxCount());
        return List.of(new DoubleIota(getActual ? grabStack.getStack().getCount() : maxCount));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}




