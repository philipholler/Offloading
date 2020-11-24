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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.InlineObject;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
    public ResponseEntity<Void> postJob(UserCredentials userCredentials, @NotNull @Valid Integer requestedWorkers, @Valid MultipartFile jobfile) {
        System.out.println("Posting job....");
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            System.out.println("Trying to save job...");
            String path = JobFileManager.saveJob(userCredentials.getUsername(), jobfile);
            System.out.println("Job saved...");
            UserEntity userEntity = userRepository.getUserByUsername(userCredentials.getUsername());
            System.out.println("Username pulled");
            JobEntity jobEntity = jobRepository.save(new JobEntity(userEntity, path, jobfile.getOriginalFilename(), requestedWorkers));
            System.out.println("Job entity saved...");
            return ResponseEntity.ok().build();
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

        System.out.println(userCredentials);
        Iterable<JobEntity> jobIterable = jobRepository.getJobsByUsername(userCredentials.getUsername());
        List<Job> listOfJobs = new ArrayList<>();

        for(JobEntity jobEntity : jobIterable){
            Job job = new Job();

            job.setStatus(jobEntity.jobStatus.name());
            job.setTimestamp(jobEntity.uploadTime);
            job.setJobpath(jobEntity.jobPath);
            job.setId(jobEntity.getJobId());
            job.setEmployer(jobEntity.employer.getUserName());
            job.setName(jobEntity.getName());
            job.setWorkersRequested(jobEntity.workersRequested);
            job.setWorkersAssigned(jobEntity.workersAssigned);

            listOfJobs.add(job);
        }

        return ResponseEntity.ok(listOfJobs);

    }
}
