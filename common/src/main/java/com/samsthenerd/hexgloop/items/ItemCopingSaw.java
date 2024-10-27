package com.samsthenerd.hexgloop.items;

import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;

public class ItemCopingSaw extends Item{
    public ItemCopingSaw(Settings settings){
        super(settings);
    }

    public static Modification overcastModifer(CastingEnvironment ctx, Boolean original){
        if(ctx.getCastingEntity() instanceof ServerPlayerEntity caster){
            PlayerInventory pInv = caster.getInventory();
            for(int i = 0; i < PlayerInventory.getHotbarSize(); i++){
                if(pInv.getStack(i).getItem() == HexGloopItems.COPING_SAW_ITEM.get()){
                    return Modification.DISABLE;
                }
            }
        }
        return Modification.NONE;
    }
}
