package com.samsthenerd.hexgloop.casting.redstone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.samsthenerd.hexgloop.blocks.BlockConjuredRedstone;
import com.samsthenerd.hexgloop.blocks.HexGloopBlocks;
import com.samsthenerd.hexgloop.casting.MishapThrowerWrapper;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.common.casting.arithmetic.operator.OperatorUtilsKt;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.spell.casting.CastingContext;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import kotlin.Triple;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OpConjureRedstone implements SpellAction{
    private static final Random RANDOM = new Random();
    @Override
    public int getArgc(){ return 2;}

    @Override
    public boolean hasCastingSound(CastingContext context){ return true;}

    @Override
    public boolean awardsCastingStat(CastingContext context){ return true;}

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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingContext context){
        try{
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        context.assertVecInRange(pos);

        int power = OperatorUtils.getInt(args, 1, getArgc());



        if (!context.getWorld().canPlayerModifyAt(context.getCaster(), pos))
            return null;

        ItemPlacementContext placeContext = new ItemPlacementContext(context.getWorld(), context.getCaster(), 
            context.getCastingHand(), ItemStack.EMPTY, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));

        BlockState worldState = context.getWorld().getBlockState(pos);
        if (!worldState.canReplace(placeContext))
            // return null;
            throw MishapBadBlock.of(pos, "replaceable");

        List<ParticleSpray> particles = new ArrayList<>();
        particles.add(ParticleSpray.cloud(Vec3d.ofCenter(pos), 1.0, 1));

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(pos, power),
            MediaConstants.DUST_UNIT,
            particles
        );
        } catch (MishapBadBlock e){
            MishapThrowerWrapper.throwMishap(e);
            return null;
        }
    }

    private class Spell implements RenderedSpell {
        BlockPos pos;
        int power;
        public Spell(BlockPos pos, int power) {
            this.pos = pos;
            this.power = power;
        }

        @Override
        public void cast(CastingContext context) {
            if (!context.canEditBlockAt(pos))
                return;
            
            Block block = HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get();

            FrozenPigment colorizer = IXplatAbstractions.INSTANCE.getColorizer(context.getCaster());

            ItemStack conjuredRSItemStack = new ItemStack(HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get().asItem());

            ItemPlacementContext placeContext = new ItemPlacementContext(context.getWorld(), context.getCaster(), 
                context.getCastingHand(), conjuredRSItemStack, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));


            BlockState worldState = context.getWorld().getBlockState(pos);
            if (worldState.canReplace(placeContext)) {
                if (!IXplatAbstractions.INSTANCE.isPlacingAllowed(context.getWorld(), pos, new ItemStack(HexGloopBlocks.CONJURED_REDSTONE_BLOCK.get().asItem()), context.getCaster()))
                    return;

                BlockState state = block.getPlacementState(placeContext);
                if (state != null) {
                    context.getWorld().setBlockState(pos, state);

                    if (context.getWorld().getBlockState(pos).getBlock() instanceof BlockConjuredRedstone) {
                        BlockConjuredRedstone.setColor(context.getWorld(), pos, colorizer);
                        BlockConjuredRedstone.setPower(context.getWorld(), pos, power);
                    }
                }
            }
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingContext castingContext){
        return SpellAction.DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
    
}