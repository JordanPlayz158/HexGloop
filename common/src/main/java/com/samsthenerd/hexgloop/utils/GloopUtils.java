package com.samsthenerd.hexgloop.utils;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.samsthenerd.hexgloop.HexGloop;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.client.ClientTickCounter;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList.Named;

public class GloopUtils {
    // yoink from IotaHolderItem because,, agony,, with Dyebooks getColor messing up our mappings
    public static int getIotaColor(ItemStack stack){
        if (NBTHelper.hasString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY)) {
            var override = NBTHelper.getString(stack, IotaHolderItem.TAG_OVERRIDE_VISUALLY);

            if (override != null && Identifier.isValid(override)) {
                var key = new Identifier(override);
                if (HexIotaTypes.REGISTRY.containsId(key)) {
                    var iotaType = HexIotaTypes.REGISTRY.get(key);
                    if (iotaType != null) {
                        return iotaType.color();
                    }
                }
            }

            return 0xFF000000 | MathHelper.hsvToRgb(ClientTickCounter.getTotal() * 2 % 360 / 360F, 0.75F, 1F);
        }

        if(!(stack.getItem() instanceof IotaHolderItem iotaHolder)) return 0xFFFFFF;

        var tag = iotaHolder.readIotaTag(stack);
        if (tag == null) {
            return HexUtils.ERROR_COLOR;
        }

        return IotaType.getColor(tag);
    }

    private static Supplier<ItemConvertible>[] CACHED_STAFFS = null;

    public static Supplier<ItemConvertible>[] getAllStaffs(){
        if(CACHED_STAFFS != null) return CACHED_STAFFS;
        List<Supplier<ItemConvertible>> staffs = new ArrayList<Supplier<ItemConvertible>>();
        Named<Item> entryList = Registries.ITEM.getOrCreateEntryList(HexTags.Items.STAVES);
        HexGloop.logPrint("getting all " + entryList.size() + " staffs");
        for(RegistryEntry<Item> entry : entryList){
            entry.getKeyOrValue().ifRight(item -> {
                HexGloop.logPrint("found staff: " + item.getTranslationKey());
                staffs.add(() -> item);
            });
            entry.getKeyOrValue().ifLeft(key -> {
                HexGloop.logPrint("found staff: " + key.getValue());
                staffs.add(() -> Registries.ITEM.get(key));
            });
        }
        CACHED_STAFFS = staffs.toArray(new Supplier[0]);
        return CACHED_STAFFS;
    }

    public static String prettyPrintID(Identifier id){
        String prettyPrinted = "";
        String noSymbols = id.getPath().replace("_", " ").replace("/", ", ");
        for(String word : noSymbols.split(" ")){
            prettyPrinted += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
        }
        if(id.getNamespace() != "minecraft"){
            prettyPrinted += "[" + id.getNamespace() + "]";
        }
        return prettyPrinted;
    }
}
