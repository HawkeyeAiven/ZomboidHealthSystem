package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class ShortCodec extends Codec {
    public ShortCodec(String name, Short value) {
        super(name, value);
    }

    @Override
    public Short getValue() {
        return (Short) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            setValue(Short.parseShort(string));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
