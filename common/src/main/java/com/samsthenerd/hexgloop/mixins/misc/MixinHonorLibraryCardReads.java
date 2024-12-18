package com.samsthenerd.hexgloop.mixins.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.items.ItemLibraryCard;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

@Mixin(targets = {
    "at/petrak/hexcasting/common/casting/operators/akashic/OpAkashicRead",
    "ram/talia/hexal/common/casting/actions/everbook/OpEverbookWrite"
})
public class MixinHonorLibraryCardReads {
    @WrapOperation(method = {
        "execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingEnvironment;)Ljava/util/List;",
        "execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingEnvironment;)Lkotlin/Triple;"
    },
    at = @At(value = "INVOKE", target="at/petrak/hexcasting/api/spell/casting/CastingEnvironment.getWorld ()Lnet/minecraft/server/world/ServerWorld;"))
    public ServerWorld getAlternateLibraryDim(CastingEnvironment ctx, Operation<ServerWorld> original){
        ServerWorld originalWorld = original.call(ctx);
        if(ctx.getCastingEntity() == null) return originalWorld;
        PlayerInventory pInv = ((ServerPlayerEntity) ctx.getCastingEntity()).getInventory();
        for(int i = 0; i < pInv.size(); i++){
            ItemStack stack = pInv.getStack(i);
            if(stack.getItem() instanceof ItemLibraryCard libCard){
                RegistryKey<World> dim = libCard.getDimension(stack);
                if(dim != null){
                    ServerWorld newWorld = originalWorld.getServer().getWorld(dim);
                    if(newWorld != null) return newWorld;
                }
            }
        }
        return originalWorld;
    }
}
