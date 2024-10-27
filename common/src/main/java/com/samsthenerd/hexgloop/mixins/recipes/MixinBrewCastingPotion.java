package com.samsthenerd.hexgloop.mixins.recipes;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.samsthenerd.hexgloop.items.HexGloopItems;

import at.petrak.hexcasting.api.item.PigmentItem;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.BrewingRecipeRegistry;

@Mixin(BrewingRecipeRegistry.class)
public class MixinBrewCastingPotion {
    
    @Inject(method = "craft(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private static void brewCastingPotion(ItemStack ingredient, ItemStack input, CallbackInfoReturnable<ItemStack> cir) {
        if(!input.isEmpty() && input.getNbt() != null && input.getNbt().getString("Potion").equals("minecraft:thick")){
            if(ingredient.getItem() == HexItems.AMETHYST_DUST){
                cir.setReturnValue(HexGloopItems.CASTING_POTION_ITEM.get().getDefaultStack());
            }
            if(ingredient.getItem() instanceof PigmentItem){
                // use uuid zero so that it gets synced when player touches it
                UUID uuid = new UUID(0, 0);
                FrozenPigment thisFrozen = new FrozenPigment(ingredient, uuid);
                ItemStack colorizedPotion = HexGloopItems.CASTING_POTION_ITEM.get().withColorizer(HexGloopItems.CASTING_POTION_ITEM.get().getDefaultStack(), thisFrozen);
                cir.setReturnValue(colorizedPotion);
            }
        }
    }

    @Inject(method = "hasRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private static void giveCastingRecipe(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir){
        if(!input.isEmpty()){
            NbtCompound tag = input.getNbt();
            if(tag == null){
                return;
            }
            if(!tag.contains("Potion") || !tag.getString("Potion").equals("minecraft:thick")){
                return;
            }
            if(ingredient.getItem() == HexItems.AMETHYST_DUST || ingredient.getItem() instanceof PigmentItem){
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method="isPotionRecipeIngredient(Lnet/minecraft/item/ItemStack;)Z", at=@At("HEAD"), cancellable=true)
    private static void isPotionRecipeIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir){
        if(stack.getItem() instanceof PigmentItem || stack.getItem() == HexItems.AMETHYST_DUST){
            cir.setReturnValue(true);
        }
    }
}
