package com.benjamin.deepdark.item;

import com.benjamin.deepdark.util.DeepDarkPortalIgniter;
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

        // Probeer de portal aan te steken
        boolean success = DeepDarkPortalIgniter.tryIgnitePortal(world, pos.offset(context.getSide()));

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

            // Damage het item niet - het is een catalyst, geen consumable
            // Als je wil dat het slijt, voeg dit toe:
            // if (player != null && !player.getAbilities().creativeMode) {
            //     context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
            // }

            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}
