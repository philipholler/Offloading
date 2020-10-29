package p7gruppe.p7.offloading.controller;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import p7gruppe.p7.offloading.api.JobsApi;
import p7gruppe.p7.offloading.model.Job;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Controller
public class JobsController implements JobsApi {

    @Override
    public ResponseEntity<Resource> jobsGet(@NotNull @Valid Job name) {
        File file = new File("/home/magnus/IdeaProjects/Offloading/data/" + name.getName() + ".txt");
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

    @Override
    public ResponseEntity<Void> jobsPost(@NotNull @Valid Job query) {
        System.out.println("Got post request");
        System.out.println("Received post - id: " + query.getId() + ", name: " + query.getName());
        return null;
    }
}
