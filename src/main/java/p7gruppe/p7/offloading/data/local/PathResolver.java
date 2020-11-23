package p7gruppe.p7.offloading.data.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathResolver {

    static final String DATA_PREFIX = System.getProperty("user.dir") + File.separator + "data" + File.separator;
    private static final String JOBS_PREFIX = DATA_PREFIX + "jobs" + File.separator;

    private static List<String> temporaryReservedPaths = new ArrayList<>();

    public static synchronized String generateNewJobFolder(String userName){
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

    private static synchronized String generateJobName(int i) {
        return "job_" + i;
    }


    public static synchronized String generateNewResultFolder(String jobFolderPath) {
        String resultFolderPath = jobFolderPath + File.separator + "results" + File.separator;
        File resultFolder = new File(resultFolderPath);

        if(!resultFolder.exists()){
            resultFolder.mkdirs();
        }

        return resultFolderPath;
    }
}
