package com.samsthenerd.hexgloop.mixins.mishapprotection;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import org.spongepowered.asm.mixin.Mixin;

import com.samsthenerd.hexgloop.casting.mishapprotection.IMishapStorage;

import net.minecraft.text.Text;

@Mixin(CastingEnvironment.class)
public class MixinStoreLastMishapContext implements IMishapStorage{
    private Text lastMishap = null;

    public Text getLastMishap(){
        return lastMishap;
    }

    public void setLastMishap(Text mishap){
        lastMishap = mishap;
    }
}
