package by.roodxx.runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class LinearExecutionRunner {

    public static final List<String> PERMITTED_EXTENSIONS = Arrays.asList("jpg", "doc", "mp3");
    public static final long MIN_SIZE = 716800;

    public static void main(String[] args) throws IOException {
        String root = "f:\\recover_test\\source";
        String copyRoot = "f:\\recover_test\\work_directory\\";
        Queue<File> directories = new LinkedBlockingQueue<>();
        directories.add(new File(root));
        for (String extension : PERMITTED_EXTENSIONS) {
            new File(copyRoot + extension).mkdir();
        }
        System.out.println("Start time: " + new Date().getTime());
        while (!directories.isEmpty()) {
            File targetDirectory = directories.poll();
            for (File file : targetDirectory.listFiles()) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else if (isFileSuit(file)) {
                    String name = file.getName();
                    String extension = getFileExtension(name);
                    Files.copy(file.toPath(), new File(copyRoot + "\\" + extension + "\\" + name).toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        }
        System.out.println("Finish time: " + new Date().getTime());
    }

    public static boolean isFileSuit(File file) {
        String extension = getFileExtension(file.getName());
        long size = file.length();
        return size >= MIN_SIZE && PERMITTED_EXTENSIONS.contains(extension);
    }

    public static String getFileExtension(String fileName) {
        String extension = null;
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex > -1) {
            extension = fileName.substring(pointIndex + 1);
        }
        return extension;
    }
}
