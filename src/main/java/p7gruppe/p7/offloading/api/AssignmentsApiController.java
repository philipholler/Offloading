package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.JobEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

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
        UserEntity user = new UserEntity(userCredentials.getUsername(), userCredentials.getPassword());
        userRepository.save(user);
        DeviceEntity device = new DeviceEntity(user, deviceId.getImei());
        deviceRepository.save(device);

        Optional<JobEntity> job = jobScheduler.assignJob(device);

        if (job.isPresent()){
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().build();
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
