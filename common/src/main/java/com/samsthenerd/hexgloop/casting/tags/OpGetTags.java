package com.samsthenerd.hexgloop.casting.tags;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.text.Text;
import ram.talia.moreiotas.api.spell.iota.StringIota;


// requires moreiotas and is better with hexal
public class OpGetTags implements ConstMediaAction{

    public OpGetTags(){

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
        Iota inputIota = args.get(0);
        List<Iota> tagList = new ArrayList<>();
        for(String tag : TagUtils.getTagChecker(inputIota, context).getTags()){
            StringIota tagIota = null;
            try{
                tagIota = new StringIota(tag);
                tagList.add(tagIota);
            } catch(MishapInvalidIota e){}
        }
        return List.of(new ListIota(tagList));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}

