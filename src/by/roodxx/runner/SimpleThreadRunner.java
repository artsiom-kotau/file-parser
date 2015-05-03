package by.roodxx.runner;

import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.FileHelper;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static by.roodxx.helper.Consts.*;

public class SimpleThreadRunner {
    public static void main(String[] args) {
        Map<String, Long> extensionSizeMap = createExtensionSizeMap();
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        Controller<File> directoryQueueController = new QueueController<>(directoryQueue);
        directoryQueueController.add(new File(ROOT));
        AtomicInteger threadCounter = new AtomicInteger(1);
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        System.out.println("Start time: " + new Date().getTime());
        while (threadCounter.get() > 0) {
            File targetFile = directoryQueue.poll();
            if (targetFile != null) {
                threadCounter.incrementAndGet();
                Thread thread = new Thread(new FileParserThread(directoryQueueController, fileFilter, targetFile, threadCounter));
                thread.start();
            }
            threadCounter.compareAndSet(1, 0);
        }
        System.out.println("Finish time: " + new Date().getTime());
    }

    public static Map<String, Long> createExtensionSizeMap() {
        Map<String, Long> extensionSize = new HashMap<>();
        extensionSize.put("jpg", JPG_SIZE);
        extensionSize.put("doc", DOC_SIZE);
        extensionSize.put("mp3", MP3_SIZE);
        return extensionSize;
    }

    public static class FileParserThread implements Runnable {
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
            //System.out.println("Start process directory: " + targetDirectory.getName() + "; process amount: " + threadCounter.get());
            for (File file : targetDirectory.listFiles()) {
                if (file.isDirectory()) {
                    directoryController.add(file);
                } else if (fileFilter.accept(file)) {
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
            //System.out.println("Finish process directory: " + targetDirectory.getName() + "; process amount: " + threadCounter.decrementAndGet());
        }
    }

    public interface Controller<E> {
        void add(E element);
    }

    public static class QueueController<E> implements Controller<E> {
        private final Queue<E> queue;

        public QueueController(Queue<E> queue) {
            this.queue = queue;
        }

        @Override
        public void add(E element) {
            queue.add(element);
        }
    }
}

