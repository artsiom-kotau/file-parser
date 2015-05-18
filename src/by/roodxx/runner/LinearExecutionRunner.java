package by.roodxx.runner;

import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.Consts;
import by.roodxx.helper.FileHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static by.roodxx.helper.Consts.*;
// first start 11.8021
//second start 11.01595
public class LinearExecutionRunner {

    public static void main(String[] args) throws IOException {
        Map<String,Long> directoryProcessTime = new HashMap<>();
        Map<String, Long> extensionSizeMap = Consts.extensionSizeMap;
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directories = new LinkedBlockingQueue<>();
        directories.add(new File(ROOT));
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        long startTime = System.currentTimeMillis();
        while (!directories.isEmpty()) {
            File targetDirectory = directories.poll();
            long directoryStartTime = System.currentTimeMillis();
            for (File file : targetDirectory.listFiles()) {
                if (file.isDirectory()) {
                    directories.add(file);
                } else if (fileFilter.accept(file)) {
                    String name = file.getName();
                    String extension = FileHelper.getFileExtension(name);
                    Files.copy(file.toPath(), new File(COPY_ROOT + "\\" + extension + "\\" + name).toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                }
            }
            directoryProcessTime.put(targetDirectory.getName(),System.currentTimeMillis()-directoryStartTime);
        }
        System.out.println("All time: " + ((System.currentTimeMillis()-startTime)/1000.0)/60.0);
        for(Map.Entry<String,Long> directoryTime : directoryProcessTime.entrySet()) {
            System.out.println("Directory: "+directoryTime.getKey()+"; time: "+directoryTime.getValue());
        }
        assert FileUtils.sizeOfDirectory(new File(Consts.STANDART_ROOT)) ==
                FileUtils.sizeOfDirectory(new File(Consts.COPY_ROOT));
    }
}
