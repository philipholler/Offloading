package p7gruppe.p7.offloading.data.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathResolver {

    private final String DATA_PREFIX;
    private final String JOBS_PREFIX;

    private static List<String> temporaryReservedPaths = new ArrayList<>();

    public PathResolver(String dataFolderName) {
        DATA_PREFIX = System.getProperty("user.dir") + File.separator + dataFolderName + File.separator;
        JOBS_PREFIX = DATA_PREFIX + "jobs" + File.separator;
    }

    public synchronized String generateNewJobFolder(String userName){
        String pathPrefix = JOBS_PREFIX + userName + File.separator;

        File rootDirectory = new File(pathPrefix);
        if (!rootDirectory.exists())
            rootDirectory.mkdirs(); // todo Throw exception if false

        int i = 0;
        File file;
        String jobName;
        do {
            jobName = generateJobName(i++);
            file = new File(pathPrefix + jobName);
        }while (file.exists());

        file.mkdir(); // todo Throw exception if false
        return pathPrefix + jobName + File.separator;
    }

    private synchronized String generateJobName(int i) {
        return "job_" + i;
    }

    public synchronized String generateNewResultFolder(String jobFolderPath) {
        String resultFolderPath = jobFolderPath + File.separator + "results" + File.separator;
        File resultFolder = new File(resultFolderPath);

        if(!resultFolder.exists()){
            resultFolder.mkdirs();
        }

        return resultFolderPath;
    }

    @Override
    public String toString() {
        return "PathResolver{" +
                "DATA_PREFIX='" + DATA_PREFIX + '\'' +
                ", JOBS_PREFIX='" + JOBS_PREFIX + '\'' +
                '}';
    }
}
