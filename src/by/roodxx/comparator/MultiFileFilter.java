package by.roodxx.comparator;

import by.roodxx.helper.FileHelper;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;

public class MultiFileFilter implements FileFilter {

    private Map<String,Long> sizeByExtension;

    public MultiFileFilter(Map<String, Long> sizeByExtension) {
        if (sizeByExtension == null) {
            throw new IllegalArgumentException("Extension map is null");
        }
        this.sizeByExtension = sizeByExtension;
    }

    @Override
    public boolean accept(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is null");
        }
        boolean result = false;
        String extension = FileHelper.getFileExtension(file.getName());
        if (sizeByExtension.containsKey(extension))  {
            Long minimumSize = sizeByExtension.get(extension);
            result = minimumSize == null || file.length() >= minimumSize;
        }
        return result;
    }
}
