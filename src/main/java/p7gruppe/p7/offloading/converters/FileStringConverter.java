package p7gruppe.p7.offloading.converters;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Arrays;

public class FileStringConverter {

    public static byte[] fileToBytes(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        return bytes;
    }

    public static File saveStringToFile(String data, String path){
        return null;
    }


}
