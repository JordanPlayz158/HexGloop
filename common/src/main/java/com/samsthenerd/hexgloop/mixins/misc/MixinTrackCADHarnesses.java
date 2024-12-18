package com.samsthenerd.hexgloop.mixins.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public class MixinTrackCADHarnesses implements ICADHarnessStorage{
    private Set<CastingHarness> harnesses = new HashSet<CastingHarness>();

    public void addHarness(CastingVM harness){
        harnesses.add(harness);
    }

    // get the harness that has this context
    public CastingVM getHarness(CastingEnvironment ctx){
        for(CastingHarness harness : harnesses){
            if(harness.getCtx() == ctx) return harness;
        }
        return null;
    }

    public Set<CastingVM> getHarnesses(){
        return harnesses;
    }

    public void removeHarness(CastingVM harness){
        harnesses.remove(harness);
    }
}
