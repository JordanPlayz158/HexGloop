package com.samsthenerd.hexgloop.mixins.castingmodifiers;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.samsthenerd.hexgloop.casting.ContextModificationHandlers;
import com.samsthenerd.hexgloop.casting.ContextModificationHandlers.Modification;
import java.util.function.BiFunction;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerBasedCastEnv.class)
public abstract class MixinPlayerBasedCastEnvModifiers {
  @ModifyReturnValue(method="canOvercast()Z", at=@At("RETURN"), remap = false)
  public boolean doOvercastModification(boolean original){
    boolean current = original;
    for(Pair<BiFunction<CastingEnvironment, Boolean, Modification>, Integer> pair : ContextModificationHandlers.OVERCAST_MODIFIERS){
      BiFunction<CastingEnvironment, Boolean, Modification> modifier = pair.getLeft();
      Modification mod = modifier.apply((CastingEnvironment)(Object)this, current);
      if(mod != Modification.NONE){
        current = (mod == Modification.ENABLE);
      }
    }
    return current;
  }
}