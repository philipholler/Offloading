package p7gruppe.p7.offloading.api;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.database.DataManager;
import p7gruppe.p7.offloading.exceptions.FileExistsException;
import p7gruppe.p7.offloading.managers.DirectoryManager;

import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class JobsController implements JobsApi {
    static final String jobsPath = System.getProperty("user.dir") + File.separator + "data";

    //deletes a job from db and directory
    @Override
    public ResponseEntity<Void> deleteJob(Long jobId, @NotNull @Valid UserCredentials username) {
        try {
            File fileToDelete = new File(DataManager.getJobPath(jobId));
            fileToDelete.delete();
            DataManager.removeJob(jobId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //sends a job to a client asking with username and jobid
    @Override
    public ResponseEntity<Resource> getJobFile(@NotNull @Valid UserCredentials username, Long jobId) {
        try {
            File file = new File(DataManager.getJobPath(jobId));
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            DataManager.updateJobStatus("calculating", jobId);
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //sends a job result to a client asking with username and jobid
    @Override
    public ResponseEntity<Resource> getJobResult(Long jobId, @NotNull @Valid UserCredentials username) {
        try {
            File file = new File(DataManager.getJobResult(jobId, username.getUsername()));
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    //the server recives a job and sends it to database
    @Override
    public ResponseEntity<Integer> postJob(@NotNull @Valid UserCredentials username, @Valid MultipartFile file) {
        try {
            File directory = DirectoryManager.generateDirectoryTree(file.getOriginalFilename());
            file.transferTo(directory);
            DataManager.insertJobInDB(file.getOriginalFilename(), directory.getAbsolutePath(), username.getUsername());
        } catch (IllegalStateException | IOException e) {
            return ResponseEntity.badRequest().header("error").build();
        } catch (FileExistsException e) {
            return ResponseEntity.badRequest().header("File Already Exists").build();
        }
        return ResponseEntity.ok().header("File added").build();
    }

    @Override
    public ResponseEntity<Void> postJobResult(Long jobId, @NotNull @Valid UserCredentials username, @Valid MultipartFile file) {
        try {
            file.transferTo(new File(DataManager.getJobResult(jobId, username.getUsername())));
            DataManager.updateJobStatus("done", jobId);
        } catch (IllegalStateException | IOException e) {
            return ResponseEntity.badRequest().header("error").build();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return ResponseEntity.ok().header("Result added").build();
    }

    @Override
    public ResponseEntity<Void> quitJob(Long jobId, @NotNull @Valid Long deviceID, @NotNull @Valid UserCredentials username) {
        DataManager.updateJobStatus("waiting", jobId);
        return null;
    }
}
