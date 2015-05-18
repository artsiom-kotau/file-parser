package by.roodxx.api;

import by.roodxx.helper.FileHelper;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;

import static by.roodxx.helper.Consts.COPY_ROOT;

public class FileParserThread implements Runnable {
    private final Controller<File> directoryController;
    private final File targetDirectory;
    private final AtomicInteger threadCounter;
    private final FileFilter fileFilter;

    public FileParserThread(Controller<File> directoryController, FileFilter fileFilter, File targetDirectory, AtomicInteger threadCounter) {
        this.directoryController = directoryController;
        this.targetDirectory = targetDirectory;
        this.threadCounter = threadCounter;
        this.fileFilter = fileFilter;
    }

    @Override
    public void run() {
        for (File file : targetDirectory.listFiles()) {
            if (file.isDirectory()) {
                directoryController.add(file);
            } else if (fileFilter.accept(file)){
                try {
                    String name = file.getName();
                    String extension = FileHelper.getFileExtension(name);
                    Files.copy(file.toPath(), new File(COPY_ROOT + "\\" + extension + "\\" + name).toPath(), StandardCopyOption.COPY_ATTRIBUTES);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

            }
        }
        threadCounter.decrementAndGet();
    }

    public File getTargetDirectory() {
        return targetDirectory;
    }
}
