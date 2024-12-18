package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.ArrayList;
import java.util.List;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.spell.casting.CastingHarness;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHexSword extends ItemHexTool implements IExtendedEnchantable.IWeaponEnchantable{

    public ItemHexSword(Settings pProperties) {
        super(pProperties, 2.0f, -2.4f);
    }

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if (state.isOf(Blocks.COBWEB)) {
			return 15.0F;
		} else {
			Material material = state.getMaterial();

			return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
		}
	}

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(hasMediaToUse(stack)){
            withdrawMedia(stack, Math.min(1*MediaConstants.DUST_UNIT, getMedia(stack)), false);
            // should probably also do extra damage here ? - i don't think it'll stack with the other damage because of iframes or whatever ?
            // hm, might be neat if it scaled to do more damage for more media, but idk if that should be based on max media size or just the amount ?
            float enchantmentDamage = EnchantmentHelper.getAttackDamage(stack, target.getGroup());
            target.damage(DamageSource.MAGIC, 8.0F + enchantmentDamage);
            if(!(target.getWorld() instanceof ServerWorld sWorld)) return false;
            List<Iota> instrs = getHex(stack, sWorld);
            if (instrs == null) {
                return false;
            }
            if(!(attacker instanceof ServerPlayerEntity sPlayer)) return false;
            var ctx = new CastingEnvironment(sPlayer, attacker.getStackInHand(Hand.MAIN_HAND) == stack ? Hand.MAIN_HAND : Hand.OFF_HAND, CastingContext.CastSource.PACKAGED_HEX);
            if(((Object)ctx) instanceof IContextHelper ctxHelper){
                ctxHelper.setCastingItem(stack);
            }
            var harness = new CastingVM(ctx);
            harness.setStack(new ArrayList<Iota>(List.of(new EntityIota(target))));
            
            ((ICADHarnessStorage)(Object)sPlayer).addHarness(harness);
            var info = harness.executeIotas(instrs, sWorld);
            ((ICADHarnessStorage)(Object)sPlayer).removeHarness(harness);

            sPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
            return true;
        }
        return false;
	}

	public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if(hasMediaToUse(stack)){
            if (state.getHardness(world, pos) != 0.0F) {
                withdrawMedia(stack, Math.min(2*MediaConstants.DUST_UNIT, getMedia(stack)), false);
                // not sure if it needs a break sound or whatever ?
            }
            return true;
        }
        return false;
	}

	public boolean isSuitableFor(BlockState state) {
		return state.isOf(Blocks.COBWEB);
	}
}
