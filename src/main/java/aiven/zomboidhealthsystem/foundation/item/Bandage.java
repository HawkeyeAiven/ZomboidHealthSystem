package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.ModItems;

public class Bandage extends BandageItem{
    @Override
    public BandageItem getDirtyBandageItem() {
        return (BandageItem) ModItems.DIRTY_BANDAGE;
    }

    @Override
    public float getHealAmount() {
        return 4;
    }
}
