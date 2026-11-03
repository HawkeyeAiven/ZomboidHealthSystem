package aiven.zomboidhealthsystem.json5;

import com.github.HawkeyeAiven.json5.ObjectMapperOptions;
import com.github.HawkeyeAiven.json5.deserializer.Deserializer;
import com.github.HawkeyeAiven.json5.deserializer.ObjectDeserializer;

import de.marhali.json5.Json5Element;

import net.minecraft.world.item.Item;

import java.lang.reflect.Type;

public class ItemDeserializer extends Deserializer {
    private final ObjectDeserializer deserializer = new ObjectDeserializer();

    @Override
    public <T> T deserialize(Json5Element element, Type type, ObjectMapperOptions options) {
        int id = deserializer.deserialize(element, Integer.class, options);
        return (T) Item.byId(id);
    }

    @Override
    public boolean supports(Type type) {
        return type.equals(Item.class) || Item.class.isAssignableFrom((Class<?>) type);
    }
}
