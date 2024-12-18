package com.samsthenerd.hexgloop.mixins.ring;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.samsthenerd.hexgloop.HexGloop;
import com.samsthenerd.hexgloop.casting.IContextHelper;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CastingVM.class)
public class MixinLetRingDrawMedia {
    @ModifyExpressionValue(
        method = "withdrawMedia(IZ)I",
        at = @At(value = "INVOKE", target="net/minecraft/item/ItemStack.isIn (Lnet/minecraft/tag/TagKey;)Z"))
    private boolean allowRingCastingMediaDraw(boolean original){
        CastingEnvironment ctx = ((CastingVM)(Object)this).getEnv();
        ServerPlayerEntity player = (ServerPlayerEntity) ctx.getCastingEntity();
        if(player == null) return original;
        return original || (HexGloop.TRINKETY_INSTANCE.isCastingRingEquipped(player) && ctx instanceof StaffCastEnv);
    }

    // and also the cat

    @ModifyExpressionValue(
        method = "withdrawMedia(IZ)I",
        at = @At(value = "INVOKE", target="net/minecraft/server/network/ServerPlayerEntity.getStackInHand (Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;"))
    private ItemStack allowCastingCatMediaDraw(ItemStack original){
        CastingEnvironment ctx = ((CastingVM)(Object)this).getEnv();
        if(((IContextHelper)(Object)ctx).isKitty()){
            return ((IContextHelper)(Object)ctx).getKitty();
        }
        // and also forg
        if(((IContextHelper)(Object)ctx).isFrogCasting()){
            return ((IContextHelper)(Object)ctx).getFrog();
        }
        return original;
    }
}
