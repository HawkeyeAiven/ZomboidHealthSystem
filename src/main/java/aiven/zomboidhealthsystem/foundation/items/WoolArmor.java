package aiven.zomboidhealthsystem.foundation.items;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.List;

public class WoolArmor implements ArmorMaterial {
    int[] protections = new int[]{1,3,2,1};
    private final static List<EquipmentSlot> equipmentSlots = List.of(EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET);

    @Override
    public int getDurability(ArmorItem.Type type) {
        return 300;
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return protections[equipmentSlots.indexOf(type.getEquipmentSlot())];
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(Items.WHITE_WOOL);
    }

    @Override
    public String getName() {
        return "wool";
    }

    @Override
    public float getToughness() {
        return 0;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
