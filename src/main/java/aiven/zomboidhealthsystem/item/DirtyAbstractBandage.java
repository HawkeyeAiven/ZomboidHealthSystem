package aiven.zomboidhealthsystem.item;


import aiven.zomboidhealthsystem.ModItems;

public class DirtyAbstractBandage extends AbstractBandageItem {
    @Override
    public AbstractBandageItem getDirtyBandageItem() {
        return this;
    }

    @Override
    public float getHealAmount() {
        return 2;
    }

    @Override
    public boolean isStopBleeding() {
        return true;
    }

    @Override
    public AbstractBandageItem getCleanBandageItem() {
        return (AbstractBandageItem) ModItems.BANDAGE;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
