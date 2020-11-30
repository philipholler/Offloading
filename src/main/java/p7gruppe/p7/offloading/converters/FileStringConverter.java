package p7gruppe.p7.offloading.converters;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;

public class FileStringConverter {

    public static byte[] fileToBytes(File file) throws IOException {
        byte[] bytes = FileUtils.readFileToByteArray(file);
        System.out.println(Arrays.toString(bytes));
        return bytes;
    }

    public static File saveStringToFile(String data, String path){
        return null;
    }


}
