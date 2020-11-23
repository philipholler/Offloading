package p7gruppe.p7.offloading.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import p7gruppe.p7.offloading.data.enitity.DeviceEntity;
import p7gruppe.p7.offloading.data.enitity.UserEntity;
import p7gruppe.p7.offloading.data.repository.DeviceRepository;
import p7gruppe.p7.offloading.data.repository.UserRepository;
import p7gruppe.p7.offloading.model.DeviceId;
import p7gruppe.p7.offloading.model.UserCredentials;
import p7gruppe.p7.offloading.scheduling.JobScheduler;

import javax.validation.Valid;
import java.util.Optional;

@javax.annotation.Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2020-11-18T11:02:06.033+01:00[Europe/Copenhagen]")

@Controller
@RequestMapping("${openapi.offloading.base-path:}")
public class UsersApiController implements UsersApi {

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceRepository deviceRepository;

    private final NativeWebRequest request;

    @Override
    public ResponseEntity<UserCredentials> createUser(UserCredentials userCredentials) {
        System.out.println("RECEIVED USER POST");
        if (userRepository.userExists(userCredentials.getUsername())){
            return ResponseEntity.badRequest().build();
        }
        UserEntity user = new UserEntity(userCredentials.getUsername(), userCredentials.getPassword());
        userRepository.save(user);

        return ResponseEntity.ok(userCredentials);
    }

    @Override
    public ResponseEntity<UserCredentials> deleteUser(UserCredentials userCredentials) {
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.badRequest().build();
        }

        long id = userRepository.getUserID(userCredentials.getUsername());

        userRepository.deleteById(id);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<UserCredentials> login(UserCredentials userCredentials, @Valid DeviceId deviceId) {
        UserEntity testUser = userRepository.getUserByUsername(userCredentials.getUsername());

        // First check password
        if (!userRepository.isPasswordCorrect(userCredentials.getUsername(), userCredentials.getPassword())){
            return ResponseEntity.status(404).build();
        }

        // If logged in from an employer client
        if(deviceId.getImei().equals("null")){
            return ResponseEntity.ok(userCredentials);
        }
        // If logged in from a worker
        else {
            // If worker not seen before, add it
            if(!deviceRepository.isDevicePresent(deviceId.getImei())){
                UserEntity deviceUser = userRepository.getUserByUsername(userCredentials.getUsername());
                // If user does not exist or the device is already registered but with a different user.
                if(deviceUser == null || !(deviceUser.getUserName().equals(userCredentials.getUsername()))) return ResponseEntity.status(404).build();
                DeviceEntity newDevice = new DeviceEntity(deviceUser, deviceId.getImei());
                deviceRepository.save(newDevice);
            }

            return ResponseEntity.ok(userCredentials);
        }
    }

    @org.springframework.beans.factory.annotation.Autowired
    public UsersApiController(NativeWebRequest request) {
        this.request = request;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }
}
