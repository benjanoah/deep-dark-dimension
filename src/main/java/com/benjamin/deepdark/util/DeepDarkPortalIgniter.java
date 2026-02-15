package com.benjamin.deepdark.util;

import com.benjamin.deepdark.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DeepDarkPortalIgniter {
    
    public static boolean tryIgnitePortal(World world, BlockPos clickedPos) {
        // Probeer portals te vinden in beide richtingen (X en Z)
        if (tryIgnitePortalInAxis(world, clickedPos, Direction.Axis.X)) {
            return true;
        }
        if (tryIgnitePortalInAxis(world, clickedPos, Direction.Axis.Z)) {
            return true;
        }
        return false;
    }

    private static boolean tryIgnitePortalInAxis(World world, BlockPos clickedPos, Direction.Axis axis) {
        Direction dir1 = axis == Direction.Axis.X ? Direction.WEST : Direction.NORTH;
        Direction dir2 = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;

        // Zoek in een gebied rond het geklikte blok
        for (int xOff = -21; xOff <= 21; xOff++) {
            for (int yOff = -21; yOff <= 21; yOff++) {
                BlockPos testPos = clickedPos.offset(dir1.getAxis(), xOff).offset(Direction.Axis.Y, yOff);
                
                // Probeer een portal te maken met deze positie als linker-onder hoek
                PortalFrame frame = new PortalFrame(world, testPos, axis);
                if (frame.isValid() && frame.containsBlock(clickedPos)) {
                    frame.ignite();
                    return true;
                }
            }
        }
        return false;
    }

    private static class PortalFrame {
        private final World world;
        private final BlockPos bottomLeft;
        private final Direction.Axis axis;
        private final Direction horizontal;
        private int width;
        private int height;

        public PortalFrame(World world, BlockPos bottomLeft, Direction.Axis axis) {
            this.world = world;
            this.bottomLeft = bottomLeft;
            this.axis = axis;
            this.horizontal = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
            
            calculateSize();
        }

        private void calculateSize() {
            // Bereken breedte
            width = 0;
            for (int i = 0; i < 21; i++) {
                BlockPos pos = bottomLeft.offset(horizontal, i);
                if (world.getBlockState(pos).isAir()) {
                    width++;
                } else {
                    break;
                }
            }

            // Bereken hoogte
            height = 0;
            for (int i = 0; i < 21; i++) {
                BlockPos pos = bottomLeft.up(i);
                if (world.getBlockState(pos).isAir()) {
                    height++;
                } else {
                    break;
                }
            }
        }

        public boolean isValid() {
            // Minimaal 2 breed, 3 hoog (zoals Nether portal)
            if (width < 2 || height < 3) {
                return false;
            }

            // Check of de frame van deepslate is
            return isFrameComplete();
        }

        private boolean isFrameComplete() {
            // Check onderkant
            for (int x = -1; x <= width; x++) {
                BlockPos pos = bottomLeft.offset(horizontal, x).down();
                if (!isDeepslate(pos)) return false;
            }

            // Check bovenkant
            for (int x = -1; x <= width; x++) {
                BlockPos pos = bottomLeft.offset(horizontal, x).up(height);
                if (!isDeepslate(pos)) return false;
            }

            // Check linkerkant
            for (int y = 0; y < height; y++) {
                BlockPos pos = bottomLeft.offset(horizontal, -1).up(y);
                if (!isDeepslate(pos)) return false;
            }

            // Check rechterkant
            for (int y = 0; y < height; y++) {
                BlockPos pos = bottomLeft.offset(horizontal, width).up(y);
                if (!isDeepslate(pos)) return false;
            }

            // Check dat de binnenkant leeg is
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    BlockPos pos = bottomLeft.offset(horizontal, x).up(y);
                    if (!world.getBlockState(pos).isAir()) {
                        return false;
                    }
                }
            }

            return true;
        }

        private boolean isDeepslate(BlockPos pos) {
            return world.getBlockState(pos).getBlock() == Blocks.DEEPSLATE;
        }

        public boolean containsBlock(BlockPos pos) {
            // Check of dit blok deel uitmaakt van de frame
            int dx = axis == Direction.Axis.X ? pos.getX() - bottomLeft.getX() : pos.getZ() - bottomLeft.getZ();
            int dy = pos.getY() - bottomLeft.getY();

            // Onderkant of bovenkant
            if (dy == -1 || dy == height) {
                return dx >= -1 && dx <= width;
            }

            // Linkerkant of rechterkant
            if (dx == -1 || dx == width) {
                return dy >= 0 && dy < height;
            }

            return false;
        }

        public void ignite() {
            // Vul met portal blokken
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    BlockPos pos = bottomLeft.offset(horizontal, x).up(y);
                    world.setBlockState(pos, ModBlocks.DEEP_DARK_PORTAL.getDefaultState(), 3);
                }
            }
        }
    }
}
