package by.roodxx.runner;

import by.roodxx.api.Controller;
import by.roodxx.api.FileParserThread;
import by.roodxx.api.QueueController;
import by.roodxx.comparator.MultiFileFilter;
import by.roodxx.helper.Consts;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static by.roodxx.helper.Consts.*;
//third start 14.206933333333334
//first start 14.1348

public class SimpleThreadRunner {
    public static void main(String[] args) {
        Map<String, Long> extensionSizeMap = Consts.extensionSizeMap;
        FileFilter fileFilter = new MultiFileFilter(extensionSizeMap);
        Queue<File> directoryQueue = new ConcurrentLinkedQueue<>();
        Controller<File> directoryQueueController = new QueueController<>(directoryQueue);
        directoryQueueController.add(new File(ROOT));
        AtomicInteger threadCounter = new AtomicInteger(1);
        for (String extension : extensionSizeMap.keySet()) {
            new File(COPY_ROOT + extension).mkdir();
        }
        long startTime = new Date().getTime();
        while (threadCounter.get() > 0) {
            File targetFile = directoryQueue.poll();
            if (targetFile != null) {
                threadCounter.incrementAndGet();
                Thread thread = new Thread(new FileParserThread(directoryQueueController, fileFilter, targetFile, threadCounter));
                thread.start();
            }
            threadCounter.compareAndSet(1, 0);
        }
        System.out.println("All time: " + ((new Date().getTime() - startTime) / 1000.0) / 60.0);

    }

}

