package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class IntegerCodec extends Codec {

    public IntegerCodec(String name, Integer value) {
        super(name, value);
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            setValue(Integer.parseInt(string));
            return true;
        } catch (NumberFormatException e) {
            setValue(null);
            return false;
        }
    }

    @Override
    public Integer getValue() {
        return (Integer) super.getValue();
    }
}
