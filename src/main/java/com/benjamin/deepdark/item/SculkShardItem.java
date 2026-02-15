package com.benjamin.deepdark.item;

import com.benjamin.deepdark.util.DeepDarkPortalIgniter;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SculkShardItem extends Item {
    public SculkShardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (world.isClient) {
            return ActionResult.SUCCESS;
        }

        // Check of je op deepslate klikt
        if (world.getBlockState(pos).getBlock() != Blocks.DEEPSLATE) {
            return ActionResult.PASS;
        }

        // Probeer de portal aan te steken vanaf dit deepslate blok
        boolean success = DeepDarkPortalIgniter.tryIgnitePortal(world, pos);

        if (success) {
            // Speel portal ignite sound
            world.playSound(
                null,
                pos,
                SoundEvents.ITEM_FLINTANDSTEEL_USE,
                SoundCategory.BLOCKS,
                1.0F,
                1.0F
            );

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
