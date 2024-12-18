package com.samsthenerd.hexgloop.mixins.wnboi;

import at.petrak.hexcasting.common.msgs.MsgShiftScrollC2S;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

@Mixin(MsgShiftScrollC2S.class)
public interface MixinScrollbookInvoker {
    @Invoker("spellbook")
    public void invokeHandleSpellbook(ServerPlayerEntity sender, Hand hand, ItemStack stack, double delta);
}
