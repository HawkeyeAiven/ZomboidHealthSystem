package aiven.zomboidhealthsystem.item;

import net.minecraft.world.item.Item;

public class EmptyCupItem extends Item {

    private static final Item.Properties itemProperties;
    static {
        itemProperties = new Properties();
        itemProperties.stacksTo(1);
    }


    public EmptyCupItem() {
        super(itemProperties);
    }
}
