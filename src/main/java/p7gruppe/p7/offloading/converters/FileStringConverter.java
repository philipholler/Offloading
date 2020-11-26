package p7gruppe.p7.offloading.converters;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;


public class FileStringConverter {

    public static byte[] fileToBytes(File file){
        try {
            byte[] bytes = FileUtils.readFileToByteArray(file);
            //FileUtils.writeByteArrayToFile(new File("/home/magnus/IdeaProjects/OffloadingResourceServer/data/jobs/magnus/job_5/job_files_2.zip"), bytes);
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File saveStringToFile(String data, String path){
        return null;
    }


}
