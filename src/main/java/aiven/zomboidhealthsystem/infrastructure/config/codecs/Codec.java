package aiven.zomboidhealthsystem.infrastructure.config.codecs;

public abstract class Codec {
    private Object value;
    private final String key;

    public Codec(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public abstract boolean setParsedValue(String string);
}
