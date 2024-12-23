package com.samsthenerd.hexgloop.casting.mishapprotection;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.compat.moreIotas.MoreIotasMaybeIotas;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import dev.architectury.platform.Platform;
import net.minecraft.text.Text;

public class OpRevealLastMishap implements ConstMediaAction {
   private boolean revealOrString;

   public OpRevealLastMishap(boolean revealOrString) {
      this.revealOrString = revealOrString;
   }

   @Override
   public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx){
      Text mishapText = ((IMishapStorage)(Object)ctx).getLastMishap();
      Text wrapperText = Text.translatable("hexgloop.mishap.catch_wrapper" + (mishapText == null ? ".none" : ""), mishapText);
      List<Iota> result = new ArrayList<>();
      if(revealOrString){
         ctx.getCaster().sendMessageToClient(wrapperText, false);
      } else if(Platform.isModLoaded("moreiotas")){
         result.add(MoreIotasMaybeIotas.makeStringIota(wrapperText.getString()));
      }
      return result;
   }

   @Override
   public OperationResult operate(CastingEnvironment env, CastingImage image, SpellContinuation continuation){
      return ConstMediaAction.DefaultImpls.operate(this, env, image, continuation);
   }

   public int getMediaCost(){
      return 0;
   }

   public int getArgc(){
      return 0;
   }

   public boolean isGreat() {
      return DefaultImpls.isGreat(this);
   }

   public boolean getAlwaysProcessGreatSpell() {
      return DefaultImpls.getAlwaysProcessGreatSpell(this);
   }

   public boolean getCausesBlindDiversion() {
      return DefaultImpls.getCausesBlindDiversion(this);
   }

   @NotNull
   public Text getDisplayName() {
      return DefaultImpls.getDisplayName(this);
   }
}