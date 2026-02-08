package aiven.zomboidhealthsystem.infrastructure.config.codecs;


import org.joml.Vector2f;

public class Vector2fCodec extends Codec {
    public Vector2fCodec(String name, Vector2f value) {
        super(name, value);
    }

    @Override
    public Vector2f getValue() {
        return (Vector2f) super.getValue();
    }

    @Override
    public boolean setParsedValue(String string) {
        try {
            StringBuilder stringBuilder = new StringBuilder(string.replace(',', '.').replaceAll(" {2}", " "));
            stringBuilder.deleteCharAt(0);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            if (stringBuilder.charAt(0) == ' ') {
                stringBuilder.deleteCharAt(0);
            }
            String[] numbers = stringBuilder.toString().split(" ");
            setValue(new Vector2f(Float.parseFloat(numbers[0]), Float.parseFloat(numbers[1])));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
