package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.List;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import net.minecraft.text.Text;

public class OpCheckAmbit implements ConstMediaAction{

    public OpCheckAmbit(){

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
        Iota firstArg = args.get(0);
        if(firstArg instanceof Vec3Iota vIota){
            return List.of(new BooleanIota(context.isVecInRange(vIota.getVec3())));
        }
        if(firstArg instanceof EntityIota eIota){
            return List.of(new BooleanIota(context.isEntityInRange(eIota.getEntity())));
        }
        Mishap m = MishapInvalidIota.ofType(firstArg, 0, "vector");
        MishapThrowerWrapper.throwMishap(m);
        return List.of(new BooleanIota(false));
    }

    @Override
    public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation){
        return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
    }
    
}

