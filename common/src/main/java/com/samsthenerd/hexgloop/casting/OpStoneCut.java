package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.compat.hexal.HexalMaybeIotas;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadItem;
import dev.architectury.platform.Platform;
import kotlin.Triple;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.StonecuttingRecipe;
import net.minecraft.text.Text;

// entity, int | block type -> 
public class OpStoneCut implements SpellAction {

    public OpStoneCut(){
    }

    @Override
    public int getArgc(){ return 2;}

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
        ItemEntity itemEnt = OperatorUtils.getItemEntity(args, 0, getArgc());
        context.assertEntityInRange(itemEnt);
        Inventory inv = new SimpleInventory(itemEnt.getStack());
        List<StonecuttingRecipe> matchingRecipes =  context.getWorld().getRecipeManager().getAllMatches(RecipeType.STONECUTTING, inv, context.getWorld());
        if(matchingRecipes.isEmpty()){
            MishapThrowerWrapper.throwMishap(MishapBadItem.of(itemEnt, "no_stonecutting"));
            return null;
        }
        Iota selectorArg = args.get(1);
        StonecuttingRecipe selectedRecipe = null;
        if(Platform.isModLoaded("hexal")){
            Item selectedItem = HexalMaybeIotas.getItemFromIota(selectorArg);
            if(selectedItem != null){
                for(StonecuttingRecipe recipe : matchingRecipes){
                    if(recipe.getOutput().getItem() == selectedItem){
                        selectedRecipe = recipe;
                        break;
                    }
                }
            }
        }
        if(selectedRecipe == null){
            int index = OperatorUtils.getInt(args, 1, getArgc()) % matchingRecipes.size();
            selectedRecipe = matchingRecipes.get(index);
        }



        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(itemEnt, selectedRecipe), 
            MediaConstants.DUST_UNIT / 8,
            List.of());
    }

    public class Spell implements RenderedSpell{
        private ItemEntity itemEnt;
        private StonecuttingRecipe recipe;

        public Spell(ItemEntity itemEnt, StonecuttingRecipe recipe){
            this.itemEnt = itemEnt;
            this.recipe = recipe;
        }

        public void cast(CastingEnvironment ctx){
            ItemStack stack = itemEnt.getStack();
            ItemStack result = recipe.craft(new SimpleInventory(stack));
            result.setCount(stack.getCount()); // craft the whole thing ! 
            stack.setCount(0);
            ItemEntity resultEnt = new ItemEntity(ctx.getWorld(), itemEnt.getX(), itemEnt.getY(), itemEnt.getZ(), result, 0, 0, 0);
            ctx.getWorld().spawnEntity(resultEnt);
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment CastingEnvironment){
        return DefaultImpls.operate(this, continuation, stack, ravenmind, CastingEnvironment);
    }
}


