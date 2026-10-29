package aiven.zomboidhealthsystem.item;

import net.minecraft.world.item.Item;

public class AlcoholWipe extends Item {
    public static final int DISINFECT_COOLDOWN_TIME = 3 * 20;

    private static final Item.Properties itemProperties;
    static {
        itemProperties = new Properties();
        itemProperties.stacksTo(1);
        itemProperties.durability(8);
        itemProperties.defaultDurability(8);
    }

    public AlcoholWipe() {
        super(itemProperties);
    }
}
