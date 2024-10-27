package com.samsthenerd.hexgloop.casting.truenameclassaction;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class MishapClearedTruename extends Mishap {
    PlayerEntity refdPlayer;

    // expects the list to match the source name suffixes from sourceFromCtx
    public MishapClearedTruename(PlayerEntity refdPlayer) {
        super();
        this.refdPlayer = refdPlayer;
    }

    @NotNull
    public FrozenPigment accentColor(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(errorCtx, "errorCtx");
        return this.dyeColor(DyeColor.BROWN);
    }

    public void execute(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx, @NotNull List<Iota> stack) {
        
    }

    public Text makeError(Text actionName, PlayerEntity player){
        Object[] errorArgs;
        errorArgs = new Object[]{actionName, player.getName()};
        return this.error("cleared_truename", errorArgs);
    }

    @NotNull
    public Text errorMessage(@NotNull CastingContext ctx, @NotNull Mishap.Context errorCtx) {
        return this.makeError(this.actionName(errorCtx.getAction()), refdPlayer);
    }

}