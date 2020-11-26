package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import p7gruppe.p7.offloading.converters.FileStringConverter;
import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.Assignment;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.Jobresult;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
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
    public ResponseEntity<Assignment> getJobForDevice(UserCredentials userCredentials, DeviceId deviceId) {
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
            AssignmentEntity oldAssignment = possibleOldAssignment.get();
            Optional<JobEntity> job = jobRepository.findById(oldAssignment.job.getJobId());
            JobEntity jobValue = job.get();
            File jobFile = JobFileManager.getJobFile(job.get().jobPath);

            Assignment assignment = new Assignment().jobid(jobValue.getJobId()).jobfile(FileStringConverter.fileToBytes(jobFile));
            return ResponseEntity.status(HttpStatus.OK).body(assignment);
        }

        /**
         * If device is not already doing a job find one through the scheduler
         */
        Optional<JobEntity> job = jobScheduler.assignJob(device);

        if(job.isPresent()){
            // If some job is available for computation
            JobEntity jobValue = job.get();
            File jobFile = JobFileManager.getJobFile(job.get().jobPath);
            // create assignment entity to save in the database
            AssignmentEntity assignmentEntity = new AssignmentEntity(AssignmentEntity.Status.PROCESSING, device, job.get());
            // Update workers assigned
            jobValue.workersAssigned++;
            // Set status to Proccesing (it might already be, but then it doesn't make a difference)
            jobValue.jobStatus = JobEntity.JobStatus.PROCESSING;
            // Save the job changes
            jobRepository.save(jobValue);
            assignmentRepository.save(assignmentEntity);
            Assignment assignment = new Assignment().jobid(jobValue.getJobId()).jobfile(FileStringConverter.fileToBytes(jobFile));
            return ResponseEntity.ok(assignment);
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
    public ResponseEntity<Void> uploadJobResult(UserCredentials userCredentials, DeviceId deviceId, Long jobId, @Valid Jobresult jobresult) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.badRequest().build();
        }

        // TODO: 19/11/2020 Check status of all others doing the same job. If all are done, then combine results.

        // Check that job is still present
        Optional<JobEntity> job = jobRepository.findById(jobId);
        JobEntity jobValue;
        if(!job.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        else {
            jobValue = job.get();
        }

        // Update assignment to set as done
        DeviceEntity device = deviceRepository.getDeviceByIMEI(deviceId.getImei());
        Optional<AssignmentEntity> possibleAssignment = assignmentRepository.getProcessingAssignmentForDevice(device.deviceId);
        if (possibleAssignment.isPresent()){
            return ResponseEntity.badRequest().build();
        }
        AssignmentEntity assignment = possibleAssignment.get();
        assignment.setStatus(AssignmentEntity.Status.DONE_NOT_CHECKED);

        // If present upload file
        try {
            JobFileManager.saveResult(jobValue.jobPath, JobFileManager.decodeJobByte64(jobresult.getResult()));
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
