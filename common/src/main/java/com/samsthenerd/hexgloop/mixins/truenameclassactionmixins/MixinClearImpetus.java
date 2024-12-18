package com.samsthenerd.hexgloop.mixins.truenameclassactionmixins;

import at.petrak.hexcasting.common.blocks.circles.impetuses.BlockEntityRedstoneImpetus;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.samsthenerd.hexgloop.casting.truenameclassaction.ISetImpetusKey;
import com.samsthenerd.hexgloop.casting.truenameclassaction.MishapClearedTruename;
import com.samsthenerd.hexgloop.misc.worldData.TruenameLockState;

import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

// just put it on any impetus and check if it's a cleric when we need to
@Mixin(BlockEntityAbstractImpetus.class)
public class MixinClearImpetus implements ISetImpetusKey {

    private static final String KEY_UUID_KEY = "keyUuid";
    private UUID keyUuid = null;

    // so we can change it from the block class
    public void setKeyUUID(UUID keyUuid) {
        this.keyUuid = keyUuid;
    }

    @Inject(method = "castSpell()V", at = @At("HEAD"), remap = false, cancellable = true)
    public void CheckLockBeforeCircleCast(CallbackInfo ci) {
        BlockEntityAbstractImpetus impetus = (BlockEntityAbstractImpetus) (Object) this;
        // only do this check since we don't get the key uuid for other impetus types rn

        // Despite the different name/potentially purpose, this new name seems to have the most
        //   similar structure as the old BlockEntityStoredPlayerImpetus
        if (impetus instanceof BlockEntityRedstoneImpetus clericImpetus) {
            ServerPlayerEntity player = clericImpetus.getStoredPlayer();

            if (player == null)
                return;

            ServerWorld world = sPlayer.getWorld();
            if (world == null)
                return;
            UUID lockUuid = TruenameLockState.getServerState(world.getServer())
                .getLockUUID(player.getUuid());
            if (lockUuid == null)
                return;
            if (!lockUuid.equals(keyUuid)) {
                // cancel the cast
                impetus.postMishap((new MishapClearedTruename(player)).makeError(
                    Text.translatable("hexgloop.generic_circle_spell"), player));
                ci.cancel();
            }

        }
    }

    @Inject(method = "saveModData(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    public void SaveImpetusKeyUuid(NbtCompound nbt, CallbackInfo ci) {
        if (keyUuid != null) {
            nbt.putUuid(KEY_UUID_KEY, keyUuid);
        }
    }

    @Inject(method = "loadModData(Lnet/minecraft/nbt/NbtCompound;)V", at = @At("TAIL"))
    public void LoadImpetusKeyUuid(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.containsUuid(KEY_UUID_KEY)) {
            keyUuid = nbt.getUuid(KEY_UUID_KEY);
        }
    }
}