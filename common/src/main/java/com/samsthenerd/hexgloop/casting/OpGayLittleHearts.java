package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import kotlin.Triple;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


// this seems like more work than i feel like doing tonight,, another day though perhaps
public class OpGayLittleHearts implements SpellAction{
    private static final Random RANDOM = new Random();
    @Override
    public int getArgc(){ return 2;}

    @Override
    public boolean hasCastingSound(CastingEnvironment context){ return true;}

    @Override
    public boolean awardsCastingStat(CastingEnvironment context){ return true;}

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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingEnvironment context){
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        context.assertVecInRange(pos);
        
        List<ParticleSpray> particles = new ArrayList<>();
        particles.add(ParticleSpray.cloud(Vec3d.ofCenter(pos), 1.0, 1));

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(pos),
            MediaConstants.DUST_UNIT,
            particles
        );
    }

    private class Spell implements RenderedSpell {
        BlockPos pos;
        public Spell(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public void cast(CastingEnvironment context) {
            if (!context.canEditBlockAt(pos))
                return;

            FrozenPigment colorizer = IXplatAbstractions.INSTANCE.getColorizer(context.getCaster());
            // context.getWorld().spawnParticles(ParticleTypes.SNEEZE,
            //             waterPos.getX() + 0.5 + rand.nextDouble()*0.5, waterPos.getY() + 0.3 + rand.nextDouble()*0.5, waterPos.getZ()+0.5 + rand.nextDouble()*0.5, 1, 0, 0.3, 0, 0.01);
            
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment CastingEnvironment){
        return SpellAction.DefaultImpls.operate(this, continuation, stack, ravenmind, CastingEnvironment);
    }
    
}