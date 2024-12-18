package com.samsthenerd.hexgloop.casting.gloopifact;


import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

public class OpWriteGloopifact implements ConstMediaAction {
    public static List<String> expectedSources = List.of("gloopifact");
    private boolean simulate;
    
    public OpWriteGloopifact(boolean simulate){
        this.simulate = simulate;
    }

    @Override
    public int getArgc(){ return 1;}

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
        Pair<ItemStack, ItemGloopifact> gloopifactLore = GloopifactUtils.assertGloopcasting(context);
        ItemStack castHandStack = gloopifactLore.getLeft();
        ItemGloopifact gloopifactItem = gloopifactLore.getRight();

        Iota iota = args.get(0);

        boolean canWrite = gloopifactItem.canWrite(castHandStack, iota);
        if(simulate) return List.of(new BooleanIota(canWrite));
        if(!canWrite){
            MishapThrowerWrapper.throwMishap(MishapBadOffhandItem.of(castHandStack, context.getCastingHand(), "iota.write", new Object[0]));
            return List.of();
        }
        gloopifactItem.writeDatum(castHandStack, iota);
        return new ArrayList<>();
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }   
}