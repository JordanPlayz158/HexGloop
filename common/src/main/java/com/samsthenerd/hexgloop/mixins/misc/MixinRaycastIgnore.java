package com.samsthenerd.hexgloop.mixins.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.samsthenerd.hexgloop.blocks.ICantBeRaycasted;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.common.casting.actions.raycast.OpBlockAxisRaycast;
import at.petrak.hexcasting.common.casting.actions.raycast.OpBlockRaycast;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

@Mixin(value={
    OpBlockRaycast.class,
    OpBlockAxisRaycast.class
})
public class MixinRaycastIgnore {
    @WrapOperation(method="execute(Ljava/util/List;Lat/petrak/hexcasting/api/spell/casting/CastingEnvironment;)Ljava/util/List;",
    at=@At(value="INVOKE", target="net/minecraft/server/world/ServerWorld.raycast (Lnet/minecraft/world/RaycastContext;)Lnet/minecraft/util/hit/BlockHitResult;"))
    public BlockHitResult ignoreBlockRaycast(ServerWorld world, RaycastContext context, Operation<BlockHitResult> original, List<Iota> args, CastingEnvironment ctx){
        BlockHitResult hitResult = original.call(world, context);
        while(hitResult.getType() == BlockHitResult.Type.BLOCK 
            && world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof ICantBeRaycasted raycastModifierBlock){
            if(!raycastModifierBlock.passThrough(world, hitResult.getBlockPos())){
                return BlockHitResult.createMissed(hitResult.getPos(), hitResult.getSide(), hitResult.getBlockPos());
            }
            double soFarDistance = hitResult.getPos().distanceTo(context.getStart());
            if(soFarDistance >= 32) break;
            Vec3d direction = context.getEnd().subtract(context.getStart()).normalize();

            Vec3d newStart = hitResult.getPos().add(direction);
            RaycastContext newContext = new RaycastContext(newStart, context.getEnd(), ShapeType.COLLIDER, FluidHandling.NONE, ctx.getCastingEntity());
            hitResult = original.call(world, newContext);
        }
        return hitResult;
    }
}
