package aiven.zomboidhealthsystem.infrastructure.config;

import aiven.zomboidhealthsystem.infrastructure.config.codecs.Codec;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JsonConfig {
    private final ArrayList<Codec> codecs = new ArrayList<>();

    public <T extends Codec> Codec add(T codec) {
        codecs.add(codec);
        return codec;
    }

    public void save(File file) throws IOException {
        Json json = new Json();
        for(Codec codec : codecs) {
            json.add(codec.getKey(), codec.getValue().toString());
        }
        json.save(file);
    }

    public void loadValues(File file) throws IOException {
        Json json = new Json();
        json.load(file);

        for(Codec codec : codecs) {
            String stringValue = json.getValue(codec.getKey());
            if(!stringValue.equals("null")) {
                codec.setParsedValue(stringValue);
            } else {
                codec.setValue(null);
            }
        }
    }

    @Override
    public String toString() {
        Json json = new Json();
        for(Codec codec : codecs) {
            json.add(codec.getKey(), codec.getValue().toString());
        }
        return json.toString();
    }
}
