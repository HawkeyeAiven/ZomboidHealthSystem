package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class BooleanCodec extends Codec {

    public BooleanCodec(String name, Boolean value) {
        super(name, value);
    }

    @Override
    public Boolean getValue() {
        return (Boolean) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        setValue(Boolean.parseBoolean(string));
        return true;
    }
}
