package com.samsthenerd.hexgloop.mixins.misc;

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ItemPackagedHex.class)
public class MixinActuallyTrackHarnesses {
    @WrapOperation(method="use",
        at=@At(value="INVOKE", target="Lat/petrak/hexcasting/api/casting/eval/vm/CastingVM;queueExecuteAndWrapIotas(Ljava/util/List;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/casting/eval/ExecutionClientView;"))
    public ExecutionClientView trackHarnesses(CastingVM vm, List<? extends Iota> instrs,
        ServerWorld sWorld, Operation<ExecutionClientView> original){

        ServerPlayerEntity sPlayer = (ServerPlayerEntity) vm.getEnv().getCastingEntity();
        ItemStack castingStackProbably = sPlayer.getStackInHand(vm.getEnv().getCastingHand());
        ((IContextHelper)(Object)vm.getEnv()).setCastingItem(castingStackProbably);
        if(sPlayer != null){
            ICADHarnessStorage storage = (ICADHarnessStorage)(Object)sPlayer;
            storage.addHarness(vm);
        }
        ExecutionClientView info = original.call(vm, instrs, sWorld);
        if(sPlayer != null){
            ICADHarnessStorage storage = (ICADHarnessStorage)(Object)sPlayer;
            storage.removeHarness(vm);
        }
        return info;
    }
}
