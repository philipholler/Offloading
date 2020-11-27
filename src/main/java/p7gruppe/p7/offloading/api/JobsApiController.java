package p7gruppe.p7.offloading.api;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.converters.FileStringConverter;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.Job;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.Result;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.ws.Response;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
    public ResponseEntity<Void> postJob(UserCredentials userCredentials, @NotNull @Valid Integer requestedWorkers, @NotNull @Valid String jobname, @NotNull @Valid Integer timeout, @Valid byte[] body) {
        System.out.println("Posting job....");
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        try {
            byte[] decoded = JobFileManager.decodeJobByte64(body);
            String path = JobFileManager.saveJob(userCredentials.getUsername(), decoded);
            System.out.println("Job saved...");
            UserEntity userEntity = userRepository.getUserByUsername(userCredentials.getUsername());
            System.out.println("Username pulled");
            JobEntity jobEntity = jobRepository.save(new JobEntity(userEntity, path, jobname, requestedWorkers, timeout));
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
    public ResponseEntity<JobFiles> getJobFiles(Long jobId, UserCredentials userCredentials) {
        // First check password
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        Optional<JobEntity> job = jobRepository.findById(jobId);

        if (!job.isPresent())
            return ResponseEntity.badRequest().build();
        // If some job is available for computation
        File file = JobFileManager.getJobFile(job.get().jobPath);

        try {
            byte[] bytes = FileStringConverter.fileToBytes(file); // These bytes ARE correct.

            // byte[] encoded = Base64.getEncoder().encodeToString(bytes).getBytes(); // These are WRONG.
            JobFiles jobfiles = new JobFiles();
            jobfiles.setData(bytes);
            jobfiles.jobid(jobId);

            return ResponseEntity.status(HttpStatus.OK).body(jobfiles);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    public ResponseEntity<JobFiles> getJobResult(Long jobId, UserCredentials userCredentials) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())) {
            return ResponseEntity.badRequest().build();
        }

        // Find the job
        Optional<JobEntity> job = jobRepository.findById(jobId);

        if (!job.isPresent()) {
            System.out.println("Job not found for get job result");
            // If job not even in system
            return ResponseEntity.badRequest().build();
        }

        JobEntity jobValue = job.get();

        // Check that the status is done, otherwise do not include
        if(jobValue.jobStatus != JobEntity.JobStatus.DONE){
            // If result file not ready yet
            System.out.println("Job status not done, could not fetch result");
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }

        // Try to fetch result files
        try {
            System.out.println("Trying to get result file");
            File file = JobFileManager.getResultFile(job.get().jobPath);
            JobFiles resultFiles = new JobFiles();
            resultFiles.setJobid(jobId);
            resultFiles.setData(FileStringConverter.fileToBytes(file));
            return ResponseEntity.ok(resultFiles);
        }
        catch (Exception e){
            // If result file not ready yet
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
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
