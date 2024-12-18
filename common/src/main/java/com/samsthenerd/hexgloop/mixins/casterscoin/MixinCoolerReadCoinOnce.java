package com.samsthenerd.hexgloop.mixins.casterscoin;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemCastersCoin;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.rw.OpTheCoolerRead;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(OpTheCoolerRead.class)
public class MixinCoolerReadCoinOnce {
    @WrapOperation(method ="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingContext;)Ljava/util/List;",
    at = @At(value="INVOKE", target="at/petrak/hexcasting/api/addldata/ADIotaHolder.readIota (Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/iota/Iota;", ordinal=0))
    private Iota readCoinOnce(ADIotaHolder holder, ServerWorld world, Operation<Iota> original, @NotNull List<? extends Iota> args, @NotNull CastingEnvironment ctx){
        Iota iota = original.call(holder, world);
        Entity ent = OperatorUtils.getEntity(args, 0, ((OpTheCoolerRead)(Object)(this)).getArgc());
        ItemStack heldStack = ItemStack.EMPTY;
        Consumer<ItemStack> updateHeldItem = (stack) -> {};
        if(ent instanceof ItemEntity itemEnt){
            heldStack = itemEnt.getStack();
            updateHeldItem = (stack) -> {
                itemEnt.setStack(stack);
            };
        } else if(ent instanceof ItemFrameEntity frameEnt){
            heldStack = frameEnt.getHeldItemStack();
            updateHeldItem = (stack) -> {
                if(stack.isEmpty())
                    frameEnt.setHeldItemStack(ItemStack.EMPTY);
                else
                    frameEnt.setHeldItemStack(stack);
            };
        }
        if(heldStack != null && heldStack.getItem() instanceof ItemCastersCoin readOnlyItem){
            if(holder != null){
                // if it's gonna mishap then don't do the read once stuff
                if(iota == null){
                    if(holder.emptyIota() == null){ 
                        return iota;
                    }
                }
                // shouldn't read mishap so do the read once stuff
                ItemStack newStack = readOnlyItem.useCoin(heldStack);
                updateHeldItem.accept(heldStack); // incase it doesn't auto update 
                ItemEntity newEnt = new ItemEntity(ctx.getWorld(), ent.getX(), ent.getY(), ent.getZ(), newStack);
                ctx.getWorld().spawnEntity(newEnt);
            }
        }
        return iota;
    }
}
