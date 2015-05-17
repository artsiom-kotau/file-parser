package by.roodxx.helper;

import java.util.HashMap;
import java.util.Map;

public class Consts {
    public static final String ROOT = "f:\\recover_test\\source";
    public static final String COPY_ROOT = "f:\\recover_test\\work_directory\\";
    public static final String STANDART_ROOT = "f:\\recover_test\\standart_directory\\";
    public static final Long JPG_SIZE=716800l;
    public static final Long MP3_SIZE=null;
    public static final Long DOC_SIZE=2097152l;

    public static Map<String, Long> extensionSizeMap = new HashMap<>();

    static {
        extensionSizeMap.put("jpg", JPG_SIZE);
        extensionSizeMap.put("doc", DOC_SIZE);
        extensionSizeMap.put("mp3", MP3_SIZE);
    }
}
