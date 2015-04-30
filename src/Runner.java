import java.io.File;
import java.nio.file.Files;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;

public class Runner {

    public static void main(String[] args) {
        String rootPath = "/";
        Queue<File> directoryQueue = new SynchronousQueue<File>();
        Controller<File> directoryController = new QueueController<File>(directoryQueue);
        directoryController.add(new File(rootPath));
        //use latch for stop
        while (true) {
            final File newDirectory = directoryQueue.poll();
            if (newDirectory != null) {
                Thread thread = new Thread(FileParserThread(directoryController, newDirectory));
                thread.start();
            }
        }
    }

    public static class FileParserThread implements Callable {
        private final Controller<File> directoryController;
        private final File targetDirectory;

        public FileParserThread(Controller<File> directoryController, File targetDirectory) {
            this.directoryController = directoryController;
            this.targetDirectory = targetDirectory;
        }

        @Override
        public Object call() throws Exception {
            for (File file : targetDirectory.listFiles()) {
                if (file.isDirectory()) {
                    directoryController.add(file);
                } else {
                    String fileName = file.getName();

                    Files.copy(file.toPath(), new File("/target/" + fileName).toPath());
                }
            }
            return null;
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
