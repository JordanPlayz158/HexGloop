package com.samsthenerd.hexgloop.mixins.misc;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.spell.casting.ControllerInfo;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.items.magic.ItemPackagedHex;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ItemPackagedHex.class)
public class MixinActuallyTrackHarnesses {
    @WrapOperation(method="use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
    at=@At(value="INVOKE", target="at/petrak/hexcasting/api/spell/casting/CastingVM.executeIotas (Ljava/util/List;Lnet/minecraft/server/world/ServerWorld;)Lat/petrak/hexcasting/api/spell/casting/ControllerInfo;"))
    public ControllerInfo trackHarnesses(CastingVM harness, List<Iota> instrs, ServerWorld sWorld, Operation<ControllerInfo> original){
        ServerPlayerEntity sPlayer = (ServerPlayerEntity) harness.getEnv().getCastingEntity();
        ItemStack castingStackProbably = sPlayer.getStackInHand(harness.getEnv().getCastingHand());
        ((IContextHelper)(Object)harness.getEnv()).setCastingItem(castingStackProbably);
        if(sPlayer != null){
            ICADHarnessStorage storage = (ICADHarnessStorage)(Object)sPlayer;
            storage.addHarness(harness);
        }
        ControllerInfo info = original.call(harness, instrs, sWorld);
        if(sPlayer != null){
            ICADHarnessStorage storage = (ICADHarnessStorage)(Object)sPlayer;
            storage.removeHarness(harness);
        }
        return info;
    }
}
