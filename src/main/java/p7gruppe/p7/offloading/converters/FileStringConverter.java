package p7gruppe.p7.offloading.converters;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;


public class FileStringConverter {

    public static byte[] fileToBytes(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }

    public static File saveStringToFile(String data, String path){
        return null;
    }


}
