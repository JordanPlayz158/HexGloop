package com.samsthenerd.hexgloop.casting.inventorty;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.List;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.AutoGrabbable;
import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.GrabbableStack;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

public class OpStackTransfer implements ConstMediaAction{

    public OpStackTransfer(){
    }

    @Override
    public int getArgc(){ return 3;}

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
        InventortyUtils.assertKittyCasting(context);
        GrabbableStack fromGrabbable = InventortyUtils.getStackFromGrabbable(args.get(0), context, 0, getArgc());
        if(fromGrabbable == null || fromGrabbable instanceof AutoGrabbable){
            return List.of(new DoubleIota(0)); // failing silently is bad ! but oh well - fix later
        }
        ItemStack fromStack = fromGrabbable.getStack();
        int inputCount = Math.max(OperatorUtils.getInt(args, 2, getArgc()), 0);
        int transferCount = Math.min(inputCount, fromStack.getCount());
        int amtLeft = fromStack.getCount();
        ItemEntity maybeNewEnt = null;
        if(args.get(1) instanceof NullIota){
            // want to make a new item entity
            ItemStack newStack = fromStack.split(transferCount);
            Vec3d playerPos = context.getCaster().getPos();
            ItemEntity newEnt = new ItemEntity(context.getWorld(), playerPos.x, playerPos.y, playerPos.z, newStack, 0,0,0);
            context.getWorld().spawnEntity(newEnt);
            amtLeft = fromStack.getCount();
            maybeNewEnt = newEnt;
        } else {
            GrabbableStack toGrabbable = InventortyUtils.getStackFromGrabbable(args.get(1), context, 1, getArgc());
            // HexGloop.logPrint("In OpStackTransfer:\n\ttoGrabbable: " + toGrabbable.getStack() + "\n\tfromGrabbable: " + fromGrabbable.getStack() +
            //     "\n\tcanTake: " + fromGrabbable.canTake(context.getCaster()) +
            //     "\n\tcanInsert: " + toGrabbable.canInsert(fromGrabbable.getStack())); 
            // let it find the slot it can go into
            if(toGrabbable == null){
                return List.of(new DoubleIota(amtLeft));
            }
            if(toGrabbable instanceof AutoGrabbable toAutoFill){
                if(!toAutoFill.findSlot(fromStack, context)){
                    // couldn't find a slot so return nothing happened
                    return List.of(new DoubleIota(amtLeft));
                }
            }
            transferCount = Math.min(transferCount, 
                toGrabbable.getMaxCount() - (toGrabbable.getStack().isEmpty() ? 0 : toGrabbable.getStack().getCount()) 
            );
            
            if(ItemStack.canCombine(fromGrabbable.getStack(), toGrabbable.getStack()) || toGrabbable.getStack().isEmpty()){
                ItemStack takenStack = fromGrabbable.takeStack(transferCount, context.getCaster());
                // HexGloop.logPrint("transferCount: " + transferCount + "; takenStack: " + takenStack + "; fromGrabbable: " + fromGrabbable.getStack());
                toGrabbable.insertStack(takenStack);
                amtLeft = fromGrabbable.getStack().getCount();
            }
        }
        return maybeNewEnt == null ? List.of(new DoubleIota(amtLeft)) : List.of(new DoubleIota(amtLeft), new EntityIota(maybeNewEnt));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}