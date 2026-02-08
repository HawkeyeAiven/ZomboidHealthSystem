package aiven.zomboidhealthsystem.foundation.items;

import aiven.zomboidhealthsystem.ModItems;

public class Rag extends BandageItem {
    @Override
    public BandageItem getDirtyBandageItem() {
        return (BandageItem) ModItems.DIRTY_RAG;
    }

    @Override
    public float getHealAmount() {
        return 2.5f;
    }
}
