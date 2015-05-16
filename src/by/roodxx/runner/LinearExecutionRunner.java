package by.roodxx.runner;

import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.FileHelper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static by.roodxx.helper.Consts.*;
// first start 11.8021
public class LinearExecutionRunner {

    public static void main(String[] args) throws IOException {
        Map<String, Long> extensionSizeMap = createExtensionSizeMap();
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directories = new LinkedBlockingQueue<>();
        directories.add(new File(ROOT));
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        long startTime = new Date().getTime();
        while (!directories.isEmpty()) {
            File targetDirectory = directories.poll();
            for (File file : targetDirectory.listFiles()) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else if (fileFilter.accept(file)) {
                    String name = file.getName();
                    String extension = FileHelper.getFileExtension(name);
                    Files.copy(file.toPath(), new File(COPY_ROOT + "\\" + extension + "\\" + name).toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
        }
        System.out.println("All time: " + ((new Date().getTime()-startTime)/1000.0)/60.0);
    }

    public static Map<String, Long> createExtensionSizeMap() {
        Map<String, Long> extensionSize = new HashMap<>();
        extensionSize.put("jpg", JPG_SIZE);
        extensionSize.put("doc", DOC_SIZE);
        extensionSize.put("mp3", MP3_SIZE);
        return extensionSize;
    }
}
