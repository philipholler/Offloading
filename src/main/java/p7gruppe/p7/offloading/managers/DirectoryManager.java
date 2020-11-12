package p7gruppe.p7.offloading.managers;

import p7gruppe.p7.offloading.exceptions.FileExistsException;

import java.io.File;
import java.io.IOException;

public class DirectoryManager {
    static final String rootPath = "C:/Programming/Offloading/data/jobs";

    public static File generateDirectoryTree(String jobID) throws IOException, FileExistsException {
        File rootDirectory = new File(rootPath);
        if (!rootDirectory.exists())
            rootDirectory.mkdir();


        File jobDircetory = new File(rootPath + File.separator + jobID.substring(0, jobID.lastIndexOf('.')));
        if (!jobDircetory.exists())
            jobDircetory.mkdir();

        File recivedFile = new File(jobDircetory.getAbsolutePath() + File.separator + jobID);
        if (!recivedFile.exists())
            recivedFile.createNewFile();
        else
            throw new FileExistsException("File already exists");

        return recivedFile;

    }
}
