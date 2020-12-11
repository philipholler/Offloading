package p7gruppe.p7.offloading.performance.mock;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@SpringBootTest
public class MockResultData {

    private static byte[][] maliciousBytes;
    private static byte[] correctBytes;
    private static int maliciousFileCount = -1;

    public static byte[] getMaliciousBytes(Random random){
        if (maliciousBytes == null) {
            maliciousBytes = getMaliciousResultBytes("malicious");
        }
        return maliciousBytes[random.nextInt(maliciousFileCount)];
    }

    public static byte[] getCorrectResultBytes(){
        if (correctBytes == null) {
            correctBytes = getMockResultBytes("good.zip");
        }
        return correctBytes;
    }

    private static byte[] getMockResultBytes(String fileName){
        File file = new File(MockResultData.class.getResource(File.separator + "mockresults" + File.separator + fileName).getFile());
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not convert mock result file to byte");
        }
    }

    private static byte[][] getMaliciousResultBytes(String folderName){
        File folder = new File(MockResultData.class.getResource(File.separator + "mockresults" + File.separator + folderName).getFile());
        maliciousFileCount = folder.list().length;
        byte[][] fileBytes = new byte[maliciousFileCount][];
        File[] files = folder.listFiles();
        for (int i = 0; i < maliciousFileCount; i++) {
            try {
                File file = files[i];
                fileBytes[i] = FileUtils.readFileToByteArray(file);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not convert mock result file to byte");
            }
        }
        return fileBytes;
    }

    public static boolean equalsAnyMalicious(byte[] result) {
        for (byte[] bytes : maliciousBytes) {
            if (Arrays.equals(result, bytes)) return true;
        }
        return false;
    }
}
