package aiven.zomboidhealthsystem.foundation.items;


public class Plaster extends BandageItem {
    @Override
    public BandageItem getDirtyBandageItem() {
        return null;
    }

    @Override
    public float getHealAmount() {
        return 1;
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isStopBleeding() {
        return false;
    }
}
