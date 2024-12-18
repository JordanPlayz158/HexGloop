package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.casting.OpFrogEat.Spell;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.castables.SpellAction.DefaultImpls;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import kotlin.Triple;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FrogEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class OpFrogEat implements SpellAction {

    public OpFrogEat(){
    }

    @Override
    public int getArgc(){ return 1;}

    public boolean hasCastingSound(@NotNull CastingEnvironment ctx){
        return false;
    }

    public boolean awardsCastingStat(@NotNull CastingEnvironment ctx){
        return true;
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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingEnvironment context){
        IContextHelper contextHelper = (IContextHelper)(Object)context;
        if(!contextHelper.isFrogCasting()){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List.of("frog_casting")));
            return null;
        }
        ItemStack frogStack = contextHelper.getFrog();
        LivingEntity target = OperatorUtils.getLivingEntityButNotArmorStand(args, 0, getArgc());
        
        if(!FrogEntity.isValidFrogFood(target)){
            MishapThrowerWrapper.throwMishap(MishapBadEntity.of(target, "not_frog_food"));
            return null;
        }

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(target, frogStack), 
            MediaConstants.SHARD_UNIT,
            List.of());
    }

    public class Spell implements RenderedSpell{
        private LivingEntity target = null;
        private ItemStack frogStack = null;

        public Spell(LivingEntity target, ItemStack frogStack){
            this.target = target;
            this.frogStack = frogStack;
        }

        public void cast(CastingEnvironment ctx){
            if(target == null || frogStack == null) return;
            FrogEntity fakeFrog = EntityType.FROG.create(ctx.getWorld());
            fakeFrog.setVariant(HexGloopItems.CASTING_FROG_ITEM.get().getFrogVariant(frogStack));
            // not sure if this will target only slimes and whatnot 
            ctx.getWorld().playSoundFromEntity(null, fakeFrog, SoundEvents.ENTITY_FROG_EAT, SoundCategory.NEUTRAL, 2.0f, 1.0f);
            fakeFrog.tryAttack(target);
            if (!target.isAlive()) {
                target.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation){
        return DefaultImpls.operate(this, env, image, continuation);
    }
}

