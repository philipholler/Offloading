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
import p7gruppe.p7.offloading.fileutils.FileUtilsKt;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.JobFiles;
import p7gruppe.p7.offloading.model.Jobresult;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        if(!deviceRepository.doesDeviceBelongToUser(deviceId.getImei(), userID)){
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
    public ResponseEntity<Void> uploadJobResult(UserCredentials userCredentials, DeviceId deviceId, Long jobId, @Valid Jobresult jobresult) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.badRequest().build();
        }

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
        Optional<AssignmentEntity> possibleAssignment = assignmentRepository.getProcessingAssignmentForDevice(device.getDeviceId());
        if (!possibleAssignment.isPresent()){
            System.out.println("Assignment not present for device " + device.toString());
            return ResponseEntity.badRequest().build();
        }
        AssignmentEntity assignment = possibleAssignment.get();
        assignment.setStatus(AssignmentEntity.Status.DONE_NOT_CHECKED);
        assignmentRepository.save(assignment);

        // If present upload file
        try {
            JobFileManager.saveResult(jobValue.jobPath, JobFileManager.decodeJobByte64(jobresult.getResult().getData()), assignment.getAssignmentId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterable<AssignmentEntity> assignmentsForJob = assignmentRepository.getAssignmentForJob(jobValue.getJobId());

        // Check that enough assignments have been made
        if(jobValue.workersAssigned == jobValue.workersRequested){
            // Check that all assignments are done
            boolean allAssignmentsDone = true;
            for (AssignmentEntity assig : assignmentsForJob){
                if(assig.getStatus() != AssignmentEntity.Status.DONE_NOT_CHECKED){
                    allAssignmentsDone = false;
                    break;
                }
            }

            // If all assignments done, check equality of results
            if(allAssignmentsDone){
                boolean resultsAreEqual = checkResultFilesAreEqual(jobValue.getJobPath());

                if(resultsAreEqual){
                    jobValue.setJobStatus(JobEntity.JobStatus.DONE);
                    for(AssignmentEntity assig : assignmentsForJob){
                        assig.setStatus(AssignmentEntity.Status.DONE);
                        assignmentRepository.save(assig);
                    }
                    jobRepository.save(jobValue);
                    JobFileManager.saveFinalResultFromIntermediate(jobValue.getJobPath());
                }
                else {
                    jobValue.setJobStatus(JobEntity.JobStatus.DONE_CONFLICTING_RESULTS);
                    for(AssignmentEntity assig : assignmentsForJob){
                        assig.setStatus(AssignmentEntity.Status.DONE_MAYBE_WRONG);
                        assignmentRepository.save(assig);
                    }
                    jobRepository.save(jobValue);
                }
            }

        }

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

    public boolean checkResultFilesAreEqual(String pathToJobDir){
        ArrayList<File> resultFiles = new ArrayList<>();

        File directoryFile = new File(pathToJobDir + File.separator + "results" + File.separator);
        for(File f : directoryFile.listFiles()){
            resultFiles.add(f);
        }

        // If only 1 result
        if (resultFiles.size() == 1) return true;

        File lastFile = resultFiles.get(0);
        for(int i = 1; i < resultFiles.size(); i++){
            if (!FileUtilsKt.checkZipFilesEquality(lastFile, resultFiles.get(i))) return false;
        }

        return true;
    }
}
