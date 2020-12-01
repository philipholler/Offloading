package p7gruppe.p7.offloading.data.local;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import p7gruppe.p7.offloading.converters.FileStringConverter;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class JobFileManager {

    @Autowired
    PathResolver pathResolver;

    private static final String JOB_FILE_NAME = "job_file.zip";
    private static final String RESULT_FILE_NAME = "result_file.zip";
    private static final String INTERMEDIATE_RESULT_FILE_NAME = "result_file_";

    // Saves a job to a directory that is generated from the given username
    // Returns the directory where job files are located
    public String saveJob(String username, byte[] fileBytes) throws IOException {
        System.out.println("PathResolver: " + pathResolver);
        String directoryPath = pathResolver.generateNewJobFolder(username);
        File f = new File(directoryPath + File.separator + JOB_FILE_NAME);
        FileUtils.writeByteArrayToFile(f, fileBytes);
        return directoryPath;
    }

    public String saveResult(String path, byte[] fileBytes, long assignmentId) throws IOException {
        String resultDirectoryPath = pathResolver.generateNewResultFolder(path);
        File f = new File(resultDirectoryPath + File.separator + INTERMEDIATE_RESULT_FILE_NAME + assignmentId + ".zip");
        FileUtils.writeByteArrayToFile(f, fileBytes);
        return resultDirectoryPath;
    }

    public void saveFinalResultFromIntermediate(String jobPath) {
        String resultDirectoryPath = pathResolver.generateNewResultFolder(jobPath);
        File f = new File(resultDirectoryPath + File.separator);
        File firstResult = f.listFiles()[0];

        File finalResultFile = new File(jobPath + File.separator + RESULT_FILE_NAME);
        try {
            FileUtils.copyFile(firstResult, finalResultFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getResultFile(String jobDirectoryPath){
        File file = new File(jobDirectoryPath + File.separator + RESULT_FILE_NAME);
        if (!file.exists()) throw new RuntimeException("Job file does not exist : " + jobDirectoryPath);
        return file;
    }

    public File getJobFile(String jobDirectoryPath){
        File file = new File(jobDirectoryPath + JOB_FILE_NAME);
        if (!file.exists()) throw new RuntimeException("Job file does not exist : " + jobDirectoryPath);
        return file;
    }

    public void deleteDirectory(String jobDirectoryPath) throws IOException {
        File file = new File(jobDirectoryPath);
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                deleteDirectory(subFile.getPath());
            } else {
                subFile.delete();
            }
        }
        file.delete();
    }

    public static byte[] encodeJobBytes(byte[] fileBytes){
        return Base64.encodeBase64(fileBytes);
    }

    public static byte[] decodeJobByte64(byte[] fileBytes){
        return Base64.decodeBase64(fileBytes);
    }

}
