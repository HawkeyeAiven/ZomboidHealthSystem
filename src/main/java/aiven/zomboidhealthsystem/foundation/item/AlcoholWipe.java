package aiven.zomboidhealthsystem.foundation.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;

public class AlcoholWipe extends ToolItem {
    public static final int DISINFECT_COOLDOWN_TIME = 3 * 20;

    public AlcoholWipe() {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return 8;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return 0;
            }

            @Override
            public float getAttackDamage() {
                return 0;
            }

            @Override
            public int getMiningLevel() {
                return 0;
            }

            @Override
            public int getEnchantability() {
                return 0;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }
        },new FabricItemSettings().maxCount(1));
    }
}
