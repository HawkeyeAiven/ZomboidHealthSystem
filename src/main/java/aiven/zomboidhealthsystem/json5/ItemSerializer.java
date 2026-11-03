package aiven.zomboidhealthsystem.json5;

import com.github.HawkeyeAiven.json5.ObjectMapperOptions;
import com.github.HawkeyeAiven.json5.serializer.ObjectSerializer;
import com.github.HawkeyeAiven.json5.serializer.Serializer;

import de.marhali.json5.Json5Element;

import net.minecraft.world.item.Item;

import java.lang.reflect.Type;

public class ItemSerializer extends Serializer {
    private final ObjectSerializer serializer = new ObjectSerializer();

    @Override
    public Json5Element serialize(Object object, ObjectMapperOptions options) {
        return serializer.serialize(Item.getId((Item) object), options);
    }

    @Override
    public boolean supports(Type type) {
        return type.equals(Item.class) || Item.class.isAssignableFrom((Class<?>) type);
    }
}
