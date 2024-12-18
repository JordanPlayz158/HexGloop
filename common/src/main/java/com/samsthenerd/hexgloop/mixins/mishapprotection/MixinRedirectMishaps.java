package com.samsthenerd.hexgloop.mixins.mishapprotection;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.mishapprotection.ICatchyFrameEval;
import com.samsthenerd.hexgloop.casting.mishapprotection.IMishapStorage;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import net.minecraft.server.world.ServerWorld;

@Mixin(CastingVM.class)
public class MixinRedirectMishaps {

    @Shadow
    @Final
    private HexPattern getPatternForFrame(ContinuationFrame frame) {
        return null;
    }

    @Shadow
    @Final
    private Action getOperatorForFrame(ContinuationFrame frame, ServerWorld world) {
        return null;
    }

    @WrapOperation(method = "executeIotas(Ljava/util/List;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/casting/ControllerInfo;",
    at = @At(value = "INVOKE", target = "at/petrak/hexcasting/api/spell/casting/eval/ContinuationFrame.evaluate (Lat/petrak/hexcasting/api/spell/casting/eval/SpellContinuation;Lnet/minecraft/server/world/ServerWorld;Lat/petrak/hexcasting/api/spell/casting/CastingVM;)Lat/petrak/hexcasting/api/spell/casting/CastingVM$CastResult;"))
    public CastResult executeIotasWrap(ContinuationFrame frame, SpellContinuation spCont, ServerWorld sWorld, CastingVM harness,
    Operation<CastResult> original) {
        Mishap caughtMishap = null;
        CastResult result = null;
        try{
            // equivalent-ish to frame.evaluate
            result = original.call(frame, spCont, sWorld, harness);
            for(OperatorSideEffect sideEffect : result.getSideEffects()){
                if(sideEffect instanceof OperatorSideEffect.DoMishap mishapEffect){
                    caughtMishap = mishapEffect.getMishap();
                    break;
                }
            }
        } catch (Throwable e){
            if(e instanceof Mishap mishap){
                caughtMishap = mishap;
            } else {
                throw e; // i guess ? the original doesn't handle it so not my issue ?
            }
        }
        if(caughtMishap == null){
            return result;
        }
        // mishap, need to handle it
        HexPattern pattern = getPatternForFrame(frame);
        Action operator = null;
        try {
            operator = getOperatorForFrame(frame, sWorld);
        } catch (Throwable e) {

        }
        ((IMishapStorage)(Object)harness.getEnv()).setLastMishap(caughtMishap.errorMessage(harness.getEnv(), new Mishap.Context(pattern != null ? pattern : HexPattern.fromAngles("", HexDir.WEST), operator)));
        // want to go back until we can find a catchy frame
        // yoink from OpHalt
        boolean done = false;

        SpellContinuation newCont;
        int frameC = 0;
        for(newCont = spCont; !done && newCont instanceof SpellContinuation.NotDone; newCont = ((SpellContinuation.NotDone)newCont).getNext()) {
            ContinuationFrame cFrame = ((SpellContinuation.NotDone)newCont).getFrame();
            if(((Object)cFrame) instanceof ICatchyFrameEval cEval) {
                done = cEval.isCatchy();
            }
        }

        if(done){ // found a catchy frame to back up to
            List<Iota> stack = harness.getStack();
            stack.add(new BooleanIota(true));
            harness.setStack(stack);
            return new CastResult(
                newCont,
                null,
                ResolvedPatternType.EVALUATED, // uhh actually i have no idea what this should be
                List.of(), // do we need something here ? idk
                HexEvalSounds.NOTHING // could maybe try it with the mishap sound and see how it goes
            );
        } else { // no catchy frame to back up to
            MishapThrowerWrapper.throwMishap(caughtMishap);
            return null; // unreachable but kotlin is dumb
        }

    }
}
