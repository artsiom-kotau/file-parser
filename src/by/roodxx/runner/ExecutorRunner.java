package by.roodxx.runner;

import by.roodxx.api.Controller;
import by.roodxx.api.FileParserThread;
import by.roodxx.api.QueueController;
import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.Consts;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static by.roodxx.helper.Consts.*;
// second start 15.271283333333335
//third start 13.478433333333333

public class ExecutorRunner {

    public static void main(String[] args) {
        Map<String,Long> extensionSizeMap = Consts.extensionSizeMap;
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        Controller<File> directoryQueueController = new QueueController<>(directoryQueue);
        directoryQueueController.add(new File(ROOT));
        AtomicInteger threadCounter = new AtomicInteger(1);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        long startTime = new Date().getTime();
        while (threadCounter.get() > 0) {
            File targetFile = directoryQueue.poll();
            if (targetFile != null) {
                threadCounter.incrementAndGet();
                executorService.execute(new FileParserThread(directoryQueueController,fileFilter, targetFile, threadCounter));
            }
            threadCounter.compareAndSet(1, 0);
        }
        System.out.println("All time: " + ((new Date().getTime()-startTime)/1000.0)/60.0);
        executorService.shutdown();
        System.out.println("Shutting down executor service...");
        while (!executorService.isShutdown()) {

        }
        System.out.println("Executor service was shut down");
    }
}
