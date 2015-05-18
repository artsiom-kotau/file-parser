package by.roodxx.runner;

import by.roodxx.api.Controller;
import by.roodxx.api.FileParserThread;
import by.roodxx.api.QueueController;
import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.Consts;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static by.roodxx.helper.Consts.COPY_ROOT;
import static by.roodxx.helper.Consts.ROOT;

// first start 13.796516666666667
public class FixedPoolRunner {
    public static void main(String[] args) {
        int nThread = 2;
        Map<String, Long> directoryProcessTime = new ConcurrentHashMap<>();
        Map<String, Long> extensionSizeMap = Consts.extensionSizeMap;
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        Controller<File> directoryQueueController = new QueueController<>(directoryQueue);
        directoryQueueController.add(new File(ROOT));
        AtomicInteger threadCounter = new AtomicInteger(1);
        ExecutorService executorService = Executors.newFixedThreadPool(nThread);
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        long startTime = new Date().getTime();
        while (threadCounter.get() > 0) {
            File targetFile = directoryQueue.poll();
            if (targetFile != null) {
                threadCounter.incrementAndGet();
                executorService.execute(new LogFileParserThread(directoryProcessTime,
                        directoryQueueController, fileFilter, targetFile, threadCounter));
            }
            threadCounter.compareAndSet(1, 0);
        }
        System.out.println("All time: " + ((new Date().getTime() - startTime) / 1000.0) / 60.0);
        executorService.shutdown();
        System.out.println("Shutting down executor service...");
        while (!executorService.isShutdown()) {

        }
        System.out.println("Executor service was shut down");
        for (Map.Entry<String, Long> directoryTime : directoryProcessTime.entrySet()) {
            System.out.println("Directory: " + directoryTime.getKey() + "; time: " + directoryTime.getValue());
        }
        assert FileUtils.sizeOfDirectory(new File(Consts.STANDART_ROOT)) ==
                FileUtils.sizeOfDirectory(new File(Consts.COPY_ROOT));
    }

    public static class LogFileParserThread extends FileParserThread {
        private Map<String, Long> directoryProcessTime;

        public LogFileParserThread(Map<String, Long> directoryProcessTime, Controller<File> directoryController,
                                   FileFilter fileFilter, File targetDirectory, AtomicInteger threadCounter) {
            super(directoryController, fileFilter, targetDirectory, threadCounter);
            this.directoryProcessTime = directoryProcessTime;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            super.run();
            directoryProcessTime.put(getTargetDirectory().getName(), System.currentTimeMillis() - startTime);
        }
    }
}
