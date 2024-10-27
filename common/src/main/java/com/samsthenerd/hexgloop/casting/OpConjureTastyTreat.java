package com.samsthenerd.hexgloop.casting;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import kotlin.Triple;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class OpConjureTastyTreat implements SpellAction {

    public OpConjureTastyTreat(){
    }

    @Override
    public int getArgc(){ return 1;}

    public boolean hasCastingSound(@NotNull CastingContext ctx){
        return false;
    }

    public boolean awardsCastingStat(@NotNull CastingContext ctx){
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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingContext context){
        // position to summon snacks
        Vec3d pos = OperatorUtils.getVec3(args, 0, getArgc());
        context.assertVecInRange(pos);
        ItemStack snackStack = HexGloopItems.HEX_SNACK.get().getDefaultStack();
        snackStack.setCount(1);
        ItemEntity snack = new ItemEntity(context.getWorld(), pos.getX(), pos.getY(), pos.getZ(), snackStack);
        
        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(new Spell(snack), MediaConstants.DUST_UNIT, List.of());
        // return List.of(new EntityIota(snack));
    }

    public class Spell implements RenderedSpell{
        private ItemEntity snack;
        public Spell(ItemEntity snack){
            this.snack = snack;
        }

        public void cast(CastingContext ctx){
            ctx.getWorld().spawnEntity(snack);
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}
