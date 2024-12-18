package com.samsthenerd.hexgloop.mixins.mirroritems;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.mirror.SyncedItemHandling;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(CastingEnvironment.class)
public class MixinDirectlyOnGetHeldItem {
    @WrapOperation(method = "getHeldItemToOperateOn(Lkotlin/jvm/functions/Function1;)Lkotlin/Pair;",
    at = @At(value = "INVOKE", target="net/minecraft/server/network/ServerPlayerEntity.getStackInHand (Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    public ItemStack getAlternateHandStack(ServerPlayerEntity player, Hand hand, Operation<ItemStack> original){
        ItemStack altStack = SyncedItemHandling.getAlternateHandStack(player, hand, (CastingEnvironment) (Object)this);
        return altStack == null ? original.call(player, hand) : altStack;
    }

    // these are for the inventory/operative slot discoverer thingies - i don't think they work though
    @Inject(method = {
        "_init_$lambda$3(Lat/petrak/hexcasting/api/spell/casting/CastingEnvironment;)Ljava/util/List;",
        "_init_$lambda$4(Lat/petrak/hexcasting/api/spell/casting/CastingEnvironment;)Ljava/util/List;"
    },
    at = @At("RETURN"), cancellable = true, remap = false)
    private static void wrapMirrorStacksFromDiscoverer(CastingEnvironment context, CallbackInfoReturnable<List<ItemStack>> cir){
        List<ItemStack> stacks = cir.getReturnValue();
        ItemStack altMain = SyncedItemHandling.getAlternateHandStack(context.getCastingEntity(), Hand.MAIN_HAND, context);
        ItemStack altOffStack = SyncedItemHandling.getAlternateHandStack(context.getCastingEntity(), Hand.OFF_HAND, context);
        if(altMain != null){
            stacks.add(0, altMain);
        }
        if(altOffStack != null){
            stacks.add(0, altOffStack);
        }
        cir.setReturnValue(stacks);
    }
}
