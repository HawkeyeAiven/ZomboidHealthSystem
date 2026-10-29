package aiven.zomboidhealthsystem.item;


import aiven.zomboidhealthsystem.ModItems;

public class Bandage extends AbstractBandageItem {
    @Override
    public AbstractBandageItem getDirtyBandageItem() {
        return (AbstractBandageItem) ModItems.DIRTY_BANDAGE;
    }

    @Override
    public float getHealAmount() {
        return 4;
    }

    @Override
    public boolean isStopBleeding() {
        return true;
    }

    @Override
    public AbstractBandageItem getCleanBandageItem() {
        return this;
    }

    @Override
    public boolean isDirty() {
        return false;
    }
}
