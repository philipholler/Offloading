package p7gruppe.p7.offloading.controller;
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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Paths;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
@Controller
public class JobsController implements JobsApi {

    static final String jobsPath = System.getProperty("user.dir") + File.separator + "data";
    static final String rootPath ="C:/Programming/Offloading/data";


    @Override
    public ResponseEntity<Resource> jobsGet(@NotNull @Valid Job name) {
        File file = new File(rootPath + name.getName());
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }


    @PostMapping(value = "/addFile/{username}")
    public ResponseEntity<?> addFile(@RequestParam("file") MultipartFile file, String deviceName, @PathVariable("username") String username) {


        try {
            File directory = DirectoryManager.generateDirectoryTree(file.getOriginalFilename());
            file.transferTo(directory);
            DataManager.insertJobInDB(file.getOriginalFilename(),directory.getAbsolutePath());
        } catch (IllegalStateException | IOException  e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        } catch (FileExistsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File Already Exists");
        }
        return ResponseEntity.ok("File added");
    }

}