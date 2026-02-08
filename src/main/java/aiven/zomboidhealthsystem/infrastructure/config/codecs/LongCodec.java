package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class LongCodec extends Codec {

    public LongCodec(String name, Long value) {
        super(name, value);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            setValue(Long.parseLong(string));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
