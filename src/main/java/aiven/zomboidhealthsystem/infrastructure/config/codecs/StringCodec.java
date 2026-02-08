package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public class StringCodec extends Codec {
    public StringCodec(String name, String value) {
        super(name, value);
    }

    @Override
    public String getValue() {
        return (String) super.getValue();

    }

    @Override
    public boolean setParsedValue(String string) {
        setValue(string);
        return true;
    }
}
