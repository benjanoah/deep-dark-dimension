package com.benjamin.deepdark.block;

import com.benjamin.deepdark.ModDimension;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DeepDarkPortalBlock extends Block {
    protected static final VoxelShape X_SHAPE = Block.createCuboidShape(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_SHAPE = Block.createCuboidShape(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public DeepDarkPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        // Bepaal richting van de portal
        if (isXAxis(world, pos)) {
            return X_SHAPE;
        }
        return Z_SHAPE;
    }

    private boolean isXAxis(BlockView world, BlockPos pos) {
        BlockState north = world.getBlockState(pos.north());
        BlockState south = world.getBlockState(pos.south());
        return north.getBlock() == Blocks.DEEPSLATE || south.getBlock() == Blocks.DEEPSLATE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(100) == 0) {
            world.playSound(
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.BLOCK_PORTAL_AMBIENT,
                SoundCategory.BLOCKS,
                0.5F,
                random.nextFloat() * 0.4F + 0.8F,
                false
            );
        }

        // Sculk-blauwe particles (cyan)
        for (int i = 0; i < 4; ++i) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            double vx = (random.nextDouble() - 0.5) * 0.5;
            double vy = (random.nextDouble() - 0.5) * 0.5;
            double vz = (random.nextDouble() - 0.5) * 0.5;
            
            // Gebruik soul flame particles voor blauwe gloed
            world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, vx, vy, vz);
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && !entity.hasVehicle() && !entity.hasPassengers() && entity.canUsePortals()) {
            if (world instanceof ServerWorld serverWorld) {
                ServerWorld destination;
                
                // Als we in de Overworld zijn, ga naar Deep Dark
                if (world.getRegistryKey() == World.OVERWORLD) {
                    destination = serverWorld.getServer().getWorld(ModDimension.DEEP_DARK_WORLD);
                } 
                // Als we in Deep Dark zijn, ga terug naar Overworld
                else if (world.getRegistryKey() == ModDimension.DEEP_DARK_WORLD) {
                    destination = serverWorld.getServer().getWorld(World.OVERWORLD);
                } else {
                    return;
                }

                if (destination != null) {
                    entity.moveToWorld(destination);
                }
            }
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(
        BlockState state,
        Direction direction,
        BlockState neighborState,
        WorldAccess world,
        BlockPos pos,
        BlockPos neighborPos
    ) {
        // Check of de portal nog geldig is
        Direction.Axis axis = isXAxis(world, pos) ? Direction.Axis.X : Direction.Axis.Z;
        if (!isValidPortalFrame(world, pos, axis)) {
            return Blocks.AIR.getDefaultState();
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private boolean isValidPortalFrame(WorldAccess world, BlockPos pos, Direction.Axis axis) {
        // Check of er deepslate aan de zijkanten zit
        if (axis == Direction.Axis.X) {
            return world.getBlockState(pos.north()).getBlock() == Blocks.DEEPSLATE || 
                   world.getBlockState(pos.south()).getBlock() == Blocks.DEEPSLATE;
        } else {
            return world.getBlockState(pos.east()).getBlock() == Blocks.DEEPSLATE || 
                   world.getBlockState(pos.west()).getBlock() == Blocks.DEEPSLATE;
        }
    }
}
