package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class FloatCodec extends Codec {
    public FloatCodec(String name, Float value) {
        super(name, value);
    }

    @Override
    public Float getValue() {
        return (Float) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            setValue(Float.parseFloat(string));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
