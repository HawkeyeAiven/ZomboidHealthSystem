package aiven.zomboidhealthsystem.item;


import aiven.zomboidhealthsystem.ModItems;

public class Rag extends AbstractBandageItem {
    @Override
    public AbstractBandageItem getDirtyBandageItem() {
        return (AbstractBandageItem) ModItems.DIRTY_RAG;
    }

    @Override
    public float getHealAmount() {
        return 2.5f;
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
