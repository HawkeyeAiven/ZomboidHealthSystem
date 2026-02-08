package aiven.zomboidhealthsystem.infrastructure.utility.helpers;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FileHelper {

    private FileHelper() {

    }

    public static ArrayList<File> getAllFiles(File folder){
        ArrayList<File> files = new ArrayList<>();
        if(folder != null && folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    files.add(file);
                    files.addAll(getAllFiles(file));
                }
            }
        }
        return files;
    }

    public static boolean copy(File who, File folder) throws IOException {
        if(who.isFile()) {
            File file = new File(folder, who.getName());
            folder.mkdirs();
            write(file, read(who));
            return file.exists() || file.createNewFile();
        } else {
            return false;
        }
    }

    public static boolean copyFolder(File who, File path, String foldername) throws IOException {
        if(who.isDirectory()) {
            File[] files = who.listFiles();
            if(files == null) {
                return false;
            }
            path = new File(path.getPath() + "\\" + foldername);
            path.mkdirs();
            for(File file : files) {
                if(file.isFile()) {
                    copy(file, path);
                } else {
                    copyFolder(file, new File(path.getPath()));
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean copyFolder(File who, File path) throws IOException{
        return copyFolder(who, path, who.getName());
    }

    public static void write(File file, String text) throws IOException {
        write(file, StandardCharsets.ISO_8859_1, text);
    }

    public static void write(File file, Charset encoding, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(file, encoding);
        fileWriter.write(text);
        fileWriter.close();
    }

    public static String read(File file) throws IOException {
        return read(file, StandardCharsets.ISO_8859_1);
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
