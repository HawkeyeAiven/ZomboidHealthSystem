package aiven.zomboidhealthsystem.health.moodles;

import aiven.zomboidhealthsystem.health.Health;
import aiven.zomboidhealthsystem.utility.Util;

import java.io.*;

public abstract class SimpleMoodle extends Moodle {
    protected float value = defaultValue();

    public SimpleMoodle(Health health) {
        super(health);
    }

    public float minValue() {
        return 0;
    }

    public float maxValue() {
        return Float.MAX_VALUE;
    }

    protected float defaultValue() {
        return 0;
    }

    @Override
    public byte[] getNbt(){
        if (this.value == defaultValue()) {
            return null;
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArrayOutputStream);
                out.writeFloat(this.value);
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void readNbt(byte[] value) {
        try {
            if (value != null) {
                DataInputStream in = new DataInputStream(new ByteArrayInputStream(value));
                this.setValue(in.readFloat());
            } else {
                this.setValue(defaultValue());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Util.clamp(value, minValue(), maxValue());
    }

    public void addValue(float value) {
        setValue(getValue() + value);
    }

    public int getAmplifier() {
        return (int) getValue();
    }
}
