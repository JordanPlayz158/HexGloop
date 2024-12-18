package com.samsthenerd.hexgloop.mixins.spellmodifiers;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.Set;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(World.class)
public class MixinEnchantedBlockBreaking {
    @ModifyExpressionValue(
        method="breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;I)Z",
        at=@At(value="FIELD", target="net/minecraft/item/ItemStack.EMPTY : Lnet/minecraft/item/ItemStack;")
    )
    private ItemStack modifyBreakingBlock(ItemStack original, BlockPos pos, boolean drop, @Nullable Entity breakingEntity, int maxUpdateDepth){
        if(breakingEntity instanceof ServerPlayerEntity sPlayer){
            Set<CastingVM> harnesses = ((ICADHarnessStorage)(Object)sPlayer).getHarnesses();
            if(!harnesses.iterator().hasNext()) return original;
            CastingVM harness = harnesses.iterator().next();
            ItemStack maybeStack = ((IContextHelper)(Object)(harness.getEnv())).getCastingItem();
            if(maybeStack != null){
                return maybeStack;
            }
        }

        return original;
    }
}
