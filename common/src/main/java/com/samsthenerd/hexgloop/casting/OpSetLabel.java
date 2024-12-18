package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.items.LabelyItem;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker;
import com.samsthenerd.hexgloop.misc.wnboi.LabelMaker.Label;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import kotlin.Triple;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class OpSetLabel implements SpellAction{
    @Override
    public int getArgc(){ return 1;}

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

        ItemStack stack = context.getHeldItemToOperateOn(s -> {
            return s.getItem() instanceof LabelyItem;
        }).getFirst();

        if(!(stack.getItem() instanceof LabelyItem)){
            MishapThrowerWrapper.throwMishap(MishapBadOffhandItem.of(stack, context.getOtherHand(), "labely_item"));
        }

        Iota labelyIota = new NullIota();
        if(args.size() < 1){
            MishapThrowerWrapper.throwMishap(new MishapNotEnoughArgs(1, 0));
        } else {
            labelyIota = args.get(0);
        }

        Label label = LabelMaker.fromIota(labelyIota);
        // HexGloop.logPrint("putting label: " + label.toNbt().toString());

        List<ParticleSpray> particles = new ArrayList<>();
        particles.add(ParticleSpray.cloud(context.getCaster().getPos(), 1.0, 1));

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(stack, label),
            MediaConstants.DUST_UNIT,
            particles
        );
    }

    private class Spell implements RenderedSpell {
        Label label;
        ItemStack stack;
        public Spell(ItemStack stack, Label label) {
            this.label = label;
            this.stack = stack;
        }

        @Override
        public void cast(CastingEnvironment context) {
            // need to handle null label here
            NbtCompound nbt = new NbtCompound();
            if(label != null) nbt = label.toNbt();
            ((LabelyItem) stack.getItem()).putLabel(stack, nbt);
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment CastingEnvironment){
        return SpellAction.DefaultImpls.operate(this, continuation, stack, ravenmind, CastingEnvironment);
    }
    
}
