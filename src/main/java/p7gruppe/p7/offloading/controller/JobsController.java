package p7gruppe.p7.offloading.controller;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.database.DataManager;
import p7gruppe.p7.offloading.model.Job;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.file.Paths;
import java.sql.SQLException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Controller
public class JobsController implements JobsApi {

    static final String jobsPath = System.getProperty("user.dir") + File.separator + "data";

    @Override
    public ResponseEntity<Resource> jobsGet(@NotNull @Valid Job name) {
        File file = new File(jobsPath + name.getName());
        try {
            System.out.println(DataManager.getFirstDbname());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
