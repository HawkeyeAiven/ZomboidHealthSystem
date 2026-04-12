package aiven.zomboidhealthsystem.infrastructure.config;

import aiven.zomboidhealthsystem.infrastructure.utility.helpers.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;


public class Json {
    private final LinkedHashMap<String, String> map = new LinkedHashMap<>();

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
        StringBuilder builder = new StringBuilder(getValue(key));
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

        for(String key : map.keySet()) {
            builder.append(key, getValue(key));
        }

        return builder.toString();
    }

    public static String getValue(String json, String key) {
        Objects.requireNonNull(json);
        Objects.requireNonNull(key);

        StringBuilder content = new StringBuilder(json);
        String str = "\n\t\"" + key + "\": ";

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

        return json.substring(startIndex, endIndex).replace("\n\t", "\n");
    }

    public static String[] getKeys(String json) {
        StringBuilder builder = new StringBuilder(Objects.requireNonNull(json));
        int index = 0;
        ArrayList<String> keys = new ArrayList<>();
        String s = "\n\t\"";
        while ((index = builder.indexOf(s, index)) != -1) {
            index+=s.length();
            int endIndex = builder.indexOf("\"", index);
            keys.add(builder.substring(index, endIndex));
        }
        return keys.toArray(new String[0]);
    }
}