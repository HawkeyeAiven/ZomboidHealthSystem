package aiven.zomboidhealthsystem.infrastructure.config;

public class JsonBuilder {
    private int objects = 0;
    private final StringBuilder result = new StringBuilder();

    public JsonBuilder(){

    }

    public JsonBuilder append(String key, String value){
        value = value.replaceAll("\n", "\n\t");
        if(objects == 0){
            result.append("{\n");
        } else {
            result.append(",\n");
        }
        result
                .append("\t")
                .append("\"")
                .append(key)
                .append("\": ")
                .append(value);

        objects++;
        return this;
    }

    public JsonBuilder add(String string){
        result.append(string);
        return this;
    }

    public int size() {
        return objects;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public String toString() {
        if(result.isEmpty()){
            return result + "{\n}";
        }
        return result + "\n" + "}";
    }
}
