package com.samsthenerd.hexgloop.casting.inventorty;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.inventorty.InventortyUtils.KittyContext;
import com.samsthenerd.hexgloop.compat.moreIotas.MoreIotasMaybeIotas;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import dev.architectury.platform.Platform;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;

public class OpGetInventoryLore implements ConstMediaAction{

    boolean getSizeOrName;

    // could make this not a boolean later if we want to add other types
    public OpGetInventoryLore(boolean getSizeOrName){
        this.getSizeOrName = getSizeOrName;
    }

    @Override
    public int getArgc(){ return 0;}

    @Override
    public int getMediaCost(){
        return 0;
    }

    @Override
    public boolean isGreat(){ return false;}

    @Override
    public boolean getCausesBlindDiversion(){ return false;}

    @Override 
    public boolean getAlwaysProcessGreatSpell(){ return false;}

    @Override
    public Text getDisplayName(){ 
        return DefaultImpls.getDisplayName(this);
    }

    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment context){
        KittyContext kCtx = InventortyUtils.assertKittyCasting(context);
        List<Iota> lore = new ArrayList<>();
        for(int i = 0; i < kCtx.getInventoryCount(); i++){
            Inventory inv = kCtx.getInventory(i);
            if(this.getSizeOrName){ // get size
                int size = kCtx.getSlots(i).size();
                lore.add(new DoubleIota(size));
            } else { // get names
                String name = InventortyUtils.getInventoryName(inv, kCtx);
                if(Platform.isModLoaded("moreiotas")){
                    lore.add(MoreIotasMaybeIotas.makeStringIota(name));
                } else { // this shouldn't ever happen, but just in case
                    lore.add(new NullIota());
                }
            }
        }
        return List.of(new ListIota(lore));
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return ConstMediaAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}