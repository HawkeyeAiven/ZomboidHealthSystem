package aiven.zomboidhealthsystem.utility;

import java.util.ArrayList;
import java.util.List;

public final class StringHelper {
    public static final int STRING_MAX_SIZE = Integer.MAX_VALUE - 2;

    private StringHelper() {

    }

    public static String replaceNext(String where, String who, String replace, int fromIndex) {
        if(!where.contains(who)) {
            return where;
        }
        int index = where.indexOf(who, fromIndex);
        return replace(where, who, replace, (index1 -> index1 == index));
    }

    public static String replace(String where, String who, String replace, ReplaceFilter filter) {
        StringBuilder builder = new StringBuilder(where);
        List<Integer> indexes = getIndexes(where, who);
        for(int index : indexes) {
            if(filter.accept(index)) {
                builder.replace(index, index + who.length(), replace);
            }
        }
        return builder.toString();
    }

    public static List<Integer> getIndexes(String where, String who) {
        ArrayList<Integer> list = new ArrayList<>();
        int index = 0;
        while ((index = where.indexOf(who, index)) != -1) {
            list.add(index);
            index+=who.length();
        }
        return list;
    }

    public static byte[] getSymbolTypes(String string) {
        ArrayList<Integer> types = new ArrayList<>();
        for(Character sym : string.toCharArray()) {
            int type = Character.getType(sym);
            if(!types.contains(type)) {
                types.add(type);
            }
        }
        byte[] array = new byte[types.size()];
        for(int i = 0; i < array.length; i++) {
            int d = types.get(i);
            array[i] = (byte) d;
        }
        return array;
    }


    public static int getCount(char symbol, String text){
        int count = 0;
        for(char sym : text.toCharArray()){
            if(symbol == sym){
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @param string Кого
     * @param stringSize Макс размер строки
     * @return Делит строку на строки с макс размером stringSize
     */
    public static String[] split(String string, int stringSize) {
        ArrayList<String> array = new ArrayList<>();
        for(int d = 0; d < string.length(); d += stringSize) {
            array.add(string.substring(d, Math.min(d + stringSize, string.length())));
        }
        return array.toArray(new String[0]);
    }

    /**
     *
     * @param where Где
     * @param string Что
     * @return Сколько раз встречается
     */
    public static int getCount(String where, String string) {
        if(!where.contains(string)) {
            return -1;
        }
        int count = 0;
        char[] chars = where.toCharArray();
        for(int index = 0; index < chars.length; index++) {
            if(chars[index] == string.charAt(0)) {
                if(string.equals(where.substring(index, index + string.length()))) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     *
     * @param string Откуда
     * @param start Начало
     * @param end Конец
     * @param include Включать символы start и end?
     * @param fromIndex Начиная с
     * @return Извлеченную строку начиная с start и заканчивая по end
     */
    public static String substring(String string, String start, String end, boolean include, int fromIndex) {
        int startIndex = string.indexOf(start, fromIndex) + (include ? 0 : start.length());
        int endIndex = string.indexOf(end, startIndex + (include ? start.length() : 0)) + (include ? end.length() : 0);
        if(startIndex != -1 && endIndex != -1) {
            return string.substring(startIndex, endIndex);
        } else {
            return string;
        }
    }

    public static String substring(String string, String start, String end, boolean include) {
        return substring(string, start, end, include, 0);
    }

    /**
     *
     * @param string Что
     * @param start Начальные символы
     * @param end Конечные символы
     * @param include если true, start и end тоже вырезать
     * @return Строку вырезав символы со start по end
     */
    public static String cut(String string, String start, String end, boolean include){
        int startIndex = string.indexOf(start) + start.length();
        int endIndex = string.indexOf(end);
        if(startIndex != -1 && endIndex != -1) {
            if (include) {
                startIndex -= start.length();
                endIndex += end.length();
            }
            StringBuilder result = new StringBuilder(string);
            result.delete(startIndex, endIndex);
            return result.toString();
        } else {
            return string;
        }
    }

    public static String getWord(String text, int index) {
        return text.trim().split(" ")[index];
    }

    public static String createData(int megabytes, int kilobytes, int bytes){
        return "s".repeat(Math.max(0, (bytes) + (kilobytes * 1024) + (megabytes * 1024 * 1024)));
    }

    public static String setMaxLength(String text, int length){
        return setMaxLength(text, length, null);
    }

    public static String setMaxLength(String text, int length, String ellipsisString){
        if(text.length() <= length) {
            return text;
        }
        StringBuilder stringBuilder = new StringBuilder(text);
        stringBuilder.setLength(length - (ellipsisString == null ? 0 : ellipsisString.length()));
        if(ellipsisString != null) {
            stringBuilder.append(ellipsisString);
        }
        return stringBuilder.toString();
    }

    public interface ReplaceFilter {
        boolean accept(int index);
    }
}
