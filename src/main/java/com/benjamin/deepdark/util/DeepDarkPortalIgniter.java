package com.benjamin.deepdark.util;

import com.benjamin.deepdark.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DeepDarkPortalIgniter {
    private final World world;
    private final Direction.Axis axis;
    private final Direction negativeDir;
    private final Direction positiveDir;
    private BlockPos lowerCorner;
    private int width;
    private int height;

    public DeepDarkPortalIgniter(World world, BlockPos pos, Direction.Axis axis) {
        this.world = world;
        this.axis = axis;
        
        if (axis == Direction.Axis.X) {
            this.negativeDir = Direction.EAST;
            this.positiveDir = Direction.WEST;
        } else {
            this.negativeDir = Direction.NORTH;
            this.positiveDir = Direction.SOUTH;
        }

        // Vind de linker onderhoek van de portal
        BlockPos bottom = findBottomCorner(pos);
        if (bottom == null) {
            this.lowerCorner = null;
            return;
        }
        
        this.lowerCorner = bottom;
        this.width = calculateWidth();
        this.height = calculateHeight();
    }

    private BlockPos findBottomCorner(BlockPos pos) {
        // Ga naar beneden tot we de bodem vinden
        BlockPos current = pos;
        for (int i = 0; i < 21; i++) {
            BlockState below = world.getBlockState(current.down());
            if (below.getBlock() != Blocks.DEEPSLATE && !below.isAir()) {
                break;
            }
            if (below.getBlock() == Blocks.DEEPSLATE) {
                current = current.down();
                break;
            }
            current = current.down();
        }

        // Ga naar links tot we de linkerrand vinden
        for (int i = 0; i < 21; i++) {
            BlockState side = world.getBlockState(current.offset(negativeDir));
            if (side.getBlock() != Blocks.DEEPSLATE && !side.isAir()) {
                break;
            }
            if (side.getBlock() == Blocks.DEEPSLATE) {
                current = current.offset(negativeDir);
                break;
            }
            current = current.offset(negativeDir);
        }

        return current;
    }

    private int calculateWidth() {
        for (int w = 0; w < 21; w++) {
            BlockPos checkPos = lowerCorner.offset(positiveDir, w);
            BlockState state = world.getBlockState(checkPos);
            
            if (state.getBlock() == Blocks.DEEPSLATE) {
                return w - 1;
            }
            
            if (!state.isAir()) {
                return 0;
            }
        }
        return 0;
    }

    private int calculateHeight() {
        for (int h = 0; h < 21; h++) {
            BlockPos checkPos = lowerCorner.up(h);
            BlockState state = world.getBlockState(checkPos);
            
            if (state.getBlock() == Blocks.DEEPSLATE) {
                return h - 1;
            }
            
            if (!state.isAir()) {
                return 0;
            }
        }
        return 0;
    }

    public boolean isValid() {
        if (lowerCorner == null) {
            return false;
        }
        
        // Portal moet minimaal 2 breed en 3 hoog zijn (zoals Nether)
        if (width < 2 || width > 21 || height < 3 || height > 21) {
            return false;
        }

        // Check of de frame compleet is
        return isFrameComplete();
    }

    private boolean isFrameComplete() {
        // Check onderkant
        for (int x = -1; x <= width; x++) {
            BlockPos pos = lowerCorner.offset(positiveDir, x).down();
            if (world.getBlockState(pos).getBlock() != Blocks.DEEPSLATE) {
                return false;
            }
        }

        // Check bovenkant
        for (int x = -1; x <= width; x++) {
            BlockPos pos = lowerCorner.offset(positiveDir, x).up(height);
            if (world.getBlockState(pos).getBlock() != Blocks.DEEPSLATE) {
                return false;
            }
        }

        // Check linkerkant
        for (int y = 0; y < height; y++) {
            BlockPos pos = lowerCorner.offset(negativeDir).up(y);
            if (world.getBlockState(pos).getBlock() != Blocks.DEEPSLATE) {
                return false;
            }
        }

        // Check rechterkant
        for (int y = 0; y < height; y++) {
            BlockPos pos = lowerCorner.offset(positiveDir, width).up(y);
            if (world.getBlockState(pos).getBlock() != Blocks.DEEPSLATE) {
                return false;
            }
        }

        return true;
    }

    public void ignite() {
        if (!isValid()) {
            return;
        }

        // Vul de portal met portal blocks
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                BlockPos pos = lowerCorner.offset(positiveDir, x).up(y);
                world.setBlockState(pos, ModBlocks.DEEP_DARK_PORTAL.getDefaultState(), 3);
            }
        }
    }

    public static boolean tryIgnitePortal(World world, BlockPos pos) {
        // Probeer X-axis
        DeepDarkPortalIgniter xIgniter = new DeepDarkPortalIgniter(world, pos, Direction.Axis.X);
        if (xIgniter.isValid()) {
            xIgniter.ignite();
            return true;
        }

        // Probeer Z-axis
        DeepDarkPortalIgniter zIgniter = new DeepDarkPortalIgniter(world, pos, Direction.Axis.Z);
        if (zIgniter.isValid()) {
            zIgniter.ignite();
            return true;
        }

        return false;
    }
}
