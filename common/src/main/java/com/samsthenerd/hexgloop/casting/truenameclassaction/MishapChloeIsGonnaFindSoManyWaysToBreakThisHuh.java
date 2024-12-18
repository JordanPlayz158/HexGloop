package com.samsthenerd.hexgloop.casting.truenameclassaction;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.StaffCastEnv;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.items.ItemGloopifact;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import dev.architectury.platform.Platform;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

// otherwise known as MishapBadCastingSource
public class MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh extends Mishap {
    private List<String> acceptedSources;

    // expects the list to match the source name suffixes from sourceFromCtx
    public MishapChloeIsGonnaFindSoManyWaysToBreakThisHuh(List<String> acceptedSources) {
        super();
        this.acceptedSources = acceptedSources;
    }

    @NotNull
    public FrozenPigment accentColor(@NotNull CastingEnvironment ctx, @NotNull Mishap.Context errorCtx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(errorCtx, "errorCtx");
        return this.dyeColor(DyeColor.BROWN);
    }

    public void execute(@NotNull CastingEnvironment ctx, @NotNull Mishap.Context errorCtx, @NotNull List<Iota> stack) {
        
    }

    public static String sourceFromCtx(CastingEnvironment ctx){
        if(((IContextHelper)(Object)ctx).isKitty()){
            return "inventorty";
        }
        if(ctx instanceof StaffCastEnv){
            return "staff";
        } else if(ctx instanceof CircleCastEnv){
            return "circle";
        } else if(ctx instanceof PackagedItemCastEnv){
            if(Platform.isModLoaded("hexal")){
                if(HexalWispWrapper.isWisp(ctx)){
                    return "wisp";
                }
            }
            // TODO: May be able to replace with CastingEnvironment#getCastingItem
            ItemStack castHandStack = ctx.getCastingEntity().getStackInHand(ctx.getCastingHand());
            if(castHandStack.getItem() instanceof ItemGloopifact gloopifactItem){
                return "gloopifact";
            }
            return "packaged_hex";
        }
        return "unknown";
    }

    private Text expectedSourcesList(){
        MutableText list = Text.literal("");
        int i = 0;
        for(String source : this.acceptedSources){
            list.append(Text.translatable("hexgloop.source_type."+source));
            i++;
            if(i != this.acceptedSources.size()){
                list.append(Text.literal(", "));
            }
        }
        return list;
    }

    @NotNull
    public Text errorMessage(@NotNull CastingEnvironment ctx, @NotNull Mishap.Context errorCtx) {
        Text errorText;
        Object[] errorArgs;
            errorArgs = new Object[]{this.actionName(errorCtx.getName()), expectedSourcesList(), Text.translatable("hexgloop.source_type."+sourceFromCtx(ctx))};
            errorText = this.error("wrong_casting_source" + (acceptedSources.size() > 1 ? "s" : "" ), errorArgs);
        return errorText;
    }
}
