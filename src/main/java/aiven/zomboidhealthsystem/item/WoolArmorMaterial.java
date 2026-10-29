package aiven.zomboidhealthsystem.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WoolArmorMaterial implements ArmorMaterial {
    private static final List<ArmorItem.Type> types = List.of(ArmorItem.Type.HELMET, ArmorItem.Type.CHESTPLATE, ArmorItem.Type.LEGGINGS, ArmorItem.Type.BOOTS);
    private static final int[] protections = new int[]{1,3,2,1};
    private static final int[] durability = new int[]{300, 400, 350, 250};

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return durability[types.indexOf(type)];
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return protections[types.indexOf(type)];
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_LEATHER;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(Items.WHITE_WOOL);
    }

    @Override
    public @NotNull String getName() {
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
