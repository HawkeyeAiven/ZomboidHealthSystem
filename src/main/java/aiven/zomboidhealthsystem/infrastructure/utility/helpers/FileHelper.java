package aiven.zomboidhealthsystem.infrastructure.utility.helpers;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.ISO_8859_1;

    private FileHelper() {

    }

    public static File[] getAllFiles(File dir, boolean withDirs){
        ArrayList<File> files = new ArrayList<>();
        if(dir != null && dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    if(withDirs) {
                        files.add(file);
                    }
                    files.addAll(List.of(getAllFiles(file, withDirs)));
                }
            }
        }
        return files.toArray(new File[0]);
    }

    /**
     * @param file Какой файл копировать
     * @param dir В какую папку
     */
    public static void copy(File file, File dir) throws IOException {
        File result = new File(dir, file.getName());
        int c;
        FileReader fileReader = new FileReader(file, DEFAULT_CHARSET);
        FileWriter fileWriter = new FileWriter(result, DEFAULT_CHARSET);
        while ((c = fileReader.read()) != -1) {
            fileWriter.write(c);
        }
        fileReader.close();
        fileWriter.close();
    }

    /**
     * @param dir Папка
     * @param destination Куда
     * @param name Следующее имя папки
     * @return true, если удалось
     */
    public static boolean copyDirectory(File dir, File destination, String name) throws IOException {
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            if(files == null) {
                return false;
            }
            destination = new File(destination.getPath() + "\\" + name);
            destination.mkdirs();
            for(File file : files) {
                if(file.isFile()) {
                    copy(file, destination);
                } else {
                    copyDirectory(file, new File(destination.getPath()));
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param dir Папка
     * @param destination Куда
     * @return true, если удалось
     */
    public static boolean copyDirectory(File dir, File destination) throws IOException{
        return copyDirectory(dir, destination, dir.getName());
    }

    public static void write(File file, String text) throws IOException {
        write(file, DEFAULT_CHARSET, text);
    }

    public static void write(File file, Charset encoding, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(file, encoding);
        fileWriter.write(text);
        fileWriter.close();
    }

    public static String[] readAll(File file, Charset encoding) throws IOException {
        FileReader fileReader = new FileReader(file, encoding);
        ArrayList<StringBuilder> list = new ArrayList<>();
        list.add(new StringBuilder());
        int c;
        while ((c = fileReader.read()) != -1) {
            if(list.get(list.size() - 1).length() == Integer.MAX_VALUE - 2) {
                list.add(new StringBuilder());
            }
            list.get(list.size() - 1).append((char) c);
        }
        fileReader.close();
        String[] strings = new String[list.size()];
        for(int i = 0; i < strings.length; i++) {
            strings[i] = list.get(i).toString();
        }
        return strings;
    }

    public static String[] readAll(File file) throws IOException {
        return readAll(file, DEFAULT_CHARSET);
    }

    public static void write(File file, String[] strings, Charset charset) throws IOException {
        Writer fileWriter = new FileWriter(file, charset);
        for (String string : strings) {
            fileWriter.write(string);
        }
        fileWriter.close();
    }

    public static void write(File file, String[] strings) throws IOException {
        write(file, strings, DEFAULT_CHARSET);
    }

    public static String read(File file) throws IOException {
        return read(file, DEFAULT_CHARSET);
    }

    public static String read(File file, Charset encoding) throws IOException {
        FileReader fileReader = new FileReader(file, encoding);
        StringBuilder text = new StringBuilder();
        int c;
        while ((c = fileReader.read()) != -1) {
            text.append((char) c);
        }
        fileReader.close();
        return text.toString();
    }
}
