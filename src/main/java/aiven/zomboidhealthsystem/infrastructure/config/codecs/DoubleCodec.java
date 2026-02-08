package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class DoubleCodec extends Codec {
    public DoubleCodec(String name, Double value) {
        super(name, value);
    }

    @Override
    public Double getValue() {
        return (Double) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            setValue(Double.parseDouble(string));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
