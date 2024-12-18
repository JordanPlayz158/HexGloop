package com.samsthenerd.hexgloop.casting.gloopifact;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import java.util.List;

import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.HexalWispWrapper;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import dev.architectury.platform.Platform;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class GloopifactUtils {
    public static List<String> expectedSources = List.of("gloopifact");

    public static Pair<ItemStack, ItemGloopifact> assertGloopcasting(CastingEnvironment context){
        ItemStack castHandStack = context.getCaster().getStackInHand(context.getCastingHand());
        // make sure we're casting from atleast some packaged hex
        if(!(context instanceof PackagedItemCastEnv)
        || (Platform.isModLoaded("hexal") && HexalWispWrapper.isWisp(context))
        || !(castHandStack.getItem() instanceof ItemGloopifact gloopifactItem)){
            MishapThrowerWrapper.throwMishap(new MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(expectedSources));
            return null;
        }
        return new Pair<>(castHandStack, gloopifactItem);
    }
}
