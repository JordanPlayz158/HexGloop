package com.samsthenerd.hexgloop.items;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.samsthenerd.hexgloop.casting.IContextHelper;
import com.samsthenerd.hexgloop.casting.gloopifact.ICADHarnessStorage;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemHexMiningTool extends ItemHexTool implements IExtendedEnchantable.IToolEnchantable{

    private final Set<TagKey<Block>> effectiveBlocks;
    private final float realAttackDamage; // total
    private final boolean castOnAttack;
    private final float miningSpeed = 10.0F;

    public ItemHexMiningTool(Settings pProperties, float attackDamage, float attackSpeed, Set<TagKey<Block>> effectiveBlocks, boolean castOnAttack) {
        super(pProperties, 2.0f, attackSpeed);
        this.effectiveBlocks = effectiveBlocks;
        this.realAttackDamage = attackDamage;
        this.castOnAttack = castOnAttack;
    }

	public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
		if(!hasMediaToUse(stack)){
            return 0.5F;
        }
        for(TagKey<Block> tag : effectiveBlocks){
            if(state.isIn(tag)){
                return miningSpeed;
            }
        }
        return 1.0F;
	}

	public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(hasMediaToUse(stack)){
            withdrawMedia(stack, Math.min(1*MediaConstants.DUST_UNIT, getMedia(stack)), false);
            // should probably also do extra damage here ? - i don't think it'll stack with the other damage because of iframes or whatever ?
            // hm, might be neat if it scaled to do more damage for more media, but idk if that should be based on max media size or just the amount ?
            float enchantmentDamage = EnchantmentHelper.getAttackDamage(stack, target.getGroup());
            target.damage(DamageSource.MAGIC, realAttackDamage + enchantmentDamage);
            if(!(target.getWorld() instanceof ServerWorld sWorld)) return false;
            if(!(attacker instanceof ServerPlayerEntity sPlayer)) return false;
            if(castOnAttack){
                List<Iota> instrs = getHex(stack, sWorld);
                if (instrs == null) {
                    return false;
                }
                var ctx = new CastingEnvironment(sPlayer, attacker.getStackInHand(Hand.MAIN_HAND) == stack ? Hand.MAIN_HAND : Hand.OFF_HAND, CastingEnvironment.CastSource.PACKAGED_HEX);
                if(((Object)ctx) instanceof IContextHelper ctxHelper){
                    ctxHelper.setCastingItem(stack);
                }
                var harness = new CastingVM(ctx);
                harness.setStack(new ArrayList<Iota>(List.of(new EntityIota(target))));
                
                var info = harness.executeIotas(instrs, sWorld);
            }
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
            if(!(miner instanceof ServerPlayerEntity sPlayer)) return false;
            if(!(world instanceof ServerWorld sWorld)) return false;

            List<Iota> instrs = getHex(stack, sWorld);
            if (instrs == null) {
                return false;
            }
            var ctx = new CastingEnvironment(sPlayer, miner.getStackInHand(Hand.MAIN_HAND) == stack ? Hand.MAIN_HAND : Hand.OFF_HAND, CastingEnvironment.CastSource.PACKAGED_HEX);
            if(((Object)ctx) instanceof IContextHelper ctxHelper){
                ctxHelper.setCastingItem(stack);
            }
            var harness = new CastingVM(ctx);
            harness.setStack(new ArrayList<Iota>(
                List.of(new Vec3Iota(new Vec3d(pos.getX(), pos.getY(), pos.getZ())))
            ));
            
            ((ICADHarnessStorage)(Object)sPlayer).addHarness(harness);
            var info = harness.executeIotas(instrs, sWorld);
            ((ICADHarnessStorage)(Object)sPlayer).removeHarness(harness);
            sPlayer.incrementStat(Stats.USED.getOrCreateStat(this));
            return true;
        }
        return false;
	}

	public boolean isSuitableFor(BlockState state) {
		for(TagKey<Block> tag : effectiveBlocks){
            if(state.isIn(tag)){
                return true;
            }
        }
        return false;
	}
}
