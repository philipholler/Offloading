package p7gruppe.p7.offloading.fileutils;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;
import p7gruppe.p7.offloading.data.local.PathResolver;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class FileUtilsTest {

    String pathToStartingData = System.getProperty("user.dir") + File.separator + "test_data" + File.separator + "resultComparisonStartingData";
    String pathToWorkingDir = System.getProperty("user.dir") + File.separator + "test_data" + File.separator + "result_test";

    @Autowired
    PathResolver pathResolver;

    @AfterEach
    public void cleanup(){
        File resultDirFile = new File(pathToWorkingDir);
        try {
            FileUtils.deleteDirectory(resultDirFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void checkZipFilesEquality1(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identical");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertEquals(true, FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality2(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identical");
        File folderToZip2 = new File(pathToStartingData + File.separator + "notidentical");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertThrows(ZipFilesNotEqualException.class, () -> FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality3(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identicalinner");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertEquals(true, FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }

    @Test
    public void checkZipFilesEquality4(){
        // Zip two identical files
        String resultDir = pathResolver.generateNewResultFolder(pathToWorkingDir);

        // Folder to zip (it is the same for both, then the result should be, that they are equal
        File folderToZip = new File(pathToStartingData + File.separator + "identicalinner");
        File folderToZip2 = new File(pathToStartingData + File.separator + "notidenticalinner");

        File zipFile1 = new File(resultDir + File.separator + "zipfile1.zip");
        File zipFile2 = new File(resultDir + File.separator + "zipfile2.zip");

        // Zip both files
        FileUtilsKt.zipDir(folderToZip.getAbsolutePath(), zipFile1.getAbsolutePath());
        FileUtilsKt.zipDir(folderToZip2.getAbsolutePath(), zipFile2.getAbsolutePath());

        assertThrows(ZipFilesNotEqualException.class, () -> FileUtilsKt.checkZipFilesEquality(zipFile1, zipFile2));
    }
}
