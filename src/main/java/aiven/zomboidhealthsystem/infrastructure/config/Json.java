package aiven.zomboidhealthsystem.infrastructure.config;

import aiven.zomboidhealthsystem.infrastructure.utility.helpers.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Json {
    private final ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    public void add(String key, String value) {
        map.put(key, value);
    }

    public void addString(String key, String value) {
        add(key, "\"" + value + "\"");
    }

    public void remove(String key) {
        map.remove(key);
    }

    public String getValue(String key) {
        return map.get(key);
    }

    public String getValueOfString(String key) {
        StringBuilder builder = new StringBuilder(map.get(key));
        if(builder.charAt(0) == '\"') {
            builder.deleteCharAt(0);
        }
        if(builder.charAt(builder.length() - 1) == '\"') {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public boolean contains(String key) {
        return map.containsKey(key);
    }

    public void clear() {
        map.clear();
    }

    public void load(File file) throws IOException {
        load(FileHelper.read(file));
    }

    public void load(String config) {
        String[] keys = Json.getKeys(config);

        for(String key : keys) {
            this.add(key, Json.getValue(config, key));
        }
    }

    public void save(File file) throws IOException {
        FileHelper.write(file, this.toString());
    }

    public String[] getKeys() {
        return map.keySet().toArray(new String[0]);
    }

    @Override
    public String toString() {
        JsonBuilder builder = new JsonBuilder();

        for(String key : getKeys()) {
            builder.append(key, getValue(key));
        }

        return builder.toString();
    }

    public static String getValue(String config, String name) {
        StringBuilder content = new StringBuilder(Objects.requireNonNull(config));
        String str = "\n\t\"" + name + "\": ";

        int startIndex = content.indexOf(str);
        if(startIndex == -1) {
            return null;
        }
        startIndex += str.length();

        int endIndex = content.indexOf(",\n\t\"", startIndex);
        if(endIndex == -1) {
            endIndex = content.indexOf("\n}", startIndex);
            if(endIndex == -1) {
                return null;
            }
        }

        int index = startIndex;
        StringBuilder value = new StringBuilder();

        while (index != endIndex) {
            value.append(content.charAt(index));
            index++;
        }

        return value.toString().trim().replace("\n\t", "\n"); // replace нужен!!
    }

    public static String[] getKeys(String config) {
        ArrayList<String> names = new ArrayList<>();
        StringBuilder content = new StringBuilder(config);

        int lastIndex = 0;
        int index;

        while ((index = content.indexOf("\n\t\"", lastIndex)) != -1) {
            index += 3;
            lastIndex = index + 1;

            StringBuilder name = new StringBuilder();
            char sym;
            while ((sym = content.charAt(index)) != '"') {
                name.append(sym);
                index++;
            }

            names.add(name.toString());
        }

        return names.toArray(new String[names.size()]);
    }
}