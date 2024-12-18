package com.samsthenerd.hexgloop.casting;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.mixins.misc.MixinDispenserBehaviorAccessor;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import kotlin.Triple;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OpDispense implements SpellAction {

    public OpDispense(){
    }

    @Override
    public int getArgc(){ return 3;}

    public boolean hasCastingSound(@NotNull CastingEnvironment ctx){
        return false;
    }

    public boolean awardsCastingStat(@NotNull CastingEnvironment ctx){
        return true;
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
    public Triple<RenderedSpell, Integer, List<ParticleSpray>> execute(List<? extends Iota> args, CastingEnvironment context){
        // position to dispense from
        BlockPos pos = OperatorUtils.getBlockPos(args, 0, getArgc());
        context.assertVecInRange(pos);
        Vec3d dir = OperatorUtils.getVec3(args, 1, getArgc());
        ItemEntity itemEnt = OperatorUtils.getItemEntity(args, 2, getArgc());
        context.assertEntityInRange(itemEnt);

        ItemStack stackToDispense = itemEnt.getStack();

        DispenserBehavior behavior = ((MixinDispenserBehaviorAccessor)Blocks.DISPENSER).callGetBehaviorForItem(stackToDispense);

        int cost = 0;
        if(behavior != DispenserBehavior.NOOP && !stackToDispense.isEmpty()){
            cost = MediaConstants.DUST_UNIT / 2;
            if(behavior instanceof ProjectileDispenserBehavior){
                cost = MediaConstants.DUST_UNIT * 3; // 3 to launch a non arrow projectile
            }
            if(stackToDispense.getItem() instanceof ArrowItem){
                cost = MediaConstants.CRYSTAL_UNIT; // make it cost 1 charged for an arrow
            }
        }

        return new Triple<RenderedSpell, Integer, List<ParticleSpray>>(
            new Spell(itemEnt, pos, dir), 
            cost, 
            List.of());
    }

    public class Spell implements RenderedSpell{
        private ItemEntity dispenseEnt;
        private BlockPos pos;
        private Vec3d dir;

        public Spell(ItemEntity dispenseEnt, BlockPos pos, Vec3d dir){
            this.dispenseEnt = dispenseEnt;
            this.pos = pos;
            this.dir = dir;
        }

        public void cast(CastingEnvironment ctx){
            ItemStack stackToDispense = dispenseEnt.getStack();

            DispenserBehavior behavior = ((MixinDispenserBehaviorAccessor)Blocks.DISPENSER).callGetBehaviorForItem(stackToDispense);

            if(behavior != DispenserBehavior.NOOP && !stackToDispense.isEmpty()){
                BlockState fakeDispenserState = Blocks.DISPENSER.getDefaultState().with(DispenserBlock.FACING, Direction.getFacing(dir.getX(), dir.getY(), dir.getZ()));
                DispenserBlockEntity be = new DispenserBlockEntity(pos, fakeDispenserState);
                BlockPointer bp = new BlockPointerImpl(ctx.getWorld(), be.getPos()){

                    @SuppressWarnings("unchecked")
                    @Override
                    public <T extends BlockEntity> T getBlockEntity(){
                        return (T)be;
                    }

                    @Override
                    public BlockState getBlockState() {
                        return fakeDispenserState;
                    }
                };
                dispenseEnt.setStack(behavior.dispense(bp, stackToDispense));
                for(int i = 0; i < be.size(); i++){
                    ItemStack leftoverStack = be.getStack(i);
                    if(leftoverStack.isEmpty()){
                        continue;
                    }
                    ItemEntity leftoverItem = new ItemEntity(ctx.getWorld(), pos.getX(), pos.getY(), pos.getZ(), leftoverStack);
                    ctx.getWorld().spawnEntity(leftoverItem);
                }
                
            }
        }
    }

    @Override
    public OperationResult operate(SpellContinuation continuation, List<Iota> stack, Iota ravenmind, CastingEnvironment castingContext){
        return DefaultImpls.operate(this, continuation, stack, ravenmind, castingContext);
    }
}

