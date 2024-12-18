package com.samsthenerd.hexgloop.mixins.lociathome;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(CastingEnvironment.class)
public class MixinContextHelper implements IContextHelper{
    @Shadow
    @Final
    private ServerPlayerEntity caster;

    private ItemStack kittyStack = null;

    public void setKitty(ItemStack kitty){
        kittyStack = kitty;
    }

    public ItemStack getKitty(){
        return kittyStack;
    }

    private StackReference cursorRef = null;

    public void setCursorRef(StackReference cursorRef){
        this.cursorRef = cursorRef;
    }

    public StackReference getCursorRef(){
        return cursorRef;
    }

    private KittyContext kittyContext = null;

    public void setKittyContext(KittyContext kCtx){
        kittyContext = kCtx;
    }

    public KittyContext getKittyContext(){
        return kittyContext;
    }

    private ItemStack frogStack = null;
    
    public void setFrog(ItemStack frog){
        frogStack = frog;
    }

    public ItemStack getFrog(){
        return frogStack;
    }

    // moving to this in the future but don't feel like rewriting to use it for frog and cat rn bleh - should probably also move it to a different folder

    private ItemStack castingStack = null;

    public ItemStack getCastingItem(){
        return castingStack;
    }

    public void setCastingItem(ItemStack stack){
        castingStack = stack;
    }
}
