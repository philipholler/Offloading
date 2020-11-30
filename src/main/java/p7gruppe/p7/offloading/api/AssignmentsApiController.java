package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.converters.FileStringConverter;
import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.Result;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-18T11:02:06.033+01:00[Europe/Copenhagen]")

@Controller
@RequestMapping("${openapi.offloading.base-path:}")
public class AssignmentsApiController implements AssignmentsApi {
    private final NativeWebRequest request;

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    JobRepository jobRepository;


    @Override
    public ResponseEntity<JobFiles> getJobForDevice(UserCredentials userCredentials, DeviceId deviceId) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            System.out.println("GET_ASSIGNMENT - Invalid password: " + userCredentials.toString());
            return ResponseEntity.badRequest().build();
        }

        long userID = userRepository.getUserID(userCredentials.getUsername());
        // Check if device belongs to user
        if(!deviceRepository.doesDeviceBelongToUser(userID, deviceId.getImei())){
            System.out.println("GET_ASSIGNMENT - Device does not belong to user: " + deviceId + " " + userCredentials.toString());
            return ResponseEntity.badRequest().build();
        }

        DeviceEntity device = deviceRepository.getDeviceByIMEI(deviceId.getImei());

        /**
         * Check if device is already doing a job, but crashed or something
         */
        Optional<AssignmentEntity> possibleOldAssignment = assignmentRepository.getProcessingAssignmentForDevice(device.deviceId);
        if (possibleOldAssignment.isPresent()){
            System.out.println("GET_ASSIGNMENT - Device already has an active assignment");
            AssignmentEntity oldAssignment = possibleOldAssignment.get();
            Optional<JobEntity> job = jobRepository.findById(oldAssignment.job.getJobId());
            JobEntity jobValue = job.get();
            File jobFile = JobFileManager.getJobFile(job.get().jobPath);
            JobFiles jobfiles = null;
            try {
                jobfiles = new JobFiles().jobid(jobValue.getJobId()).data(FileStringConverter.fileToBytes(jobFile));
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
            return ResponseEntity.ok(jobfiles);
        }

        /**
         * If device is not already doing a job find one through the scheduler
         */
        Optional<JobEntity> job = jobScheduler.assignJob(device);

        if(job.isPresent()){
            // If some job is available for computation
            JobEntity jobValue = job.get();
            File jobFile = JobFileManager.getJobFile(job.get().jobPath);
            JobFiles jobfile = null;
            try {
                jobfile = new JobFiles().jobid(jobValue.getJobId()).data(FileStringConverter.fileToBytes(jobFile));
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }
            // create assignment entity to save in the database
            AssignmentEntity assignmentEntity = new AssignmentEntity(AssignmentEntity.Status.PROCESSING, device, job.get());
            // Update workers assigned
            jobValue.workersAssigned++;
            // Set status to Proccesing (it might already be, but then it doesn't make a difference)
            jobValue.jobStatus = JobEntity.JobStatus.PROCESSING;
            // Save the job changes
            jobRepository.save(jobValue);
            assignmentRepository.save(assignmentEntity);
            return ResponseEntity.ok(jobfile);
        }

        // If not job is present, return status 202
        return ResponseEntity.status(202).build();
    }

    @Override
    public ResponseEntity<Void> quitAssignment(UserCredentials userCredentials, DeviceId deviceId, Long jobId) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.badRequest().build();
        }

        // Update status for assignment to QUIT
        DeviceEntity quittingDevice = deviceRepository.getDeviceByIMEI(deviceId.getImei());
        Optional<AssignmentEntity> possibleAssignment = assignmentRepository.getProcessingAssignmentForDevice(quittingDevice.deviceId);
        if (!possibleAssignment.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        AssignmentEntity assignment = possibleAssignment.get();
        assignment.setStatus(AssignmentEntity.Status.QUIT);
        assignmentRepository.save(assignment);

        // Decrement workers assigned to job
        Optional<JobEntity> job = jobRepository.findById(jobId);
        if (job.isPresent()) {
            JobEntity jobValue = job.get();
            jobValue.workersAssigned -= 1;
            jobRepository.save(jobValue);

            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }

    }

    @Override
    public ResponseEntity<Void> uploadJobResult(UserCredentials userCredentials, DeviceId deviceId, Long jobId, @Valid Result result) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            System.err.println("Attempted result upload. Invalid user credentials " + userCredentials.toString());
            return ResponseEntity.badRequest().build();
        }

        // TODO: 19/11/2020 Check status of all others doing the same job. If all are done, then combine results. - Philip
        // Possibly do hash of zip files and check equality

        // Check that job is still present
        Optional<JobEntity> job = jobRepository.findById(jobId);
        JobEntity jobValue;
        if(!job.isPresent()){
            // todo Better response that can be interpreted on android side (so that the worker knows to quit this job)
            System.err.println("Attempted result upload. Job result is no longer present" + jobId);
            return ResponseEntity.badRequest().build();
        } else {
            jobValue = job.get();
        }

        // Update assignment to set as done
        DeviceEntity device = deviceRepository.getDeviceByIMEI(deviceId.getImei());
        Optional<AssignmentEntity> possibleAssignment = assignmentRepository.getProcessingAssignmentForDevice(device.deviceId);
        if (!possibleAssignment.isPresent()){
            System.err.println("Attempted result upload. But device (" + device.deviceId + ", " + deviceId.getImei() + ") does not have matching assignment");
            return ResponseEntity.badRequest().build();
        }
        AssignmentEntity assignment = possibleAssignment.get();
        assignment.setStatus(AssignmentEntity.Status.DONE_NOT_CHECKED);

        // If present upload file
        try {
            JobFileManager.saveResult(jobValue.jobPath, result.getResultfile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        assignmentRepository.save(assignment);
        return ResponseEntity.ok().build();
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AssignmentsApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
}
