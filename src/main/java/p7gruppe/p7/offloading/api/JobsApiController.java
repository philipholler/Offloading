package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-18T11:02:06.033+01:00[Europe/Copenhagen]")

@Controller
@RequestMapping("${openapi.offloading.base-path:}")
public class JobsApiController implements JobsApi {

    @Autowired
    JobScheduler scheduler;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    UserRepository userRepository;

    private final NativeWebRequest request;

    @Autowired
    public JobsApiController(NativeWebRequest request) {
        this.request = request;

    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Long> postJob(UserCredentials userCredentials, @NotNull @Valid Integer requestedWorkers, @Valid MultipartFile file) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            String path = JobFileManager.saveJob(userCredentials.getUsername(), file);
            UserEntity userEntity = userRepository.getUserByUsername(userCredentials.getUsername());
            JobEntity jobEntity = jobRepository.save(new JobEntity(userEntity, path, file.getOriginalFilename(), requestedWorkers));
            return ResponseEntity.ok(jobEntity.getJobId());
        } catch (IOException e) {
            // Fatal server io error // todo add error logging
            return ResponseEntity.status(500).build();
        }
    }

    @Override
    public ResponseEntity<List<Job>> getJobsForUser(UserCredentials userCredentials) {
        return null;
    }
}
