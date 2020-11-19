package p7gruppe.p7.offloading.data.local;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class JobFileManager {

    private static final String JOB_FILE_NAME = "job_file.zip";
    private static final String RESULT_FILE_NAME = "result_file.zip";

    // Saves a job to a directory that is generated from the given username
    // Returns the directory where job files are located
    public static String saveJob(String username, MultipartFile file) throws IOException {
        String directoryPath = PathResolver.generateNewJobFolder(username);
        file.transferTo(new File(directoryPath + File.separator + JOB_FILE_NAME));
        return directoryPath;
    }

    public static String saveResult(String path, MultipartFile file) throws IOException {
        String resultDirectoryPath = PathResolver.generateNewResultFolder(path);
        file.transferTo(new File(resultDirectoryPath + File.separator + RESULT_FILE_NAME));
        return resultDirectoryPath;
    }

    public static File getJobFile(String jobDirectoryPath){
        File file = new File(jobDirectoryPath + JOB_FILE_NAME);
        if (!file.exists()) throw new RuntimeException("Job file does not exist : " + jobDirectoryPath);
        return file;
    }
    public static void deleteDirectory(String jobDirectoryPath) throws IOException {
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

}
