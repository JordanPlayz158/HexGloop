package com.samsthenerd.hexgloop.mixins.lociathome;

import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.FunctionalData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import kotlin.Pair;

@Mixin(CastingVM.class)
public interface MixinExposeHarnessStuff {
    @Invoker(value="handleParentheses", remap = false)
    public Pair<FunctionalData, ResolvedPatternType> invokehandleParentheses(Iota iota);
}
