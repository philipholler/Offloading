package p7gruppe.p7.offloading.performance.mock;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class MockJobData {

    private static byte[] maliciousBytes;
    private static byte[] correctBytes;

    public static byte[] getMaliciousBytes(){
        if (maliciousBytes == null) {
            maliciousBytes = getMockResultBytes("bad.zip");
        }
        return maliciousBytes;
    }

    public static byte[] getCorrectResultBytes(){
        if (correctBytes == null) {
            correctBytes = getMockResultBytes("good.zip");
        }
        return correctBytes;
    }

    private static byte[] getMockResultBytes(String fileName){
        File file = new File(MockJobData.class.getResource("mockresults" + File.separator + fileName).getFile());
        try {
            return maliciousBytes = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not convert mock result file to byte");
        }
    }





}
