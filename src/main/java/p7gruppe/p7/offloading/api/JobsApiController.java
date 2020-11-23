package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import javax.swing.text.html.parser.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
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
    public ResponseEntity<Void> deleteJob(Long jobId, UserCredentials userCredentials) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<JobEntity> job = jobRepository.findById(jobId);
        if (!job.isPresent())
            return ResponseEntity.badRequest().build();
        try {
            JobFileManager.deleteDirectory(job.get().jobPath);
            jobRepository.deleteById(jobId);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return ResponseEntity.status(200).build();
    }

    @Override
    public ResponseEntity<Resource> getJobFiles(Long jobId, UserCredentials userCredentials) {
        // First check password
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<JobEntity> job = jobRepository.findById(jobId);

        if (!job.isPresent())
            return ResponseEntity.badRequest().build();
        // If some job is available for computation
        File file = JobFileManager.getJobFile(job.get().jobPath);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .headers(new HttpHeaders())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @Override
    public ResponseEntity<Resource> getJobResult(Long jobId, UserCredentials userCredentials) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        return null;
    }

    @Override
    public ResponseEntity<List<Job>> getJobsForUser(UserCredentials userCredentials) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        Iterable jobIterable = jobRepository.getJobsByUsername(userCredentials.getUsername());
        Iterator<JobEntity> iter = jobIterable.iterator();
        List<Job> listOfJobs = new ArrayList<>();

        while (iter.hasNext()) {

            Job job = new Job();
            JobEntity jobEntity = iter.next();

            // casts upload time to datetime
            OffsetDateTime ldt = Instant.ofEpochMilli(jobEntity.uploadTime)
                    .atZone(ZoneId.systemDefault()).toOffsetDateTime();
            job.setStatus(jobEntity.jobStatus.toString());

            // casts jobEntity id to int
            long l = jobEntity.getJobId();
            int i = (int) l;

            job.setTimestamp(ldt);
            job.setJobpath(jobEntity.jobPath);
            job.setId(i);
            job.setEmployer(jobEntity.employer.toString());
            job.setName(jobEntity.getName());
            job.setWorkersRequested(jobEntity.requestedWorkers);

            listOfJobs.add(job);
        }


        return ResponseEntity.ok(listOfJobs);

    }
}
