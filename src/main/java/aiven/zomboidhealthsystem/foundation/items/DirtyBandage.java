package aiven.zomboidhealthsystem.foundation.items;


import aiven.zomboidhealthsystem.ModItems;

public class DirtyBandage extends BandageItem{
    @Override
    public BandageItem getDirtyBandageItem() {
        return this;
    }

    @Override
    public float getHealAmount() {
        return 2;
    }

    @Override
    public BandageItem getCleanBandageItem() {
        return (BandageItem) ModItems.BANDAGE;
    }
}
