package com.benjamin.deepdark.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class InvertGlassesItem extends ArmorItem {

    // Custom armor material - geen bescherming, alleen wearable
    public static final ArmorMaterial GLASSES_MATERIAL = new ArmorMaterial() {
        @Override
        public int getDurability(Type type) { return 100; }

        @Override
        public int getProtection(Type type) { return 0; }

        @Override
        public int getEnchantability() { return 0; }

        @Override
        public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER; }

        @Override
        public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }

        @Override
        public String getName() { return "invert_glasses"; }

        @Override
        public float getToughness() { return 0.0f; }

        @Override
        public float getKnockbackResistance() { return 0.0f; }
    };

    public InvertGlassesItem() {
        super(GLASSES_MATERIAL, Type.HELMET, new Item.Settings().maxCount(1));
    }
}
