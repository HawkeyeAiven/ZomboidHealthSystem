package aiven.zomboidhealthsystem.foundation.item;

import aiven.zomboidhealthsystem.ModItems;

public class DirtyRag extends BandageItem {
    @Override
    public BandageItem getDirtyBandageItem() {
        return this;
    }

    @Override
    public float getHealAmount() {
        return 1.25f;
    }

    @Override
    public BandageItem getCleanBandageItem() {
        return (BandageItem) ModItems.RAG;
    }
}
