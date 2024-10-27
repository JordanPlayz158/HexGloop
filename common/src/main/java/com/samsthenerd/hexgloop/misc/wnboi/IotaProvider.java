package com.samsthenerd.hexgloop.misc.wnboi;



import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

/*
 * An interface that gives Iotas for the IotaWheelScreen. 
 * 
 * extends label provider so that all iotaproviders have label providers but we can still have other label providers
 */
public interface IotaProvider extends LabelProvider{

    public NbtCompound getIotaNBT(int index);

    public Random getRNG();

    public Text getName(int index);

    public default FrozenPigment getColorizer(){
        return IXplatAbstractions.INSTANCE.getColorizer(MinecraftClient.getInstance().player);
    }
}