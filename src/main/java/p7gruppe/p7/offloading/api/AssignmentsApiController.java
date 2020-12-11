package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.api.dataclasses.ConfidenceResult;
import p7gruppe.p7.offloading.converters.FileStringConverter;
import p7gruppe.p7.offloading.data.enitity.AssignmentEntity;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.AssignmentRepository;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.JobRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.fileutils.FileUtilsKt;
import p7gruppe.p7.offloading.model.*;
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

    @Autowired
    JobFileManager jobFileManager;

    @Override
    public ResponseEntity<Void> pingAssignment(UserCredentials userCredentials, DeviceId deviceId, Long jobId) {
        DeviceEntity deviceEntity = deviceRepository.getDeviceByIMEI(deviceId.getImei());
        AssignmentEntity assignmentEntity = assignmentRepository.getAssignmentForJob(jobId, deviceEntity.getDeviceId());

        boolean shouldContinue = jobScheduler.shouldContinue(assignmentEntity.getAssignmentId());

        if (shouldContinue) return ResponseEntity.ok().build();

        // Reward workers even though the job is quit. Do not reward users for calculating their own jobs
        JobEntity job = jobRepository.getJobByID(jobId);
        if(job.getEmployer().getUserId() != deviceEntity.getOwner().getUserId()){
            UserEntity workerUser = deviceEntity.getOwner();
            long cpuTimeReward = System.currentTimeMillis() - assignmentEntity.timeOfAssignmentInMs;
            workerUser.setCpuTimeContributedInMs(workerUser.getCpuTimeContributedInMs() + cpuTimeReward);
            userRepository.save(workerUser);
        }


        return ResponseEntity.status(HttpStatus.GONE).build();
    }

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
            System.out.println("GET_ASSIGNMENT - Device already has an active assignment");
            AssignmentEntity oldAssignment = possibleOldAssignment.get();
            Optional<JobEntity> job = jobRepository.findById(oldAssignment.job.getJobId());
            JobEntity jobValue = job.get();
            File jobFile = jobFileManager.getJobFile(job.get().jobPath);
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
            File jobFile = jobFileManager.getJobFile(job.get().jobPath);
            JobFiles jobfile = null;
            try {
                jobfile = new JobFiles().jobid(jobValue.getJobId()).data(FileStringConverter.fileToBytes(jobFile));
            } catch (IOException e) {
                return ResponseEntity.status(500).build();
            }

            AssignmentEntity assignmentEntity = null;
            if(jobScheduler.usingTestAssignments() && !jobScheduler.shouldTrustDevice(device)){
                // The worker is not trusted, but gets the assignment anyway to get a chance
                // His answer is not counted, but his trustscore can increase, if his answer is correct
                assignmentEntity = new AssignmentEntity(AssignmentEntity.Status.PROCESSING, device, job.get(), true);
            } else {
                // create assignment entity to save in the database
                assignmentEntity = new AssignmentEntity(AssignmentEntity.Status.PROCESSING, device, job.get(), false);
                // Update workers assigned
                jobValue.workersAssigned++;
                // Set status to Proccesing (it might already be, but then it doesn't make a difference)
                jobValue.jobStatus = JobEntity.JobStatus.PROCESSING;
            }

            // Increase cpu time spent by timeout for employer. This decreases his priority
            // Only if the job and device is not his own
            if(device.getOwner().getUserId() != jobValue.getEmployer().getUserId()){
                UserEntity employer = jobValue.getEmployer();
                employer.setCpuTimeSpentInMs(employer.getCpuTimeSpentInMs() + (jobValue.timeoutInMinutes * 60 * 1000));
                userRepository.save(employer);
            }

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

            // Reinsert cpu time spent to employer, if a worker quits. This increases his priority.
            // Do not increase the cpu time, if the job was his own.
            UserEntity employer = jobValue.getEmployer();
            if(quittingDevice.getOwner().getUserId() != employer.getUserId()){
                employer.setCpuTimeSpentInMs(employer.getCpuTimeSpentInMs() + (jobValue.timeoutInMinutes * 60 * 1000));
                userRepository.save(employer);
            }

            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }

    }


    public ResponseEntity<Void> uploadJobResult(UserCredentials userCredentials, DeviceId deviceId, Long jobId, @Valid Jobresult jobresult) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            System.err.println("Attempted result upload. Invalid user credentials " + userCredentials.toString());
            return ResponseEntity.badRequest().build();
        }

        // Check that job is still present
        Optional<JobEntity> job = jobRepository.findById(jobId);
        JobEntity jobValue;
        if(!job.isPresent()){
            // todo Better response that can be interpreted on android side (so that the worker knows to quit this job)
            System.err.println("Attempted result upload. Job result is no longer present" + jobId);
            return ResponseEntity.badRequest().build();
        }
        else {
            jobValue = job.get();
        }

        /*
        Update the assignment status, if it is present in the database
         */
        DeviceEntity device = deviceRepository.getDeviceByIMEI(deviceId.getImei());
        Optional<AssignmentEntity> possibleAssignment = assignmentRepository.getProcessingAssignmentForDevice(device.deviceId);
        if (!possibleAssignment.isPresent()){
            // Check that the assignment was actually present in the first place
            // but has since been marked as done (maybe the user got a result from his own worker)
            if(!assignmentRepository.getAssignmentForJobAndWorker(device.deviceId, jobId).iterator().hasNext()){
                // if the assignment was never present, some went wrong
                System.err.println("Attempted result upload. But device (" + device.deviceId + ", " + deviceId.getImei() + ") does not have matching assignment");
                return ResponseEntity.badRequest().build();
            }
            else {
                return ResponseEntity.ok().build();
            }
        }
        AssignmentEntity assignment = possibleAssignment.get();
        assignment.setStatus(AssignmentEntity.Status.DONE_NOT_CHECKED);
        assignment.setTimeOfCompletionInMs(System.currentTimeMillis());
        assignmentRepository.save(assignment);

        // Give worker reward for contributing, if the worker and employer is not the same user
        UserEntity userContributing = device.getOwner();
        UserEntity employer = jobValue.getEmployer();
        boolean isUsersOwnDevice = userContributing.getUserId() == employer.getUserId();
        if(!isUsersOwnDevice && !assignment.isTrustTestAssignment){
            // Reward the user
            long timeSpendOnAssignment = assignment.getTimeOfCompletionInMs() - assignment.getTimeOfAssignmentInMs();
            userContributing.setCpuTimeContributedInMs(userContributing.getCpuTimeContributedInMs() + timeSpendOnAssignment);
            userRepository.save(userContributing);

            // Take "payment" from the employer. Update his cpu time spent.
            long originalTimeoutInMs = jobValue.getTimeoutInMinutes() * 60 * 1000;
            long newCpuTimeSpent = employer.getCpuTimeSpentInMs() + originalTimeoutInMs - timeSpendOnAssignment;
            employer.setCpuTimeSpentInMs(newCpuTimeSpent);
            userRepository.save(employer);

            // Reward the device for finishing
            device.incrementAssignmentsFinished();
            deviceRepository.save(device);
        }

        // If present upload file
        try {
            jobFileManager.saveResult(jobValue.jobPath,
                    jobFileManager.decodeFromBase64(jobresult.getResult().getData()),
                    assignment.getAssignmentId(),
                    assignment.isTrustTestAssignment);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Iterable<AssignmentEntity> assignmentsForJob = assignmentRepository.getAssignmentForJob(jobValue.getJobId());

        // If the device belongs to the user, we assume the answer to be correct.
        if(isUsersOwnDevice && !assignment.isTrustTestAssignment){
            // Set all assignments for job to done
            // This causes the scheduler to make them stop the job when pinging
            for(AssignmentEntity a : assignmentsForJob){
                a.setStatus(AssignmentEntity.Status.DONE);
                assignmentRepository.save(a);
            }

            // Save correct result file, i.e. the result from the users own worker
            jobFileManager.saveFinalResultFromSpecificAssignment(assignment.getAssignmentId(), jobValue.getJobPath());

            // Set job to done and confidence to 1.0, since it was the employers own worker
            jobValue.setJobStatus(JobEntity.JobStatus.DONE);
            jobValue.setConfidenceLevel(1.0);
            jobRepository.save(jobValue);
        }
        // If worker does not belong to user, check that all assignments are done
        // If done update statuses and extract the best result file.
        else if(jobValue.workersAssigned == jobValue.answersNeeded){
            // Check that all assignments are done, that are not test assigments
            boolean allAssignmentsDone = true;
            for (AssignmentEntity assig : assignmentsForJob){
                // Only count if real workers are not done. Ignore test assignments
                if(assig.getStatus() != AssignmentEntity.Status.DONE_NOT_CHECKED && !assig.isTrustTestAssignment){
                    allAssignmentsDone = false;
                    break;
                }
            }

            // If all assignments done, check equality of results
            if(allAssignmentsDone){
                ConfidenceResult confidenceData = getConfidenceLevelAndBestFile(jobValue.getJobPath());

                // Update assignment status and reward worker according to result
                for(AssignmentEntity assig : assignmentsForJob){
                    // Just kill test assignments by setting them done if all other "real" workers are done
                    if(assig.isTrustTestAssignment){
                        assig.setTimeOfCompletionInMs(System.currentTimeMillis());
                    }
                    assig.setStatus(AssignmentEntity.Status.DONE);
                    assignmentRepository.save(assig);
                    // increment assignments done
                    DeviceEntity worker = assig.worker;
                    // If the device was in the majority with results, increment the finishedAssignments correct.
                    if(confidenceData.hasCorrectAnswerFromAssignment(assignment)){
                        // Finished correct results are only counted for real assignments. Not test assigments
                        if(!assig.isTrustTestAssignment){
                            worker.incrementAssignmentsFinishedCorrectResult();
                        }
                        worker.updateTrustScore(true);
                    } else {
                        worker.updateTrustScore(false);
                    }
                    deviceRepository.save(worker);
                }

                double delta = 0.001;
                // If the confidence is 1.0, all agree with the result
                if(Math.abs(confidenceData.getConfidenceLevel() - 1.0) < delta){
                    jobValue.setJobStatus(JobEntity.JobStatus.DONE);
                    jobValue.setConfidenceLevel(confidenceData.getConfidenceLevel());
                    jobRepository.save(jobValue);
                    // Simply take any intermediate file, since they all agree
                    jobFileManager.saveFinalResultFromIntermediate(jobValue.getJobPath());
                }
                else {
                    jobValue.setJobStatus(JobEntity.JobStatus.DONE_CONFLICTING_RESULTS);
                    jobValue.setConfidenceLevel(confidenceData.getConfidenceLevel());
                    // Save the file with the highest confidence level as final result
                    jobFileManager.saveFinalResultFromIntermediateWithConfidence(confidenceData.getBestFilePath(),jobValue.getJobPath());
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


    public ConfidenceResult getConfidenceLevelAndBestFile(String pathToJobDir){
        ArrayList<File> resultFiles = new ArrayList<>();

        File directoryFile = new File(pathToJobDir + File.separator + "results" + File.separator);
        for(File f : directoryFile.listFiles()){
            resultFiles.add(f);
        }

        return FileUtilsKt.getConfidenceLevel(resultFiles);
    }
}
