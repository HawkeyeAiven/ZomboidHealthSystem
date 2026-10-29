package aiven.zomboidhealthsystem.item;

import aiven.zomboidhealthsystem.ModItems;

public class DirtyRag extends AbstractBandageItem {
    @Override
    public AbstractBandageItem getDirtyBandageItem() {
        return this;
    }

    @Override
    public float getHealAmount() {
        return 1.25f;
    }

    @Override
    public boolean isStopBleeding() {
        return true;
    }

    @Override
    public AbstractBandageItem getCleanBandageItem() {
        return (AbstractBandageItem) ModItems.RAG;
    }

    @Override
    public boolean isDirty() {
        return true;
    }
}
