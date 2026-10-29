package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

public abstract class SimpleMoodle extends Moodle {
    protected float value;

    public SimpleMoodle(Health health) {
        super(health);
    }

    protected float minValue() {
        return 0;
    }

    protected float maxValue() {
        return Float.MAX_VALUE;
    }

    protected float defaultValue() {
        return 0;
    }

    @Override
    public String getNbt(){
        if (value == 0) {
            return null;
        } else {
            return String.valueOf(getValue());
        }
    }

    @Override
    public void readNbt(String value) {
        if(value != null) {
            this.setValue(Float.parseFloat(value));
        } else {
            this.setValue(defaultValue());
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Util.clamp(value, minValue(), maxValue());
    }

    public void addAmount(float amount) {
        setValue(getValue() + amount);
    }

    public int getAmplifier() {
        return (int) getValue();
    }
}
