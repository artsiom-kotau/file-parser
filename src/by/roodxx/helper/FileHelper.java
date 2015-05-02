package by.roodxx.helper;

public class FileHelper {

    public static String getFileExtension(String fileName) {
        String extension = null;
        int pointIndex = fileName.lastIndexOf(".");
        if (pointIndex > -1) {
            extension = fileName.substring(pointIndex + 1);
        }
        return extension;
    }
}
