package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

public abstract class SimpleMoodle extends Moodle {
    protected float amount;

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
        if (amount == 0) {
            return null;
        } else {
            return String.valueOf(getAmount());
        }
    }

    @Override
    public void readNbt(String value) {
        if(value != null) {
            this.setAmount(Float.parseFloat(value));
        } else {
            this.setAmount(defaultValue());
        }
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = Util.clamp(amount, minValue(), maxValue());
    }

    public void addAmount(float amount) {
        setAmount(getAmount() + amount);
    }

    public int getAmplifier() {
        return (int) getAmount();
    }
}
