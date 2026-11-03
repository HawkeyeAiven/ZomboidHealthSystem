package aiven.zomboidhealthsystem.json5;

import com.github.HawkeyeAiven.json5.Deserializers;
import com.github.HawkeyeAiven.json5.Serializers;

public final class Json5 {

    private Json5() {

    }


    public static void initialize() {
        Serializers.register(new ItemSerializer());
        Deserializers.register(new ItemDeserializer());
    }
}
