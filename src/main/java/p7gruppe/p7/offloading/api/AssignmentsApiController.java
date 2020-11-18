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
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.local.JobFileManager;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-18T11:02:06.033+01:00[Europe/Copenhagen]")

@Controller
@RequestMapping("${openapi.offloading.base-path:}")
public class AssignmentsApiController implements AssignmentsApi {

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    private final NativeWebRequest request;

    @Override
    public ResponseEntity<Resource> getJobForDevice(UserCredentials userCredentials, DeviceId deviceId) {
        // First check password
        if(!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.badRequest().build();
        }

        long userID = userRepository.getUserID(userCredentials.getUsername());
        // Check if device belongs to user
        if(!deviceRepository.doesDeviceBelongToUser(userID, deviceId.getImei())){
            return ResponseEntity.badRequest().build();
        }

        DeviceEntity device = deviceRepository.getDeviceByIMEI(deviceId.getImei());

        Optional<JobEntity> job = jobScheduler.assignJob(device);

        if(job.isPresent()){
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

        // If not job is present, return status 202
        return ResponseEntity.status(202).build();
    }

    @Override
    public ResponseEntity<Void> quitAssignment(UserCredentials userCredentials, DeviceId deviceId, Long jobId) {
        return null;
    }

    @Override
    public ResponseEntity<Void> uploadJobResult(UserCredentials userCredentials, DeviceId deviceId, Long jobId) {
        return null;
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
