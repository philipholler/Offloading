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
import p7gruppe.p7.offloading.model.InlineResponse200;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Controller
public class JobsController implements JobsApi {
    static final String jobsPath = System.getProperty("user.dir") + File.separator + "data";

    @Override
    public ResponseEntity<Job> deleteJob(Long jobId, @NotNull @Valid UserCredentials username) {
        return null;
    }

    @Override
    public ResponseEntity<InlineResponse200> getJobFile(@NotNull @Valid UserCredentials username, Long jobId) {
        return null;
    }

    @Override
    public ResponseEntity<InlineResponse200> getJobResult(Long jobId, @NotNull @Valid UserCredentials username) {
        return null;
    }

    @Override
    public ResponseEntity<Integer> postJob(@NotNull @Valid UserCredentials username, @Valid MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<Void> postJobResult(Long jobId, @NotNull @Valid UserCredentials username) {
        return null;
    }

    @Override
    public ResponseEntity<Void> quitJob(Long jobId, @NotNull @Valid Long deviceID, @NotNull @Valid UserCredentials username) {
        return null;
    }
}
