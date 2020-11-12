package p7gruppe.p7.offloading.managers;

import p7gruppe.p7.offloading.exceptions.FileExistsException;

import java.io.File;
import java.io.IOException;

public class DirectoryManager {
    static final String jobsPath = System.getProperty("user.dir") + File.separator + "data" + File.separator + "jobs";

    public static File generateDirectoryTree(String jobID) throws IOException, FileExistsException {
        File rootDirectory = new File(jobsPath);
        if (!rootDirectory.exists())
            rootDirectory.mkdir();


        File jobDirectory = new File(jobsPath + File.separator + jobID.substring(0, jobID.lastIndexOf('.')));
        if (!jobDirectory.exists())
            jobDirectory.mkdir();

        File receivedFile = new File(jobDirectory.getAbsolutePath() + File.separator + jobID);
        if (!receivedFile.exists())
            receivedFile.createNewFile();
        else
            throw new FileExistsException("File already exists");

        return receivedFile;

    }
}
