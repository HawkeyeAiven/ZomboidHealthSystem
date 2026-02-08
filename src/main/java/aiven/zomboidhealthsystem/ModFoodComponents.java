package aiven.zomboidhealthsystem;

import net.minecraft.item.FoodComponent;

public class ModFoodComponents {
    public static FoodComponent COFFEE = new FoodComponent.Builder().hunger(1).alwaysEdible().build();
    public static FoodComponent BOTTLE_OF_WATER = new FoodComponent.Builder().alwaysEdible().build();
}
