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
                BlockPos destinationPos;
                
                // Als we in de Overworld zijn, ga naar Deep Dark
                if (world.getRegistryKey() == World.OVERWORLD) {
                    destination = serverWorld.getServer().getWorld(ModDimension.DEEP_DARK_WORLD);
                    // Spawn op een veilige hoogte in Deep Dark (y=64)
                    destinationPos = new BlockPos(pos.getX(), 64, pos.getZ());
                } 
                // Als we in Deep Dark zijn, ga terug naar Overworld
                else if (world.getRegistryKey() == ModDimension.DEEP_DARK_WORLD) {
                    destination = serverWorld.getServer().getWorld(World.OVERWORLD);
                    // Zoek een veilige plek in de Overworld
                    destinationPos = findSafeSpawnPos(destination, pos);
                } else {
                    return;
                }

                if (destination != null) {
                    // Maak een platform op de spawn locatie
                    createSpawnPlatform(destination, destinationPos);
                    
                    // Teleporteer de entity naar de nieuwe dimensie
                    entity.teleport(
                        destination,
                        destinationPos.getX() + 0.5,
                        destinationPos.getY() + 1.0,
                        destinationPos.getZ() + 0.5,
                        java.util.Collections.emptySet(),
                        entity.getYaw(),
                        entity.getPitch()
                    );
                }
            }
        }
    }

    private BlockPos findSafeSpawnPos(ServerWorld world, BlockPos originalPos) {
        // Zoek vanaf y=128 naar beneden voor een veilige plek
        for (int y = 128; y > -64; y--) {
            BlockPos testPos = new BlockPos(originalPos.getX(), y, originalPos.getZ());
            if (world.getBlockState(testPos).isSolidBlock(world, testPos)) {
                return testPos.up();
            }
        }
        return new BlockPos(originalPos.getX(), 64, originalPos.getZ());
    }

    private void createSpawnPlatform(ServerWorld world, BlockPos center) {
        // Maak een 7x7 eiland met sculk!
        int radius = 3;
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                // Cirkel vorm (geen vierkant)
                double distance = Math.sqrt(x * x + z * z);
                if (distance <= radius + 0.5) {
                    BlockPos platformPos = center.add(x, 0, z);
                    
                    // Top laag: Sculk Catalyst
                    world.setBlockState(platformPos, net.minecraft.block.Blocks.SCULK_CATALYST.getDefaultState());
                    
                    // 3 lagen daaronder: Sculk
                    for (int depth = 1; depth <= 3; depth++) {
                        world.setBlockState(platformPos.down(depth), net.minecraft.block.Blocks.SCULK.getDefaultState());
                    }
                    
                    // Core (dieper): Deepslate
                    for (int depth = 4; depth <= 8; depth++) {
                        world.setBlockState(platformPos.down(depth), net.minecraft.block.Blocks.DEEPSLATE.getDefaultState());
                    }
                    
                    // Clear de ruimte erboven
                    for (int height = 1; height <= 4; height++) {
                        world.setBlockState(platformPos.up(height), net.minecraft.block.Blocks.AIR.getDefaultState());
                    }
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
